package ai.devrev.sdk.sample.service

import ai.devrev.sdk.sample.MainActivity
import ai.devrev.sdk.sample.R
import ai.devrev.sdk.sample.utils.SharedPrefUtil
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebasePushNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebasePushNotificationService"
        private const val CHANNEL_ID = "push_notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        SharedPrefUtil.saveTokenToPreferences(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: getString(R.string.devrev_sdk)
            val body = notification.body ?: getString(R.string.empty_body)
            sendNotification(title, body)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.devrev_sdk),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            manager.notify(0, notificationBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, getString(R.string.notification_error))
        }
    }

}