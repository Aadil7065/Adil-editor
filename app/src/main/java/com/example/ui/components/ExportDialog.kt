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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ExportJob
import com.example.data.model.ExportStatus
import com.example.data.model.Resolution
import com.example.data.model.SubscriptionState
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
fun ExportDialog(
    show: Boolean,
    currentJob: ExportJob?,
    subscriptionState: SubscriptionState,
    onStartExport: (Resolution, Int) -> Unit,
    onCancelExport: () -> Unit,
    onClose: () -> Unit
) {
    if (!show) return

    var selectedRes by remember { mutableStateOf(Resolution.RES_1080P) }
    var selectedFps by remember { mutableIntStateOf(60) }

    val isRendering = currentJob != null && currentJob.status in listOf(
        ExportStatus.PREPARING,
        ExportStatus.PROCESSING_FRAMES,
        ExportStatus.MIXING_AUDIO,
        ExportStatus.ENCODING_H264
    )
    val isCompleted = currentJob?.status == ExportStatus.COMPLETED

    Dialog(onDismissRequest = {
        if (!isRendering) onClose()
    }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
            shadowElevation = 14.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("export_dialog_modal")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DeepViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isCompleted) "Export Complete!" else if (isRendering) "Rendering Video..." else "Export Master Video",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    if (!isRendering) {
                        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isCompleted) {
                    // Success View
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "H.264 MP4 Exported Successfully!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Size: 14.8 MB • ${selectedRes.label} • ${selectedFps} FPS",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Share */ },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", color = NeonCyan)
                            }
                            Button(
                                onClick = onClose,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Done", color = TextWhite)
                            }
                        }
                    }
                } else if (isRendering) {
                    // Rendering In Progress View
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentJob?.currentStepText ?: "Encoding video...",
                            fontSize = 12.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { currentJob?.progress ?: 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonViolet,
                            trackColor = DeepSpace,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${((currentJob?.progress ?: 0f) * 100).toInt()}% rendered",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "H.264 / AAC",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onCancelExport,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel Export", color = NeonPink)
                        }
                    }
                } else {
                    // Export Settings Config View
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Quota Indicator
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DeepSpace,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily Export Limit:",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${subscriptionState.exportsUsedToday} / ${subscriptionState.maxExportsDaily} exports used (${subscriptionState.planType.title})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (subscriptionState.exportsUsedToday >= subscriptionState.maxExportsDaily) NeonPink else NeonGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Resolution Selector
                        Text(
                            text = "Export Resolution:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Resolution.values().forEach { res ->
                                val isSelected = selectedRes == res
                                Surface(
                                    onClick = { selectedRes = res },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) DeepViolet else SurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonCyan else SurfaceBorder
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = res.name.substringAfter("RES_"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) TextWhite else TextMuted
                                        )
                                        Text(
                                            text = "${res.width}p",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Framerate Selector
                        Text(
                            text = "Framerate (FPS):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(30, 60).forEach { fps ->
                                val isSelected = selectedFps == fps
                                Surface(
                                    onClick = { selectedFps = fps },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) NeonViolet else SurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonCyan else SurfaceBorder
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "$fps FPS (Smooth)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Launch Export Button
                        Button(
                            onClick = { onStartExport(selectedRes, selectedFps) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("start_export_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start HD Rendering",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
