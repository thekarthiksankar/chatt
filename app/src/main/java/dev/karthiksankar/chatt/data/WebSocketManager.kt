package dev.karthiksankar.chatt.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object WebSocketManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val sockets = ConcurrentHashMap<String, WebSocket>()
    val socketStates =
        ConcurrentHashMap<String, MutableStateFlow<Boolean>>() // Maps conversationId to connection state. true if connected, false if disconnected
    private val pendingMessages = ConcurrentHashMap<String, MutableList<MessageEntity>>()

    fun connect(conversationId: String) = coroutineScope.launch {
        val url =
            "wss://s14912.blr1.piesocket.com/v3/$conversationId?api_key=KkPujCxVRnmw0Ik0rYKF7R7E54cDqQpGrJm8wbqo" // TODO Move this URL into a secure place
        val request = Request.Builder().url(url).build()
        val listener = ChatWebSocketListener(
            conversationId,
            onOpen = { onNewConnectionAcquired(conversationId) },
            onMessage = { onNewMessageReceived(conversationId, it) },
            onClosing = { onConnectionClosed(conversationId) },
            onFailure = { onConnectionFailed(conversationId) }
        )
        val ws = client.newWebSocket(request, listener)
        sockets[conversationId] = ws
        socketStates.getOrPut(conversationId) { MutableStateFlow(false) }
        Log.i("Socket", url)
    }

    private fun onNewConnectionAcquired(conversationId: String) {
        socketStates[conversationId]?.value = true
        coroutineScope.launch {
            retryPendingMessages(conversationId)
        }
    }

    private fun onNewMessageReceived(conversationId: String, message: String) {
        coroutineScope.launch {
            ChatStorageInMemory.onNewMessage(conversationId, message)
        }
    }

    private fun onConnectionClosed(conversationId: String) {
        socketStates[conversationId]?.value = false
    }

    private fun onConnectionFailed(conversationId: String) {
        Log.e("SocketManager", "Connection failed for conversation: $conversationId")
        socketStates[conversationId]?.value = false
        Thread.sleep(5000)
        connect(conversationId)
    }

    fun getConnectionState(conversationId: String) =
        socketStates.getOrPut(conversationId) { MutableStateFlow(false) }.asStateFlow()

    suspend fun sendMessage(conversationId: String, message: MessageEntity) {
        val socket = sockets[conversationId]
        val rawMessage = Json.encodeToString(MessageEntity.serializer(), message)
        val success = socket?.send(rawMessage) ?: false

        if (success) {
            Log.i("SocketManager", "sendMessage: $message")
            ChatStorageInMemory.updateMessageState(
                conversationId,
                message.id,
                MessageEntity.State.SENT
            )
        } else {
            Log.i("SocketManager", "sendMessage no active sockets found: $message")
            handleFailedMessage(conversationId, message)
        }
    }

    private suspend fun handleFailedMessage(conversationId: String, message: MessageEntity) {
        ChatStorageInMemory.updateMessageState(
            conversationId,
            message.id,
            MessageEntity.State.FAILED,
        )
        pendingMessages.getOrPut(conversationId) { mutableListOf() }.add(message)
    }

    suspend fun retryPendingMessages(conversationId: String) {
        pendingMessages[conversationId]?.toList()?.forEach { message ->
            pendingMessages[conversationId]?.remove(message)
            sendMessage(conversationId, message)
        }
    }
}