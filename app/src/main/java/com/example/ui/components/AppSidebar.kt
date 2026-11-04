package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SubscriptionState
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorSection

@Composable
fun AppSidebar(
    currentSection: EditorSection,
    onSelectSection: (EditorSection) -> Unit,
    onOpenRedeemKey: () -> Unit,
    subscriptionState: SubscriptionState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(SurfaceDark)
            .border(androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Brand Header with official Adil AI Logo reference
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = DeepViolet,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
                    modifier = Modifier.size(38.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_adil_ai),
                        contentDescription = "Adil AI Logo",
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "EDIT WITH",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ADIL AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MAIN WORKSPACE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // EXACTLY FOUR MAIN SECTIONS:
            // 1. EDIT — Main video editing workspace.
            SidebarMenuItem(
                title = "1. EDIT",
                icon = Icons.Default.Movie,
                isActive = currentSection == EditorSection.EDIT,
                onClick = { onSelectSection(EditorSection.EDIT) },
                testTag = "sidebar_nav_edit"
            )

            // 2. PROMPT TO VIDEO — Generate videos using text prompts.
            SidebarMenuItem(
                title = "2. PROMPT TO VIDEO",
                icon = Icons.Default.AutoAwesome,
                isActive = currentSection == EditorSection.PROMPT_TO_VIDEO,
                onClick = { onSelectSection(EditorSection.PROMPT_TO_VIDEO) },
                testTag = "sidebar_nav_prompt_to_video"
            )

            // 3. RECENT VIDEOS — Show previously edited projects.
            SidebarMenuItem(
                title = "3. RECENT VIDEOS",
                icon = Icons.Default.History,
                isActive = currentSection == EditorSection.RECENT_VIDEOS,
                onClick = { onSelectSection(EditorSection.RECENT_VIDEOS) },
                testTag = "sidebar_nav_recent_videos"
            )

            // 4. PLANS — Subscription and usage information.
            SidebarMenuItem(
                title = "4. PLANS",
                icon = Icons.Default.CardMembership,
                isActive = currentSection == EditorSection.PLANS,
                onClick = { onSelectSection(EditorSection.PLANS) },
                testTag = "sidebar_nav_plans"
            )

            // 5. IMAGE EDITOR — Full AI Image Editing Suite (same UI as video editor)
            SidebarMenuItem(
                title = "5. IMAGE EDITOR",
                icon = Icons.Default.Image,
                isActive = currentSection == EditorSection.IMAGE_EDITOR,
                onClick = { onSelectSection(EditorSection.IMAGE_EDITOR) },
                testTag = "sidebar_nav_image_editor"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Redeem Code Action in Sidebar
            Surface(
                onClick = onOpenRedeemKey,
                shape = RoundedCornerShape(10.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("sidebar_redeem_key_quick_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Redeem Key",
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Redeem Key / Code",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                }
            }
        }

        // Bottom User Account & Separate Admin Portal Gate
        Column {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subscriptionState.planType.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DeepViolet
                        ) {
                            Text(
                                text = if (subscriptionState.isAdmin) "ADMIN" else "PRO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${subscriptionState.exportsUsedToday}/${subscriptionState.maxExportsDaily} daily exports",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dedicated Separate Admin Portal Button
            Surface(
                onClick = { onSelectSection(EditorSection.ADMIN_PANEL) },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E1430),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sidebar_open_admin_portal")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Portal",
                        tint = NeonAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Separate Admin Portal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) DeepViolet else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) NeonCyan else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) NeonCyan else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) TextWhite else TextMuted
            )
        }
    }
}
