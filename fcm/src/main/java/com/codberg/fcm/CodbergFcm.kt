package com.codberg.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CodbergFcm(val mContext: Context, val pushIconResourceId: Int): FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Firebase", "Fcm Token : $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(p0)
        remoteMessage?.let {
            if(it.data.isNotEmpty()) {
                sendNotification(remoteMessage)
            }
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        val channelId = "Fcm_01"
        val channelName = "Push"

        var notificationBuilder: NotificationCompat.Builder? = null

        // 오레오 버전 이상은 알림 채널 생성
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
            }
            notiChannel.createNotificationChannel(channelMessage)

            notificationBuilder = NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(pushIconResourceId)
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(channelId)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
        }
        else {
            notificationBuilder = NotificationCompat.Builder(mContext, "")
                .setSmallIcon(pushIconResourceId)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
        }

        notificationBuilder?.let {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Constant.FCM_ID, it.build())
        }
    }
}