package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AspectRatio
import com.example.data.model.VideoFilter
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorViewModel

/**
 * 5. IMAGE EDITOR Screen — Complete AI Image Editing Suite
 * Built with the exact same futuristic 3D CapCut / VeoStudio style UI
 * as the main video editor workspace.
 */
@Composable
fun ImageEditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val imageLayers by viewModel.imageLayers.collectAsState()
    val selectedLayerId by viewModel.selectedImageLayerId.collectAsState()
    val activeFilter by viewModel.imageFilter.collectAsState()
    val brightness by viewModel.imageBrightness.collectAsState()
    val contrast by viewModel.imageContrast.collectAsState()
    val saturation by viewModel.imageSaturation.collectAsState()
    val vignette by viewModel.imageVignette.collectAsState()
    val aspectRatio by viewModel.imageAspectRatio.collectAsState()
    val isAiProcessing by viewModel.isImageAiProcessing.collectAsState()
    val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsState()

    var activeTab by remember { mutableStateOf("AI_TOOLS") } // AI_TOOLS, FILTERS, ADJUST, 3D_TEXT, LAYERS
    var customTextPrompt by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(voiceStatusMessage) {
        voiceStatusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissVoiceStatus()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // TOP BAR: Title, Aspect Ratio chips, Undo/Redo, Connected Zero-Gap [✓ Saved][⤓ Export]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "5. IMAGE EDITOR",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DeepViolet,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                    ) {
                        Text(
                            text = "GEMINI ARGON-4 AI",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPink,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Aspect Ratio Selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        AspectRatio.RATIO_1_1,
                        AspectRatio.RATIO_9_16,
                        AspectRatio.RATIO_16_9,
                        AspectRatio.RATIO_4_5
                    ).forEach { ratio ->
                        val isSel = aspectRatio == ratio
                        Surface(
                            onClick = { viewModel.setImageAspectRatio(ratio) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) DeepViolet else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder)
                        ) {
                            Text(
                                text = ratio.label.take(4),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) NeonCyan else TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Right controls: Undo/Redo, Connected Zero-Gap [✓ Saved][⤓ Export]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.undo() },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = TextWhite, modifier = Modifier.size(17.dp))
                    }
                    IconButton(
                        onClick = { viewModel.redo() },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo", tint = TextWhite, modifier = Modifier.size(17.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Zero-Gap Connected Cluster: [✓ Saved][⤓ Export]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.quickSaveProject() },
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                            color = DeepViolet,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.height(28.dp).testTag("image_top_saved_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Saved", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                        }

                        Button(
                            onClick = { viewModel.openExportDialog() },
                            shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("image_top_export_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = TextWhite, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Export", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // CENTER VISUAL PREVIEW CANVAS (Exact same 3D depth aesthetic as Video Editor)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Preview Canvas with simulated artwork & real-time filter matrix
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF090D1A))
                    ) {
                        // Drawing base high-tech spaceship/cyberpunk scene
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Dynamic gradient background according to active filter
                            val primaryAccent = when (activeFilter) {
                                VideoFilter.CYBERPUNK -> Color(0xFF9333EA)
                                VideoFilter.CINEMATIC -> Color(0xFF0891B2)
                                VideoFilter.NOIR -> Color(0xFF475569)
                                VideoFilter.VIBRANT -> Color(0xFFF59E0B)
                                else -> Color(0xFF6366F1)
                            }

                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(primaryAccent.copy(alpha = 0.45f * saturation), Color(0xFF050711)),
                                    center = Offset(w * 0.5f, h * 0.4f),
                                    radius = w * 0.8f
                                )
                            )

                            // Futuristic Neon Perspective Corridor
                            val horizonY = h * 0.5f
                            val vp = Offset(w * 0.5f, horizonY)

                            // Neon Grid lines
                            for (i in -4..4) {
                                val floorX = w * 0.5f + (i * w * 0.22f)
                                drawLine(
                                    color = NeonCyan.copy(alpha = 0.35f),
                                    start = vp,
                                    end = Offset(floorX, h),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = NeonPink.copy(alpha = 0.25f),
                                    start = vp,
                                    end = Offset(floorX, 0f),
                                    strokeWidth = 1.5f
                                )
                            }

                            // Glowing Core Portal
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(NeonPink, NeonCyan, Color.Transparent),
                                    center = vp,
                                    radius = 70f * contrast
                                ),
                                radius = 70f * contrast,
                                center = vp
                            )

                            // 3D Typography Layer (if visible)
                            if (imageLayers.find { it.type == "3D_TEXT" }?.isVisible == true) {
                                drawCircle(
                                    color = NeonAmber.copy(alpha = 0.4f),
                                    radius = 35f,
                                    center = Offset(w * 0.5f, h * 0.72f)
                                )
                            }

                            // Vignette overlay
                            if (vignette > 0f) {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = vignette)),
                                        center = Offset(w * 0.5f, h * 0.5f),
                                        radius = w * 0.65f
                                    )
                                )
                            }
                        }

                        // Overlay 3D Title rendered over Canvas
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepSpace.copy(alpha = 0.85f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "ADIL AI • 3D NEON IMAGE COMPOSITION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonCyan,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Processing Indicator
                        if (isAiProcessing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Gemini Argon-4 AI Processing...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LARGE AI MICROPHONE BUTTON (Gemini Argon-4)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonViolet),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = DeepViolet,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI VOICE IMAGE EDITOR (GEMINI ARGON-4)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Speak or tap to transform: background, 3D text, neon lighting",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.applyGeminiArgon4ImageAction("Neon Cyberpunk Glow Enhancement")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TextWhite, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto AI Edit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggestion Chips for Gemini Argon-4 Image Prompts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Add Cyberpunk Neon",
                            "Remove Background",
                            "Add 3D Glowing Text",
                            "Enhance 4K Clarity",
                            "Sci-Fi Hologram Overlay",
                            "Golden Hour Lighting"
                        ).forEach { prompt ->
                            Surface(
                                onClick = { viewModel.applyGeminiArgon4ImageAction(prompt) },
                                shape = RoundedCornerShape(8.dp),
                                color = DeepSpace,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = prompt, fontSize = 10.sp, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MULTILAYER IMAGE STACK (Matching Video Multitrack Timeline UI)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "IMAGE LAYER STACK (${imageLayers.size} Layers)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        Text(
                            text = "Multitrack Composition",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Image Layers List
                    imageLayers.forEach { layer ->
                        val isSelected = selectedLayerId == layer.id
                        Surface(
                            onClick = { viewModel.selectImageLayer(layer.id) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) DeepViolet else SurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else SurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.toggleImageLayerVisibility(layer.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Visibility",
                                            tint = if (layer.isVisible) NeonCyan else TextDark,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = layer.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (layer.isVisible) TextWhite else TextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (layer.type) {
                                        "BASE" -> Color(0xFF0F3E4D)
                                        "AI_OVERLAY" -> DeepViolet
                                        "3D_TEXT" -> Color(0xFF332000)
                                        else -> Color(0xFF1E1430)
                                    }
                                ) {
                                    Text(
                                        text = layer.type,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (layer.type) {
                                            "BASE" -> NeonCyan
                                            "AI_OVERLAY" -> NeonPink
                                            "3D_TEXT" -> NeonAmber
                                            else -> NeonViolet
                                        },
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IMAGE TOOL PANELS (FILTERS, ADJUST, 3D TEXT, AI TOOLS)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Tool tabs selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("AI TOOLS", "AI_TOOLS"),
                            Pair("FILTERS", "FILTERS"),
                            Pair("ADJUST", "ADJUST"),
                            Pair("3D TEXT", "3D_TEXT")
                        ).forEach { (label, tabKey) ->
                            val isSel = activeTab == tabKey
                            Surface(
                                onClick = { activeTab = tabKey },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) DeepViolet else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) NeonCyan else TextMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (activeTab) {
                        "AI_TOOLS" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "✨ Auto Generative Fill",
                                    "✂ Remove Background",
                                    "🔮 Cyberpunk Glow",
                                    "🔍 4K Upscaler",
                                    "🎨 Color Grade Pro"
                                ).forEach { toolName ->
                                    Surface(
                                        onClick = { viewModel.applyGeminiArgon4ImageAction(toolName) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                                    ) {
                                        Text(
                                            text = toolName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        "FILTERS" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VideoFilter.entries.forEach { f ->
                                    val isSel = activeFilter == f
                                    Surface(
                                        onClick = { viewModel.setImageFilter(f) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSel) DeepViolet else SurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder)
                                    ) {
                                        Text(
                                            text = f.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) NeonCyan else TextWhite,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        "ADJUST" -> {
                            Column {
                                Text("Brightness: ${(brightness * 100).toInt()}%", fontSize = 10.sp, color = TextWhite)
                                Slider(
                                    value = brightness,
                                    onValueChange = { viewModel.setImageBrightness(it) },
                                    valueRange = -0.5f..0.5f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                                )
                                Text("Contrast: ${(contrast * 100).toInt()}%", fontSize = 10.sp, color = TextWhite)
                                Slider(
                                    value = contrast,
                                    onValueChange = { viewModel.setImageContrast(it) },
                                    valueRange = 0.5f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                                )
                                Text("Vignette: ${(vignette * 100).toInt()}%", fontSize = 10.sp, color = TextWhite)
                                Slider(
                                    value = vignette,
                                    onValueChange = { viewModel.setImageVignette(it) },
                                    valueRange = 0f..1.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonAmber, activeTrackColor = NeonAmber)
                                )
                            }
                        }

                        "3D_TEXT" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("3D Neon Extrude", "Cyberpunk Hologram", "Bold Chrome", "Acid Outline").forEach { style ->
                                    Surface(
                                        onClick = { viewModel.applyGeminiArgon4ImageAction("Add 3D Text: $style") },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                                    ) {
                                        Text(
                                            text = style,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}
