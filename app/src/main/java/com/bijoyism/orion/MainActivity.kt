package com.bijoyism.orion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrionTheme {
                OrionApp()
            }
        }
    }
}

@Composable
fun OrionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color(0xFFB8C9FF),
            onPrimary = Color(0xFF102E5C),
            secondary = Color(0xFFB8DDF5),
            background = Color(0xFF0D0F14),
            surface = Color(0xFF151820),
            surfaceVariant = Color(0xFF20232C)
        ),
        content = content
    )
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun OrionApp() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "Hello. I'm ORION. How can I help you?",
                    false
                )
            )
        )
    }

    Scaffold(
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Chat"
                        )
                    },
                    label = {
                        Text("Chat")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            Icons.Default.Memory,
                            contentDescription = "Memory"
                        )
                    },
                    label = {
                        Text("Memory")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }
    ) { padding ->

        if (selectedTab == 0) {

            HomeScreen(
                modifier = Modifier.padding(padding),
                onSend = { text ->

                    if (text.isNotBlank()) {

                        messages = messages +
                                ChatMessage(text.trim(), true)

                        messages = messages +
                                ChatMessage(
                                    orionReply(text),
                                    false
                                )

                        selectedTab = 1
                    }
                }
            )

        } else if (selectedTab == 1) {

            ChatScreen(
                modifier = Modifier.padding(padding),
                messages = messages,
                onSend = { text ->

                    if (text.isNotBlank()) {

                        messages = messages +
                                ChatMessage(text.trim(), true)

                        messages = messages +
                                ChatMessage(
                                    orionReply(text),
                                    false
                                )
                    }
                }
            )

        } else if (selectedTab == 2) {

            SimpleScreen(
                "Memory",
                Modifier.padding(padding)
            )

        } else {

            SimpleScreen(
                "Settings",
                Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF101521),
                        Color(0xFF0D0F14)
                    )
                )
            )
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "ORION",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your personal AI assistant",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = {}
            ) {

                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = "Notifications"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(38.dp)
        )

        Text(
            text = "Good morning",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "How can I help?",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(35.dp)
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            Color(0xFF6D8DFF),
                            Color(0xFF344C9A),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✦",
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Ask ORION anything...")
            },
            trailingIcon = {

                IconButton(
                    onClick = {

                        if (input.isNotBlank()) {
                            onSend(input)
                            input = ""
                        }
                    }
                ) {

                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "Quick actions",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            QuickAction(
                "Study",
                Icons.Default.School,
                Modifier.weight(1f)
            )

            QuickAction(
                "Notes",
                Icons.Default.EditNote,
                Modifier.weight(1f)
            )

            QuickAction(
                "Tasks",
                Icons.Default.TaskAlt,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickAction(
    title: String,
    icon: ImageVector,
    modifier: Modifier
) {

    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                icon,
                contentDescription = title
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = title,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    onSend: (String) -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✦",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = "ORION",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Online",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            for (message in messages) {

                item {

                    MessageBubble(message)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Message ORION...")
                },
                shape = RoundedCornerShape(26.dp),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            FilledIconButton(
                onClick = {

                    if (input.isNotBlank()) {

                        onSend(input)
                        input = ""
                    }
                }
            ) {

                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (message.isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {

            Text(
                text = message.text,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 11.dp
                ),
                color = if (message.isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontSize = 15.sp
            )
        }
    }
}

fun orionReply(
    message: String
): String {

    val text = message
        .lowercase(Locale.getDefault())
        .trim()

    return when {

        text.contains("hello") ||
        text.contains("hi") ||
        text.contains("hey") -> {
            "Hello. I'm ORION. Nice to hear from you."
        }

        text.contains("who are you") ||
        text.contains("what are you") -> {
            "I'm ORION, your personal AI assistant."
        }

        text.contains("how are you") -> {
            "I'm operating normally and ready to help."
        }

        text.contains("time") -> {

            val formatter = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            )

            "The current time is ${formatter.format(Date())}."
        }

        text.contains("date") ||
        text.contains("today") -> {

            val formatter = SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
            )

            "Today is ${formatter.format(Date())}."
        }

        else -> {
            "I understand: \"$message\". Full AI intelligence will be connected in a later update."
        }
    }
}

@Composable
fun SimpleScreen(
    title: String,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}