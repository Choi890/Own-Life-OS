package com.ownlifeos

import androidx.compose.runtime.Composable
import com.ownlifeos.ui.AppViewModelFactory
import com.ownlifeos.ui.navigation.OwnLifeNavHost

@Composable
fun OwnLifeOsApp(factory: AppViewModelFactory) {
    // Keep the root composable thin; feature ownership lives inside the navigation destinations.
    OwnLifeNavHost(factory = factory)
}
