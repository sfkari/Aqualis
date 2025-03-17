package com.cmc.aqualis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class WaterReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "WaterReminderChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        Log.d("WaterReminderWorker", "doWork() exécuté - affichage de la notification")
        showWaterReminderNotification()
        return Result.success()
    }

    private fun showWaterReminderNotification() {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Vérification et création du canal de notification (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Water Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, HomeActivity::class.java) // Utiliser MainActivity au lieu d'un fragment
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent pour ajouter de l'eau depuis la notification
        val addWaterIntent = Intent(context, AddWaterReceiver::class.java)
        val addWaterPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, addWaterIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construction de la notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icône système pour éviter les erreurs
            .setContentTitle("Rappel pour boire de l'eau")
            .setContentText("Il est temps de boire de l'eau !")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_input_add, "Ajouter 500ml", addWaterPendingIntent) // Action de bouton
            .setDefaults(Notification.DEFAULT_ALL)

        // Afficher la notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Log.d("WaterReminderWorker", "Notification affichée avec succès")
        Toast.makeText(context, "Notification déclenchée", Toast.LENGTH_SHORT).show()
    }
}
