package com.ownlifeos

import androidx.compose.runtime.Composable
import com.ownlifeos.ui.AppViewModelFactory
import com.ownlifeos.ui.navigation.OwnLifeNavHost

@Composable
fun OwnLifeOsApp(factory: AppViewModelFactory) {
    // 루트 Composable은 앱의 큰 골격만 연결한다.
    // 실제 홈, 체크인, 리포트, 태스크 화면의 로직은 OwnLifeNavHost 내부 destination들이 담당한다.
    OwnLifeNavHost(factory = factory)
}
