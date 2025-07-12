package dev.karthiksankar.chatt.ui.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.ConversationEntity
import dev.karthiksankar.chatt.data.MessageEntity
import dev.karthiksankar.chatt.data.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ConversationListingViewModel : ViewModel() {
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    private val conversations = _conversations

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    private val messages = _messages

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ChatStorageInMemory.db.conversationDao.getConversations().collect {
                _conversations.value = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            ChatStorageInMemory.db.messageDao.getMessages().collect {
                _messages.value = it
            }
        }
    }

    val uiState = conversations.combine(messages) { conversations, messages ->
        ConversationListingUiState(
            conversations = conversations.map { conversation ->
                val messages = messages.filter { it.conversationId == conversation.id }
                val lastMessage = messages.lastOrNull()
                ConversationListingUiState.ConversationItemUiState(
                    id = conversation.id,
                    title = conversation.title,
                    lastMessage = lastMessage?.text,
                    lastMessageTime = lastMessage?.timestamp?.getFormattedTime(),
                    lastMessageState = lastMessage?.state,
                    unreadCount = messages.count { it.state == MessageEntity.State.UNREAD },
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
        initialValue = ConversationListingUiState()
    )

    private val _sideEffect = MutableSharedFlow<ConversationListingSideEffect>()
    val sideEffect: SharedFlow<ConversationListingSideEffect> = _sideEffect

    fun onClickCompose() {
        viewModelScope.launch(Dispatchers.IO) {
            val conversation = ChatStorageInMemory.createConversation()
            WebSocketManager.connect(conversation.id)
            _sideEffect.emit(ConversationListingSideEffect.OpenConversation(conversation))
        }
    }

    private fun Long.getFormattedTime(): String {
        val now = Calendar.getInstance()
        val msgTime = Calendar.getInstance().apply { timeInMillis = this@getFormattedTime }
        return if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR)
        ) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this@getFormattedTime))
        } else {
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(this@getFormattedTime))
        }
    }
}