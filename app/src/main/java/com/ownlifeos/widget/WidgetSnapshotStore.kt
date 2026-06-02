package com.ownlifeos.widget

import android.content.Context

data class WidgetSnapshot(
    val mode: String = "Booting",
    val battery: String = "--",
    val overload: String = "계산 전",
    val nextAction: String = "오늘의 운영 전략 생성"
)

class WidgetSnapshotStore(
    private val context: Context
) {
    fun save(snapshot: WidgetSnapshot) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, snapshot.mode)
            .putString(KEY_BATTERY, snapshot.battery)
            .putString(KEY_OVERLOAD, snapshot.overload)
            .putString(KEY_NEXT_ACTION, snapshot.nextAction)
            .apply()
        OwnLifeWidgetProvider.updateAll(context)
    }

    companion object {
        private const val PREFS_NAME = "own_life_widget"
        private const val KEY_MODE = "mode"
        private const val KEY_BATTERY = "battery"
        private const val KEY_OVERLOAD = "overload"
        private const val KEY_NEXT_ACTION = "next_action"

        fun read(context: Context): WidgetSnapshot {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return WidgetSnapshot(
                mode = prefs.getString(KEY_MODE, null) ?: "Booting",
                battery = prefs.getString(KEY_BATTERY, null) ?: "--",
                overload = prefs.getString(KEY_OVERLOAD, null) ?: "계산 전",
                nextAction = prefs.getString(KEY_NEXT_ACTION, null) ?: "오늘의 운영 전략 생성"
            )
        }
    }
}
