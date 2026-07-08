package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    var showVoiceDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showVoiceDialog = true
        }
    }

    // Suggestions to help the user start talking to AquaBot
    val suggestions = listOf(
        "Tell me about DSR ₹4000 incentive",
        "Crop diversification ₹7000 policy",
        "85% micro-irrigation subsidy",
        "Groundwater in Kaithal & Kurukshetra"
    )

    // Auto-scroll to latest message when messages change
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header with actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Assistant Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 6.dp)
                        )
                        Text(
                            text = "AquaBot AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                    Text(
                        text = "Haryana Water Advisory Agent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("chat_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Compliant security disclaimer notice warning (Option B Prototype mode)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Security Alert",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Demo/Prototype Mode: Utilizing direct API credentials. Do not distribute this APK publicly as keys can be decompiled.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    lineHeight = 14.sp
                )
            }
        }

        // Messages Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("chat_messages_list"),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }

                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }

        // Suggestions Area (Horizontal scroll list of suggestion chips)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            onSendMessage(suggestion)
                            focusManager.clearFocus()
                        },
                        label = {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("suggestion_${suggestion.replace(" ", "_").lowercase()}")
                    )
                }
            }
        }

        // Input Area
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask AquaBot about Haryana conservation...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                onSendMessage(textInput)
                                textInput = ""
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input")
                )

                // Microphone input button
                IconButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            showVoiceDialog = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), CircleShape)
                        .testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (textInput.isBlank()) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable(enabled = textInput.isNotBlank() && !isLoading) {
                            onSendMessage(textInput)
                            textInput = ""
                            focusManager.clearFocus()
                        }
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (showVoiceDialog) {
            VoiceInputDialog(
                onDismiss = { showVoiceDialog = false },
                onResult = { result ->
                    if (result.isNotBlank()) {
                        textInput = result
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER
    val isError = message.sender == MessageSender.ERROR

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    val backgroundColor = when {
        isUser -> MaterialTheme.colorScheme.primary
        isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    val textColor = when {
        isUser -> Color.White
        isError -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = bubbleShape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // If it's a markdown-like list, we can format bold elements simply
                FormattedText(
                    text = message.text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isUser) "You" else if (isError) "System Error" else "AquaBot",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun FormattedText(
    text: String,
    color: Color,
    style: androidx.compose.ui.text.TextStyle
) {
    // Simple inline parser for **bold** elements to make AI output clean and beautiful
    val parts = remember(text) { parseBoldText(text) }

    androidx.compose.material3.Text(
        text = androidx.compose.ui.text.buildAnnotatedString {
            parts.forEach { part ->
                if (part.isBold) {
                    pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                    append(part.text)
                    pop()
                } else {
                    append(part.text)
                }
            }
        },
        color = color,
        style = style
    )
}

data class TextPart(val text: String, val isBold: Boolean)

private fun parseBoldText(input: String): List<TextPart> {
    val result = mutableListOf<TextPart>()
    val regex = """\*\*(.*?)\*\*""".toRegex()
    var lastIdx = 0

    regex.findAll(input).forEach { matchResult ->
        val range = matchResult.range
        if (range.first > lastIdx) {
            result.add(TextPart(input.substring(lastIdx, range.first), false))
        }
        result.add(TextPart(matchResult.groupValues[1], true))
        lastIdx = range.last + 1
    }

    if (lastIdx < input.length) {
        result.add(TextPart(input.substring(lastIdx), false))
    }

    return result
}

@Composable
fun TypingIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.widthIn(max = 120.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thinking",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

class SpeechRecognizerHelper(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onPartialResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onStateChange: (Boolean) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition is not available on this device.")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onStateChange(true)
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        onStateChange(false)
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_CLIENT -> "Client-side error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech matched. Try speaking again."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy. Try again."
                            SpeechRecognizer.ERROR_SERVER -> "Server error."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected."
                            else -> "An unknown error occurred during recognition."
                        }
                        onError(message)
                        onStateChange(false)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        } else {
                            onError("Could not recognize speech.")
                        }
                        onStateChange(false)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onPartialResult(matches[0])
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to start speech recognizer.")
            onStateChange(false)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun cancel() {
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val helper = remember {
        SpeechRecognizerHelper(
            context = context,
            onResult = { text ->
                recognizedText = text
                onResult(text)
                onDismiss()
            },
            onPartialResult = { partial ->
                recognizedText = partial
            },
            onError = { err ->
                errorMessage = err
            },
            onStateChange = { listening ->
                isListening = listening
            }
        )
    }

    LaunchedEffect(Unit) {
        helper.startListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            helper.cancel()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (errorMessage != null) {
                TextButton(
                    onClick = {
                        errorMessage = null
                        recognizedText = ""
                        helper.startListening()
                    },
                    modifier = Modifier.testTag("voice_retry_button")
                ) {
                    Text("Retry")
                }
            } else if (recognizedText.isNotEmpty()) {
                Button(
                    onClick = {
                        onResult(recognizedText)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("voice_use_button")
                ) {
                    Text("Use Text")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("voice_cancel_button")
            ) {
                Text("Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (errorMessage != null) "Speech Recognition" else "Listening...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMessage == null) {
                    // Pulsing Microphone Graphic
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.4f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 0.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        // Outer Pulsing Circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                    alpha = pulseAlpha
                                }
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                        )

                        // Inner solid circle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Pulsing Mic Active",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (recognizedText.isEmpty()) "Speak now about water conservation..." else recognizedText,
                        style = if (recognizedText.isEmpty()) MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                else MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_recognized_text")
                    )
                } else {
                    Text(
                        text = errorMessage ?: "An unknown speech recognition error occurred.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.testTag("voice_error_message")
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.testTag("voice_input_dialog")
    )
}
