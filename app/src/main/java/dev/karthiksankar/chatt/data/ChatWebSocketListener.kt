package dev.karthiksankar.chatt.data

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatWebSocketListener(private val conversationId: String) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        // FIXME Cyclic dependency issue - ChattWebSocketListener should not depend on WebSocketManager
        WebSocketManager.socketStates[conversationId]?.value = true
        Log.i("WebSocket", "Connected to channel: $conversationId")
        // FIXME Cyclic dependency issue - ChattWebSocketListener should not depend on WebSocketManager
        WebSocketManager.retryPendingMessages(conversationId)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        ChatStorageInMemory.onNewMessage(conversationId, text)
        Log.i("WebSocket", "Message received: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        // FIXME Cyclic dependency issue - ChattWebSocketListener should not depend on WebSocketManager
        WebSocketManager.socketStates[conversationId]?.value = false
        Log.i("WebSocket", "Connection closing for channel: $conversationId")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        // FIXME Cyclic dependency issue - ChattWebSocketListener should not depend on WebSocketManager
        WebSocketManager.socketStates[conversationId]?.value = false
        Log.e("WebSocket", "Error in channel $conversationId: ${t.message}")

        // TODO Replace this with proper retry mechanism
        // Attempt to reconnect after a delay
        Thread.sleep(5000)
        // FIXME Cyclic dependency issue - ChattWebSocketListener should not depend on WebSocketManager
        WebSocketManager.connect(conversationId)
    }
}
