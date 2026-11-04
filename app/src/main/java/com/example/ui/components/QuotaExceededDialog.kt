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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.PlanType
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun QuotaExceededDialog(
    show: Boolean,
    onUpgradeToPro: () -> Unit,
    onClose: () -> Unit
) {
    if (!show) return

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("quota_exceeded_dialog")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF332200)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Daily Export Limit Reached",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You have used all 2 video exports included on the Free Plan today.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Adil Pro Offer Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DeepViolet.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ADIL PRO MONTHLY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan
                            )
                            Text(
                                text = "₹50 / month",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ 40 Video Exports per day\n✓ Full HD 1080p & 4K Output\n✓ Unlimited AI Voice Editing\n✓ AI B-Roll & 3D Captions Generator",
                            fontSize = 11.sp,
                            color = TextWhite,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Not Now", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            onUpgradeToPro()
                            onClose()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upgrade_pro_button")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upgrade (₹50)", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
