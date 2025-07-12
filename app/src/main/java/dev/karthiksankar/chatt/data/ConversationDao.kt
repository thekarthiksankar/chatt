package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.Flow

interface ConversationDao {
    suspend fun getConversations(): Flow<List<ConversationEntity>>
    suspend fun getConversationById(id: String): Flow<ConversationEntity?>
    suspend fun insert(conversation: ConversationEntity)
}
