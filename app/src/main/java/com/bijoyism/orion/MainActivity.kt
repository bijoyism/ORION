package com.bijoyism.orion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
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
        colorScheme = darkColorScheme(
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

    var thinking by remember {
        mutableStateOf(false)
    }

    Scaffold(
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(Icons.Default.Home, "Home")
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(Icons.Default.Chat, "Chat")
                    },
                    label = {
                        Text("Chat")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(Icons.Default.Memory, "Memory")
                    },
                    label = {
                        Text("Memory")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(Icons.Default.Settings, "Settings")
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }
    ) { padding ->

        when (selectedTab) {

            0 -> HomeScreen(
                modifier = Modifier.padding(padding),
                onSend = { text ->

                    if (text.isNotBlank()) {

                        messages = messages +
                                ChatMessage(text.trim(), true)

                        thinking = true
                        selectedTab = 1
                    }
                }
            )

            1 -> ChatScreen(
                modifier = Modifier.padding(padding),
                messages = messages,
                thinking = thinking,
                onClear = {

                    messages = listOf(
                        ChatMessage(
                            "Chat cleared. How can I help?",
                            false
                        )
                    )
                },
                onSend = { text ->

                    if (text.isNotBlank() && !thinking) {

                        messages = messages +
                                ChatMessage(text.trim(), true)

                        thinking = true
                    }
                }
            )

            2 -> SimpleScreen(
                "Memory",
                Modifier.padding(padding)
            )

            3 -> SimpleScreen(
                "Settings",
                Modifier.padding(padding)
            )
        }
    }

    LaunchedEffect(thinking) {

        if (thinking) {

            kotlinx.coroutines.delay(700)

            val lastMessage = messages.lastOrNull()

            if (lastMessage != null && lastMessage.isUser) {

                messages = messages +
                        ChatMessage(
                            orionReply(lastMessage.text),
                            false
                        )
            }

            thinking = false
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
                    "ORION",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Your personal AI assistant",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            IconButton(onClick = {}) {

                Icon(
                    Icons.Default.NotificationsNone,
                    "Notifications"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(38.dp)
        )

        Text(
            greeting(),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            "How can I help?",
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
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF6D8DFF),
                            Color(0xFF344C9A),
                            Color.Transparent
                        )
                    ),
                    CircleShape
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
                    "✦",
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
                        "Send"
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
            "Quick actions",
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
                title
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                title,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    thinking: Boolean,
    onClear: () -> Unit,
    onSend: (String) -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    var listening by remember {
        mutableStateOf(false)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    var speechRecognizer by remember {
        mutableStateOf<SpeechRecognizer?>(null)
    }

    val startListening: () -> Unit = {

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            input = "Speech recognition is not available on this device."
        } else {

            if (speechRecognizer == null) {

                speechRecognizer =
                    SpeechRecognizer.createSpeechRecognizer(context)

                speechRecognizer?.setRecognitionListener(
                    object : RecognitionListener {

                        override fun onReadyForSpeech(
                            params: Bundle?
                        ) {
                            listening = true
                        }

                        override fun onBeginningOfSpeech() {
                            listening = true
                        }

                        override fun onRmsChanged(
                            rmsdB: Float
                        ) {
                        }

                        override fun onBufferReceived(
                            buffer: ByteArray?
                        ) {
                        }

                        override fun onEndOfSpeech() {
                            listening = false
                        }

                        override fun onError(
                            error: Int
                        ) {
                            listening = false
                        }

                        override fun onResults(
                            results: Bundle?
                        ) {

                            val matches =
                                results?.getStringArrayList(
                                    SpeechRecognizer.RESULTS_RECOGNITION
                                )

                            if (!matches.isNullOrEmpty()) {
                                input = matches[0]
                            }

                            listening = false
                        }

                        override fun onPartialResults(
                            partialResults: Bundle?
                        ) {
                        }

                        override fun onEvent(
                            eventType: Int,
                            params: Bundle?
                        ) {
                        }
                    }
                )
            }

            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                false
            )

            speechRecognizer?.startListening(intent)
        }
    }

    val permissionLauncher =
        androidx.activity.compose.rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startListening()
            }
        }

    DisposableEffect(Unit) {

        onDispose {
            speechRecognizer?.destroy()
        }
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
                    "✦",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "ORION",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    if (thinking) {
                        "Thinking..."
                    } else if (listening) {
                        "Listening..."
                    } else {
                        "Online"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onClear
            ) {

                Icon(
                    Icons.Default.DeleteOutline,
                    "Clear chat"
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

            if (thinking) {

                item {

                    ThinkingBubble()
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
                leadingIcon = {

                    IconButton(
                        onClick = {

                            if (
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                startListening()

                            } else {

                                permissionLauncher.launch(
                                    Manifest.permission.RECORD_AUDIO
                                )
                            }
                        }
                    ) {

                        Icon(
                            if (listening) {
                                Icons.Default.MicOff
                            } else {
                                Icons.Default.Mic
                            },
                            contentDescription = "Voice input"
                        )
                    }
                },
                shape = RoundedCornerShape(26.dp),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            FilledIconButton(
                onClick = {

                    if (input.isNotBlank() && !thinking) {
                        onSend(input)
                        input = ""
                    }
                }
            ) {

                Icon(
                    Icons.Default.Send,
                    "Send"
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
                message.text,
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

@Composable
fun ThinkingBubble() {

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Text(
            "ORION is thinking...",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 11.dp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp
        )
    }
}

fun greeting(): String {

    val hour = Calendar
        .getInstance()
        .get(Calendar.HOUR_OF_DAY)

    return when {

        hour < 12 -> "Good morning"

        hour < 18 -> "Good afternoon"

        else -> "Good evening"
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
            "I understand: \"$message\". I'm still learning, but my full AI system is coming."
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
            title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}