package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio as AspectRatioEnum
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.Project
import com.example.data.model.VideoClip
import com.example.data.model.VideoFilter
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun VideoPreviewPlayer(
    project: Project?,
    activeClip: VideoClip?,
    activeCaption: CaptionItem?,
    isPlaying: Boolean,
    playheadMs: Long,
    totalDurationMs: Long,
    onTogglePlayPause: () -> Unit,
    onRewind5s: () -> Unit,
    onForward5s: () -> Unit,
    onChangeAspectRatio: (AspectRatioEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_preview")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    var showAspectMenu by remember { mutableStateOf(false) }

    val formattedCurrentTime = remember(playheadMs) { formatTimestamp(playheadMs) }
    val formattedTotalTime = remember(totalDurationMs) { formatTimestamp(totalDurationMs) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Top Player Sub-Header: Title, Aspect Ratio Chip & Fullscreen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) NeonCyan else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = activeClip?.title ?: project?.name ?: "Preview",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Aspect Ratio Selector Chip
                Surface(
                    onClick = {
                        val ratios = AspectRatioEnum.values()
                        val currentIdx = ratios.indexOf(project?.aspectRatio ?: AspectRatioEnum.RATIO_16_9)
                        val nextRatio = ratios[(currentIdx + 1) % ratios.size]
                        onChangeAspectRatio(nextRatio)
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("aspect_ratio_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect Ratio",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = project?.aspectRatio?.label ?: "16:9",
                            fontSize = 11.sp,
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Resolution Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DeepViolet.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "1080P 60FPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Video Viewport with aspect ratio constraint & futuristic canvas render
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(DeepSpace)
                .border(1.dp, SurfaceBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val ratio = project?.aspectRatio ?: AspectRatioEnum.RATIO_16_9
            val aspect = ratio.widthRatio / ratio.heightRatio

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(aspect, matchHeightConstraintsFirst = false)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicBlack),
                contentAlignment = Alignment.Center
            ) {
                // Simulated High-Fidelity Rendering Canvas matching VeoStudio reference
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height

                    // Dynamic background gradient based on active clip filter
                    val gradient = when (activeClip?.filter) {
                        VideoFilter.CYBERPUNK -> Brush.radialGradient(
                            colors = listOf(Color(0xFF3B1254), Color(0xFF140D2E), Color(0xFF070614)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.8f
                        )
                        VideoFilter.CINEMATIC -> Brush.radialGradient(
                            colors = listOf(Color(0xFF0F3E4D), Color(0xFF08202A), Color(0xFF040B0F)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.8f
                        )
                        VideoFilter.NOIR -> Brush.radialGradient(
                            colors = listOf(Color(0xFF333333), Color(0xFF111111), Color(0xFF000000)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.8f
                        )
                        VideoFilter.WARM -> Brush.radialGradient(
                            colors = listOf(Color(0xFF5A2A18), Color(0xFF281108), Color(0xFF090402)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.8f
                        )
                        else -> Brush.radialGradient(
                            colors = listOf(Color(0xFF251A40), Color(0xFF110D20), Color(0xFF08070F)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.8f
                        )
                    }

                    drawRect(brush = gradient)

                    // Draw futuristic spaceship corridor perspective wireframe (referencing VeoStudio)
                    val corridorCenter = Offset(w * 0.5f, h * 0.48f)
                    val neonColor = when (activeClip?.filter) {
                        VideoFilter.CINEMATIC -> Color(0xFF06B6D4)
                        VideoFilter.WARM -> Color(0xFFF59E0B)
                        else -> Color(0xFFEC4899)
                    }

                    // Perspective corridor lines
                    drawLine(
                        color = neonColor.copy(alpha = 0.5f * pulseGlow),
                        start = Offset(0f, 0f),
                        end = corridorCenter,
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = neonColor.copy(alpha = 0.5f * pulseGlow),
                        start = Offset(w, 0f),
                        end = corridorCenter,
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF8B5CF6).copy(alpha = 0.6f * pulseGlow),
                        start = Offset(0f, h),
                        end = corridorCenter,
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF8B5CF6).copy(alpha = 0.6f * pulseGlow),
                        start = Offset(w, h),
                        end = corridorCenter,
                        strokeWidth = 2f
                    )

                    // Neon arches / panels inside the corridor
                    for (i in 1..4) {
                        val factor = i * 0.22f
                        val archW = w * factor
                        val archH = h * factor
                        val left = (w - archW) / 2f
                        val top = (h - archH) / 2f
                        drawRoundRect(
                            color = if (i % 2 == 0) neonColor.copy(alpha = 0.4f * pulseGlow) else Color(0xFF8B5CF6).copy(alpha = 0.4f),
                            topLeft = Offset(left, top),
                            size = androidx.compose.ui.geometry.Size(archW, archH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                            style = Stroke(width = 1.5f)
                        )
                    }

                    // Central hyper-jump portal / energy sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color.Transparent),
                            center = corridorCenter,
                            radius = w * 0.12f * pulseGlow
                        ),
                        radius = w * 0.12f * pulseGlow,
                        center = corridorCenter
                    )
                }

                // AI Live Overlay / HUD Badges
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Top Left: AI Active Tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0x99000000),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.6f)),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADIL AI RENDER • ${activeClip?.filter?.displayName?.uppercase() ?: "REAL-TIME"}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }

                    // Center Bottom: 3D Animated Captions
                    if (activeCaption != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            // 3D Shadow layer
                            Text(
                                text = activeCaption.text,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
                            )
                            // 3D Neon Glow Layer
                            Text(
                                text = activeCaption.text,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (activeCaption.style == CaptionStyle.NEON_3D) NeonCyan else Color.Yellow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Playback Controls Bar (Timecode, Rewind, Play/Pause, Forward, Scrub)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Timecode display: 00:00:02 / 01:35:30
            Text(
                text = "$formattedCurrentTime / $formattedTotalTime",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = TextWhite,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.testTag("timecode_display")
            )

            // Playback buttons: |<  <5  PLAY/PAUSE  5>  >|
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onRewind5s() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("rewind_5s_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay5,
                        contentDescription = "Rewind 5s",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Primary Play / Pause Button with Neon Glow
                Surface(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    color = NeonViolet,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onForward5s() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("forward_5s_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 5s",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Quick Tools: Fullscreen & Volume
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Fullscreen",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun formatTimestamp(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
