package dev.karthiksankar.chatt.data

data class ConversationEntity(
    val id: String,
    val title: String,
    val messages: List<MessageEntity>
)