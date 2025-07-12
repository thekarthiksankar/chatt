package dev.karthiksankar.chatt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.karthiksankar.chatt.ui.listing.ConversationListingScreen

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Destination>(Destination.ConversationList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            conversationListEntry()
        }
    )
}

fun EntryProviderBuilder<*>.conversationListEntry() = entry<Destination.ConversationList> {
    ConversationListingScreen(
        conversations = emptyList(),
        onClickConversation = {
            // TODO Launch conversation detail screen
        },
        onClickCompose = {
            // TODO Launch compose screen
        },
    )
}