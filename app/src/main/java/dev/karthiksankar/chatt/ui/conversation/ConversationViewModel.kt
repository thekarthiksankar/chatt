package dev.karthiksankar.chatt.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.ConversationEntity
import dev.karthiksankar.chatt.data.MessageEntity
import dev.karthiksankar.chatt.data.WebSocketManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class ConversationViewModel(val id: String) : ViewModel() {

    /**
     * The [StateFlow] that emits the conversation with the given [id].
     * It will emit `null` if no conversation is found with the given [id].
     */
    private val conversation: StateFlow<ConversationEntity?> =
        ChatStorageInMemory.conversations.map { conversations ->
            conversations.find { it.id == this.id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val isConnectionActive = WebSocketManager.getConnectionState(id)

    val uiState: StateFlow<ConversationUiState> =
        combine(conversation, isConnectionActive) { conversation, isConnected ->
            ConversationUiState(
                messages = conversation?.messages ?: emptyList(),
                isConnected = isConnected,
                name = conversation?.title ?: "",
                conversationId = conversation?.id ?: ""
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConversationUiState()
        )

    /**
     * Sends a message to the conversation.
     * This is a placeholder function and should be implemented to actually send a message.
     */
    fun sendMessage(message: String) {
        val conversation = conversation.value ?: return
        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            text = message.trim(),
            timestamp = System.currentTimeMillis(),
            state = MessageEntity.State.SENDING,
            isOutgoing = true
        )
        ChatStorageInMemory.addMessage(conversation.id, message)
        WebSocketManager.sendMessage(conversation.id, message)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val id: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ConversationViewModel(id) as T
        }
    }
}