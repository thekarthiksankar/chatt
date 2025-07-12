package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ChatStorageInMemory {
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = _conversations.asStateFlow()

    fun createConversation(): ConversationEntity {
        val channelId = java.util.UUID.randomUUID().toString()
        val newConversation = ConversationEntity(
            id = java.util.UUID.randomUUID().toString(),
            title = "Alice",
            channelId = channelId,
            messages = emptyList()
        )
        addConversation(newConversation)
        return newConversation
    }

    private fun addConversation(conversation: ConversationEntity) {
        val currentList = _conversations.value.toMutableList()
        currentList.add(conversation)
        _conversations.value = currentList
    }
}
