package com.autoencoder.glasdemoapp.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.main.MainActivity

private const val CHANNEL_ID = "Glas_Notification_Channel"
private const val NOTIFICATIONS = "notifications"

object NotificationProvider {

    private var notificationChannel: NotificationChannel? = null

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                notificationChannel = this
                // Register the channel with the system
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(this)
            }
        }
    }

    fun createNotification(context: Context?) {
        context?.let { context1 ->
            val largeIconWidth = context.resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
            val largeIconHeight = context.resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
            val notification = NotificationCompat.Builder(context1, CHANNEL_ID).run {
                val pendingIntent = TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                setContentIntent(pendingIntent)
                setSmallIcon(R.drawable.logo)
                setContentText(context.getString(R.string.location_must_be_turned_on))
                setLargeIcon(
                    ContextCompat.getDrawable(
                        context1,
                        R.mipmap.ic_launcher
                    )?.toBitmap(largeIconWidth, largeIconHeight))
                setContentTitle(context1.getString(R.string.app_name))
                build()
            }
            NotificationManagerCompat.from(context1)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
