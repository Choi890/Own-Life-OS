package com.ownlifeos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ownlifeos.ui.AppViewModelFactory
import com.ownlifeos.ui.components.OwnLifeTopBar
import com.ownlifeos.ui.screens.decision.DecisionCheckScreen
import com.ownlifeos.ui.screens.decision.DecisionCheckViewModel
import com.ownlifeos.ui.screens.evening.EveningReviewScreen
import com.ownlifeos.ui.screens.evening.EveningReviewViewModel
import com.ownlifeos.ui.screens.forecast.ForecastScreen
import com.ownlifeos.ui.screens.forecast.ForecastViewModel
import com.ownlifeos.ui.screens.health.SystemHealthScreen
import com.ownlifeos.ui.screens.health.SystemHealthViewModel
import com.ownlifeos.ui.screens.home.HomeScreen
import com.ownlifeos.ui.screens.home.HomeViewModel
import com.ownlifeos.ui.screens.morning.MorningCheckInScreen
import com.ownlifeos.ui.screens.morning.MorningCheckInViewModel
import com.ownlifeos.ui.screens.pattern.OperatingPatternScreen
import com.ownlifeos.ui.screens.pattern.OperatingPatternViewModel
import com.ownlifeos.ui.screens.report.WeeklyReportScreen
import com.ownlifeos.ui.screens.report.WeeklyReportViewModel
import com.ownlifeos.ui.screens.simulation.LifeSimulationScreen
import com.ownlifeos.ui.screens.simulation.LifeSimulationViewModel
import com.ownlifeos.ui.screens.tasks.TaskQueueScreen
import com.ownlifeos.ui.screens.tasks.TaskQueueViewModel

@Composable
fun OwnLifeNavHost(
    factory: AppViewModelFactory,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: OwnLifeRoute.Home.route
    val title = allRoutes.firstOrNull { it.route == currentRoute }?.let { "Own Life OS · ${it.label}" }
        ?: "Own Life OS"

    Scaffold(
        modifier = modifier,
        topBar = { OwnLifeTopBar(title = title) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                mainRoutes.forEach { route ->
                    NavigationBarItem(
                        selected = currentRoute == route.route,
                        onClick = {
                            if (currentRoute != route.route) {
                                navController.navigate(route.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = route.icon,
                                contentDescription = route.label
                            )
                        },
                        label = { Text(route.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OwnLifeRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(OwnLifeRoute.Home.route) {
                val viewModel: HomeViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onOpenMorning = { navController.navigate(OwnLifeRoute.Morning.route) },
                    onOpenTasks = { navController.navigate(OwnLifeRoute.Tasks.route) },
                    onOpenEvening = { navController.navigate(OwnLifeRoute.Evening.route) },
                    onOpenReport = { navController.navigate(OwnLifeRoute.Report.route) },
                    onOpenForecast = { navController.navigate(OwnLifeRoute.Forecast.route) },
                    onOpenSimulation = { navController.navigate(OwnLifeRoute.Simulation.route) },
                    onOpenHealth = { navController.navigate(OwnLifeRoute.Health.route) },
                    onOpenPattern = { navController.navigate(OwnLifeRoute.Pattern.route) },
                    onConfirmAutoCheckIn = viewModel::confirmEstimatedCheckIn,
                    onRecordStrategyFeedback = viewModel::recordStrategyFeedback
                )
            }

            composable(OwnLifeRoute.Morning.route) {
                val viewModel: MorningCheckInViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MorningCheckInScreen(
                    uiState = uiState,
                    onSleepChange = viewModel::updateSleepHours,
                    onMoodChange = viewModel::updateMood,
                    onBodyChange = viewModel::updateBodyCondition,
                    onBurdenChange = viewModel::updateBurdenLevel,
                    onMemoChange = viewModel::updateMemo,
                    onOpenHome = { navController.navigate(OwnLifeRoute.Home.route) },
                    onSave = viewModel::save
                )
            }

            composable(OwnLifeRoute.Tasks.route) {
                val viewModel: TaskQueueViewModel = viewModel(factory = factory)
                val tasks by viewModel.tasks.collectAsStateWithLifecycle()
                val rankedTasks by viewModel.rankedTasks.collectAsStateWithLifecycle()
                val formState by viewModel.formState.collectAsStateWithLifecycle()
                TaskQueueScreen(
                    tasks = tasks,
                    rankedTasks = rankedTasks,
                    formState = formState,
                    onTitleChange = viewModel::updateTitle,
                    onImportanceChange = viewModel::updateImportance,
                    onEnergyChange = viewModel::updateEnergyCost,
                    onFocusNeedChange = viewModel::updateFocusNeed,
                    onDeadlineChange = viewModel::updateDeadlineDate,
                    onAddTask = viewModel::addTask,
                    onStatusChange = viewModel::updateStatus,
                    onDefer = viewModel::defer,
                    onDelete = viewModel::delete
                )
            }

            composable(OwnLifeRoute.Evening.route) {
                val viewModel: EveningReviewViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val pendingTasks by viewModel.pendingTasks.collectAsStateWithLifecycle()
                EveningReviewScreen(
                    uiState = uiState,
                    pendingTasks = pendingTasks,
                    onGoodThingsChange = viewModel::updateGoodThings,
                    onErrorLogsChange = viewModel::updateErrorLogs,
                    onCarryOverChange = viewModel::updateCarryOver,
                    onUsePendingTasks = viewModel::usePendingTasksAsCarryOver,
                    onApplyDraft = viewModel::applyDraft,
                    onSave = viewModel::save
                )
            }

            composable(OwnLifeRoute.Report.route) {
                val viewModel: WeeklyReportViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                WeeklyReportScreen(uiState = uiState)
            }

            composable(OwnLifeRoute.Decision.route) {
                val viewModel: DecisionCheckViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                DecisionCheckScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    onEnergyChange = viewModel::updateEnergy,
                    onUrgencyChange = viewModel::updateUrgency,
                    onReversibilityChange = viewModel::updateReversibility,
                    onImportanceChange = viewModel::updateImportance,
                    onSave = viewModel::saveDecision
                )
            }

            composable(OwnLifeRoute.Forecast.route) {
                val viewModel: ForecastViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ForecastScreen(uiState = uiState)
            }

            composable(OwnLifeRoute.Simulation.route) {
                val viewModel: LifeSimulationViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LifeSimulationScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    onEnergyChange = viewModel::updateEnergy,
                    onDurationChange = viewModel::updateDuration,
                    onUrgencyChange = viewModel::updateUrgency,
                    onReversibilityChange = viewModel::updateReversibility,
                    onImportanceChange = viewModel::updateImportance,
                    onCheck = viewModel::checkSimulation,
                    onSave = viewModel::saveSimulation
                )
            }

            composable(OwnLifeRoute.Health.route) {
                val viewModel: SystemHealthViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SystemHealthScreen(uiState = uiState)
            }

            composable(OwnLifeRoute.Pattern.route) {
                val viewModel: OperatingPatternViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                OperatingPatternScreen(uiState = uiState)
            }
        }
    }
}
