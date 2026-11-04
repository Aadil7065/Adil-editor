package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.LicenseKey
import com.example.data.model.PlanType
import com.example.service.LicenseKeyService
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

/**
 * Secure LicenseRedemption dialog component allowing users to input alphanumeric
 * license keys to upgrade their subscription plan level, connecting to the
 * 'licenseKeys' Firestore collection.
 */
@Composable
fun LicenseRedemptionDialog(
    show: Boolean,
    licenseKeyService: LicenseKeyService,
    onSuccessRedeem: (LicenseKey) -> Unit,
    onClose: () -> Unit
) {
    if (!show) return

    val scope = rememberCoroutineScope()
    var inputCode by remember { mutableStateOf("") }
    var isValidating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var validatedKey by remember { mutableStateOf<LicenseKey?>(null) }

    Dialog(onDismissRequest = { if (!isValidating) onClose() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (validatedKey != null) NeonGreen else NeonCyan),
            shadowElevation = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .testTag("license_redemption_dialog")
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (validatedKey != null) Color(0xFF0E331A) else DeepViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (validatedKey != null) Icons.Default.CheckCircle else Icons.Default.Key,
                                contentDescription = null,
                                tint = if (validatedKey != null) NeonGreen else NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FIRESTORE CLOUD LICENSE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "License Key Redemption",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        enabled = !isValidating,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (validatedKey == null) {
                    Text(
                        text = "Enter your alphanumeric license key (e.g., ADIL-PRO-XXXX-XXXX). The key will be securely authenticated against the 'licenseKeys' cloud collection to immediately elevate your account.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Field
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = {
                            inputCode = it.uppercase().trim()
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = "e.g. ADIL-PRO-8K9M-2X7A",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("license_key_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonRose
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Security Indicator Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Connected to Firestore 'licenseKeys' • Zero-Trust Verification",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Redeem Button
                    Button(
                        onClick = {
                            if (inputCode.isBlank()) {
                                errorMessage = "Please enter an alphanumeric license key."
                                return@Button
                            }

                            isValidating = true
                            errorMessage = null

                            scope.launch {
                                val result = licenseKeyService.redeemLicenseKey(inputCode)
                                isValidating = false
                                result.onSuccess { key ->
                                    validatedKey = key
                                    onSuccessRedeem(key)
                                }.onFailure { err ->
                                    errorMessage = err.message ?: "Invalid key or redemption error."
                                }
                            }
                        },
                        enabled = !isValidating,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_redeem_license_button")
                    ) {
                        if (isValidating) {
                            CircularProgressIndicator(
                                color = TextWhite,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying in Firestore...", fontSize = 12.sp, color = TextWhite)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Redeem & Upgrade Plan",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // Success View
                    val key = validatedKey!!
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F2B1D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Plan Upgraded Successfully!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "New Plan Tier: ${key.planType.title}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                            Text(
                                text = "Daily Exports Quota: ${key.dailyExportLimit} exports/day",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                            Text(
                                text = "License Expiry: ${key.expiryDate}",
                                fontSize = 11.sp,
                                color = NeonAmber
                            )
                            Text(
                                text = "Key Code: ${key.key}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onClose,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "Done & Return to Studio",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
