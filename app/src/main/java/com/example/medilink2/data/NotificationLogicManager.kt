package com.example.medilink2.data

import android.content.Context
import com.example.medilink2.notifications.NotificationHelper
import com.example.medilink2.ui.components.DrugItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.database.FirebaseDatabase
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
    val timestamp: Any? = null,
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
            timestamp = FieldValue.serverTimestamp()
        )

        // Save to Firestore for patient's notification history
        db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .set(request).await()

        // Track subscriber in RTDB for instant owner-triggered notifications
        FirebaseDatabase.getInstance().getReference("pharmacies")
            .child(pharmacyId)
            .child("drugs")
            .child(drug.id)
            .child("subscribers")
            .child(userId)
            .setValue(true)
    }

    suspend fun removeNotificationRequest(userId: String, drugId: String, pharmacyId: String) {
        val requestId = "${pharmacyId}_$drugId"
        db.collection("notifications")
            .document(userId)
            .collection("my_requests")
            .document(requestId)
            .delete().await()

        // Remove from RTDB subscribers list
        FirebaseDatabase.getInstance().getReference("pharmacies")
            .child(pharmacyId)
            .child("drugs")
            .child(drugId)
            .child("subscribers")
            .child(userId)
            .removeValue()
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

    suspend fun checkStockAndNotify(context: Context, pharmacyId: String, drugId: String, newStock: Int) {
        if (newStock <= 0) return

        try {
            val pharmacyRef = FirebaseDatabase.getInstance().getReference("pharmacies").child(pharmacyId)
            val drugRef = pharmacyRef.child("drugs").child(drugId)

            val snapshot = drugRef.child("subscribers").get().await()
            
            if (snapshot.exists()) {
                val drugName = drugRef.child("name").get().await().value as? String ?: "A medicine"
                
                for (userChild in snapshot.children) {
                    val patientId = userChild.key ?: continue
                    val requestId = "${pharmacyId}_$drugId"
                    
                    try {
                        db.collection("notifications")
                            .document(patientId)
                            .collection("my_requests")
                            .document(requestId)
                            .update(mapOf(
                                "status" to "notified",
                                "timestamp" to FieldValue.serverTimestamp()
                            ))
                            .await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                NotificationHelper.showNotification(
                    context,
                    "Subscribers Notified",
                    "Stock updated for $drugName and alerts sent."
                )
                
                drugRef.child("subscribers").removeValue().await()
            } else {
                NotificationHelper.showNotification(
                    context,
                    "Stock Updated",
                    "Inventory updated successfully (no subscribers to notify)."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Optional: notify the UI about the failure
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
