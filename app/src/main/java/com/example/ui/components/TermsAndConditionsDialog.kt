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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun TermsAndConditionsDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 620.dp)
                .testTag("terms_and_conditions_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepViolet)
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LEGAL & COMPLIANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Terms & Conditions",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("terms_dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Legal Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(DeepSpace, RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TermsSection(
                        title = "1. Introduction & Ownership",
                        content = "Welcome to EDIT WITH ADIL AI. By accessing or using our application, video editing studio, and AI generation suite, you agree to be bound by these Terms and Conditions. All application assets, algorithms, and branding are the property of Adil Media Works."
                    )

                    TermsSection(
                        title = "2. Commercial License & Exports",
                        content = "Videos and images created using EDIT WITH ADIL AI grant the creator full commercial monetization rights on YouTube, Instagram, TikTok, and commercial television. Watermarks are fully removed on paid plans (Adil Pro Monthly and Adil VIP 3-Months)."
                    )

                    TermsSection(
                        title = "3. Subscription Plans & Quota",
                        content = "• Free Plan: 2 exports/day, 720p resolution.\n• Adil Pro Monthly (₹50 / Month): 40 exports/day, 4K resolution, Gemini Argon-4 AI, full audio multitrack mixing.\n• Adil VIP (₹109 / 3 Months): 100 exports/day, full VIP access, 5. Image Editor studio, 3D captions, and priority cloud rendering."
                    )

                    TermsSection(
                        title = "4. Admin Revenue Vault & Payouts",
                        content = "All subscription payments made by users are accumulated securely inside the Admin Revenue Vault. The system administrator (ADILPAPA786) holds exclusive authority to withdraw collected funds directly to verified UPI IDs or Paytm accounts with instantaneous digital settlement."
                    )

                    TermsSection(
                        title = "5. Gemini Argon-4 AI Usage Rules",
                        content = "Our integrated Gemini Argon-4 multimodal engine generates video scripts, scene storyboards, and contextual B-Roll clips. Users agree not to input malicious, deceptive, or defamatory prompts. The system retains the right to reject non-compliant generation queries."
                    )

                    TermsSection(
                        title = "6. License Key Activation & Security",
                        content = "License keys issued through the Admin Dashboard are unique alphanumeric vouchers. Once redeemed by an account, keys are permanently bound and cannot be transferred or shared."
                    )

                    TermsSection(
                        title = "7. Refund & Cancellation Policy",
                        content = "Because digital quotas and AI computing cycles are provisioned immediately upon upgrade, all purchases are non-refundable once quota is utilized."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons with Perfect Alignment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("terms_accept_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I Understand & Accept",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TermsSection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NeonPink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 11.sp,
            color = TextWhite.copy(alpha = 0.85f),
            lineHeight = 16.sp
        )
    }
}
