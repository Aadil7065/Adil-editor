package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrackItem
import com.example.data.model.BrollItem
import com.example.data.model.CaptionItem
import com.example.data.model.VideoClip
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TimelineAiLayer1
import com.example.ui.theme.TimelineAiLayer2
import com.example.ui.theme.TimelineAiLayer3
import com.example.ui.theme.TimelineAudioWave
import com.example.ui.theme.TimelinePlayhead
import com.example.ui.theme.TimelineTrackBg
import com.example.ui.viewmodel.ActiveToolTab

@Composable
fun MultitrackTimeline(
    clips: List<VideoClip>,
    audioTracks: List<AudioTrackItem>,
    brollItems: List<BrollItem>,
    captions: List<CaptionItem>,
    playheadMs: Long,
    totalDurationMs: Long,
    selectedClipId: String?,
    zoomFactor: Float,
    canUndo: Boolean,
    canRedo: Boolean,
    onSeek: (Long) -> Unit,
    onSelectClip: (String?) -> Unit,
    onSplitClip: () -> Unit,
    onDeleteSelectedClip: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    activeToolTab: ActiveToolTab,
    onToggleToolTab: (ActiveToolTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // 1 second corresponds to ~30dp at 1.0x zoom
    val pxPerSecond = 32.dp * zoomFactor
    val totalTimelineWidth = ((totalDurationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(800.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        // Timeline Header: Film Timeline title, Undo, Redo, Split, Trim, Zoom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Film Timeline",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Undo & Redo
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.size(32.dp).testTag("timeline_undo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) TextWhite else TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.size(32.dp).testTag("timeline_redo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) TextWhite else TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Split at playhead
                IconButton(
                    onClick = onSplitClip,
                    modifier = Modifier.size(32.dp).testTag("timeline_split_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Split Clip",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete selected
                if (selectedClipId != null) {
                    IconButton(
                        onClick = onDeleteSelectedClip,
                        modifier = Modifier.size(32.dp).testTag("timeline_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Clip",
                            tint = NeonRose,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Tool Panels Switcher Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TimelineToolIcon(
                    icon = Icons.Default.ColorLens,
                    label = "Filters",
                    isActive = activeToolTab == ActiveToolTab.FILTERS,
                    onClick = { onToggleToolTab(ActiveToolTab.FILTERS) }
                )
                TimelineToolIcon(
                    icon = Icons.Default.Transform,
                    label = "Transitions",
                    isActive = activeToolTab == ActiveToolTab.TRANSITIONS,
                    onClick = { onToggleToolTab(ActiveToolTab.TRANSITIONS) }
                )
                TimelineToolIcon(
                    icon = Icons.Default.Speed,
                    label = "Speed",
                    isActive = activeToolTab == ActiveToolTab.SPEED,
                    onClick = { onToggleToolTab(ActiveToolTab.SPEED) }
                )
                TimelineToolIcon(
                    icon = Icons.Default.GraphicEq,
                    label = "Audio",
                    isActive = activeToolTab == ActiveToolTab.AUDIO,
                    onClick = { onToggleToolTab(ActiveToolTab.AUDIO) }
                )
                TimelineToolIcon(
                    icon = Icons.Default.Subtitles,
                    label = "Captions",
                    isActive = activeToolTab == ActiveToolTab.CAPTIONS,
                    onClick = { onToggleToolTab(ActiveToolTab.CAPTIONS) }
                )
                TimelineToolIcon(
                    icon = Icons.Default.Layers,
                    label = "B-Roll",
                    isActive = activeToolTab == ActiveToolTab.BROLL,
                    onClick = { onToggleToolTab(ActiveToolTab.BROLL) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Zoom controls
                IconButton(onClick = onZoomOut, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${(zoomFactor * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
                IconButton(onClick = onZoomIn, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Timeline Body: Left Track Labels Column + Right Horizontally Scrollable Tracks Canvas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            // Left Track Labels Header
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .background(SurfaceDark)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder))
            ) {
                // Top Ruler Space
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(SurfaceElevated),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "  TIMECODE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }

                // AI Preview 1
                TrackLabelRow(title = "AI Preview 1", color = TimelineAiLayer2)

                // Clips 1
                TrackLabelRow(title = "Clips 1", color = NeonViolet, icon = Icons.Default.Movie)

                // Audio Track
                TrackLabelRow(title = "Audio Track", color = NeonCyan, icon = Icons.Default.VolumeUp)

                // AI Layer 1
                TrackLabelRow(title = "AI Layer 1", color = TimelineAiLayer1)

                // AI Layer 2
                TrackLabelRow(title = "AI Layer 2", color = TimelineAiLayer3)
            }

            // Right Scrollable Multitrack Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
                    .background(TimelineTrackBg)
            ) {
                // Background Track Grid & Ruler
                Column(modifier = Modifier.width(totalTimelineWidth)) {
                    // Time Ruler
                    TimeRuler(
                        totalDurationMs = totalDurationMs,
                        pxPerSecond = pxPerSecond.value,
                        onSeek = onSeek,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(SurfaceElevated)
                    )

                    // Track 0: AI Preview 1 Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        val aiPreview = brollItems.firstOrNull { it.id.contains("preview") }
                        if (aiPreview != null) {
                            val startX = (aiPreview.startMs / 1000f) * pxPerSecond.value
                            val width = (aiPreview.durationMs / 1000f) * pxPerSecond.value
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TimelineAiLayer2.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .offset { IntOffset(startX.toInt().dp.roundToPx(), 0) }
                                    .width(width.dp)
                                    .height(30.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = aiPreview.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // Track 1: Clips 1 (Video clips with frames, trim borders)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(Color(0xFF0F131E))
                            .padding(vertical = 4.dp)
                    ) {
                        clips.forEach { clip ->
                            val startX = (clip.startMs / 1000f) * pxPerSecond.value
                            val clipWidth = (clip.durationMs / 1000f) * pxPerSecond.value
                            val isSelected = clip.id == selectedClipId

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(startX.toInt().dp.roundToPx(), 0) }
                                    .width(clipWidth.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF312E81),
                                                Color(0xFF1E1B4B),
                                                Color(0xFF4338CA)
                                            )
                                        )
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else Color(0xFF6366F1),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onSelectClip(clip.id) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = clip.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${clip.speed}x",
                                            fontSize = 9.sp,
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = clip.filter.displayName,
                                            fontSize = 9.sp,
                                            color = NeonPink
                                        )
                                        Text(
                                            text = "${clip.durationMs / 1000}s",
                                            fontSize = 9.sp,
                                            color = TextMuted,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Track 2: Audio Track (Waveform bars)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(Color(0xFF0B1019))
                            .padding(vertical = 4.dp)
                    ) {
                        audioTracks.forEach { audio ->
                            val startX = (audio.startMs / 1000f) * pxPerSecond.value
                            val audioWidth = (audio.durationMs / 1000f) * pxPerSecond.value

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(startX.toInt().dp.roundToPx(), 0) }
                                    .width(audioWidth.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0B2533))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            ) {
                                AudioWaveformVisualizer(
                                    modifier = Modifier.fillMaxSize(),
                                    waveColor = if (audio.isVoiceOver) NeonPink else NeonCyan
                                )
                                Text(
                                    text = audio.title,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    // Track 3: AI Layer 1 (Pink block matching screenshot)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(vertical = 3.dp)
                    ) {
                        brollItems.filter { it.layerIndex == 1 && !it.id.contains("preview") }.forEach { item ->
                            val startX = (item.startMs / 1000f) * pxPerSecond.value
                            val itemWidth = (item.durationMs / 1000f) * pxPerSecond.value

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TimelineAiLayer1.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .offset { IntOffset(startX.toInt().dp.roundToPx(), 0) }
                                    .width(itemWidth.dp)
                                    .height(28.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // Track 4: AI Layer 2 (Blue block matching screenshot)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(vertical = 3.dp)
                    ) {
                        brollItems.filter { it.layerIndex == 2 }.forEach { item ->
                            val startX = (item.startMs / 1000f) * pxPerSecond.value
                            val itemWidth = (item.durationMs / 1000f) * pxPerSecond.value

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TimelineAiLayer3.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .offset { IntOffset(startX.toInt().dp.roundToPx(), 0) }
                                    .width(itemWidth.dp)
                                    .height(28.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                // Vertical Glowing Red/Pink Playhead Cursor
                val playheadX = (playheadMs / 1000f) * pxPerSecond.value
                Box(
                    modifier = Modifier
                        .offset { IntOffset(playheadX.toInt().dp.roundToPx(), 0) }
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(TimelinePlayhead)
                ) {
                    // Playhead Top Needle Handle
                    Box(
                        modifier = Modifier
                            .offset(x = (-6).dp, y = 0.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(TimelinePlayhead)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackLabelRow(
    title: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (title.contains("Clips")) 58.dp else if (title.contains("Audio")) 44.dp else 37.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite,
            maxLines = 1
        )
    }
}

@Composable
private fun TimeRuler(
    totalDurationMs: Long,
    pxPerSecond: Float,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val seconds = offset.x / pxPerSecond
                onSeek((seconds * 1000L).toLong())
            }
        }
    ) {
        val totalSec = totalDurationMs / 1000
        for (sec in 0..totalSec step 5) {
            val x = sec * pxPerSecond
            val isMajor = sec % 10 == 0L
            drawLine(
                color = if (isMajor) Color(0xFF6B7280) else Color(0xFF374151),
                start = Offset(x, if (isMajor) 0f else size.height * 0.4f),
                end = Offset(x, size.height),
                strokeWidth = if (isMajor) 1.5f else 1f
            )
        }
    }
}

@Composable
private fun AudioWaveformVisualizer(modifier: Modifier = Modifier, waveColor: Color = NeonCyan) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h / 2f
        val barCount = (w / 4f).toInt().coerceAtLeast(10)

        for (i in 0 until barCount) {
            val x = i * 4f
            // Generate simulated acoustic waveform curve
            val wave = kotlin.math.sin(i * 0.25).toFloat() * 0.4f +
                       kotlin.math.cos(i * 0.15).toFloat() * 0.3f + 0.3f
            val barHeight = (h * wave * 0.7f).coerceIn(4f, h * 0.9f)
            drawLine(
                color = waveColor.copy(alpha = 0.75f),
                start = Offset(x, midY - barHeight / 2f),
                end = Offset(x, midY + barHeight / 2f),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun TimelineToolIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) NeonViolet else SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) NeonCyan else SurfaceBorder
        ),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) TextWhite else TextMuted,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isActive) TextWhite else TextMuted,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
