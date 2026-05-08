package com.example.medilink2.data

import com.example.medilink2.ui.screens.DrugItem
import com.example.medilink2.ui.screens.PharmacyDetails

/**
 * Repository responsible for retrieving pharmacy inventory and price details.
 * In a real app, this would fetch data from a Room database or a Remote API.
 */
class PharmacyRepository {

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

    fun getAllPharmacies(): List<PharmacyDetails> = pharmacyData

    fun getPharmacyDetails(pharmacyId: String): PharmacyDetails {
        val base = pharmacyData.find { it.id == pharmacyId } ?: pharmacyData[0]
        return base.copy(inventory = getInventoryForPharmacy(pharmacyId))
    }

    fun searchDrugs(query: String): List<DrugItem> {
        return fullInventory.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true) 
        }
    }

    fun getPharmaciesStocking(drugName: String): List<PharmacyDetails> {
        return pharmacyData.filter { pharmacy ->
            getInventoryForPharmacy(pharmacy.id).any { it.name.contains(drugName, ignoreCase = true) }
        }
    }

    private fun getInventoryForPharmacy(pharmacyId: String): List<DrugItem> {
        val random = java.util.Random(pharmacyId.hashCode().toLong())
        return fullInventory.shuffled(random).take(8 + random.nextInt(7))
    }
}
