package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryConversationDao : ConversationDao {
    private val conversationsFlow = MutableStateFlow<List<ConversationEntity>>(emptyList())

    override suspend fun getConversations(): Flow<List<ConversationEntity>> = conversationsFlow

    override suspend fun getConversationById(id: String): Flow<ConversationEntity?> =
        conversationsFlow.map { list ->
            list.find { it.id == id }
        }

    override suspend fun insert(conversation: ConversationEntity) {
        conversationsFlow.update { it + conversation }
    }
}
