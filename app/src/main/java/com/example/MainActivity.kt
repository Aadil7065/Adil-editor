package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.ui.components.AppSidebar
import com.example.ui.components.ExportDialog
import com.example.ui.components.LicenseRedemptionDialog
import com.example.ui.components.QuotaExceededDialog
import com.example.ui.components.RedeemKeyDialog
import com.example.ui.components.VoiceCommandDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ImageEditorScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.PromptToVideoScreen
import com.example.ui.screens.RecentVideosScreen
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorSection
import com.example.ui.viewmodel.EditorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: EditorViewModel) {
    val currentSection by viewModel.currentSection.collectAsState()
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    // Modals
    val proposedVoiceCommand by viewModel.proposedVoiceCommand.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()
    val showQuotaExceededDialog by viewModel.showQuotaExceededDialog.collectAsState()
    val showRedeemKeyDialog by viewModel.showRedeemKeyDialog.collectAsState()
    val currentExportJob by viewModel.currentExportJob.collectAsState()

    // Handle Android Back button: return to main editor if on subscreen or admin
    BackHandler(enabled = currentSection != EditorSection.EDIT) {
        viewModel.setSection(EditorSection.EDIT)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepSpace
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 720.dp

            if (currentSection == EditorSection.ADMIN_PANEL) {
                // Completely separate full-screen executive Admin Dashboard!
                AdminDashboardScreen(viewModel = viewModel)
            } else {
                // Video Editing Studio with Left Sidebar
                if (isWideScreen) {
                    // Landscape / Tablet layout with fixed left sidebar
                    Row(modifier = Modifier.fillMaxSize()) {
                        AppSidebar(
                            currentSection = currentSection,
                            onSelectSection = { viewModel.setSection(it) },
                            onOpenRedeemKey = { viewModel.openRedeemKeyDialog() },
                            subscriptionState = subscriptionState
                        )
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            when (currentSection) {
                                EditorSection.EDIT -> EditorScreen(viewModel = viewModel)
                                EditorSection.PROMPT_TO_VIDEO -> PromptToVideoScreen(viewModel = viewModel)
                                EditorSection.RECENT_VIDEOS -> RecentVideosScreen(viewModel = viewModel)
                                EditorSection.PLANS -> PlansScreen(viewModel = viewModel)
                                EditorSection.IMAGE_EDITOR -> ImageEditorScreen(viewModel = viewModel)
                                else -> EditorScreen(viewModel = viewModel)
                            }
                        }
                    }
                } else {
                    // Portrait Mobile layout: Top branded nav bar with the sections + main workspace
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopMobileNavBar(
                            currentSection = currentSection,
                            onSelectSection = { viewModel.setSection(it) },
                            onOpenRedeemKey = { viewModel.openRedeemKeyDialog() },
                            onOpenAdmin = { viewModel.setSection(EditorSection.ADMIN_PANEL) }
                        )

                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            when (currentSection) {
                                EditorSection.EDIT -> EditorScreen(viewModel = viewModel)
                                EditorSection.PROMPT_TO_VIDEO -> PromptToVideoScreen(viewModel = viewModel)
                                EditorSection.RECENT_VIDEOS -> RecentVideosScreen(viewModel = viewModel)
                                EditorSection.PLANS -> PlansScreen(viewModel = viewModel)
                                EditorSection.IMAGE_EDITOR -> ImageEditorScreen(viewModel = viewModel)
                                else -> EditorScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }

        // Global Modals
        VoiceCommandDialog(
            commandResult = proposedVoiceCommand,
            onApply = { viewModel.applyProposedVoiceCommand() },
            onCancel = { viewModel.dismissProposedVoiceCommand() }
        )

        ExportDialog(
            show = showExportDialog,
            currentJob = currentExportJob,
            subscriptionState = subscriptionState,
            onStartExport = { res, fps -> viewModel.triggerExport(res, fps) },
            onCancelExport = { viewModel.cancelExport() },
            onClose = { viewModel.closeExportDialog() }
        )

        QuotaExceededDialog(
            show = showQuotaExceededDialog,
            onUpgradeToPro = { viewModel.upgradeSubscription(PlanType.PRO_MONTHLY) },
            onClose = { viewModel.dismissQuotaDialog() }
        )

        LicenseRedemptionDialog(
            show = showRedeemKeyDialog,
            licenseKeyService = viewModel.licenseKeyService,
            onSuccessRedeem = { key ->
                viewModel.redeemActivationCode(key.key)
            },
            onClose = { viewModel.closeRedeemKeyDialog() }
        )
    }
}

@Composable
fun TopMobileNavBar(
    currentSection: EditorSection,
    onSelectSection: (EditorSection) -> Unit,
    onOpenRedeemKey: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            // Brand row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = DeepViolet,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_adil_ai),
                            contentDescription = "Adil AI Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EDIT WITH ADIL AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Redeem Key Button
                    Surface(
                        onClick = onOpenRedeemKey,
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF332000),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                        modifier = Modifier.testTag("mobile_top_redeem_key_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Redeem Key", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Separate Admin Dashboard Portal
                    Surface(
                        onClick = onOpenAdmin,
                        shape = CircleShape,
                        color = DeepViolet,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("mobile_top_admin_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Portal", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // The Exactly Four Main Sections:
            // 1. EDIT | 2. PROMPT TO VIDEO | 3. RECENT VIDEOS | 4. PLANS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MobileNavTab(
                    title = "1. EDIT",
                    icon = Icons.Default.Movie,
                    isSelected = currentSection == EditorSection.EDIT,
                    onClick = { onSelectSection(EditorSection.EDIT) },
                    testTag = "mobile_tab_edit"
                )
                MobileNavTab(
                    title = "2. PROMPT TO VIDEO",
                    icon = Icons.Default.AutoAwesome,
                    isSelected = currentSection == EditorSection.PROMPT_TO_VIDEO,
                    onClick = { onSelectSection(EditorSection.PROMPT_TO_VIDEO) },
                    testTag = "mobile_tab_prompt_to_video"
                )
                MobileNavTab(
                    title = "3. RECENT VIDEOS",
                    icon = Icons.Default.History,
                    isSelected = currentSection == EditorSection.RECENT_VIDEOS,
                    onClick = { onSelectSection(EditorSection.RECENT_VIDEOS) },
                    testTag = "mobile_tab_recent_videos"
                )
                MobileNavTab(
                    title = "4. PLANS",
                    icon = Icons.Default.CardMembership,
                    isSelected = currentSection == EditorSection.PLANS,
                    onClick = { onSelectSection(EditorSection.PLANS) },
                    testTag = "mobile_tab_plans"
                )
                MobileNavTab(
                    title = "5. IMAGE EDITOR",
                    icon = Icons.Default.Image,
                    isSelected = currentSection == EditorSection.IMAGE_EDITOR,
                    onClick = { onSelectSection(EditorSection.IMAGE_EDITOR) },
                    testTag = "mobile_tab_image_editor"
                )
            }
        }
    }
}

@Composable
private fun MobileNavTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DeepViolet else SurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else SurfaceBorder
        ),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) NeonCyan else TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextWhite else TextMuted
            )
        }
    }
}
