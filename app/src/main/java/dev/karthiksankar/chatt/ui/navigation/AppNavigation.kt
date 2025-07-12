package dev.karthiksankar.chatt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.karthiksankar.chatt.data.ConversationEntity
import dev.karthiksankar.chatt.ui.conversation.ConversationScreen
import dev.karthiksankar.chatt.ui.conversation.ConversationViewModel
import dev.karthiksankar.chatt.ui.listing.ConversationListingScreen
import dev.karthiksankar.chatt.ui.listing.ConversationListingSideEffect
import dev.karthiksankar.chatt.ui.listing.ConversationListingViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Destination>(Destination.ConversationList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            conversationListEntry(
                onClickConversation = { conversation ->
                    backStack.add(Destination.Conversation(conversation.id))
                }
            )
            conversationDetailEntry()
        }
    )
}

fun EntryProviderBuilder<*>.conversationListEntry(
    onClickConversation: (ConversationEntity) -> Unit
) = entry<Destination.ConversationList> {
    val viewModel = viewModel<ConversationListingViewModel>()
    val conversations by viewModel.conversations.collectAsState()

    val scope = rememberCoroutineScope()
    scope.launch {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is ConversationListingSideEffect.OpenConversation -> {
                    onClickConversation(sideEffect.conversation)
                }
            }
        }
    }

    ConversationListingScreen(
        conversations = conversations,
        onClickConversation = onClickConversation,
        onClickCompose = viewModel::onClickCompose,
    )
}

fun EntryProviderBuilder<*>.conversationDetailEntry() =
    entry<Destination.Conversation> { destination ->
        val viewModel = viewModel<ConversationViewModel>(
            factory = ConversationViewModel.Factory(destination.id)
        )
        val uiState by viewModel.uiState.collectAsState()
        ConversationScreen(
            uiState = uiState,
            onClickSend = viewModel::sendMessage,
            onConversationOpened = viewModel::onConversationOpened,
        )
    }