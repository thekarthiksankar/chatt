package dev.karthiksankar.chatt.ui.navigation

sealed class Destination {
    object ConversationList : Destination()
    object ConversationDetail : Destination()
}