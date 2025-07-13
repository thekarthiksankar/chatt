package dev.karthiksankar.chatt.ui.conversation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import dev.karthiksankar.chatt.data.MessageEntity
import dev.karthiksankar.chatt.data.WebSocketManager
import dev.karthiksankar.chatt.ui.components.ChattAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    uiState: ConversationUiState,
    onClickSend: (String) -> Unit,
    onConversationOpened: () -> Unit,
) {
    LaunchedEffect(uiState.conversationId) {
        onConversationOpened()
    }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        topBar = {
            Toolbar(
                name = uiState.name,
                conversationEndPoint = WebSocketManager.getConversationEndPoint(uiState.conversationId),
                isConnected = uiState.isConnected
            )
        },
        bottomBar = { InputText(onClickSend = onClickSend) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!uiState.isConnected) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            MessageList(messages = uiState.messages)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    name: String,
    conversationEndPoint: String,
    isConnected: Boolean
) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        modifier = Modifier.clickable {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, conversationEndPoint)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },
        title = { Text(name) },
        colors = ChattAppBarDefaults.topAppBarColors(),
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(12.dp)
                    .background(
                        color = if (isConnected) Color.Green else Color.Red,
                        shape = CircleShape
                    )
            )
        }
    )
}

@Composable
private fun MessageList(messages: List<MessageEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        reverseLayout = true
    ) {
        items(messages.reversed()) { message ->
            MessageBubble(message)
        }
    }
}

@Composable
private fun MessageBubble(message: MessageEntity) {
    val (backgroundColor, textColor) = when {
        message.state == MessageEntity.State.FAILED -> Pair(Color.Red, Color.White)
        message.isOutgoing -> Pair(Color(0xFF1976D2), Color.Black)
        else -> Pair(Color(0xFFE0E0E0), Color.DarkGray)
    }
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(if (message.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = message.text, color = textColor)
        }
    }
}

@Composable
fun InputText(
    onClickSend: (String) -> Unit,
) {
    var messageText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Type a message") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onClickSend(messageText.trim())
                            messageText = ""
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        )
    }
}
