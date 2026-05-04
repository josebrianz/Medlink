package com.example.medilink2.data

import com.example.medilink2.ui.screens.DrugItem
import com.example.medilink2.ui.screens.PharmacyDetails
import com.example.medilink2.ui.screens.SearchResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Repository responsible for retrieving pharmacy inventory and details from Firebase.
 */
class PharmacyRepository {

    private val database = FirebaseDatabase.getInstance().getReference("pharmacies")

    suspend fun getAllPharmacies(): List<SearchResult> = suspendCancellableCoroutine { continuation ->
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = snapshot.children.mapNotNull { child ->
                    try {
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val location = child.child("location").getValue(String::class.java) ?: ""
                        val distance = child.child("distance").getValue(String::class.java) ?: ""
                        val price = child.child("price").getValue(String::class.java) ?: ""
                        val rating = child.child("rating").getValue(String::class.java) ?: ""
                        val closingTime = child.child("closingTime").getValue(String::class.java) ?: ""
                        val inStock = child.child("inStock").getValue(Boolean::class.java) ?: false
                        val stockLevel = child.child("stockLevel").getValue(String::class.java) ?: "Medium"
                        
                        val tags = mutableListOf<String>()
                        child.child("tags").children.forEach { tagSnapshot ->
                            tagSnapshot.getValue(String::class.java)?.let { tags.add(it) }
                        }

                        SearchResult(name, location, distance, price, rating, closingTime, inStock, stockLevel, tags)
                    } catch (e: Exception) {
                        null
                    }
                }
                continuation.resume(results)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(emptyList())
            }
        })
    }

    suspend fun getPharmacyDetails(pharmacyId: String): PharmacyDetails? = suspendCancellableCoroutine { continuation ->
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Find pharmacy by name or ID
                val child = snapshot.children.firstOrNull { 
                    it.child("name").getValue(String::class.java) == pharmacyId 
                }
                
                if (child != null) {
                    val inventory = mutableListOf<DrugItem>()
                    child.child("inventory").children.forEach { drugSnapshot ->
                        try {
                            val name = drugSnapshot.child("name").getValue(String::class.java) ?: ""
                            val category = drugSnapshot.child("category").getValue(String::class.java) ?: ""
                            val price = drugSnapshot.child("price").getValue(String::class.java) ?: ""
                            val inStock = drugSnapshot.child("inStock").getValue(Boolean::class.java) ?: false
                            val stockLevel = drugSnapshot.child("stockLevel").getValue(String::class.java) ?: ""
                            inventory.add(DrugItem(name, category, price, inStock, stockLevel))
                        } catch (e: Exception) {}
                    }

                    continuation.resume(PharmacyDetails(
                        name = child.child("name").getValue(String::class.java) ?: "",
                        location = child.child("location").getValue(String::class.java) ?: "",
                        distance = child.child("distance").getValue(String::class.java) ?: "",
                        rating = child.child("rating").getValue(String::class.java) ?: "",
                        closingTime = child.child("closingTime").getValue(String::class.java) ?: "",
                        inventory = inventory
                    ))
                } else {
                    continuation.resume(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(null)
            }
        })
    }

    fun seedDatabase(pharmacies: List<SearchResult>) {
        pharmacies.forEach { pharmacy ->
            val key = pharmacy.name.replace(Regex("[.#$\\[\\]]"), "_")
            val ref = database.child(key)
            ref.setValue(pharmacy)
            
            // Add sample inventory based on tags for search functionality
            val inventory = pharmacy.tags.map { tag ->
                DrugItem(tag, "General", pharmacy.price, pharmacy.inStock, pharmacy.stockLevel)
            }
            ref.child("inventory").setValue(inventory)
        }
    }
}
