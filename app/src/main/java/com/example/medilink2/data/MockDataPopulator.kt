package com.example.medilink2.data

import com.google.firebase.database.FirebaseDatabase

object MockDataPopulator {
    private val database = FirebaseDatabase.getInstance()
    private val pharmaciesRef = database.getReference("pharmacies")
    private val usersRef = database.getReference("users")

    data class MockPharmacy(
        val id: String,
        val name: String,
        val location: String,
        val distance: String,
        val price: String,
        val rating: String,
        val closingTime: String,
        val inStock: Boolean,
        val drugs: List<MockDrug>,
        val lat: Double,
        val lon: Double
    )

    data class MockDrug(
        val name: String,
        val category: String,
        val price: String,
        val stockLevel: String,
        val inStock: Boolean
    )

    private val mockData = listOf(
        MockPharmacy("1", "MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "UGX 3,000", "4.8", "9:00 PM", true, listOf(
            MockDrug("Paracetamol", "Pain Relief", "UGX 3,000", "50", true),
            MockDrug("Panadol Extra", "Pain Relief", "UGX 5,000", "30", true),
            MockDrug("Diclofenac Gel", "Topical", "UGX 12,000", "15", true),
            MockDrug("Gaviscon", "Antacid", "UGX 15,000", "20", true)
        ), 0.3136, 32.5811),
        MockPharmacy("2", "City Chemist", "Jinja Road, Near Total", "1.2 km", "UGX 2,500", "4.5", "8:00 PM", true, listOf(
            MockDrug("Paracetamol", "Pain Relief", "UGX 2,500", "100", true),
            MockDrug("Amoxicillin", "Antibiotic", "UGX 8,000", "40", true),
            MockDrug("Vitamin C", "Supplements", "UGX 1,500", "200", true),
            MockDrug("Augustin", "Antibiotic", "UGX 25,000", "10", true)
        ), 0.3162, 32.5855),
        MockPharmacy("3", "HealthGuard Pharmacy", "Nasser Road, Block B", "1.8 km", "UGX 3,500", "4.2", "10:00 PM", true, listOf(
            MockDrug("Aspirin 81mg", "Heart", "UGX 3,500", "60", true),
            MockDrug("Atorvastatin", "Cholesterol", "UGX 30,000", "25", true),
            MockDrug("Lisinopril", "Blood Pressure", "UGX 15,000", "30", true),
            MockDrug("Amlodipine", "Blood Pressure", "UGX 12,000", "45", true)
        ), 0.3120, 32.5880),
        MockPharmacy("4", "QuickMeds", "Bombo Road, Wandegeya", "3.1 km", "UGX 2,800", "4.6", "7:00 PM", false, listOf(
            MockDrug("Insulin Glargine", "Diabetes", "UGX 45,000", "0", false),
            MockDrug("Metformin", "Diabetes", "UGX 5,000", "0", false)
        ), 0.3340, 32.5780),
        MockPharmacy("5", "Allergy Care", "Wandegeya Market", "2.0 km", "UGX 4,000", "4.4", "6:00 PM", true, listOf(
            MockDrug("Cetirizine", "Allergy", "UGX 4,000", "80", true),
            MockDrug("Loratadine", "Allergy", "UGX 4,500", "70", true),
            MockDrug("Piriton", "Allergy", "UGX 2,000", "100", true)
        ), 0.3350, 32.5750),
        MockPharmacy("6", "General Wellness", "Mulago Hill", "2.5 km", "UGX 1,500", "4.1", "11:00 PM", true, listOf(
            MockDrug("ORS Sachet", "Rehydration", "UGX 1,500", "150", true),
            MockDrug("Salbutamol Inhaler", "Asthma", "UGX 18,000", "20", true),
            MockDrug("Omeprazole", "Gastro", "UGX 6,000", "50", true),
            MockDrug("Folic Acid", "Supplements", "UGX 1,000", "300", true)
        ), 0.3380, 32.5850),
        MockPharmacy("7", "First Care Pharmacy", "Kikuubo Lane", "0.5 km", "UGX 2,200", "4.7", "11:00 PM", true, listOf(
            MockDrug("Paracetamol", "Pain Relief", "UGX 2,200", "120", true),
            MockDrug("Amoxicillin", "Antibiotic", "UGX 7,500", "50", true),
            MockDrug("Metronidazole", "Antibiotic", "UGX 4,000", "60", true)
        ), 0.3140, 32.5780),
        MockPharmacy("8", "Eco Pharmacy", "Kisementi", "2.2 km", "UGX 5,500", "4.9", "12:00 AM", true, listOf(
            MockDrug("Vitamin C", "Supplements", "UGX 5,500", "100", true),
            MockDrug("Folic Acid", "Supplements", "UGX 2,500", "150", true),
            MockDrug("Gaviscon", "Antacid", "UGX 18,000", "30", true)
        ), 0.3350, 32.5950),
        MockPharmacy("9", "Vine Pharmacy", "Lugogo Mall", "3.5 km", "UGX 35,000", "4.6", "10:00 PM", true, listOf(
            MockDrug("Gaviscon", "Antacid", "UGX 35,000", "40", true),
            MockDrug("Ventolin", "Asthma", "UGX 22,000", "25", true)
        ), 0.3250, 32.6020),
        MockPharmacy("10", "Family Health Pharmacy", "Ntinda Road", "4.1 km", "UGX 10,000", "4.3", "9:30 PM", true, listOf(
            MockDrug("Durex Condoms", "Sexual Health", "UGX 10,000", "50", true),
            MockDrug("Loratadine", "Allergy", "UGX 5,000", "40", true)
        ), 0.3540, 32.6110)
    )


    fun seedDatabase() {
        mockData.forEach { pharmacy ->
            // 1. Create Mock Pharmacy Owner User
            val userMap = mapOf(
                "fullName" to "${pharmacy.name} Owner",
                "phoneNumber" to "+256 700 000 ${pharmacy.id.padStart(2, '0')}",
                "email" to "owner${pharmacy.id}@medlink.com",
                "role" to "PHARMACY_OWNER"
            )
            usersRef.child(pharmacy.id).setValue(userMap)

            // 2. Create Pharmacy Details
            val pharmacyMap = mapOf(
                "name" to pharmacy.name,
                "location" to pharmacy.location,
                "rating" to pharmacy.rating,
                "closingTime" to pharmacy.closingTime,
                "distance" to pharmacy.distance,
                "latitude" to pharmacy.lat,
                "longitude" to pharmacy.lon
            )
            pharmaciesRef.child(pharmacy.id).updateChildren(pharmacyMap)

            // 3. Add Drugs to Pharmacy
            val drugsRef = pharmaciesRef.child(pharmacy.id).child("drugs")
            pharmacy.drugs.forEach { drug ->
                val drugId = drugsRef.push().key ?: return@forEach
                val drugMap = mapOf(
                    "name" to drug.name,
                    "category" to drug.category,
                    "price" to drug.price,
                    "stockLevel" to drug.stockLevel,
                    "inStock" to drug.inStock
                )
                drugsRef.child(drugId).setValue(drugMap)
            }
        }
    }
}
