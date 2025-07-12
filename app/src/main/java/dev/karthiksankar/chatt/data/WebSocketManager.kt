package dev.karthiksankar.chatt.data

import android.util.Log
import dev.karthiksankar.chatt.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object WebSocketManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val sockets = ConcurrentHashMap<String, WebSocket>()
    val socketStates =
        ConcurrentHashMap<String, MutableStateFlow<Boolean>>() // Maps conversationId to connection state. true if connected, false if disconnected

    fun connect(conversationId: String) {
        val url =
            "wss://s14912.blr1.piesocket.com/v3/$conversationId?api_key=${BuildConfig.PIE_API_KEY}"
        val request = Request.Builder().url(url).build()
        val listener = ChatWebSocketListener(conversationId)
        val ws = client.newWebSocket(request, listener)
        sockets[conversationId] = ws
        socketStates.getOrPut(conversationId) { MutableStateFlow(false) }
        Log.i("Socket", url)
    }

    fun getConnectionState(conversationId: String) =
        socketStates.getOrPut(conversationId) { MutableStateFlow(false) }.asStateFlow()

    fun sendMessage(conversationId: String, message: MessageEntity) {
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

    private fun handleFailedMessage(conversationId: String, message: MessageEntity) {
        ChatStorageInMemory.updateMessageState(
            conversationId,
            message.id,
            MessageEntity.State.FAILED,
        )
    }
}