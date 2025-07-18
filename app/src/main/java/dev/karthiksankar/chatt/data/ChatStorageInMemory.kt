package dev.karthiksankar.chatt.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ChatStorageInMemory {

    private val NAMES = listOf("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry")
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = _conversations.asStateFlow()

    fun createConversation(): ConversationEntity {
        val channelId = java.util.UUID.randomUUID().toString()
        val newConversation = ConversationEntity(
            title = NAMES.random(),
            id = channelId,
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

    fun addMessage(channelId: String, message: MessageEntity) {
        val currentList = _conversations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == channelId }
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
            state = MessageEntity.State.UNREAD,
            isOutgoing = false,
        )
        addMessage(channelId, message)
    }

    fun updateMessageState(channelId: String, messageId: String, newState: MessageEntity.State) {
        val currentList = _conversations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == channelId }
        if (index != -1) {
            val conversation = currentList[index]
            val messageIndex = conversation.messages.indexOfFirst { it.id == messageId }
            if (messageIndex != -1) {
                val newMessages = conversation.messages.toMutableList()
                newMessages[messageIndex] = newMessages[messageIndex].copy(state = newState)
                currentList[index] = conversation.copy(messages = newMessages)
                _conversations.value = currentList
            }
        }
    }
}
