package com.example.medilink2.notifications

import com.example.medilink2.data.UserManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MedlinkMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserManager.updateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        remoteMessage.notification?.let {
            NotificationHelper.showNotification(this, it.title ?: "Drug Available", it.body ?: "")
        } ?: run {
            val title = remoteMessage.data["title"] ?: "Drug Available"
            val body = remoteMessage.data["body"] ?: ""
            NotificationHelper.showNotification(this, title, body)
        }
    }
}
