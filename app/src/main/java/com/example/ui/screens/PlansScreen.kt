package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.ui.components.TermsAndConditionsDialog
import com.example.ui.theme.CosmicBlack
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
import com.example.ui.viewmodel.EditorSection
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun PlansScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    var showTermsDialog by remember { mutableStateOf(false) }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DeepViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "4. PLANS & USAGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Subscriptions & Developer Keys",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Surface(
                onClick = { showTermsDialog = true },
                shape = RoundedCornerShape(8.dp),
                color = DeepViolet,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("open_terms_and_conditions_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Terms & Rules",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Usage Status Box
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT PLAN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = subscriptionState.planType.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (subscriptionState.isAdmin) DeepViolet else Color(0xFF0F3E4D)
                    ) {
                        Text(
                            text = if (subscriptionState.isAdmin) "ADMIN ACCESS" else "ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (subscriptionState.isAdmin) NeonPink else NeonGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val progress = (subscriptionState.exportsUsedToday.toFloat() / subscriptionState.maxExportsDaily.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = NeonCyan,
                    trackColor = DeepSpace
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${subscriptionState.exportsUsedToday} of ${subscriptionState.maxExportsDaily} video exports used today",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Renews: ${subscriptionState.renewalDateText}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // PROMINENT KEY REDEEM CARD (Code Daalne Ka Option)
        // ==========================================
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF332000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Have an Activation Code or Developer Key?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Enter code given by admin to instantly unlock quota",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Button(
                    onClick = { viewModel.openRedeemKeyDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("open_redeem_key_modal_button")
                ) {
                    Text("Redeem Key", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Select Subscription Plan:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Plan 1: FREE PLAN
        PlanCard(
            title = "FREE PLAN",
            price = "₹0 / forever",
            badge = "BASIC",
            features = listOf(
                "Maximum 2 video exports daily",
                "Basic video cuts, split & trim",
                "Limited AI prompt features",
                "720p HD export rendering"
            ),
            isCurrent = subscriptionState.planType == PlanType.FREE,
            onSelect = { viewModel.upgradeSubscription(PlanType.FREE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 2: ADIL PRO MONTHLY (₹50/month)
        PlanCard(
            title = "ADIL PRO MONTHLY",
            price = "₹50 / month",
            badge = "POPULAR",
            features = listOf(
                "Up to 40 video exports per day",
                "Full HD 1080p and 4K MP4 export",
                "Unlimited AI Voice Editing & Intent Engine",
                "AI B-Roll generator & stock overlay",
                "3D Dynamic captions & karaoke animations",
                "Multitrack audio mixing & noise reduction"
            ),
            isCurrent = subscriptionState.planType == PlanType.PRO_MONTHLY,
            accentColor = NeonCyan,
            onSelect = { viewModel.upgradeSubscription(PlanType.PRO_MONTHLY) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 3: ADIL VIP 3-MONTHS (₹109 / 3 Months)
        PlanCard(
            title = "ADIL VIP 3-MONTHS",
            price = "₹109 / 3 Months (90 Days)",
            badge = "BEST VALUE • ALL INCLUDED",
            features = listOf(
                "Up to 100 video & image exports per day",
                "Full HD 1080p and 4K Ultra-HD MP4 export",
                "Full access to 5. Image Editor studio",
                "Gemini Argon-4 AI Auto B-Roll & Script Synthesis",
                "3D Dynamic captions & karaoke animations",
                "Multitrack audio, noise reduction & vocal clarity",
                "Subscription revenue safely held in Admin Vault",
                "Commercial monetization license included"
            ),
            isCurrent = subscriptionState.planType == PlanType.PRO_QUARTERLY,
            accentColor = NeonPink,
            onSelect = { viewModel.upgradeSubscription(PlanType.PRO_QUARTERLY) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 4: ADMIN PLAN
        PlanCard(
            title = "ADMIN PLAN",
            price = "Developer Privileges",
            badge = "VIP ACCESS",
            features = listOf(
                "Unlimited video exports (9,999/day)",
                "Full Access to Separate SaaS Admin Dashboard",
                "Create and issue custom client activation keys",
                "User management & security quota control",
                "Storage and AI API telemetry inspection"
            ),
            isCurrent = subscriptionState.planType == PlanType.ADMIN,
            accentColor = NeonAmber,
            onSelect = {
                // If not admin, ask for developer key or open admin directly
                viewModel.upgradeSubscription(PlanType.ADMIN)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dedicated link to Separate Admin Dashboard
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DeepViolet.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Separate Admin Dashboard",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Enterprise SaaS metrics, CRM, & Key distribution",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.setSection(EditorSection.ADMIN_PANEL)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    modifier = Modifier.testTag("launch_separate_admin_portal_button")
                ) {
                    Text("Open Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    TermsAndConditionsDialog(
        isOpen = showTermsDialog,
        onDismiss = { showTermsDialog = false }
    )
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    badge: String,
    features: List<String>,
    isCurrent: Boolean,
    accentColor: Color = NeonViolet,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            if (isCurrent) 1.5.dp else 1.dp,
            if (isCurrent) accentColor else SurfaceBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Text(
                        text = price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DeepViolet
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            features.forEach { feat ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = feat,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isCurrent) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Current Active Plan", color = NeonCyan)
                }
            } else {
                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose $title", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
