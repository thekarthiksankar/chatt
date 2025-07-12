package dev.karthiksankar.chatt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.karthiksankar.chatt.ui.listing.ConversationListingScreen
import dev.karthiksankar.chatt.ui.listing.ConversationListingViewModel

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
    val viewModel = viewModel<ConversationListingViewModel>()
    val conversations by viewModel.conversations.collectAsState()
    ConversationListingScreen(
        conversations = conversations,
        onClickConversation = {
            // TODO Launch conversation detail screen
        },
        onClickCompose = viewModel::onClickCompose,
    )
}