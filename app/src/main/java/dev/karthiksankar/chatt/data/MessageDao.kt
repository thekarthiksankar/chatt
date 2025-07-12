package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.Flow

interface MessageDao {
    suspend fun getMessages(): Flow<List<MessageEntity>>
    suspend fun getMessages(conversationId: String): Flow<List<MessageEntity>>
    suspend fun insert(message: MessageEntity)
    suspend fun updateState(
        conversationId: String,
        messageId: String,
        newState: MessageEntity.State
    )
}
