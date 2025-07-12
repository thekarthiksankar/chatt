package dev.karthiksankar.chatt.ui.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.karthiksankar.chatt.data.ChatStorageInMemory
import dev.karthiksankar.chatt.data.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationListingViewModel : ViewModel() {

    val conversations get() = ChatStorageInMemory.conversations

    fun onClickCompose() {
        viewModelScope.launch(Dispatchers.IO) {
            val conversation = ChatStorageInMemory.createConversation()
            WebSocketManager.connect(conversation.channelId)
            // TODO Navigate to conversation detail screen
        }
    }
}