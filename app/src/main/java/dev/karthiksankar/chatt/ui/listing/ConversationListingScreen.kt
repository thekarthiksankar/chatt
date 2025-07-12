package dev.karthiksankar.chatt.ui.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.karthiksankar.chatt.R
import dev.karthiksankar.chatt.data.ConversationEntity
import dev.karthiksankar.chatt.data.MessageEntity
import dev.karthiksankar.chatt.ui.components.ChattAppBarDefaults
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        if (conversations.isEmpty()) {
            WelcomePlaceholder(innerPadding)
        } else {
            Conversations(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                conversations = conversations,
                onClickConversation = onClickConversation
            )
        }
    }
}

@Composable
fun WelcomePlaceholder(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome. Click the + button to start a new chat.",
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Conversations(
    modifier: Modifier = Modifier,
    conversations: List<ConversationEntity>,
    onClickConversation: (ConversationEntity) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(conversations) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClickConversation = onClickConversation
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ConversationItem(
    conversation: ConversationEntity,
    onClickConversation: (ConversationEntity) -> Unit,
) {
    val lastMessage = conversation.messages.lastOrNull()
    val unreadCount =
        conversation.messages.count { it.state == MessageEntity.State.UNREAD }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickConversation(conversation) }
            .padding(16.dp)
            .padding(end = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            MessageTime(
                lastMessage = lastMessage,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MessagePreview(
                message = lastMessage,
                modifier = Modifier.weight(1f)
            )

            UnreadCount(unreadCount)
        }
    }
}

private fun getFormattedTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val msgTime = Calendar.getInstance().apply { timeInMillis = timestamp }
    return if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR)
    ) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    } else {
        SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun MessagePreview(
    message: MessageEntity?,
    modifier: Modifier = Modifier
) {
    Text(
        text = message?.text.orEmpty(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyMedium,
        color = when (message?.state) {
            MessageEntity.State.FAILED -> Color.Red
            else -> Color.Gray
        },
        modifier = modifier,
    )
}

@Composable
private fun UnreadCount(count: Int) {
    if (count > 0) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(Color(0xFF1976D2), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun MessageTime(
    lastMessage: MessageEntity?,
    modifier: Modifier = Modifier,
) {
    Text(
        text = lastMessage?.let { getFormattedTime(it.timestamp) }.orEmpty(),
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
        modifier = modifier
    )
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
        colors = ChattAppBarDefaults.topAppBarColors(),
        title = { Text(stringResource(R.string.app_name)) },
    )
}

@Preview(showBackground = true)
@Composable
fun ConversationListingScreenPreview() {
    val sampleConversations = listOf(
        ConversationEntity(
            id = "channel-1",
            title = "Alice",
            messages = emptyList(),

            ),
        ConversationEntity(
            id = "channel-2",
            title = "Alice",
            messages = emptyList(),
        ),
    )
    ConversationListingScreen(
        conversations = sampleConversations,
        onClickConversation = {},
        onClickCompose = {}
    )
}
