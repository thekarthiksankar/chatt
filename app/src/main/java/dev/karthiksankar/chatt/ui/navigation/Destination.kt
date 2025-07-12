package dev.karthiksankar.chatt.ui.navigation

sealed class Destination {
    object ConversationList : Destination()
    data class Conversation(val id: String) : Destination()
}