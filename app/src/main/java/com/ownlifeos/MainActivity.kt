package com.ownlifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ownlifeos.ui.AppViewModelFactory
import com.ownlifeos.ui.theme.OwnLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as OwnLifeApplication
        val factory = AppViewModelFactory(app.container)

        setContent {
            OwnLifeTheme {
                OwnLifeOsApp(factory = factory)
            }
        }
    }
}
