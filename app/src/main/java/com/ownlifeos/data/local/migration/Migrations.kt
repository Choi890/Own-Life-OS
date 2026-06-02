package com.ownlifeos.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN deadlineDate TEXT")
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN focusNeed INTEGER NOT NULL DEFAULT 3")
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN deferredCount INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN startedAt INTEGER")
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN completedAt INTEGER")
            db.execSQL("ALTER TABLE life_tasks ADD COLUMN lastDeferredAt INTEGER")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS daily_metrics (
                    date TEXT NOT NULL PRIMARY KEY,
                    lifeBattery INTEGER NOT NULL,
                    focusLevel INTEGER NOT NULL,
                    stressLoad INTEGER NOT NULL,
                    fatigueLoad INTEGER NOT NULL,
                    todayMode TEXT NOT NULL,
                    reasonSummary TEXT NOT NULL,
                    generatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS generated_error_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    type TEXT NOT NULL,
                    severity TEXT NOT NULL,
                    title TEXT NOT NULL,
                    detail TEXT NOT NULL,
                    evidence TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    dismissed INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS task_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    taskId INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    eventType TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS decisions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    expectedEnergyCost INTEGER NOT NULL,
                    urgency INTEGER NOT NULL,
                    reversibility INTEGER NOT NULL,
                    importance INTEGER NOT NULL,
                    predictedRiskScore INTEGER NOT NULL,
                    predictedRiskLevel TEXT NOT NULL,
                    reasons TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS decision_outcomes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    decisionId INTEGER NOT NULL,
                    actualResult TEXT NOT NULL,
                    regretLevel INTEGER NOT NULL,
                    energyImpact INTEGER NOT NULL,
                    note TEXT NOT NULL,
                    recordedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS forecast_results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    timeBlock TEXT NOT NULL,
                    riskLevel TEXT NOT NULL,
                    riskScore INTEGER NOT NULL,
                    summary TEXT NOT NULL,
                    reasonsJson TEXT NOT NULL,
                    generatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS life_simulations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    scenarioTitle TEXT NOT NULL,
                    scenarioDescription TEXT NOT NULL,
                    completionPossibility TEXT NOT NULL,
                    stressImpact TEXT NOT NULL,
                    regretPossibility TEXT NOT NULL,
                    completionScore INTEGER NOT NULL,
                    stressScore INTEGER NOT NULL,
                    regretScore INTEGER NOT NULL,
                    summary TEXT NOT NULL,
                    reasonsJson TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS recovery_plans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    triggerLevel TEXT NOT NULL,
                    title TEXT NOT NULL,
                    actionsJson TEXT NOT NULL,
                    reasonsJson TEXT NOT NULL,
                    completed INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS system_health_reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    weekStartDate TEXT NOT NULL,
                    weekEndDate TEXT NOT NULL,
                    healthScore INTEGER NOT NULL,
                    stableAreasJson TEXT NOT NULL,
                    unstableAreasJson TEXT NOT NULL,
                    nextWeekStrategiesJson TEXT NOT NULL,
                    reasonsJson TEXT NOT NULL,
                    generatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS recommendation_feedback (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    surface TEXT NOT NULL,
                    feedbackType TEXT NOT NULL,
                    note TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }
}
