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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ConversationViewModel(val id: String) : ViewModel() {

    private val _conversation = kotlinx.coroutines.flow.MutableStateFlow<ConversationEntity?>(null)
    private val conversation: StateFlow<ConversationEntity?> = _conversation

    /**
     * The [StateFlow] that emits the connection state of the WebSocket for the conversation with the given [id].
     */
    private val isConnectionActive = WebSocketManager.getConnectionState(id)

    private val _messages =
        kotlinx.coroutines.flow.MutableStateFlow<List<MessageEntity>>(emptyList())
    private val messages: StateFlow<List<MessageEntity>> = _messages

    /**
     * The [StateFlow] that emits the UI state for the conversation screen.
     */
    val uiState: StateFlow<ConversationUiState> =
        combine(conversation, isConnectionActive, messages) { conversation, isConnected, messages ->
            ConversationUiState(
                messages = messages,
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
        viewModelScope.launch(Dispatchers.IO) {
        }
        viewModelScope.launch(Dispatchers.IO) {
        }
        viewModelScope.launch(Dispatchers.Default) {
            ChatStorageInMemory.db.conversationDao.getConversationById(id).collect {
                _conversation.value = it
            }

            ChatStorageInMemory.db.messageDao.getMessages(id).collect {
                _messages.value = it
            }

            conversation.collect { conv ->
                conv?.messages?.filter { it.state == MessageEntity.State.UNREAD }?.forEach { msg ->
                    ChatStorageInMemory.updateMessageState(
                        conv.id,
                        msg.id,
                        MessageEntity.State.SENT
                    )
                }
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val conversation = conversation.value ?: return@launch
            val message = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversation.id,
                text = message.trim(),
                timestamp = System.currentTimeMillis(),
                state = MessageEntity.State.SENDING,
                isOutgoing = true
            )
            ChatStorageInMemory.addMessage(message)
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