package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map

class InMemoryMessageDao : MessageDao {

    private val messages = MutableStateFlow<List<MessageEntity>>(emptyList())

    override suspend fun getMessages() = messages

    override suspend fun getMessages(conversationId: String) =
        messages.map { it.filter { it.conversationId == conversationId } }

    override suspend fun insert(message: MessageEntity) {
        messages.value += message
    }

    override suspend fun updateState(
        conversationId: String,
        messageId: String,
        newState: MessageEntity.State
    ) {
        val list = messages.lastOrNull().orEmpty().toMutableList()
        val idx = list.indexOfFirst { it.id == messageId }
        if (idx != -1) {
            val msg = list[idx]
            list[idx] = msg.copy(state = newState)
            messages.value = list
        }
    }
}
