package dev.karthiksankar.chatt.data

data class ConversationEntity(
    val id: String,
    val title: String,
    val channelId: String,
    val messages: List<MessageEntity>
)