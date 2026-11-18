package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.LanguageTogglePill
import com.example.ui.components.OnboardingDisclaimerDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AppointmentPrepScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EmergencyGuidanceScreen
import com.example.ui.screens.HealthEducationScreen
import com.example.ui.screens.HealthJournalScreen
import com.example.ui.screens.MedicationManagerScreen
import com.example.ui.screens.ReportAnalyzerScreen
import com.example.ui.screens.Routes
import com.example.ui.screens.SettingsPrivacyScreen
import com.example.ui.screens.SymptomCheckerScreen
import com.example.ui.state.HealthMateViewModel
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HealthMateTheme
import kotlinx.coroutines.launch

private data class NavDestinationItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val isEmergency: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMateApp(
    viewModel: HealthMateViewModel = viewModel()
) {
    val context = LocalContext.current
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val medicationLogs by viewModel.medicationLogs.collectAsStateWithLifecycle()
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()
    val savedReports by viewModel.savedReports.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatStreaming by viewModel.isChatStreaming.collectAsStateWithLifecycle()
    val chatError by viewModel.chatError.collectAsStateWithLifecycle()

    val symptomState by viewModel.symptomState.collectAsStateWithLifecycle()
    val localEmergencyDetected by viewModel.localEmergencyDetected.collectAsStateWithLifecycle()

    val reportBitmaps by viewModel.reportBitmaps.collectAsStateWithLifecycle()
    val reportFileName by viewModel.reportFileName.collectAsStateWithLifecycle()
    val reportLoadError by viewModel.reportLoadError.collectAsStateWithLifecycle()
    val reportAiState by viewModel.reportAiState.collectAsStateWithLifecycle()

    val appointmentAiLoadingId by viewModel.appointmentAiLoadingId.collectAsStateWithLifecycle()
    val appointmentAiError by viewModel.appointmentAiError.collectAsStateWithLifecycle()
    val educationSummaries by viewModel.educationSummaries.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.DASHBOARD

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        val msg = toastMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    val allDestinations = remember {
        listOf(
            NavDestinationItem(Routes.DASHBOARD, "Home Dashboard", Icons.Default.Home),
            NavDestinationItem(Routes.ASSISTANT, "AI Health Assistant", Icons.AutoMirrored.Filled.Chat),
            NavDestinationItem(Routes.SYMPTOM_CHECKER, "Symptom Checker", Icons.Default.MonitorHeart),
            NavDestinationItem(Routes.REPORT_ANALYZER, "Report Analyzer", Icons.Default.Description),
            NavDestinationItem(Routes.MEDICATIONS, "Medication Manager", Icons.Default.Medication),
            NavDestinationItem(Routes.JOURNAL, "Health Journal", Icons.AutoMirrored.Filled.Assignment),
            NavDestinationItem(Routes.APPOINTMENTS, "Appointment Prep", Icons.Default.CalendarMonth),
            NavDestinationItem(Routes.EDUCATION, "Health Education", Icons.Default.MenuBook),
            NavDestinationItem(Routes.EMERGENCY, "Emergency Guidance", Icons.Default.Emergency, isEmergency = true),
            NavDestinationItem(Routes.SETTINGS_PRIVACY, "Settings & Privacy", Icons.Default.Settings),
            NavDestinationItem(Routes.ABOUT, "About HealthMate AI", Icons.Default.Info)
        )
    }

    val bottomDestinations = remember {
        listOf(
            NavDestinationItem(Routes.DASHBOARD, "Home", Icons.Default.Home),
            NavDestinationItem(Routes.ASSISTANT, "Assistant", Icons.AutoMirrored.Filled.Chat),
            NavDestinationItem(Routes.SYMPTOM_CHECKER, "Symptoms", Icons.Default.MonitorHeart),
            NavDestinationItem(Routes.REPORT_ANALYZER, "Reports", Icons.Default.Description),
            NavDestinationItem(Routes.MEDICATIONS, "Meds", Icons.Default.Medication)
        )
    }

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        scope.launch { drawerState.close() }
    }

    HealthMateTheme(darkTheme = preferences.isDarkMode) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 720.dp

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.width(290.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "HealthMate AI",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your Intelligent Health Companion",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(10.dp))
                            allDestinations.forEach { dest ->
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = dest.label,
                                            color = if (dest.isEmergency) EmergencyRed else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    selected = currentRoute == dest.route,
                                    onClick = { navigateTo(dest.route) },
                                    icon = {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = dest.label,
                                            tint = if (dest.isEmergency) EmergencyRed else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                        .testTag("drawer_item_${dest.route}")
                                )
                            }
                        }
                    }
                }
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (isExpandedScreen) {
                        NavigationRail(
                            header = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Open all pages")
                                }
                            }
                        ) {
                            bottomDestinations.forEach { item ->
                                NavigationRailItem(
                                    selected = currentRoute == item.route,
                                    onClick = { navigateTo(item.route) },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) }
                                )
                            }
                            NavigationRailItem(
                                selected = currentRoute == Routes.EMERGENCY,
                                onClick = { navigateTo(Routes.EMERGENCY) },
                                icon = { Icon(Icons.Default.Emergency, contentDescription = "Emergency", tint = EmergencyRed) },
                                label = { Text("Emergency", color = EmergencyRed) }
                            )
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = allDestinations.firstOrNull { it.route == currentRoute }?.label
                                            ?: "HealthMate AI",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("nav_drawer_button")
                                    ) {
                                        Icon(Icons.Default.Menu, contentDescription = "Open navigation menu")
                                    }
                                },
                                actions = {
                                    LanguageTogglePill(
                                        language = preferences.language,
                                        onToggle = viewModel::toggleLanguage
                                    )
                                    IconButton(
                                        onClick = { navigateTo(Routes.EMERGENCY) },
                                        modifier = Modifier.testTag("topbar_emergency_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Emergency,
                                            contentDescription = "Emergency Guidance",
                                            tint = EmergencyRed
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            if (!isExpandedScreen) {
                                NavigationBar {
                                    bottomDestinations.forEach { item ->
                                        NavigationBarItem(
                                            selected = currentRoute == item.route,
                                            onClick = { navigateTo(item.route) },
                                            icon = { Icon(item.icon, contentDescription = item.label) },
                                            label = { Text(item.label) },
                                            modifier = Modifier.testTag("bottom_nav_${item.route}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Routes.DASHBOARD,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Routes.DASHBOARD) {
                                DashboardScreen(
                                    preferences = preferences,
                                    medications = medications,
                                    medicationLogs = medicationLogs,
                                    journalEntries = journalEntries,
                                    appointments = appointments,
                                    onNavigate = ::navigateTo,
                                    onMarkMedTaken = { med -> viewModel.logMedicationStatus(med, "TAKEN") },
                                    onLoadDemoData = viewModel::loadSampleDemoData
                                )
                            }
                            composable(Routes.ASSISTANT) {
                                AssistantScreen(
                                    language = preferences.language,
                                    messages = chatMessages,
                                    isStreaming = isChatStreaming,
                                    errorMessage = chatError,
                                    onInitChat = { viewModel.ensureChatInitialized(preferences.language) },
                                    onSendMessage = viewModel::sendChatMessage,
                                    onResetChat = viewModel::resetChat
                                )
                            }
                            composable(Routes.SYMPTOM_CHECKER) {
                                SymptomCheckerScreen(
                                    language = preferences.language,
                                    uiState = symptomState,
                                    localEmergencyDetected = localEmergencyDetected,
                                    onCheckRedFlags = viewModel::checkLocalRedFlags,
                                    onRunAssessment = viewModel::runSymptomAssessment,
                                    onReset = viewModel::resetSymptomAssessment,
                                    onOpenEmergency = { navigateTo(Routes.EMERGENCY) }
                                )
                            }
                            composable(Routes.REPORT_ANALYZER) {
                                ReportAnalyzerScreen(
                                    language = preferences.language,
                                    bitmaps = reportBitmaps,
                                    fileName = reportFileName,
                                    loadError = reportLoadError,
                                    uiState = reportAiState,
                                    savedReports = savedReports,
                                    onSelectFile = { uri, label -> viewModel.loadReportFile(context, uri, label) },
                                    onAnalyze = viewModel::analyzeLoadedReport,
                                    onSaveSummary = viewModel::saveCurrentReportAnalysis,
                                    onDeleteSavedReport = viewModel::deleteSavedReport
                                )
                            }
                            composable(Routes.MEDICATIONS) {
                                MedicationManagerScreen(
                                    language = preferences.language,
                                    medications = medications,
                                    logs = medicationLogs,
                                    onSaveMedication = viewModel::saveMedication,
                                    onDeleteMedication = viewModel::deleteMedication,
                                    onLogStatus = viewModel::logMedicationStatus
                                )
                            }
                            composable(Routes.JOURNAL) {
                                HealthJournalScreen(
                                    language = preferences.language,
                                    entries = journalEntries,
                                    onSaveEntry = viewModel::saveJournalEntry,
                                    onDeleteEntry = viewModel::deleteJournalEntry
                                )
                            }
                            composable(Routes.APPOINTMENTS) {
                                AppointmentPrepScreen(
                                    language = preferences.language,
                                    appointments = appointments,
                                    aiLoadingId = appointmentAiLoadingId,
                                    aiError = appointmentAiError,
                                    onSaveAppointment = viewModel::saveAppointment,
                                    onDeleteAppointment = viewModel::deleteAppointment,
                                    onToggleDocument = viewModel::toggleAppointmentDocument,
                                    onGenerateAiQuestions = viewModel::generateAiQuestionsForAppointment
                                )
                            }
                            composable(Routes.EDUCATION) {
                                HealthEducationScreen(
                                    language = preferences.language,
                                    aiSummaries = educationSummaries,
                                    onSummarizeArticle = viewModel::summarizeArticle
                                )
                            }
                            composable(Routes.EMERGENCY) {
                                EmergencyGuidanceScreen(language = preferences.language)
                            }
                            composable(Routes.SETTINGS_PRIVACY) {
                                SettingsPrivacyScreen(
                                    preferences = preferences,
                                    onToggleDarkMode = viewModel::setDarkMode,
                                    onSelectLanguage = viewModel::setLanguage,
                                    onSaveDisplayName = viewModel::setDisplayName,
                                    onLoadDemoData = viewModel::loadSampleDemoData,
                                    onDeleteAllData = viewModel::deleteAllUserData
                                )
                            }
                            composable(Routes.ABOUT) {
                                AboutScreen(language = preferences.language)
                            }
                        }
                    }
                }
            }

            if (!preferences.hasAcceptedDisclaimer) {
                OnboardingDisclaimerDialog(
                    onAccept = viewModel::acceptDisclaimer,
                    onAcceptWithDemoData = {
                        viewModel.acceptDisclaimer()
                        viewModel.loadSampleDemoData()
                    }
                )
            }
        }
    }
}
