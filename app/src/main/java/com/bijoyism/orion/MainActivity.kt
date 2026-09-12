package com.bijoyism.orion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale

private const val ORION_API_URL =
    "https://orion-api.alsoknownasbijoy.workers.dev/"

private val OrionBackground = Color(0xFF0D0E13)
private val OrionSurface = Color(0xFF202128)
private val OrionPrimary = Color(0xFFB7C4FF)
private val OrionText = Color(0xFFF2F1F7)

data class ChatMessage(val text: String, val fromUser: Boolean)

class MainActivity : ComponentActivity() {
    private var voiceCallback: ((String) -> Unit)? = null

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val text = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                if (!text.isNullOrBlank()) voiceCallback?.invoke(text)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrionTheme {
                OrionApp { callback -> startVoiceInput(callback) }
            }
        }
    }

    private fun startVoiceInput(callback: (String) -> Unit) {
        voiceCallback = callback
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to ORION")
        }
        speechLauncher.launch(intent)
    }
}

@Composable
fun OrionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = OrionPrimary,
            background = OrionBackground,
            surface = OrionSurface,
            onBackground = OrionText,
            onSurface = OrionText
        ),
        content = content
    )
}

@Composable
fun OrionApp(onVoiceInput: ((String) -> Unit) -> Unit) {
    var screen by rememberSaveable { mutableIntStateOf(0) }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Hello. I'm ORION. How can I help you?", false))
        }
    }

    Surface(Modifier.fillMaxSize(), color = OrionBackground) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (screen) {
                    0 -> HomeScreen { screen = 1 }
                    1 -> ChatScreen(messages, onVoiceInput)
                    2 -> MemoryScreen()
                    else -> SettingsScreen()
                }
            }

            NavigationBar(containerColor = OrionSurface) {
                NavigationBarItem(
                    selected = screen == 0, onClick = { screen = 0 },
                    icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = screen == 1, onClick = { screen = 1 },
                    icon = { Icon(Icons.Default.Chat, "Chat") }, label = { Text("Chat") }
                )
                NavigationBarItem(
                    selected = screen == 2, onClick = { screen = 2 },
                    icon = { Icon(Icons.Default.Memory, "Memory") }, label = { Text("Memory") }
                )
                NavigationBarItem(
                    selected = screen == 3, onClick = { screen = 3 },
                    icon = { Icon(Icons.Default.Settings, "Settings") }, label = { Text("Settings") }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onOpenChat: () -> Unit) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("ORION", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(greeting, fontSize = 18.sp, color = OrionPrimary)
        Spacer(Modifier.height(45.dp))

        Box(
            Modifier.size(170.dp).clip(CircleShape).background(OrionPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text("✦", fontSize = 70.sp, color = Color(0xFF101117))
        }

        Spacer(Modifier.height(35.dp))
        Text("How can I help you?", fontSize = 22.sp)
        Spacer(Modifier.height(25.dp))
        Button(onClick = onOpenChat) { Text("Chat with ORION") }
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Study")
            QuickAction("Notes")
            QuickAction("Tasks")
        }
    }
}

@Composable
fun QuickAction(text: String) {
    Surface(
        Modifier.height(46.dp),
        shape = RoundedCornerShape(23.dp),
        color = OrionSurface
    ) {
        Box(
            Modifier.padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) { Text(text) }
    }
}

@Composable
fun ChatScreen(
    messages: SnapshotStateList<ChatMessage>,
    onVoiceInput: ((String) -> Unit) -> Unit
) {
    var input by rememberSaveable { mutableStateOf("") }
    var thinking by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(OrionPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", fontSize = 30.sp, color = Color(0xFF101117))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text("ORION", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (thinking) "Thinking..." else "Online",
                    color = OrionPrimary
                )
            }

            IconButton({
                messages.clear()
                messages.add(ChatMessage("Chat cleared. How can I help?", false))
            }) {
                Icon(Icons.Default.Delete, "Clear chat")
            }
        }

        LazyColumn(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { ChatBubble(it) }
            if (thinking) item { ChatBubble(ChatMessage("ORION is thinking...", false)) }
        }

        Row(
            Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                Modifier.weight(1f),
                placeholder = { Text("Message ORION...") },
                leadingIcon = {
                    IconButton({ onVoiceInput { input = it } }) {
                        Icon(Icons.Default.Mic, "Voice input")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp)
            )

            Spacer(Modifier.width(8.dp))

            IconButton({
                val text = input.trim()
                if (text.isNotEmpty() && !thinking) {
                    messages.add(ChatMessage(text, true))
                    input = ""
                    thinking = true
                    askOrion(text) {
                        messages.add(ChatMessage(it, false))
                        thinking = false
                    }
                }
            }) {
                Icon(Icons.Default.Send, "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            Modifier.widthIn(max = 330.dp),
            shape = RoundedCornerShape(22.dp),
            color = if (message.fromUser) OrionPrimary else OrionSurface
        ) {
            Text(
                message.text,
                Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                color = if (message.fromUser) Color(0xFF101117) else OrionText,
                fontSize = 16.sp
            )
        }
    }
}

private fun askOrion(text: String, callback: (String) -> Unit) {
    Thread {
        try {
            val connection = URL(ORION_API_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")

            val body = JSONObject().put("message", text).toString()
            connection.outputStream.use {
                it.write(body.toByteArray(Charsets.UTF_8))
            }

            val code = connection.responseCode
            val stream =
                if (code in 200..299) connection.inputStream
                else connection.errorStream

            val response =
                stream?.bufferedReader()?.use { it.readText() } ?: ""
            connection.disconnect()

            val reply = try {
                val json = JSONObject(response)
                when {
                    json.has("reply") -> json.optString("reply")
                    json.has("response") -> json.optString("response")
                    json.has("message") -> json.optString("message")
                    json.has("error") -> "ORION error: ${json.optString("error")}"
                    else -> response
                }
            } catch (_: Exception) {
                response
            }

            runOnUiThread {
                callback(
                    if (reply.isBlank()) "I received an empty response."
                    else reply
                )
            }
        } catch (_: Exception) {
            runOnUiThread {
                callback(
                    "I couldn't connect to ORION.\n\n" +
                        "Please check your internet connection."
                )
            }
        }
    }.start()
}

@Composable
fun MemoryScreen() {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("Memory", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text(
            "ORION's memory system will appear here.",
            color = OrionPrimary,
            fontSize = 17.sp
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(40.dp))
        Text("Settings", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text("ORION", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Personal AI Assistant", color = OrionPrimary)
    }
}
