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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.util.concurrent.Executors


private const val ORION_API_URL =
    "https://orion-api.alsoknownasbijoy.workers.dev/"


private val OrionBackground = Color(0xFF0D0E13)
private val OrionSurface = Color(0xFF202128)
private val OrionPrimary = Color(0xFFB7C4FF)
private val OrionText = Color(0xFFF2F1F7)


data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)


class MainActivity : ComponentActivity() {

    private val executor = Executors.newSingleThreadExecutor()

    private var voiceCallback: ((String) -> Unit)? = null

    private val speechLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val results =
                    result.data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                val text = results?.firstOrNull()

                if (!text.isNullOrBlank()) {
                    voiceCallback?.invoke(text)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrionTheme {
                OrionApp(
                    onVoiceInput = { callback ->
                        startVoiceInput(callback)
                    }
                )
            }
        }
    }


    private fun startVoiceInput(
        callback: (String) -> Unit
    ) {
        voiceCallback = callback

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )

                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Speak to ORION"
                )
            }

        speechLauncher.launch(intent)
    }


    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}


@Composable
fun OrionTheme(
    content: @Composable () -> Unit
) {
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
fun OrionApp(
    onVoiceInput: ((String) -> Unit) -> Unit
) {

    var selectedScreen by rememberSaveable {
        mutableIntStateOf(0)
    }

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    if (messages.isEmpty()) {
        messages.add(
            ChatMessage(
                "Hello. I'm ORION. How can I help you?",
                false
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OrionBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                when (selectedScreen) {

                    0 -> HomeScreen(
                        onOpenChat = {
                            selectedScreen = 1
                        }
                    )

                    1 -> ChatScreen(
                        messages = messages,
                        onVoiceInput = onVoiceInput
                    )

                    2 -> MemoryScreen()

                    3 -> SettingsScreen()
                }
            }


            NavigationBar(
                containerColor = OrionSurface
            ) {

                NavigationBarItem(
                    selected = selectedScreen == 0,
                    onClick = {
                        selectedScreen = 0
                    },
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
                    selected = selectedScreen == 1,
                    onClick = {
                        selectedScreen = 1
                    },
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
                    selected = selectedScreen == 2,
                    onClick = {
                        selectedScreen = 2
                    },
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
                    selected = selectedScreen == 3,
                    onClick = {
                        selectedScreen = 3
                    },
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
    }
}


@Composable
fun HomeScreen(
    onOpenChat: () -> Unit
) {

    val hour = Calendar
        .getInstance()
        .get(Calendar.HOUR_OF_DAY)

    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(40.dp)
        )


        Text(
            text = "ORION",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )


        Text(
            text = greeting,
            fontSize = 18.sp,
            color = OrionPrimary
        )


        Spacer(
            modifier = Modifier.height(45.dp)
        )


        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(OrionPrimary),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "✦",
                fontSize = 70.sp,
                color = Color(0xFF101117)
            )
        }


        Spacer(
            modifier = Modifier.height(35.dp)
        )


        Text(
            text = "How can I help you?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        Button(
            onClick = onOpenChat
        ) {
            Text("Chat with ORION")
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Row(
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            QuickAction("Study")
            QuickAction("Notes")
            QuickAction("Tasks")
        }
    }
}


@Composable
fun QuickAction(
    text: String
) {

    Surface(
        modifier = Modifier.height(46.dp),
        shape = RoundedCornerShape(23.dp),
        color = OrionSurface
    ) {

        Box(
            modifier = Modifier.padding(
                horizontal = 18.dp
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}


@Composable
fun ChatScreen(
    messages: SnapshotStateList<ChatMessage>,
    onVoiceInput: ((String) -> Unit) -> Unit
) {

    var input by rememberSaveable {
        mutableStateOf("")
    }

    var thinking by rememberSaveable {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier.fillMaxSize()
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
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(OrionPrimary),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✦",
                    fontSize = 30.sp,
                    color = Color(0xFF101117)
                )
            }


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "ORION",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        if (thinking)
                            "Thinking..."
                        else
                            "Online",
                    color = OrionPrimary
                )
            }


            IconButton(
                onClick = {

                    messages.clear()

                    messages.add(
                        ChatMessage(
                            "Chat cleared. How can I help?",
                            false
                        )
                    )
                }
            ) {

                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear chat"
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            contentPadding =
                PaddingValues(vertical = 16.dp)
        ) {

            items(messages) { message ->
                ChatBubble(message)
            }


            if (thinking) {

                item {

                    ChatBubble(
                        ChatMessage(
                            "ORION is thinking...",
                            false
                        )
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
                .navigationBarsPadding(),
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

                            onVoiceInput { text ->
                                input = text
                            }
                        }
                    ) {

                        Icon(
                            Icons.Default.Mic,
                            contentDescription =
                                "Voice input"
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp)
            )


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            IconButton(
                onClick = {

                    val text = input.trim()

                    if (
                        text.isNotEmpty() &&
                        !thinking
                    ) {

                        messages.add(
                            ChatMessage(
                                text,
                                true
                            )
                        )

                        input = ""
                        thinking = true

                        askOrion(text) { reply ->

                            messages.add(
                                ChatMessage(
                                    reply,
                                    false
                                )
                            )

                            thinking = false
                        }
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
fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.fromUser)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Surface(
            modifier = Modifier.widthIn(
                max = 330.dp
            ),
            shape = RoundedCornerShape(22.dp),
            color =
                if (message.fromUser)
                    OrionPrimary
                else
                    OrionSurface
        ) {

            Text(
                text = message.text,
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                ),
                color =
                    if (message.fromUser)
                        Color(0xFF101117)
                    else
                        OrionText,
                fontSize = 16.sp
            )
        }
    }
}


private fun askOrion(
    text: String,
    callback: (String) -> Unit
) {

    Thread {

        try {

            val url =
                URL(ORION_API_URL)

            val connection =
                url.openConnection()
                    as HttpURLConnection

            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )


            val body =
                JSONObject()
                    .put("message", text)
                    .toString()


            connection.outputStream.use {
                it.write(
                    body.toByteArray(
                        Charsets.UTF_8
                    )
                )
            }


            val responseCode =
                connection.responseCode

            val stream =
                if (responseCode in 200..299)
                    connection.inputStream
                else
                    connection.errorStream


            val response =
                stream
                    ?.bufferedReader()
                    ?.use {
                        it.readText()
                    }
                    ?: ""


            connection.disconnect()


            val reply = try {

                val json =
                    JSONObject(response)

                when {

                    json.has("reply") ->
                        json.optString("reply")

                    json.has("response") ->
                        json.optString("response")

                    json.has("message") ->
                        json.optString("message")

                    json.has("error") ->
                        "ORION error: ${
                            json.optString("error")
                        }"

                    else ->
                        response
                }

            } catch (e: Exception) {

                response
            }


            runOnMain {
                callback(
                    if (reply.isBlank())
                        "I received an empty response."
                    else
                        reply
                )
            }

        } catch (e: Exception) {

            runOnMain {

                callback(
                    "I couldn't connect to ORION.\n\n" +
                    "Please check your internet connection."
                )
            }
        }

    }.start()
}


private fun MainActivity.runOnMain(
    action: () -> Unit
) {
    runOnUiThread {
        action()
    }
}


@Composable
fun MemoryScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        Text(
            text = "Memory",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text =
                "ORION's memory system will appear here.",
            color = OrionPrimary,
            fontSize = 17.sp
        )
    }
}


@Composable
fun SettingsScreen() {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp)
    ) {

        Text(
            text = "Settings",
            fontSize = 30.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "ORION",
            fontSize = 20.sp,
            fontWeight =
                FontWeight.Medium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text = "Version 0.5",
            color =
                Color.LightGray
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        TextButton(
            onClick = {}
        ) {
            Text(
                "AI settings"
            )
        }

        TextButton(
            onClick = {}
        ) {
            Text(
                "Voice settings"
            )
        }

        TextButton(
            onClick = {}
        ) {
            Text(
                "About ORION"
            )
        }
    }
}