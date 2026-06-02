package com.ownlifeos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ownlifeos.MainActivity
import com.ownlifeos.R

class OwnLifeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val snapshot = WidgetSnapshotStore.read(context)
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildRemoteViews(context, snapshot))
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, OwnLifeWidgetProvider::class.java))
            if (ids.isEmpty()) return
            val snapshot = WidgetSnapshotStore.read(context)
            ids.forEach { id ->
                manager.updateAppWidget(id, buildRemoteViews(context, snapshot))
            }
        }

        private fun buildRemoteViews(
            context: Context,
            snapshot: WidgetSnapshot
        ): RemoteViews {
            val launchIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            return RemoteViews(context.packageName, R.layout.widget_own_life).apply {
                setTextViewText(R.id.widget_mode, "Mode: ${snapshot.mode}")
                setTextViewText(R.id.widget_battery, "Battery: ${snapshot.battery}")
                setTextViewText(R.id.widget_overload, "Overload: ${snapshot.overload}")
                setTextViewText(R.id.widget_next_action, snapshot.nextAction)
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }
        }
    }
}
