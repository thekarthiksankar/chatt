package dev.karthiksankar.chatt.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MessageEntity(
    val id: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    @Transient val state: State = State.SENDING,
    @Transient val isOutgoing: Boolean = true  // true for messages sent from app, false for received
) {
    enum class State {
        SENDING,    // Message is being sent to socket
        SENT,       // Successfully sent/received
        FAILED,     // Failed to send due to network issues
    }
}
