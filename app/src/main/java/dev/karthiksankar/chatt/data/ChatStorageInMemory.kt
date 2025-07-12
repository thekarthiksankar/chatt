package dev.karthiksankar.chatt.data

object ChatStorageInMemory {
    private val NAMES = listOf("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry")
    val db = InMemoryDatabase

    suspend fun createConversation(): ConversationEntity {
        val conversationId = java.util.UUID.randomUUID().toString()
        val newConversation = ConversationEntity(
            title = NAMES.random(),
            id = conversationId,
            messages = emptyList()
        )
        db.conversationDao.insert(newConversation)
        return newConversation
    }

    suspend fun addMessage(message: MessageEntity) {
        db.messageDao.insert(message)
    }

    suspend fun onNewMessage(conversationId: String, text: String) {
        val message = MessageEntity(
            id = java.util.UUID.randomUUID().toString(),
            conversationId = conversationId,
            text = text,
            timestamp = System.currentTimeMillis(),
            state = MessageEntity.State.UNREAD,
            isOutgoing = false,
        )
        addMessage(message)
    }

    suspend fun updateMessageState(
        conversationId: String,
        messageId: String,
        newState: MessageEntity.State
    ) {
        db.messageDao.updateState(conversationId, messageId, newState)
    }
}
