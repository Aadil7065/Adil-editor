package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VoiceCommandResult
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun VoiceCommandDialog(
    commandResult: VoiceCommandResult?,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    if (commandResult == null) return

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("voice_command_modal")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header: Adil AI Voice Editor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepViolet),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ADIL AI VOICE INTENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Review Proposed Editing Changes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transcribed Command Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DeepSpace,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Spoken Instruction:",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${commandResult.transcript}\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Proposed Changes List
                Text(
                    text = "Proposed Timeline Operations:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(6.dp))

                commandResult.proposedChanges.forEachIndexed { index, change ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}. ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Text(
                            text = change,
                            fontSize = 12.sp,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: Cancel vs Apply Changes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_voice_command_button")
                    ) {
                        Text("Cancel", color = TextMuted)
                    }

                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_voice_command_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply Changes", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
