package dev.karthiksankar.chatt.ui.conversation

import dev.karthiksankar.chatt.data.MessageEntity

data class ConversationUiState(
    val messages: List<MessageEntity> = emptyList(),
    val isConnected: Boolean = false,
    val name: String = "",
    val conversationId: String = "",
)