package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivationKey
import com.example.data.model.LicenseKey
import com.example.data.model.AppUser
import com.example.data.model.PlanType
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonAmber
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
import com.example.ui.viewmodel.EditorSection
import com.example.ui.viewmodel.EditorViewModel
import java.util.UUID

/**
 * AdminDashboard UI component, gated by a role-based check,
 * and displaying the list of registered users with their current subscription status.
 */
@Composable
fun AdminDashboard(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    AdminDashboardScreen(viewModel = viewModel, modifier = modifier)
}

@Composable
fun AdminDashboardScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val isUnlocked by viewModel.isAdminSessionUnlocked.collectAsState()
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    // ROLE-BASED CHECK: Only users with the ADMIN role or authenticated admin session may enter
    val hasAdminRole = isUnlocked || subscriptionState.isAdmin

    if (!hasAdminRole) {
        AdminSecurityGateScreen(
            currentPlanTitle = subscriptionState.planType.title,
            onUnlock = { u, p -> viewModel.unlockAdminSession(u, p) },
            onExit = { viewModel.setSection(EditorSection.EDIT) },
            modifier = modifier
        )
    } else {
        AdminDashboardContent(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
private fun AdminSecurityGateScreen(
    currentPlanTitle: String,
    onUnlock: (String, String) -> Boolean,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var usernameInput by remember { mutableStateOf("ADILPAPA786") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber),
            shadowElevation = 20.dp,
            modifier = Modifier
                .width(360.dp)
                .padding(20.dp)
                .testTag("admin_security_gate_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing Security Shield
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF332000))
                        .border(1.5.dp, NeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Shield",
                        tint = NeonAmber,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ROLE-BASED ACCESS CONTROL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonAmber,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "Admin Authorization Required",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E1430),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Your Role: $currentPlanTitle (Restricted to ADMIN)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "This dashboard is strictly protected and exclusively accessible to the system Administrator. Enter verified admin credentials below.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Admin Username
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = {
                        usernameInput = it
                        errorMessage = null
                    },
                    label = { Text("Admin Username", fontSize = 11.sp, color = NeonCyan) },
                    placeholder = { Text("ADILPAPA786", fontSize = 12.sp, color = TextMuted) },
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
                        .testTag("admin_username_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Admin Password
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        errorMessage = null
                    },
                    label = { Text("Admin Password", fontSize = 11.sp, color = NeonAmber) },
                    placeholder = { Text("ADIL1234@#$", fontSize = 12.sp, color = TextMuted) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        val ok = onUnlock(usernameInput, passwordInput)
                        if (!ok) errorMessage = "Invalid Admin credentials. Access denied."
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepSpace,
                        unfocusedContainerColor = DeepSpace,
                        focusedBorderColor = NeonAmber,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 11.sp,
                        color = NeonRose,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Admin Credentials Hint Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Admin Access Credentials:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "• Username: ADILPAPA786\n• Password: ADIL1234@#$",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onExit,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Exit", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val ok = onUnlock(usernameInput, passwordInput)
                            if (!ok) errorMessage = "Invalid Admin credentials. Access denied."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("authenticate_admin_button")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unlock Portal", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDashboardContent(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val adminMetrics by viewModel.adminMetrics.collectAsState()
    val adminUsers by viewModel.adminUsers.collectAsState()
    val activationKeys by viewModel.activationKeys.collectAsState()
    val licenseKeys by viewModel.licenseKeys.collectAsState()
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    // Navigation sub-tabs inside dashboard: PAYOUTS, KEYS, USERS, METRICS
    var currentDashboardTab by remember { mutableStateOf("PAYOUTS") }

    // Admin Revenue Vault & Withdrawal State (UPI & Paytm)
    var withdrawMethod by remember { mutableStateOf("UPI") } // "UPI" or "PAYTM"
    var withdrawAccount by remember { mutableStateOf("adil@upi") }
    var withdrawAmountStr by remember { mutableStateOf("109") }
    var withdrawalFeedback by remember { mutableStateOf<String?>(null) }

    // Key Generator Form State for 'licenseKeys' collection
    var selectedPlan by remember { mutableStateOf(PlanType.PRO_MONTHLY) }
    var selectedValidityDays by remember { mutableIntStateOf(30) } // 7, 30, 90, 365, 0 (Lifetime)
    var generatedCode by remember { mutableStateOf(viewModel.generateAlphanumericKeyString(PlanType.PRO_MONTHLY)) }
    var customExportQuota by remember { mutableIntStateOf(40) }
    var keyClientNote by remember { mutableStateOf("") }
    var keyStatusFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, REDEEMED, EXPIRED

    // User Search and Filter
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedPlanFilter by remember { mutableStateOf("ALL") }
    var userToUpgrade by remember { mutableStateOf<AppUser?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Return & Security Status Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.setSection(EditorSection.EDIT) },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("exit_admin_dashboard_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Exit to Video Studio", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF142B1A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonGreen))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "FIRESTORE CLOUD CONNECTED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.lockAdminSession() },
                    modifier = Modifier.size(32.dp).testTag("lock_admin_session_button")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Lock Session", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Dashboard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Executive Admin Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(
                    text = "Real-time Google Cloud Firestore synchronization • Manage registered users & custom license keys",
                    fontSize = 11.sp,
                    color = NeonCyan
                )
            }

            // Quick Developer Boost Button
            Button(
                onClick = {
                    viewModel.redeemActivationCode("ADIL-MASTER-2026")
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("quick_boost_developer_button")
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Self Developer Boost", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top 4 KPI Metrics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminKpiCard(
                title = "Total Users",
                value = adminUsers.size.toString(),
                subText = "${adminUsers.count { it.plan == PlanType.PRO_MONTHLY }} Pro Members",
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            AdminKpiCard(
                title = "License Keys",
                value = activationKeys.size.toString(),
                subText = "${activationKeys.count { !it.isUsed }} Ready to Use",
                accentColor = NeonAmber,
                modifier = Modifier.weight(1f)
            )
            AdminKpiCard(
                title = "Monthly MRR",
                value = "₹89,420",
                subText = "+18.2% Growth",
                accentColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )
            AdminKpiCard(
                title = "Exports Today",
                value = adminMetrics.exportsToday.toString(),
                subText = "24h Rendering Pool",
                accentColor = NeonPink,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Tabs Switcher: [1. Revenue Vault & Payouts] [2. License Keys] [3. Registered Users] [4. SaaS Metrics]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardSectionTab(
                title = "Revenue Vault & Payouts (₹${adminMetrics.adminWalletBalanceInr.toInt()})",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = currentDashboardTab == "PAYOUTS",
                onClick = { currentDashboardTab = "PAYOUTS" }
            )
            DashboardSectionTab(
                title = "License Key Generator & Tracker (${activationKeys.size})",
                icon = Icons.Default.VpnKey,
                isSelected = currentDashboardTab == "KEYS",
                onClick = { currentDashboardTab = "KEYS" }
            )
            DashboardSectionTab(
                title = "Registered Users & Upgrades (${adminUsers.size})",
                icon = Icons.Default.People,
                isSelected = currentDashboardTab == "USERS",
                onClick = { currentDashboardTab = "USERS" }
            )
            DashboardSectionTab(
                title = "Executive Analytics Overview",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = currentDashboardTab == "METRICS",
                onClick = { currentDashboardTab = "METRICS" }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB 0: ADMIN REVENUE VAULT & WITHDRAWAL ENGINE (UPI & PAYTM)
        if (currentDashboardTab == "PAYOUTS") {
            // Main Revenue Vault Balance Card (Inspired by Executive SaaS overview)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_revenue_vault_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F3934)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ADMIN REVENUE VAULT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Subscription Funds Stay in Admin Account",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepViolet
                        ) {
                            Text(
                                text = "INSTANT SETTLEMENT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Withdrawable Balance Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Available Withdrawable Balance",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${adminMetrics.adminWalletBalanceInr.toInt()}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Subscriptions: ₹${adminMetrics.totalSubscriptionRevenueInr.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Total Withdrawn: ₹${adminMetrics.withdrawnRevenueInr.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonPink
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instant Withdrawal Terminal (UPI & Paytm)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_withdrawal_form_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Withdraw Revenue to UPI or Paytm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Transfer accumulated subscription revenue directly into your verified UPI ID or Paytm Wallet with zero gateway deductions.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode Selection: UPI vs Paytm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = {
                                withdrawMethod = "UPI"
                                if (!withdrawAccount.contains("@")) withdrawAccount = "adil@upi"
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (withdrawMethod == "UPI") DeepViolet else DeepSpace,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (withdrawMethod == "UPI") NeonCyan else SurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("select_upi_method_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = if (withdrawMethod == "UPI") NeonCyan else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⚡ Instant UPI Transfer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (withdrawMethod == "UPI") TextWhite else TextMuted
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                withdrawMethod = "PAYTM"
                                if (withdrawAccount.contains("@")) withdrawAccount = "+91 9876543210"
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (withdrawMethod == "PAYTM") DeepViolet else DeepSpace,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (withdrawMethod == "PAYTM") NeonPink else SurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("select_paytm_method_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = if (withdrawMethod == "PAYTM") NeonPink else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "📱 Paytm Wallet / Bank",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (withdrawMethod == "PAYTM") TextWhite else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination Account Input
                    OutlinedTextField(
                        value = withdrawAccount,
                        onValueChange = {
                            withdrawAccount = it
                            withdrawalFeedback = null
                        },
                        label = {
                            Text(
                                text = if (withdrawMethod == "UPI") "Recipient UPI ID / VPA Address" else "Paytm Registered Mobile Number",
                                fontSize = 11.sp
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (withdrawMethod == "UPI") "e.g. adil@upi or 9876543210@paytm" else "+91 9876543210",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedBorderColor = if (withdrawMethod == "UPI") NeonCyan else NeonPink,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_account_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Amount Input
                    OutlinedTextField(
                        value = withdrawAmountStr,
                        onValueChange = {
                            withdrawAmountStr = it.filter { ch -> ch.isDigit() || ch == '.' }
                            withdrawalFeedback = null
                        },
                        label = { Text("Withdrawal Amount (₹ INR)", fontSize = 11.sp) },
                        placeholder = { Text("Enter withdrawal amount in ₹", fontSize = 12.sp, color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedBorderColor = NeonAmber,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Amount Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(109, 500, 1000, 5000).forEach { quickAmt ->
                            Surface(
                                onClick = { withdrawAmountStr = quickAmt.toString() },
                                shape = RoundedCornerShape(8.dp),
                                color = DeepSpace,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                            ) {
                                Text(
                                    text = "₹$quickAmt",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                withdrawAmountStr = adminMetrics.adminWalletBalanceInr.toInt().toString()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = DeepViolet,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                        ) {
                            Text(
                                text = "Withdraw All (₹${adminMetrics.adminWalletBalanceInr.toInt()})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (withdrawalFeedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (withdrawalFeedback?.contains("success", ignoreCase = true) == true) Color(0xFF142B1A) else Color(0xFF3B151E),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (withdrawalFeedback?.contains("success", ignoreCase = true) == true) NeonGreen else NeonRose
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = withdrawalFeedback ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (withdrawalFeedback?.contains("success", ignoreCase = true) == true) NeonGreen else NeonRose,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Withdrawal Action Button
                    Button(
                        onClick = {
                            val amt = withdrawAmountStr.toDoubleOrNull() ?: 0.0
                            val (success, msg) = viewModel.withdrawAdminEarnings(amt, withdrawMethod, withdrawAccount)
                            withdrawalFeedback = msg
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (withdrawMethod == "UPI") NeonCyan else NeonPink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_withdraw_submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Withdraw to $withdrawMethod ($withdrawAccount)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recent Settlements History Table
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_payout_history_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Withdrawal Settlements",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DeepViolet
                        ) {
                            Text(
                                text = "${adminMetrics.recentWithdrawals.size} SETTLEMENTS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (adminMetrics.recentWithdrawals.isEmpty()) {
                        Text(
                            text = "No withdrawals processed yet. Funds are safe in Admin Revenue Vault.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        adminMetrics.recentWithdrawals.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (item.method == "UPI") Color(0xFF0F3934) else Color(0xFF381432)
                                            ) {
                                                Text(
                                                    text = item.method,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (item.method == "UPI") NeonCyan else NeonPink,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item.targetAccount,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Ref: ${item.id} • Instant Admin Settlement",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMuted
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${item.amountInr.toInt()}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NeonGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "✓ ${item.status}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB 1: LICENSE KEY GENERATOR & TRACKER
        if (currentDashboardTab == "KEYS") {
            // Key Generator Form Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber),
                modifier = Modifier.fillMaxWidth().testTag("key_generator_section")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(Color(0xFF332000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GENERATE ALPHANUMERIC LICENSE KEY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonAmber,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Generates secure alphanumeric keys saved to 'licenseKeys' collection in Firestore",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Code Input with Regenerate Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = generatedCode,
                            onValueChange = { generatedCode = it.uppercase() },
                            label = { Text("Alphanumeric Key (Auto-Generated)", fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DeepSpace,
                                unfocusedContainerColor = DeepSpace,
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.5f).testTag("generator_code_input")
                        )

                        IconButton(
                            onClick = {
                                generatedCode = viewModel.generateAlphanumericKeyString(selectedPlan)
                            },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate Alphanumeric Key", tint = NeonCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Plan Tier Selection (Free, Pro, Admin)
                    Text(
                        text = "1. Associated Plan Type:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("Free", PlanType.FREE, 2),
                            Triple("Pro", PlanType.PRO_MONTHLY, 40),
                            Triple("Admin", PlanType.ADMIN, 9999)
                        ).forEach { (label, plan, defLimit) ->
                            val isSel = selectedPlan == plan
                            Surface(
                                onClick = {
                                    selectedPlan = plan
                                    customExportQuota = defLimit
                                    generatedCode = viewModel.generateAlphanumericKeyString(plan)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) DeepViolet else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) TextWhite else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Associated Expiry Date Selection
                    val (previewExpiryDate, _) = viewModel.licenseKeyService.calculateExpiry(selectedValidityDays)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Associated Expiry Date:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepSpace,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                        ) {
                            Text(
                                text = "Expires: $previewExpiryDate",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Pair("7 Days", 7),
                            Pair("30 Days", 30),
                            Pair("90 Days", 90),
                            Pair("1 Year", 365),
                            Pair("Lifetime", 0)
                        ).forEach { (label, days) ->
                            val isSel = selectedValidityDays == days
                            Surface(
                                onClick = { selectedValidityDays = days },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NeonAmber else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color.White else SurfaceBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else TextWhite
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recipient Label & Note
                    OutlinedTextField(
                        value = keyClientNote,
                        onValueChange = { keyClientNote = it },
                        label = { Text("Client Label / Note (Optional)", fontSize = 10.sp) },
                        placeholder = { Text("e.g. Studio VIP User, Special Upgrade", fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Daily Export Quota Allocation
                    Text(
                        text = "3. Daily Export Quota:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(2, 40, 100, 250, 500, 9999).forEach { quota ->
                            val isSel = customExportQuota == quota
                            Surface(
                                onClick = {
                                    customExportQuota = quota
                                    if (quota == 9999) selectedPlan = PlanType.ADMIN
                                    else if (quota == 2) selectedPlan = PlanType.FREE
                                    else selectedPlan = PlanType.PRO_MONTHLY
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NeonCyan else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color.White else SurfaceBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = if (quota == 9999) "Unlimited" else "$quota",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else TextWhite
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generate Button to save into 'licenseKeys' collection
                    Button(
                        onClick = {
                            val note = if (keyClientNote.isBlank()) "${selectedPlan.title} Key ($previewExpiryDate)" else keyClientNote
                            viewModel.createLicenseKey(
                                planType = selectedPlan,
                                validityDays = selectedValidityDays,
                                customKey = generatedCode,
                                customLimit = customExportQuota,
                                label = note
                            )
                            generatedCode = viewModel.generateAlphanumericKeyString(selectedPlan)
                            keyClientNote = ""
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_generate_key_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save to Firestore Collection 'licenseKeys' ($customExportQuota Exports)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // License Key Tracking Table Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Firestore 'licenseKeys' Collection (${licenseKeys.size} Keys)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Real-time records from cloud database with plan types and expiry dates",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // Filter Chips: ALL, ACTIVE, REDEEMED
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("ALL", "ACTIVE", "REDEEMED").forEach { filter ->
                                val isSel = keyStatusFilter == filter
                                Surface(
                                    onClick = { keyStatusFilter = filter },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) DeepViolet else SurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else SurfaceBorder)
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) NeonCyan else TextMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredKeys = licenseKeys.filter {
                        when (keyStatusFilter) {
                            "ACTIVE" -> !it.isUsed && !it.isExpired
                            "REDEEMED" -> it.isUsed
                            else -> true
                        }
                    }

                    if (filteredKeys.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No license keys match the selected filter.", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        filteredKeys.forEach { keyItem ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = keyItem.key,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = NeonCyan,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = when (keyItem.planType) {
                                                    PlanType.ADMIN -> DeepViolet
                                                    PlanType.PRO_QUARTERLY -> Color(0xFF381432)
                                                    PlanType.PRO_MONTHLY -> Color(0xFF0F3E4D)
                                                    PlanType.FREE -> Color(0xFF332000)
                                                }
                                            ) {
                                                Text(
                                                    text = "${keyItem.planType.name} • ${keyItem.dailyExportLimit} EXPORTS/DAY",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (keyItem.planType) {
                                                        PlanType.ADMIN -> NeonPink
                                                        PlanType.PRO_QUARTERLY -> NeonPink
                                                        PlanType.PRO_MONTHLY -> NeonCyan
                                                        PlanType.FREE -> NeonAmber
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "${keyItem.label} • Expiry: ${keyItem.expiryDate} • Status: ${keyItem.statusText}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    // Action buttons: Status Pill, Copy & Delete
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when {
                                                keyItem.isUsed -> Color(0xFF331111)
                                                keyItem.isExpired -> Color(0xFF332200)
                                                else -> Color(0xFF0E331A)
                                            }
                                        ) {
                                            Text(
                                                text = keyItem.statusText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    keyItem.isUsed -> NeonRose
                                                    keyItem.isExpired -> NeonAmber
                                                    else -> NeonGreen
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(keyItem.key))
                                            },
                                            modifier = Modifier.size(32.dp).testTag("copy_key_${keyItem.key}")
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextWhite, modifier = Modifier.size(15.dp))
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteLicenseKey(keyItem.key) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRose.copy(alpha = 0.8f), modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        // TAB 2: REGISTERED USERS & DIRECT UPGRADES
        if (currentDashboardTab == "USERS") {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth().testTag("registered_users_section")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Registered User Accounts (${adminUsers.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Search, modify limits, grant keys, and suspend accounts",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = userSearchQuery,
                        onValueChange = { userSearchQuery = it },
                        placeholder = { Text("Search users by name or email...", fontSize = 11.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val filteredUsers = adminUsers.filter {
                        it.name.contains(userSearchQuery, ignoreCase = true) ||
                        it.email.contains(userSearchQuery, ignoreCase = true)
                    }

                    filteredUsers.forEach { user ->
                        UserManagementCard(
                            user = user,
                            availableKeys = activationKeys.filter { !it.isUsed },
                            onToggleBlock = { viewModel.toggleUserBlock(user.id) },
                            onUpdateQuota = { newQuota -> viewModel.updateUserQuota(user.id, newQuota) },
                            onAssignKey = { key -> viewModel.assignKeyToUser(user.id, key) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // TAB 3: EXECUTIVE SAAS METRICS OVERVIEW
        if (currentDashboardTab == "METRICS") {
            // User Growth Analytics Chart
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "User Acquisition & Retention Analytics",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bar Chart
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        val months = listOf(35, 45, 55, 60, 48, 70, 85, 90, 80, 95, 110, 125)
                        val w = size.width
                        val h = size.height
                        val barWidth = (w / (months.size * 2f))

                        months.forEachIndexed { i, value ->
                            val x = i * (w / months.size) + 8f
                            val barH = (value / 130f) * h

                            drawRoundRect(
                                color = Color(0xFF2563EB),
                                topLeft = androidx.compose.ui.geometry.Offset(x, h - barH * 0.7f),
                                size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barH * 0.7f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                            drawRoundRect(
                                color = NeonCyan,
                                topLeft = androidx.compose.ui.geometry.Offset(x + barWidth * 0.9f, h - barH),
                                size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").forEach {
                            Text(text = it, fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // System Health & Audit Logs
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Security & Rendering Logs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    adminMetrics.recentLogs.forEach { log ->
                        Text(
                            text = log,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun UserManagementCard(
    user: AppUser,
    availableKeys: List<ActivationKey>,
    onToggleBlock: () -> Unit,
    onUpdateQuota: (Int) -> Unit,
    onAssignKey: (String) -> Unit
) {
    var showUpgradeRow by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.isBlocked) NeonRose else SurfaceBorder
        ),
        modifier = Modifier.fillMaxWidth().testTag("user_row_${user.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(if (user.plan == PlanType.ADMIN) DeepViolet else Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (user.plan == PlanType.ADMIN) NeonPink else NeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user.plan == PlanType.ADMIN) DeepViolet else Color(0xFF0F3E4D)
                            ) {
                                Text(
                                    text = user.plan.title,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.plan == PlanType.ADMIN) NeonPink else NeonCyan,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${user.email} • Joined: ${user.joinedDate}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Status & Block Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (user.isBlocked) Color(0xFF331111) else Color(0xFF0F331A)
                    ) {
                        Text(
                            text = if (user.isBlocked) "SUSPENDED" else "ACTIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (user.isBlocked) NeonRose else NeonGreen,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onToggleBlock, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = "Toggle Suspend",
                            tint = if (user.isBlocked) NeonGreen else NeonRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Quota Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Quota: ${user.exportsToday} / ${user.allocatedLimit} exports used",
                    fontSize = 10.sp,
                    color = TextWhite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (user.assignedKeyCode != null) "Key: ${user.assignedKeyCode}" else "Default Tier",
                    fontSize = 9.sp,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val progress = (user.exportsToday.toFloat() / user.allocatedLimit.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (user.isBlocked) NeonRose else NeonCyan,
                trackColor = DeepSpace
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User Upgrade Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { showUpgradeRow = !showUpgradeRow },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSpace),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grant Upgrade Key", fontSize = 10.sp, color = NeonAmber, fontWeight = FontWeight.Bold)
                }

                // Quick Quota Boosts
                listOf(50, 100, 9999).forEach { quota ->
                    OutlinedButton(
                        onClick = { onUpdateQuota(quota) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (quota == 9999) "Unlimited" else "+$quota",
                            fontSize = 10.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expanded Key Assignment Dropdown/Selector
            AnimatedVisibility(visible = showUpgradeRow) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(DeepSpace, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Select Available Key to Grant User:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (availableKeys.isEmpty()) {
                        Text("No unclaimed keys available. Generate one above first.", fontSize = 10.sp, color = TextMuted)
                    } else {
                        availableKeys.take(3).forEach { key ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAssignKey(key.code)
                                        showUpgradeRow = false
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${key.code} (${key.dailyExportLimit} exports)",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonCyan
                                )
                                Text("Assign Key →", fontSize = 10.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    subText: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun DashboardSectionTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DeepViolet else SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else SurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) NeonCyan else TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextWhite else TextMuted
            )
        }
    }
}
