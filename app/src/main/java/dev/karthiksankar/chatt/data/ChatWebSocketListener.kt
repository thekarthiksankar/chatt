package dev.karthiksankar.chatt.data

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatWebSocketListener(private val channelId: String) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        // TODO mark the connection as open
        // TODO Try to resend any pending messages
        Log.i("WebSocket", "Connected to channel: $channelId")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        ChatStorageInMemory.onNewMessage(channelId, text)
        Log.i("WebSocket", "Message received: $text")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        // TODO mark the connection as closed
        Log.i("WebSocket", "Connection closed for channel: $channelId")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        // TODO mark the connection as failed
        Log.e("WebSocket", "Error in channel $channelId: ${t.message}")

        // Attempt to reconnect after a delay
        Thread.sleep(5000)
        WebSocketManager.connect(channelId)
    }
}
