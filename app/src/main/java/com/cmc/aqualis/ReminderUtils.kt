package com.cmc.aqualis

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object ReminderUtils {
    fun scheduleWaterReminder(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // Ne s'exécute pas si la batterie est faible
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(15, TimeUnit.MINUTES) // Minimum 15 minutes
            .setInitialDelay(15, TimeUnit.MINUTES) // Premier rappel après 15 min
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WaterReminder",
            ExistingPeriodicWorkPolicy.KEEP, // Ne recrée pas la tâche si elle existe déjà
            workRequest
        )
    }

    fun cancelWaterReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("WaterReminder")
    }
}
