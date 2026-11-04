package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun AiMicrophoneButton(
    isListening: Boolean,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onExecutePrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textPrompt by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isListening) 0.9f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 500 else 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SurfaceElevated,
                        SurfaceDark
                    )
                )
            )
            .border(
                1.dp,
                if (isListening) NeonPink else NeonViolet.copy(alpha = 0.6f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Top Row: Big Glowing 3D AI Microphone Button + Text Prompt Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large AI Microphone Button with Outer Neon Glow Rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clickable {
                        if (isListening) onStopVoice() else onStartVoice()
                    }
                    .testTag("ai_microphone_button")
            ) {
                // Outer Glow Ring
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isListening) NeonPink.copy(alpha = glowAlpha) else NeonCyan.copy(alpha = glowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Inner 3D Glassmorphic Surface
                Surface(
                    shape = CircleShape,
                    color = if (isListening) NeonPink else DeepViolet,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isListening) Color.White else NeonCyan
                    ),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "AI Voice Editor Microphone",
                            tint = TextWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // AI Prompt Input Field with Send Button
            OutlinedTextField(
                value = textPrompt,
                onValueChange = { textPrompt = it },
                placeholder = {
                    Text(
                        text = if (isListening) "Listening to voice commands..." else "Tell Adil AI what to edit or speak...",
                        fontSize = 12.sp,
                        color = if (isListening) NeonPink else TextMuted
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textPrompt.isNotBlank()) {
                        onExecutePrompt(textPrompt)
                        textPrompt = ""
                    }
                }),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (textPrompt.isNotBlank()) {
                                onExecutePrompt(textPrompt)
                                textPrompt = ""
                            }
                        },
                        enabled = textPrompt.isNotBlank(),
                        modifier = Modifier.testTag("send_ai_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Command",
                            tint = if (textPrompt.isNotBlank()) NeonCyan else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DeepSpace,
                    unfocusedContainerColor = DeepSpace,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("ai_prompt_text_field")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Command Suggestion Chips (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf(
                "Remove silence from the video",
                "Add cinematic transitions",
                "Add 3D captions",
                "Add background music",
                "Add B-roll wherever relevant",
                "Make the video look professional",
                "Is video ke starting ke 5 seconds hata do"
            )

            suggestions.forEach { prompt ->
                Surface(
                    onClick = { onExecutePrompt(prompt) },
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.8f)),
                    modifier = Modifier.testTag("chip_${prompt.take(10)}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = NeonViolet,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
