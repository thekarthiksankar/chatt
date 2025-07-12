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
        ConcurrentHashMap<String, MutableStateFlow<Boolean>>() // Maps channelId to connection state. true if connected, false if disconnected

    fun connect(channelId: String) {
        val url = "wss://s14912.blr1.piesocket.com/v3/$channelId?api_key=${BuildConfig.PIE_API_KEY}"
        val request = Request.Builder().url(url).build()
        val listener = ChatWebSocketListener(channelId)
        val ws = client.newWebSocket(request, listener)
        sockets[channelId] = ws
        socketStates.getOrPut(channelId) { MutableStateFlow(false) }
        Log.i("Socket", url)
    }

    fun getConnectionState(channelId: String) =
        socketStates.getOrPut(channelId) { MutableStateFlow(false) }.asStateFlow()

    fun sendMessage(channelId: String, message: MessageEntity) {
        val socket = sockets[channelId]

        val rawMessage = Json.encodeToString(MessageEntity.serializer(), message)

        if (socket != null) {
            val success = socket.send(rawMessage)
            if (success) {
                Log.i("SocketManager", "sendMessage: $message")
                ChatStorageInMemory.updateMessageState(
                    channelId,
                    message.id,
                    MessageEntity.State.SENT
                )
            } else {
                Log.i("SocketManager", "sendMessage failed: $message")
                // TODO Fallback to local storage or retry logic
            }
        } else {
            Log.i("SocketManager", "sendMessage no active sockets found: $message")
            // TODO Fallback to local storage or retry logic
        }
    }
}