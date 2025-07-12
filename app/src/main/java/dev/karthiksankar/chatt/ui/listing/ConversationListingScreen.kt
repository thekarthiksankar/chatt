package dev.karthiksankar.chatt.ui.listing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.karthiksankar.chatt.R
import dev.karthiksankar.chatt.data.ConversationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListingScreen(
    modifier: Modifier = Modifier,
    conversations: List<ConversationEntity>,
    onClickConversation: (ConversationEntity) -> Unit = {},
    onClickCompose: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar() },
        floatingActionButton = { ComposeFab(onClick = onClickCompose) }
    ) { innerPadding ->
        Conversations(
            modifier = modifier.padding(innerPadding),
            conversations = conversations,
            onClickConversation = onClickConversation
        )
    }
}

@Composable
private fun Conversations(
    modifier: Modifier = Modifier,
    conversations: List<ConversationEntity>,
    onClickConversation: (ConversationEntity) -> Unit
) {
    // TODO Add fallback component for empty conversations
    // FIXME Too much padding in between items
    LazyColumn {
        items(conversations) { conversation ->
            Column {
                ConversationItem(
                    modifier = modifier,
                    conversation = conversation,
                    onClickConversation = onClickConversation
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ConversationItem(
    modifier: Modifier,
    conversation: ConversationEntity,
    onClickConversation: (ConversationEntity) -> Unit
) {
    // TODO Add message preview
    // TODO Highlight unread messages
    // TODO Highlight failed messages
    Box(modifier.clickable { onClickConversation(conversation) }) {
        Text(
            text = conversation.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ComposeFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(Icons.Default.Add, contentDescription = "Compose")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(stringResource(R.string.app_name)) }
    )
}

@Preview(showBackground = true)
@Composable
fun ConversationListingScreenPreview() {
    val sampleConversations = listOf(
        ConversationEntity(
            title = "Alice",
            channelId = "channel-1",
            messages = emptyList(),

            ),
        ConversationEntity(
            title = "Alice",
            channelId = "channel-2",
            messages = emptyList(),
        ),
    )
    ConversationListingScreen(
        conversations = sampleConversations,
        onClickConversation = {},
        onClickCompose = {}
    )
}
