package com.example.medilink2.data

import android.content.Context
import com.example.medilink2.notifications.NotificationHelper
import com.example.medilink2.ui.components.DrugItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

data class NotificationRequest(
    val id: String = "",
    val drugId: String = "",
    val drugName: String = "",
    val pharmacyId: String = "",
    val pharmacyName: String = "",
    val status: String = "waiting", // "waiting" | "notified" | "read"
    val timestamp: Any = FieldValue.serverTimestamp(),
)

object NotificationLogicManager {
    private val db get() = FirebaseFirestore.getInstance()

    suspend fun saveNotificationRequest(userId: String, drug: DrugItem, pharmacyId: String, pharmacyName: String) {
        val requestId = "${pharmacyId}_${drug.id}"
        val request = NotificationRequest(
            id = requestId,
            drugId = drug.id,
            drugName = drug.name,
            pharmacyId = pharmacyId,
            pharmacyName = pharmacyName,
            status = "waiting",
        )

        db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .set(request).await()
    }

    suspend fun removeNotificationRequest(userId: String, drugId: String, pharmacyId: String) {
        val requestId = "${pharmacyId}_$drugId"
        db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .delete().await()
    }

    fun isSubscribed(userId: String, drugId: String, pharmacyId: String): Flow<Boolean> = callbackFlow {
        val requestId = "${pharmacyId}_$drugId"
        val listener = db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .addSnapshotListener { snapshot, _ ->
                val status = snapshot?.getString("status")
                trySend(status == "waiting")
            }
        awaitClose { listener.remove() }
    }

    suspend fun checkStockAndNotify(context: Context, userId: String) {
        try {
            val waitingRequests = db.collection("notifications")
                .document(userId)
                .collection("my_requests")
                .whereEqualTo("status", "waiting")
                .get().await()

            for (doc in waitingRequests.documents) {
                val drugId = doc.getString("drugId") ?: continue
                val drugName = doc.getString("drugName") ?: "A medicine"
                val pharmacyId = doc.getString("pharmacyId") ?: continue
                val pharmacyName = doc.getString("pharmacyName") ?: "a pharmacy"

                val stockDoc = db.collection("pharmacy_stock")
                    .document(pharmacyId)
                    .collection("drugs")
                    .document(drugId)
                    .get().await()

                val currentStatus = stockDoc.getString("status")

                if (currentStatus == "IN_STOCK") {
                    NotificationHelper.showNotification(
                        context,
                        "Drug Available! 🏥",
                        "$drugName is now available at $pharmacyName",
                    )
                    doc.reference.update("status", "notified").await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnreadNotificationsCount(userId: String): Flow<Int> = callbackFlow {
        val listener = db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .whereEqualTo("status", "notified")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    fun getNotifications(userId: String): Flow<List<NotificationRequest>> = callbackFlow {
        val listener = db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val requests = snapshot?.toObjects(NotificationRequest::class.java) ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markAsRead(userId: String, requestId: String) {
        db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .update("status", "read").await()
    }
}
