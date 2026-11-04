package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.CaptionStyle
import com.example.data.model.TransitionType
import com.example.data.model.VideoClip
import com.example.data.model.VideoFilter
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
import com.example.ui.viewmodel.ActiveToolTab

@Composable
fun ToolPanels(
    activeTab: ActiveToolTab,
    selectedClip: VideoClip?,
    onClose: () -> Unit,
    onApplyFilter: (VideoFilter) -> Unit,
    onApplyTransition: (TransitionType) -> Unit,
    onApplySpeed: (Float) -> Unit,
    onAddBroll: (String) -> Unit,
    onAddCaption: (String, CaptionStyle) -> Unit,
    onAutoBroll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (activeTab == ActiveToolTab.NONE) return

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header with Title & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (activeTab) {
                        ActiveToolTab.FILTERS -> "Color Grading & LUT Filters"
                        ActiveToolTab.TRANSITIONS -> "Cinematic Scene Transitions"
                        ActiveToolTab.SPEED -> "Speed Ramping & Duration Controls"
                        ActiveToolTab.AUDIO -> "Multitrack Audio Mixing & AI De-noise"
                        ActiveToolTab.CAPTIONS -> "3D Dynamic Captions & Subtitle Engine"
                        ActiveToolTab.BROLL -> "AI B-Roll Generator & Stock Overlay"
                        else -> "Editing Tool"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Panel",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (activeTab) {
                ActiveToolTab.FILTERS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoFilter.values().forEach { filter ->
                            val isSelected = selectedClip?.filter == filter
                            Surface(
                                onClick = { onApplyFilter(filter) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DeepViolet else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else SurfaceBorder
                                ),
                                modifier = Modifier.testTag("filter_${filter.name}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = filter.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TextWhite else TextMuted
                                    )
                                    Text(
                                        text = filter.colorMatrixDescription,
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                ActiveToolTab.TRANSITIONS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransitionType.values().forEach { trans ->
                            val isSelected = selectedClip?.transitionIn == trans
                            Surface(
                                onClick = { onApplyTransition(trans) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DeepViolet else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonPink else SurfaceBorder
                                ),
                                modifier = Modifier.testTag("trans_${trans.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = NeonGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = trans.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                ActiveToolTab.SPEED -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 4.0f).forEach { speed ->
                            val isSelected = selectedClip?.speed == speed
                            Surface(
                                onClick = { onApplySpeed(speed) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NeonViolet else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else SurfaceBorder
                                ),
                                modifier = Modifier.testTag("speed_${speed}x")
                            ) {
                                Text(
                                    text = "${speed}x",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                ActiveToolTab.AUDIO -> {
                    var bgmVolume by remember { mutableFloatStateOf(0.8f) }
                    var voiceVolume by remember { mutableFloatStateOf(1.0f) }
                    var noiseReduction by remember { mutableStateOf(true) }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("BGM Volume: ${(bgmVolume * 100).toInt()}%", fontSize = 11.sp, color = TextWhite, modifier = Modifier.width(130.dp))
                            Slider(
                                value = bgmVolume,
                                onValueChange = { bgmVolume = it },
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Voice Clarity: ${(voiceVolume * 100).toInt()}%", fontSize = 11.sp, color = TextWhite, modifier = Modifier.width(130.dp))
                            Slider(
                                value = voiceVolume,
                                onValueChange = { voiceVolume = it },
                                colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AI Noise Reduction (-18dB Ambient Supression)", fontSize = 11.sp, color = TextWhite)
                            Switch(
                                checked = noiseReduction,
                                onCheckedChange = { noiseReduction = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = DeepViolet)
                            )
                        }
                    }
                }

                ActiveToolTab.CAPTIONS -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CaptionStyle.values().forEach { style ->
                            Surface(
                                onClick = { onAddCaption("NEW SCENE SUBTITLE", style) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                                modifier = Modifier.testTag("caption_style_${style.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = style.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                ActiveToolTab.BROLL -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prominent Auto B-Roll Button
                        Surface(
                            onClick = onAutoBroll,
                            shape = RoundedCornerShape(10.dp),
                            color = DeepViolet,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink),
                            modifier = Modifier.testTag("panel_auto_broll_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPink, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⚡ Auto B-Roll (Gemini Argon-4)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }
                        }

                        listOf("Sci-Fi Warp", "ADIL Editor City", "HUD Telemetry", "Drone Landscape", "Abstract Particles").forEach { category ->
                            Surface(
                                onClick = { onAddBroll(category) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                modifier = Modifier.testTag("broll_${category.take(6)}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
