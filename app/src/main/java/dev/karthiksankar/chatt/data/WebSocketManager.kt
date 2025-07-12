package dev.karthiksankar.chatt.data

import android.util.Log
import dev.karthiksankar.chatt.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object WebSocketManager {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun connect(channelId: String) {
        val url = "wss://s14912.blr1.piesocket.com/v3/$channelId?api_key=${BuildConfig.PIE_API_KEY}"
        val request = Request.Builder().url(url).build()
        val listener = ChatWebSocketListener(channelId)
        client.newWebSocket(request, listener)
        Log.i("Socket", url)
    }
}