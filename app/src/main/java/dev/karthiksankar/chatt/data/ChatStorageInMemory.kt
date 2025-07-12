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
            title = "Alice", // TODO Generate random name
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

    private fun addMessage(channelId: String, message: MessageEntity) {
        val currentList = _conversations.value.toMutableList()
        val index = currentList.indexOfFirst { it.channelId == channelId }
        if (index != -1) {
            val conversation = currentList[index]
            val newMessages = conversation.messages.toMutableList().apply {
                add(message)
            }
            currentList[index] = conversation.copy(messages = newMessages)
            _conversations.value = currentList
        }
    }

    fun onNewMessage(channelId: String, text: String) {
        val message = MessageEntity(
            id = java.util.UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis(),
            state = MessageEntity.State.SENT
        )
        addMessage(channelId, message)
    }
}
