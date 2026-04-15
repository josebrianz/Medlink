package com.example.medilink2.data

import com.example.medilink2.ui.screens.DrugItem
import com.example.medilink2.ui.screens.PharmacyDetails

/**
 * Repository responsible for retrieving pharmacy inventory and price details.
 * In a real app, this would fetch data from a Room database or a Remote API.
 */
class PharmacyRepository {

    // Mock backend data for "Price retrieval" and "Inventory management"
    fun getPharmacyDetails(pharmacyId: String): PharmacyDetails {
        // Simulating a backend lookup for the specific pharmacy
        return PharmacyDetails(
            name = "MedPlus Pharmacy",
            location = "Kampala Road, Plot 23",
            distance = "0.8 km",
            rating = "4.8",
            closingTime = "9:00 PM",
            inventory = getInventoryForPharmacy(pharmacyId)
        )
    }

    private fun getInventoryForPharmacy(pharmacyId: String): List<DrugItem> {
        // This simulates the "Price retrieval" and "Inventory management" backend logic
        return listOf(
            DrugItem("Paracetamol", "Pain Relief", "UGX 3,000", true, "High"),
            DrugItem("Amoxicillin", "Antibiotic", "UGX 12,000", true, "Medium"),
            DrugItem("Ibuprofen", "Pain Relief", "UGX 5,500", false, "Out of Stock"),
            DrugItem("Cetirizine", "Allergy", "UGX 4,000", true, "High"),
            DrugItem("Panadol Extra", "Pain Relief", "UGX 4,500", true, "Low"),
            DrugItem("Vitamin C", "Supplements", "UGX 15,000", true, "Medium"),
            DrugItem("Metformin", "Diabetes", "UGX 8,000", true, "Low")
        )
    }
}
