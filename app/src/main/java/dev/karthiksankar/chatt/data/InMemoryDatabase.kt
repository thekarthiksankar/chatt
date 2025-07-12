package dev.karthiksankar.chatt.data

object InMemoryDatabase {
    val conversationDao: ConversationDao = InMemoryConversationDao()
    val messageDao: MessageDao = InMemoryMessageDao()
}

