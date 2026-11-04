package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiMicrophoneButton
import com.example.ui.components.MultitrackTimeline
import com.example.ui.components.ToolPanels
import com.example.ui.components.VideoPreviewPlayer
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val clips by viewModel.clips.collectAsState()
    val audioTracks by viewModel.audioTracks.collectAsState()
    val brollItems by viewModel.brollItems.collectAsState()
    val captions by viewModel.captions.collectAsState()

    val isPlaying by viewModel.timelineEngine.isPlaying.collectAsState()
    val playheadMs by viewModel.timelineEngine.playheadMs.collectAsState()
    val selectedClipId by viewModel.timelineEngine.selectedClipId.collectAsState()
    val timelineZoom by viewModel.timelineEngine.timelineZoom.collectAsState()
    val canUndo by viewModel.timelineEngine.canUndo.collectAsState()
    val canRedo by viewModel.timelineEngine.canRedo.collectAsState()

    val activeToolTab by viewModel.activeToolTab.collectAsState()
    val isListeningVoice by viewModel.isListeningVoice.collectAsState()
    val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsState()

    val selectedClip = remember(clips, selectedClipId) {
        clips.find { it.id == selectedClipId } ?: clips.firstOrNull()
    }
    val activeCaption = remember(captions, playheadMs) {
        captions.find { playheadMs >= it.startMs && playheadMs <= (it.startMs + it.durationMs) }
    }

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
            // Top Bar: Project title, Auto B-Roll (Gemini Argon-4), Undo/Redo, Zero-Gap Saved + Compact Export Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activeProject?.name ?: "Untitled Video",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Auto B-Roll Quick Action (Gemini Argon-4)
                    Surface(
                        onClick = { viewModel.generateAutoBrollWithGemini() },
                        shape = RoundedCornerShape(6.dp),
                        color = DeepViolet,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                        modifier = Modifier.testTag("top_auto_broll_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Auto B-Roll (Gemini Argon-4)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.size(30.dp).testTag("top_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) TextWhite else TextDark,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.size(30.dp).testTag("top_redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) TextWhite else TextDark,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Connected Zero-Gap Cluster: [✓ Saved][⤓ Export]
                    // Export button is small (chota) and gap around saved is zero (no gap)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Saved Status Button (Clickable to trigger immediate save, 0 gap to Export)
                        Surface(
                            onClick = { viewModel.quickSaveProject() },
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                            color = DeepViolet,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.height(28.dp).testTag("top_saved_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Saved",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }

                        // Compact Small Export Button (seamlessly connected with no gap)
                        Button(
                            onClick = { viewModel.openExportDialog() },
                            shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("top_export_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Export",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Video Preview Player (matching center canvas from reference image)
            VideoPreviewPlayer(
                project = activeProject,
                activeClip = selectedClip,
                activeCaption = activeCaption,
                isPlaying = isPlaying,
                playheadMs = playheadMs,
                totalDurationMs = activeProject?.durationMs ?: 95000L,
                onTogglePlayPause = { viewModel.timelineEngine.togglePlayPause() },
                onRewind5s = { viewModel.timelineEngine.rewind5s(activeProject?.durationMs ?: 95000L) },
                onForward5s = { viewModel.timelineEngine.forward5s(activeProject?.durationMs ?: 95000L) },
                onChangeAspectRatio = { viewModel.setAspectRatio(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Large AI Microphone Button Prominently Placed Above the Timeline
            AiMicrophoneButton(
                isListening = isListeningVoice,
                onStartVoice = { viewModel.triggerVoiceListening() },
                onStopVoice = { viewModel.stopVoiceListening() },
                onExecutePrompt = { prompt -> viewModel.processVoiceCommand(prompt) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Multitrack Timeline
            MultitrackTimeline(
                clips = clips,
                audioTracks = audioTracks,
                brollItems = brollItems,
                captions = captions,
                playheadMs = playheadMs,
                totalDurationMs = activeProject?.durationMs ?: 95000L,
                selectedClipId = selectedClipId,
                zoomFactor = timelineZoom,
                canUndo = canUndo,
                canRedo = canRedo,
                onSeek = { viewModel.timelineEngine.seekTo(it, activeProject?.durationMs ?: 95000L) },
                onSelectClip = { viewModel.timelineEngine.selectClip(it) },
                onSplitClip = { viewModel.splitCurrentClip() },
                onDeleteSelectedClip = { viewModel.deleteSelectedClip() },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onZoomIn = { viewModel.timelineEngine.zoomIn() },
                onZoomOut = { viewModel.timelineEngine.zoomOut() },
                activeToolTab = activeToolTab,
                onToggleToolTab = { viewModel.setActiveToolTab(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Active Tool Panel (Filters, Transitions, Speed, Audio, Captions, B-roll)
            ToolPanels(
                activeTab = activeToolTab,
                selectedClip = selectedClip,
                onClose = { viewModel.setActiveToolTab(com.example.ui.viewmodel.ActiveToolTab.NONE) },
                onApplyFilter = { viewModel.setClipFilter(it) },
                onApplyTransition = { viewModel.setClipTransition(it) },
                onApplySpeed = { viewModel.setClipSpeed(it) },
                onAddBroll = { viewModel.addBroll(it) },
                onAddCaption = { text, style -> viewModel.add3DCaption(text, style) },
                onAutoBroll = { viewModel.generateAutoBrollWithGemini() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
