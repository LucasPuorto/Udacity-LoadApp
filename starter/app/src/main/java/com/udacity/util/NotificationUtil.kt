package com.udacity.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.ui.DetailActivity
import java.io.Serializable

private const val NOTIFICATION_ID = 0

class NotificationBody(
    val title: String,
    val status: String,
    val description: String,
) : Serializable

fun NotificationManager.sendNotification(applicationContext: Context, notificationBody: NotificationBody) {

    createChannel(
        applicationContext = applicationContext,
        channelId = applicationContext.getString(R.string.load_app_channel_id),
        channelName = applicationContext.getString(R.string.load_app_channel_name),
        channelDescription = applicationContext.getString(R.string.load_app_channel_description)
    )

    val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(DetailActivity.NOTIFICATION_BODY, notificationBody)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val notificationDescription: String = applicationContext.getString(
        R.string.notification_description,
        notificationBody.description
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.load_app_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(notificationDescription)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

private fun createChannel(applicationContext: Context, channelId: String, channelName: String, channelDescription: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription
        notificationChannel.setShowBadge(false)
        val notificationManager = applicationContext.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}