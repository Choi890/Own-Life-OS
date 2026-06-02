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

        // 애플리케이션 컨테이너에서 DB와 Repository 의존성을 가져와 ViewModelFactory를 만든다.
        // 화면별 ViewModel은 이 factory를 통해 같은 데이터 저장소를 공유한다.
        val app = application as OwnLifeApplication
        val factory = AppViewModelFactory(app.container)

        setContent {
            OwnLifeTheme {
                OwnLifeOsApp(factory = factory)
            }
        }
    }
}
