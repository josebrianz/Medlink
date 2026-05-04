package com.example.medilink2.data

import com.example.medilink2.ui.components.DrugItem
import com.example.medilink2.ui.screens.PharmacyDetails

/**
 * Repository responsible for retrieving pharmacy inventory and price details.
 * In a real app, this would fetch data from a Room database or a Remote API.
 */
class PharmacyRepository {

    // Mock backend data for "Price retrieval" and "Inventory management"
    fun getPharmacyDetails(pharmacyId: String): PharmacyDetails {
        val pharmacyNames = mapOf(
            "1" to "MedPlus Pharmacy",
            "2" to "City Chemist",
            "3" to "HealthGuard Pharmacy",
            "4" to "QuickMeds",
            "5" to "Allergy Care",
            "6" to "General Wellness",
            "7" to "First Care Pharmacy",
            "8" to "Eco Pharmacy",
            "9" to "Vine Pharmacy",
            "10" to "Family Health Pharmacy",
        )
        
        val locations = mapOf(
            "1" to "Kampala Road, Plot 23",
            "2" to "Jinja Road, Near Total",
            "3" to "Nasser Road, Block B",
            "4" to "Bombo Road, Wandegeya",
            "5" to "Wandegeya Market",
            "6" to "Mulago Hill",
            "7" to "Kikuubo Lane",
            "8" to "Kisementi",
            "9" to "Lugogo Mall",
            "10" to "Ntinda Road"
        )

        return PharmacyDetails(
            id = pharmacyId,
            name = pharmacyNames[pharmacyId] ?: "Unknown Pharmacy",
            location = locations[pharmacyId] ?: "Unknown Location",
            distance = "${(0..5).random()}.${(0..9).random()} km",
            rating = "4.${(0..9).random()}",
            closingTime = "9:00 PM",
            inventory = getInventoryForPharmacy(pharmacyId)
        )
    }

    private fun getInventoryForPharmacy(pharmacyId: String): List<DrugItem> {
        val fullInventory = listOf(
            DrugItem(id = "1", name = "Paracetamol", category = "Pain Relief", price = "UGX 3,000", inStock = true, stockLevel = "High"),
            DrugItem(id = "2", name = "Amoxicillin", category = "Antibiotic", price = "UGX 12,000", inStock = true, stockLevel = "Medium"),
            DrugItem(id = "3", name = "Ibuprofen", category = "Pain Relief", price = "UGX 5,500", inStock = false, stockLevel = "Out of Stock"),
            DrugItem(id = "4", name = "Cetirizine", category = "Allergy", price = "UGX 4,000", inStock = true, stockLevel = "High"),
            DrugItem(id = "5", name = "Panadol Extra", category = "Pain Relief", price = "UGX 4,500", inStock = true, stockLevel = "Low"),
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
        
        // Return a randomized subset of items so different pharmacies look different
        val random = java.util.Random(pharmacyId.hashCode().toLong())
        return fullInventory.shuffled(random).take(8 + random.nextInt(7))
    }
}
