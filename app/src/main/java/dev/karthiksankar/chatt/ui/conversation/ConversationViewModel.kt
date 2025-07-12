package dev.karthiksankar.chatt.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.ConversationEntity
import dev.karthiksankar.chatt.data.MessageEntity
import dev.karthiksankar.chatt.data.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    /**
     * The [StateFlow] that emits the connection state of the WebSocket for the conversation with the given [id].
     */
    private val isConnectionActive = WebSocketManager.getConnectionState(id)

    /**
     * The [StateFlow] that emits the UI state for the conversation screen.
     */
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

    init {
        viewModelScope.launch(Dispatchers.Default) {
            conversation.collect { conv ->
                conv?.messages?.filter { it.state == MessageEntity.State.UNREAD }?.forEach { msg ->
                    ChatStorageInMemory.updateMessageState(conv.id, msg.id, MessageEntity.State.SENT)
                }
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val conversation = conversation.value ?: return@launch
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
    }

    fun onConversationOpened() {
        viewModelScope.launch(Dispatchers.Default) {
            conversation.value?.let { conversation ->
                conversation.messages.filter { it.state == MessageEntity.State.UNREAD }
                    .forEach { msg ->
                        ChatStorageInMemory.updateMessageState(
                            conversation.id,
                            msg.id,
                            MessageEntity.State.SENT
                        )
                    }
            }
        }
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