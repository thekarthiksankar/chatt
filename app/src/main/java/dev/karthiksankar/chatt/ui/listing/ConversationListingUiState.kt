package dev.karthiksankar.chatt.ui.listing

import dev.karthiksankar.chatt.data.MessageEntity

data class ConversationListingUiState(
    val conversations: List<ConversationItemUiState> = emptyList(),
) {
    data class ConversationItemUiState(
        val id: String = "",
        val title: String = "",
        val lastMessage: String? = null,
        val lastMessageTime: String? = null,
        val lastMessageState: MessageEntity.State? = null,
        val unreadCount: Int = 0,
    )
}