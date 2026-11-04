package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun PromptToVideoScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val promptScript by viewModel.promptScript.collectAsState()
    val selectedCharacter by viewModel.selectedCharacter.collectAsState()
    val isGenerating by viewModel.isGeneratingScriptVideo.collectAsState()

    var selectedStyle by remember { mutableStateOf("Cyberpunk 2099") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
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
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "2. PROMPT TO VIDEO GENERATOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Generate Full Multitrack Video from Text",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Left Pane (Script Editor) & Right Pane (Character Roster)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Script & Scene Prompt",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = promptScript,
                    onValueChange = { viewModel.setPromptScript(it) },
                    placeholder = {
                        Text(
                            text = "Write screenplay action or natural language prompt...\nExample: INT. SPACESHIP - DAY Captain Zara encounters alien anomaly...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("prompt_to_video_script_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepSpace,
                        unfocusedContainerColor = DeepSpace,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt Suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val promptTemplates = listOf(
                        "Cyberpunk hover-car chase through ADIL Editor City alley",
                        "Sci-Fi Captain Zara activating jump drive engines",
                        "Cinematic drone tour of futuristic neon skyline",
                        "Robot companion Kael performing system diagnostics"
                    )

                    promptTemplates.forEach { template ->
                        Surface(
                            onClick = { viewModel.setPromptScript(template) },
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = template,
                                fontSize = 11.sp,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Character Roster (Zara, Kael, Robot as in screenshot!)
        Text(
            text = "AI Cast & Characters:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val characters = listOf(
                Pair("Captain Zara", Icons.Default.Person),
                Pair("Kael (Pilot)", Icons.Default.Person),
                Pair("Robot (Unit 7)", Icons.Default.SmartToy)
            )

            characters.forEach { (charName, icon) ->
                val isSelected = selectedCharacter == charName
                Surface(
                    onClick = { viewModel.setSelectedCharacter(charName) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) DeepViolet else SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else SurfaceBorder
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonCyan else SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = charName,
                                tint = if (isSelected) DeepSpace else TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = charName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Style Presets
        Text(
            text = "Visual Aesthetic & Cinematic Style:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Cyberpunk 2099", "Cinematic Film 35mm", "Anime Epic", "3D Hyper-Real", "Vintage VHS 90s").forEach { style ->
                val isSel = selectedStyle == style
                Surface(
                    onClick = { selectedStyle = style },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) NeonViolet else SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder)
                ) {
                    Text(
                        text = style,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generate Video Button
        Button(
            onClick = { viewModel.generateVideoFromPrompt() },
            enabled = !isGenerating,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_prompt_to_video_button")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing Video Timeline...", color = TextWhite)
            } else {
                Icon(Icons.Default.Movie, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate & Assemble Multitrack Video",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
