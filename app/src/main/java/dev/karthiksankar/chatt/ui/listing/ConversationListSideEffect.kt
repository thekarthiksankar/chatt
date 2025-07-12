package dev.karthiksankar.chatt.ui.listing

import dev.karthiksankar.chatt.data.ConversationEntity

sealed class ConversationListingSideEffect {
    data class OpenConversation(val conversation: ConversationEntity) : ConversationListingSideEffect()
}