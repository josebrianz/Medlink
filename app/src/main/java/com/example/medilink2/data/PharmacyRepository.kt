package com.example.medilink2.data

import com.example.medilink2.data.local.DrugEntity
import com.example.medilink2.data.local.PharmacyDao
import com.example.medilink2.data.local.PharmacyEntity
import com.example.medilink2.ui.components.DrugItem
import com.example.medilink2.ui.screens.PharmacyDetails
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository responsible for retrieving pharmacy inventory and price details.
 * It uses Firebase Realtime Database for live data and Room for local caching.
 */
class PharmacyRepository(private val pharmacyDao: PharmacyDao? = null) {

    private val database = FirebaseDatabase.getInstance()
    private val pharmaciesRef = database.getReference("pharmacies")

    private val pharmacyData = listOf(
        PharmacyDetails("1", "MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "4.8", "9:00 PM", emptyList(), 0.3136, 32.5811),
        PharmacyDetails("2", "City Chemist", "Jinja Road, Near Total", "1.2 km", "4.5", "8:00 PM", emptyList(), 0.3162, 32.5855),
        PharmacyDetails("3", "HealthGuard Pharmacy", "Nasser Road, Block B", "1.8 km", "4.2", "10:00 PM", emptyList(), 0.3120, 32.5880),
        PharmacyDetails("4", "QuickMeds", "Bombo Road, Wandegeya", "3.1 km", "4.6", "7:00 PM", emptyList(), 0.3340, 32.5780),
        PharmacyDetails("5", "Allergy Care", "Wandegeya Market", "2.0 km", "4.4", "6:00 PM", emptyList(), 0.3350, 32.5750),
        PharmacyDetails("6", "General Wellness", "Mulago Hill", "2.5 km", "4.1", "11:00 PM", emptyList(), 0.3380, 32.5850),
        PharmacyDetails("7", "First Care Pharmacy", "Kikuubo Lane", "0.5 km", "4.7", "11:00 PM", emptyList(), 0.3140, 32.5780),
        PharmacyDetails("8", "Eco Pharmacy", "Kisementi", "2.2 km", "4.9", "12:00 AM", emptyList(), 0.3350, 32.5950),
        PharmacyDetails("9", "Vine Pharmacy", "Lugogo Mall", "3.5 km", "4.6", "10:00 PM", emptyList(), 0.3250, 32.6020),
        PharmacyDetails("10", "Family Health Pharmacy", "Ntinda Road", "4.1 km", "4.3", "9:30 PM", emptyList(), 0.3540, 32.6110)
    )

    private val fullInventory = listOf(
        DrugItem(id = "1", name = "Paracetamol", category = "Pain & Fever", price = "UGX 3,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "2", name = "Amoxicillin", category = "Antibiotic", price = "UGX 12,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "3", name = "Ibuprofen", category = "Pain & Fever", price = "UGX 5,500", inStock = false, stockLevel = "Out of Stock"),
        DrugItem(id = "4", name = "Cetirizine", category = "Allergy", price = "UGX 4,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "5", name = "Panadol Extra", category = "Pain & Fever", price = "UGX 4,500", inStock = true, stockLevel = "Low"),
        DrugItem(id = "6", name = "Vitamin C", category = "Supplements", price = "UGX 15,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "7", name = "Metformin", category = "Diabetes", price = "UGX 8,000", inStock = true, stockLevel = "Low"),
        DrugItem(id = "8", name = "Insulin Glargine", category = "Diabetes", price = "UGX 45,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "9", name = "Aspirin 81mg", category = "Heart", price = "UGX 2,500", inStock = true, stockLevel = "High"),
        DrugItem(id = "10", name = "Atorvastatin", category = "Heart", price = "UGX 18,000", inStock = true, stockLevel = "Low"),
        DrugItem(id = "11", name = "Lisinopril", category = "Heart", price = "UGX 10,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "12", name = "Salbutamol Inhaler", category = "General", price = "UGX 15,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "13", name = "Omeprazole", category = "General", price = "UGX 7,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "14", name = "Loratadine", category = "General", price = "UGX 3,500", inStock = true, stockLevel = "High"),
        DrugItem(id = "15", name = "Diclofenac Gel", category = "Pain Relief", price = "UGX 12,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "16", name = "Azithromycin", category = "Antibiotic", price = "UGX 25,000", inStock = false, stockLevel = "Out of Stock"),
        DrugItem(id = "17", name = "ORS Sachet", category = "General", price = "UGX 1,500", inStock = true, stockLevel = "High"),
        DrugItem(id = "18", name = "Metronidazole", category = "Antibiotic", price = "UGX 6,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "19", name = "Amlodipine", category = "Heart", price = "UGX 14,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "20", name = "Ventolin Inhaler", category = "General", price = "UGX 22,000", inStock = true, stockLevel = "Low"),
        DrugItem(id = "21", name = "Gaviscon Liquid", category = "General", price = "UGX 35,000", inStock = true, stockLevel = "Medium"),
        DrugItem(id = "22", name = "Augustin 625mg", category = "Antibiotic", price = "UGX 30,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "23", name = "Folic Acid", category = "Supplements", price = "UGX 5,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "24", name = "Durex Condoms", category = "General", price = "UGX 10,000", inStock = true, stockLevel = "High"),
        DrugItem(id = "25", name = "Piriton", category = "Allergy", price = "UGX 2,500", inStock = true, stockLevel = "Medium"),
    )

    fun getAllPharmaciesOld(): List<PharmacyDetails> = pharmacyData

    fun getAllPharmacies(): Flow<List<PharmacyEntity>> = callbackFlow {
        val listener = pharmaciesRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val pharmacies = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val name = child.child("name").value as? String ?: ""
                    val location = child.child("location").value as? String ?: ""
                    val rating = child.child("rating").value as? String ?: "0.0"
                    val closingTime = child.child("closingTime").value as? String ?: "9:00 PM"
                    
                    // Use list-based fallback for coordinates if missing in Firebase
                    val fallback = pharmacyData.find { it.id == id }
                    val lat = (child.child("latitude").value as? Number)?.toDouble() ?: fallback?.latitude ?: 0.3136
                    val lon = (child.child("longitude").value as? Number)?.toDouble() ?: fallback?.longitude ?: 32.5811
                    
                    PharmacyEntity(
                        id = id,
                        name = name,
                        location = location,
                        distance = "1.2 km", // Mock distance for now
                        rating = rating,
                        closingTime = closingTime,
                        isOpen = true,
                        latitude = lat,
                        longitude = lon
                    )
                }
                trySend(pharmacies)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { pharmaciesRef.removeEventListener(listener) }
    }

    fun getAllPharmaciesFromCache(): Flow<List<PharmacyEntity>> {
        return pharmacyDao?.getAllPharmacies() ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }

    suspend fun cachePharmacies(pharmacies: List<PharmacyEntity>) {
        pharmacyDao?.insertPharmacies(pharmacies)
    }

    fun getDrugsForPharmacy(pharmacyId: String): Flow<List<DrugItem>> = callbackFlow {
        val drugsRef = pharmaciesRef.child(pharmacyId).child("drugs")
        val listener = drugsRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val drugs = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val name = child.child("name").value as? String ?: ""
                    val category = child.child("category").value as? String ?: ""
                    val price = child.child("price").value as? String ?: ""
                    val stockLevel = child.child("stockLevel").value?.toString() ?: "0"
                    val inStock = (child.child("inStock").value as? Boolean == true) || ((stockLevel.toIntOrNull() ?: 0) > 0)
                    
                    DrugItem(
                        id = id,
                        name = name,
                        category = category,
                        price = price,
                        inStock = inStock,
                        stockLevel = stockLevel
                    )
                }
                trySend(drugs)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { drugsRef.removeEventListener(listener) }
    }

    private fun DrugEntity.toUiModel() = DrugItem(
        id = id,
        name = name,
        category = category,
        price = price,
        inStock = inStock,
        stockLevel = stockLevel
    )

    fun getPharmacyDetailsFlow(pharmacyId: String): Flow<PharmacyDetails?> = callbackFlow {
        val pharmacyRef = pharmaciesRef.child(pharmacyId)
        val listener = pharmacyRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (!snapshot.exists()) {
                    // Try to find in mock data
                    val base = pharmacyData.find { it.id == pharmacyId }
                    if (base != null) {
                        trySend(base.copy(inventory = getInventoryForPharmacy(pharmacyId)))
                    } else {
                        trySend(null)
                    }
                    return
                }

                val name = snapshot.child("name").value as? String ?: ""
                val location = snapshot.child("location").value as? String ?: ""
                val rating = snapshot.child("rating").value as? String ?: "0.0"
                val closingTime = snapshot.child("closingTime").value as? String ?: "9:00 PM"
                val lat = (snapshot.child("latitude").value as? Number)?.toDouble() ?: 0.3136
                val lon = (snapshot.child("longitude").value as? Number)?.toDouble() ?: 32.5811
                
                val drugsSnapshot = snapshot.child("drugs")
                val drugsList = drugsSnapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val drugName = child.child("name").value as? String ?: ""
                    val category = child.child("category").value as? String ?: ""
                    val price = child.child("price").value as? String ?: ""
                    val stockLevel = child.child("stockLevel").value?.toString() ?: "0"
                    val inStock = (child.child("inStock").value as? Boolean == true) || ((stockLevel.toIntOrNull() ?: 0) > 0)
                    
                    DrugItem(id, drugName, category, price, inStock, stockLevel)
                }

                trySend(PharmacyDetails(
                    id = pharmacyId,
                    name = name,
                    location = location,
                    distance = "1.2 km",
                    rating = rating,
                    closingTime = closingTime,
                    inventory = drugsList,
                    latitude = lat,
                    longitude = lon
                ))
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { pharmacyRef.removeEventListener(listener) }
    }

    suspend fun getPharmacyDetails(pharmacyId: String): PharmacyDetails? {
        val snapshot = pharmaciesRef.child(pharmacyId).get().await()
        if (!snapshot.exists()) {
            // Fallback to mock data if not in Firebase yet
            val base = pharmacyData.find { it.id == pharmacyId } ?: return null
            return base.copy(inventory = getInventoryForPharmacy(pharmacyId))
        }

        val name = snapshot.child("name").value as? String ?: ""
        val location = snapshot.child("location").value as? String ?: ""
        val rating = snapshot.child("rating").value as? String ?: "0.0"
        val closingTime = snapshot.child("closingTime").value as? String ?: "9:00 PM"
        
        val fallback = pharmacyData.find { it.id == pharmacyId }
        val lat = (snapshot.child("latitude").value as? Number)?.toDouble() ?: fallback?.latitude ?: 0.3136
        val lon = (snapshot.child("longitude").value as? Number)?.toDouble() ?: fallback?.longitude ?: 32.5811
        
        val drugsSnapshot = snapshot.child("drugs")
        val drugsList = drugsSnapshot.children.mapNotNull { child ->
            val id = child.key ?: return@mapNotNull null
            val drugName = child.child("name").value as? String ?: ""
            val category = child.child("category").value as? String ?: ""
            val price = child.child("price").value as? String ?: ""
            val stockLevel = child.child("stockLevel").value?.toString() ?: "0"
            val inStock = (child.child("inStock").value as? Boolean == true) || ((stockLevel.toIntOrNull() ?: 0) > 0)
            
            DrugItem(id, drugName, category, price, inStock, stockLevel)
        }

        return PharmacyDetails(
            id = pharmacyId,
            name = name,
            location = location,
            distance = "1.2 km",
            rating = rating,
            closingTime = closingTime,
            inventory = drugsList,
            latitude = lat,
            longitude = lon
        )
    }

    fun searchDrugs(query: String): List<DrugItem> {
        return fullInventory.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true) 
        }
    }

    fun getPharmaciesStocking(drugName: String): List<PharmacyDetails> {
        // This is still using mock data, let's fix SearchViewModel instead to use live data
        return pharmacyData.filter { pharmacy ->
            getInventoryForPharmacy(pharmacy.id).any { 
                it.name.contains(drugName, ignoreCase = true) || 
                it.category.contains(drugName, ignoreCase = true) 
            }
        }
    }

    /**
     * Searches for drugs across all pharmacies in the Realtime Database.
     */
    fun searchDrugsLive(query: String): Flow<List<Pair<PharmacyEntity, DrugItem>>> = callbackFlow {
        val listener = pharmaciesRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val results = mutableListOf<Pair<PharmacyEntity, DrugItem>>()
                snapshot.children.forEach { pharmacySnapshot ->
                    val pharmacyId = pharmacySnapshot.key ?: return@forEach
                    val pharmacyName = pharmacySnapshot.child("name").value as? String ?: ""
                    val location = pharmacySnapshot.child("location").value as? String ?: ""
                    val rating = pharmacySnapshot.child("rating").value as? String ?: "0.0"
                    val closingTime = pharmacySnapshot.child("closingTime").value as? String ?: "9:00 PM"
                    val lat = (pharmacySnapshot.child("latitude").value as? Number)?.toDouble() ?: 0.3136
                    val lon = (pharmacySnapshot.child("longitude").value as? Number)?.toDouble() ?: 32.5811
                    
                    val pharmacy = PharmacyEntity(
                        id = pharmacyId,
                        name = pharmacyName,
                        location = location,
                        distance = "1.2 km",
                        rating = rating,
                        closingTime = closingTime,
                        isOpen = true,
                        latitude = lat,
                        longitude = lon
                    )

                    pharmacySnapshot.child("drugs").children.forEach { drugSnapshot ->
                        val name = drugSnapshot.child("name").value as? String ?: ""
                        val category = drugSnapshot.child("category").value as? String ?: ""
                        if (name.contains(query, ignoreCase = true) || category.contains(query, ignoreCase = true)) {
                            val id = drugSnapshot.key ?: ""
                            val price = drugSnapshot.child("price").value as? String ?: ""
                            val stockLevel = drugSnapshot.child("stockLevel").value?.toString() ?: "0"
                            val inStock = (drugSnapshot.child("inStock").value as? Boolean == true) || ((stockLevel.toIntOrNull() ?: 0) > 0)
                            
                            results.add(pharmacy to DrugItem(id, name, category, price, inStock, stockLevel))
                        }
                    }
                }
                trySend(results)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { pharmaciesRef.removeEventListener(listener) }
    }

    private fun getInventoryForPharmacy(pharmacyId: String): List<DrugItem> {
        val random = java.util.Random(pharmacyId.hashCode().toLong())
        return fullInventory.shuffled(random).take(8 + random.nextInt(7))
    }
}
