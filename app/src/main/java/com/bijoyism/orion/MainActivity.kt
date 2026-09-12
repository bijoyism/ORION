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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale


private const val ORION_API_URL =
    "https://orion-api.alsoknownasbijoy.workers.dev/"


class MainActivity : ComponentActivity() {

    private var voiceResult: ((String) -> Unit)? = null

    private val speechLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data

                val results =
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                if (!results.isNullOrEmpty()) {

                    voiceResult?.invoke(
                        results[0]
                    )
                }
            }
        }


    fun startVoiceInput(
        onResult: (String) -> Unit
    ) {

        voiceResult = onResult

        val intent =
            Intent(
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
            RecognizerIntent.EXTRA_PROMPT,
            "Speak to ORION"
        )

        speechLauncher.launch(intent)
    }


    fun askOrion(
        message: String,
        onResult: (String) -> Unit
    ) {

        Thread {

            try {

                val url =
                    URL(ORION_API_URL)

                val connection =
                    url.openConnection()
                            as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true

                connection.connectTimeout =
                    15000

                connection.readTimeout =
                    30000

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )


                val requestBody =
                    JSONObject()
                        .put(
                            "message",
                            message
                        )
                        .toString()


                connection.outputStream.use { output ->

                    output.write(
                        requestBody.toByteArray(
                            Charsets.UTF_8
                        )
                    )
                }


                val responseCode =
                    connection.responseCode


                val stream =
                    if (responseCode in 200..299) {

                        connection.inputStream

                    } else {

                        connection.errorStream
                    }


                val responseText =
                    BufferedReader(
                        InputStreamReader(stream)
                    ).use { reader ->

                        reader.readText()
                    }


                connection.disconnect()


                if (responseCode !in 200..299) {

                    val errorMessage =

                        try {

                            val json =
                                JSONObject(
                                    responseText
                                )

                            json.optString(
                                "error",
                                "ORION backend error."
                            )

                        } catch (_: Exception) {

                            "ORION backend error."
                        }


                    runOnUiThread {

                        onResult(

                            "Sorry, I couldn't connect right now.\n\n" +
                                    errorMessage
                        )
                    }

                    return@Thread
                }


                val json =
                    JSONObject(
                        responseText
                    )


                val reply =
                    json.optString(
                        "reply",
                        "I didn't receive a response."
                    )


                runOnUiThread {

                    onResult(reply)
                }


            } catch (_: Exception) {

                runOnUiThread {

                    onResult(

                        "I couldn't connect to ORION.\n\n" +
                                "Please check your internet connection."
                    )
                }
            }

        }.start()
    }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContent {

            OrionTheme {

                OrionApp(

                    onVoiceInput = { callback ->

                        startVoiceInput(
                            callback
                        )
                    },

                    onAskAi = { message, callback ->

                        askOrion(
                            message,
                            callback
                        )
                    }
                )
            }
        }
    }
}



@Composable
fun OrionTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(

        colorScheme =
            darkColorScheme(

                primary =
                    Color(0xFFB8C9FF),

                onPrimary =
                    Color(0xFF102E5C),

                secondary =
                    Color(0xFFB8DDF5),

                background =
                    Color(0xFF0D0F14),

                surface =
                    Color(0xFF151820),

                surfaceVariant =
                    Color(0xFF20232C)
            ),

        content = content
    )
}



data class ChatMessage(

    val text: String,

    val isUser: Boolean
)



