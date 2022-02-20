package com.example.task3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.task3.model.Contact

class Notification(private val context: Context, private val notifyManager: NotificationManager) {
    private val contentIntent: Intent = Intent(context, MainActivity::class.java)

    fun send(contact: Contact?) {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            INTENT_ID,
            contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        createNotificationChannel()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Contact information")
            .setContentText("${contact?.firstName?.plus(" ") ?: ""}${contact?.lastName ?: ""}")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
        val notifyManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(NOTIFICATION_ID, builder)
    }

    private fun createNotificationChannel(): Unit{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val NOTIFICATION_CHANNEL_ID = "channel 666"
            val NOTIFICATION_CHANNEL_NAME = "channel"
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Task3"
            notifyManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 222
        const val CHANNEL_ID = "channel 666"
        const val INTENT_ID = 101
    }

}