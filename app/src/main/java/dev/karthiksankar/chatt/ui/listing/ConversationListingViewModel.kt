package dev.karthiksankar.chatt.ui.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ConversationListingViewModel : ViewModel() {
    val conversations get() = ChatStorageInMemory.conversations

    private val _sideEffect = MutableSharedFlow<ConversationListingSideEffect>()
    val sideEffect: SharedFlow<ConversationListingSideEffect> = _sideEffect

    fun onClickCompose() {
        viewModelScope.launch(Dispatchers.IO) {
            val conversation = ChatStorageInMemory.createConversation()
            WebSocketManager.connect(conversation.id)
            _sideEffect.emit(ConversationListingSideEffect.OpenConversation(conversation))
        }
    }
}