@Composable
fun OrionApp(

    onVoiceInput:
        ((String) -> Unit) -> Unit,

    onAskAi:
        (String, (String) -> Unit) -> Unit
) {

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


    fun sendMessage(
        text: String
    ) {

        if (
            text.isBlank() ||
            thinking
        ) {

            return
        }


        val cleanText =
            text.trim()


        messages =

            messages +

                    ChatMessage(

                        cleanText,

                        true
                    )


        thinking = true


        onAskAi(

            cleanText

        ) { reply ->


            messages =

                messages +

                        ChatMessage(

                            reply,

                            false
                        )


            thinking = false
        }
    }



    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(

                    selected =
                        selectedTab == 0,

                    onClick = {

                        selectedTab = 0
                    },

                    icon = {

                        Icon(

                            Icons.Default.Home,

                            "Home"
                        )
                    },

                    label = {

                        Text("Home")
                    }
                )


                NavigationBarItem(

                    selected =
                        selectedTab == 1,

                    onClick = {

                        selectedTab = 1
                    },

                    icon = {

                        Icon(

                            Icons.Default.Chat,

                            "Chat"
                        )
                    },

                    label = {

                        Text("Chat")
                    }
                )


                NavigationBarItem(

                    selected =
                        selectedTab == 2,

                    onClick = {

                        selectedTab = 2
                    },

                    icon = {

                        Icon(

                            Icons.Default.Memory,

                            "Memory"
                        )
                    },

                    label = {

                        Text("Memory")
                    }
                )


                NavigationBarItem(

                    selected =
                        selectedTab == 3,

                    onClick = {

                        selectedTab = 3
                    },

                    icon = {

                        Icon(

                            Icons.Default.Settings,

                            "Settings"
                        )
                    },

                    label = {

                        Text("Settings")
                    }
                )
            }
        }

    ) { padding ->


        when (selectedTab) {


            0 -> {

                HomeScreen(

                    modifier =
                        Modifier.padding(
                            padding
                        ),

                    onSend = { text ->

                        sendMessage(text)

                        selectedTab = 1
                    }
                )
            }


            1 -> {

                ChatScreen(

                    modifier =
                        Modifier.padding(
                            padding
                        ),

                    messages =
                        messages,

                    thinking =
                        thinking,

                    onClear = {

                        messages =

                            listOf(

                                ChatMessage(

                                    "Chat cleared. How can I help?",

                                    false
                                )
                            )
                    },

                    onSend = { text ->

                        sendMessage(text)
                    },

                    onVoiceInput =
                        onVoiceInput
                )
            }


            2 -> {

                SimpleScreen(

                    "Memory",

                    Modifier.padding(
                        padding
                    )
                )
            }


            3 -> {

                SimpleScreen(

                    "Settings",

                    Modifier.padding(
                        padding
                    )
                )
            }
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

            .padding(
                horizontal = 20.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Spacer(

            modifier =
                Modifier.height(28.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    "ORION",

                    fontSize = 25.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    "Your personal AI assistant",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 13.sp
                )
            }


            IconButton(

                onClick = {}
            ) {

                Icon(

                    Icons.Default.NotificationsNone,

                    "Notifications"
                )
            }
        }


        Spacer(

            modifier =
                Modifier.height(38.dp)
        )


        Text(

            greeting(),

            fontSize = 16.sp,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Text(

            "How can I help?",

            fontSize = 30.sp,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(

            modifier =
                Modifier.height(35.dp)
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

            contentAlignment =
                Alignment.Center
        ) {


            Box(

                modifier = Modifier

                    .size(115.dp)

                    .background(

                        MaterialTheme
                            .colorScheme
                            .primary,

                        CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    "✦",

                    fontSize = 48.sp,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onPrimary
                )
            }
        }


        Spacer(

            modifier =
                Modifier.height(40.dp)
        )


        OutlinedTextField(

            value = input,

            onValueChange = {

                input = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            placeholder = {

                Text(
                    "Ask ORION anything..."
                )
            },

            trailingIcon = {

                IconButton(

                    onClick = {

                        if (
                            input.isNotBlank()
                        ) {

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

            shape =
                RoundedCornerShape(28.dp),

            singleLine = true
        )


        Spacer(

            modifier =
                Modifier.height(25.dp)
        )


        Text(

            "Quick actions",

            modifier =
                Modifier.fillMaxWidth(),

            fontWeight =
                FontWeight.SemiBold,

            fontSize = 18.sp
        )


        Spacer(

            modifier =
                Modifier.height(12.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
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

        modifier =
            modifier.height(95.dp),

        shape =
            RoundedCornerShape(24.dp)
    ) {


        Column(

            modifier = Modifier

                .fillMaxSize()

                .padding(12.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Icon(

                icon,

                title
            )


            Spacer(

                modifier =
                    Modifier.height(6.dp)
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

    onSend: (String) -> Unit,

    onVoiceInput:
        ((String) -> Unit) -> Unit
) {

    var input by remember {

        mutableStateOf("")
    }


    Column(

        modifier = modifier

            .fillMaxSize()

            .background(
                Color(0xFF0D0F14)
            )
    ) {


        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(

                    horizontal = 20.dp,

                    vertical = 18.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Box(

                modifier = Modifier

                    .size(42.dp)

                    .background(

                        MaterialTheme
                            .colorScheme
                            .primary,

                        CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    "✦",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onPrimary,

                    fontSize = 22.sp
                )
            }


            Spacer(

                modifier =
                    Modifier.width(12.dp)
            )


            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    "ORION",

                    fontSize = 20.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    if (thinking)
                        "Thinking..."
                    else
                        "Online",

                    fontSize = 12.sp,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
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

                .padding(
                    horizontal = 16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(10.dp),

            contentPadding =
                PaddingValues(
                    vertical = 16.dp
                )
        ) {


            items(messages) { message ->

                MessageBubble(
                    message
                )
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

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            OutlinedTextField(

                value = input,

                onValueChange = {

                    input = it
                },

                modifier =
                    Modifier.weight(1f),

                placeholder = {

                    Text(
                        "Message ORION..."
                    )
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

                            "Voice input"
                        )
                    }
                },

                shape =
                    RoundedCornerShape(26.dp),

                singleLine = true
            )


            Spacer(

                modifier =
                    Modifier.width(8.dp)
            )


            FilledIconButton(

                onClick = {

                    if (
                        input.isNotBlank() &&
                        !thinking
                    ) {

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

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =

            if (message.isUser)

                Arrangement.End

            else

                Arrangement.Start
    ) {


        Surface(

            color =

                if (message.isUser)

                    MaterialTheme
                        .colorScheme
                        .primary

                else

                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,

            shape =
                RoundedCornerShape(18.dp)
        ) {


            Text(

                message.text,

                modifier = Modifier

                    .widthIn(
                        max = 300.dp
                    )

                    .padding(

                        horizontal = 16.dp,

                        vertical = 12.dp
                    ),

                color =

                    if (message.isUser)

                        MaterialTheme
                            .colorScheme
                            .onPrimary

                    else

                        MaterialTheme
                            .colorScheme
                            .onSurface,

                fontSize = 15.sp
            )
        }
    }
}



@Composable
fun ThinkingBubble() {

    Surface(

        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant,

        shape =
            RoundedCornerShape(18.dp)
    ) {

        Text(

            "ORION is thinking...",

            modifier =
                Modifier.padding(

                    horizontal = 16.dp,

                    vertical = 12.dp
                ),

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,

            fontSize = 14.sp
        )
    }
}



@Composable
fun SimpleScreen(

    title: String,

    modifier: Modifier = Modifier
) {

    Box(

        modifier = modifier

            .fillMaxSize()

            .background(
                Color(0xFF0D0F14)
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Text(

            title,

            fontSize = 28.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}



fun greeting(): String {

    val hour =

        java.util.Calendar
            .getInstance()
            .get(
                java.util.Calendar
                    .HOUR_OF_DAY
            )


    return when {

        hour < 12 ->
            "Good morning"

        hour < 17 ->
            "Good afternoon"

        else ->
            "Good evening"
    }
}