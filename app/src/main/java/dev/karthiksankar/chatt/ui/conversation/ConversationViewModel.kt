package dev.karthiksankar.chatt.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.ConversationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ConversationViewModel(val id: String) : ViewModel() {

    /**
     * The [StateFlow] that emits the conversation with the given [id].
     * It will emit `null` if no conversation is found with the given [id].
     */
    val conversation: StateFlow<ConversationEntity?> =
        ChatStorageInMemory.conversations.map { conversations ->
            conversations.find { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val uiState: StateFlow<ConversationUiState> = conversation.map { conversation ->
        ConversationUiState(
            messages = conversation?.messages ?: emptyList(),
            isConnected = true, // TODO: Replace with actual connection state
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
        // TODO implement sending a message
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