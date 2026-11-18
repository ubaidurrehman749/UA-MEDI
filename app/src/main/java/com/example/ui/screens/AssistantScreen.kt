package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.state.ChatMessage

@Composable
fun AssistantScreen(
    language: AppLanguage,
    messages: List<ChatMessage>,
    isStreaming: Boolean,
    errorMessage: String?,
    onInitChat: () -> Unit,
    onSendMessage: (String) -> Unit,
    onResetChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var voiceNotice by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val isUrdu = language == AppLanguage.URDU

    val starterPrompts = remember(language) {
        if (isUrdu) {
            listOf(
                "HbA1c ٹیسٹ کیا ہوتا ہے؟",
                "بلڈ پریشر نارمل رکھنے کے عام طریقے",
                "بخار اور جسم درد میں ڈاکٹر کے پاس کب جائیں؟",
                "نیند بہتر بنانے کی تجاویز"
            )
        } else {
            listOf(
                "What does an HbA1c blood test measure?",
                "How can I prepare questions for a cardiology visit?",
                "What causes mild dehydration and how to prevent it?",
                "Explain 'reference range' on a lab report"
            )
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = if (inputText.isBlank()) spoken else "$inputText $spoken"
            }
        }
    }

    LaunchedEffect(language) {
        onInitChat()
    }

    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isUrdu) "AI ہیلتھ اسسٹنٹ" else "AI Health Assistant",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = if (isUrdu) "عام صحت رہنمائی • انگریزی اور اردو" else "General health education • English & Urdu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onResetChat,
                    modifier = Modifier.testTag("chat_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Start new conversation")
                }
            }

            Spacer(Modifier.height(8.dp))
            MedicalDisclaimerBanner(language = language, compact = true)
            Spacer(Modifier.height(8.dp))

            // Quick starter prompt chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(starterPrompts) { prompt ->
                    AssistChip(
                        onClick = { if (!isStreaming) onSendMessage(prompt) },
                        label = { Text(prompt, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (!voiceNotice.isNullOrBlank()) {
                Text(
                    text = voiceNotice!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            if (isUrdu) "اپنا صحت سے متعلق سوال لکھیں..." else "Ask a general health question..."
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_input_field"),
                    maxLines = 4,
                    shape = RoundedCornerShape(18.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isUrdu) "ur-PK" else "en-US")
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, if (isUrdu) "اپنا سوال بولیں" else "Speak your health question")
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    voiceNotice = null
                                    speechLauncher.launch(intent)
                                } else {
                                    voiceNotice = "Speech recognition service is unavailable on this device."
                                }
                            },
                            modifier = Modifier.testTag("assistant_voice_button")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice input")
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        val toSend = inputText
                        inputText = ""
                        onSendMessage(toSend)
                    },
                    enabled = inputText.isNotBlank() && !isStreaming,
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("assistant_send_button")
                ) {
                    if (isStreaming) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (message.isUser) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            tonalElevation = if (message.isUser) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = 520.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text.ifBlank { "Thinking..." },
                    style = MaterialTheme.typography.bodyMedium
                )
                if (message.isStreaming) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Generating response...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
