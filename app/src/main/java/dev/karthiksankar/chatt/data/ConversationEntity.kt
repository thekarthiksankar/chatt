package dev.karthiksankar.chatt.data

data class ConversationEntity(
    val channelId: String,
    val title: String,
    val messages: List<MessageEntity>
)