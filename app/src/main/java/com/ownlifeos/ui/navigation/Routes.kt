package com.ownlifeos.ui.navigation

import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Flare
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

sealed class OwnLifeRoute(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : OwnLifeRoute("home", "홈", Icons.Outlined.Dashboard)
    data object Morning : OwnLifeRoute("morning", "체크인", Icons.Outlined.WbSunny)
    data object Tasks : OwnLifeRoute("tasks", "작업", Icons.Outlined.Checklist)
    data object Evening : OwnLifeRoute("evening", "회고", Icons.Outlined.NightsStay)
    data object Report : OwnLifeRoute("report", "리포트", Icons.Outlined.QueryStats)
    data object Decision : OwnLifeRoute("decision", "판단", Icons.AutoMirrored.Outlined.Rule)
    data object Forecast : OwnLifeRoute("forecast", "전략", Icons.Outlined.Timeline)
    data object Simulation : OwnLifeRoute("simulation", "판단", Icons.Outlined.Flare)
    data object Health : OwnLifeRoute("health", "건강도", Icons.Outlined.HealthAndSafety)
    data object Pattern : OwnLifeRoute("pattern", "패턴", Icons.Outlined.QueryStats)
}

val mainRoutes = listOf(
    OwnLifeRoute.Home,
    OwnLifeRoute.Morning,
    OwnLifeRoute.Tasks,
    OwnLifeRoute.Simulation
)

val allRoutes = mainRoutes + listOf(
    OwnLifeRoute.Forecast,
    OwnLifeRoute.Evening,
    OwnLifeRoute.Report,
    OwnLifeRoute.Decision,
    OwnLifeRoute.Health,
    OwnLifeRoute.Pattern
)
