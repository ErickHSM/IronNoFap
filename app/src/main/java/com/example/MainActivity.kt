package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.model.DayStatus
import com.example.ui.components.AccountDialog
import com.example.ui.components.AddJournalDialog
import com.example.ui.components.AddPunishmentDialog
import com.example.ui.components.DayDetailBottomSheet
import com.example.ui.components.EmergencyBreathingDialog
import com.example.ui.components.RelapseDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PunishmentScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.NoFapViewModel
import com.example.util.DateUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: NoFapViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    // Silent background auto sign-in attempt on startup
    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { user -> viewModel.onUserSignedIn(user) },
            onUnauthenticated = { /* stay in local offline mode */ },
            scope = coroutineScope
        )
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val todayStr = DateUtils.getTodayString()
    val todayRecord = uiState.records[todayStr]

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Início / Dashboard
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Início"
                        )
                    },
                    label = {
                        Text(
                            text = "Início",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003816),
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("dashboard_tab")
                )

                // Tab 1: Calendário (Visual Green/Red/Missed)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Calendário"
                        )
                    },
                    label = {
                        Text(
                            text = "Calendário",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003816),
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("calendar_tab")
                )

                // Tab 2: Estatísticas
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.QueryStats else Icons.Outlined.QueryStats,
                            contentDescription = "Estatísticas"
                        )
                    },
                    label = {
                        Text(
                            text = "Progresso",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003816),
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("stats_tab")
                )

                // Tab 3: Sistema de Punição
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        if (uiState.stats.pendingPenaltiesCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = CrimsonAlert,
                                        contentColor = Color.White
                                    ) {
                                        Text("${uiState.stats.pendingPenaltiesCount}")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 3) Icons.Default.Gavel else Icons.Outlined.Gavel,
                                    contentDescription = "Punições"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Default.Gavel else Icons.Outlined.Gavel,
                                contentDescription = "Punições"
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Punições",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003816),
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("punishment_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    stats = uiState.stats,
                    todayRecord = todayRecord,
                    dailyMissions = uiState.dailyMissions,
                    currentUser = uiState.currentUser,
                    onMarkTodayClean = { viewModel.markTodayClean() },
                    onOpenRelapseDialog = { viewModel.openRelapseDialog() },
                    onOpenEmergencySOS = { viewModel.toggleEmergencyMode(true) },
                    onOpenAccountDialog = { viewModel.openAccountDialog() },
                    onOpenJournalDialog = { viewModel.openJournalDialog() },
                    onToggleWorkout = { viewModel.toggleWorkoutMission(it) },
                    onToggleColdShower = { viewModel.toggleColdShowerMission(it) },
                    onToggleScreenDetox = { viewModel.toggleScreenDetoxMission(it) },
                    onNavigateToPunishments = { selectedTab = 3 },
                    onNavigateToCalendar = { selectedTab = 1 }
                )
                1 -> CalendarScreen(
                    records = uiState.records,
                    selectedMonth = uiState.selectedMonth,
                    selectedYear = uiState.selectedYear,
                    onChangeMonth = { delta -> viewModel.changeMonth(delta) },
                    onResetCurrentMonth = { viewModel.resetToCurrentMonth() },
                    onSelectDate = { date -> viewModel.selectDate(date) }
                )
                2 -> StatsScreen(
                    stats = uiState.stats,
                    records = uiState.records
                )
                3 -> PunishmentScreen(
                    punishments = uiState.punishments,
                    onCompletePunishment = { id, completed -> viewModel.completePunishment(id, completed) },
                    onDeletePunishment = { id -> viewModel.deletePunishment(id) },
                    onOpenAddDialog = { viewModel.openAddPunishmentDialog() },
                    onAddTemplate = { template ->
                        viewModel.addCustomPunishment(
                            template.title,
                            template.description,
                            template.category,
                            template.intensity
                        )
                    }
                )
            }
        }
    }

    // Dialogs & Sheets
    if (uiState.showRelapseDialog) {
        RelapseDialog(
            onDismiss = { viewModel.dismissRelapseDialog() },
            onConfirmRelapse = { note, trigger, penalty ->
                viewModel.markTodayRelapse(note, trigger, penalty)
            }
        )
    }

    if (uiState.showDayDetailSheet) {
        DayDetailBottomSheet(
            date = uiState.selectedDate,
            record = uiState.selectedRecord,
            onDismiss = { viewModel.dismissDayDetailSheet() },
            onSave = { status, note, urgeLevel, trigger ->
                viewModel.updateSelectedDateRecord(uiState.selectedDate, status, note, urgeLevel, trigger)
            }
        )
    }

    if (uiState.showAddPunishmentDialog) {
        AddPunishmentDialog(
            onDismiss = { viewModel.dismissAddPunishmentDialog() },
            onAdd = { title, desc, cat, inten ->
                viewModel.addCustomPunishment(title, desc, cat, inten)
            }
        )
    }

    if (uiState.showEmergencyMode) {
        EmergencyBreathingDialog(
            onDismiss = { viewModel.toggleEmergencyMode(false) }
        )
    }

    if (uiState.showJournalDialog) {
        AddJournalDialog(
            onDismiss = { viewModel.dismissJournalDialog() },
            onSubmit = { mood, energy, reflection, gratitude ->
                viewModel.addJournalEntry(mood, energy, reflection, gratitude)
            }
        )
    }

    if (uiState.showAccountDialog) {
        AccountDialog(
            currentUser = uiState.currentUser,
            onDismiss = { viewModel.dismissAccountDialog() },
            onAuthSuccess = { user -> viewModel.onUserSignedIn(user) },
            onSignOut = { viewModel.onUserSignedOut() }
        )
    }
}
