package com.ownlifeos

import androidx.compose.runtime.Composable
import com.ownlifeos.ui.AppViewModelFactory
import com.ownlifeos.ui.navigation.OwnLifeNavHost

@Composable
fun OwnLifeOsApp(factory: AppViewModelFactory) {
    OwnLifeNavHost(factory = factory)
}
