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
        val pharmacies = mapOf(
            "1" to PharmacyDetails("1", "MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "4.8", "9:00 PM", getInventoryForPharmacy("1"), 0.3136, 32.5811),
            "2" to PharmacyDetails("2", "City Chemist", "Jinja Road", "1.2 km", "4.5", "10:00 PM", getInventoryForPharmacy("2"), 0.3162, 32.5855),
            "3" to PharmacyDetails("3", "HealthGuard Pharmacy", "Nasser Rd", "0.5 km", "4.7", "8:30 PM", getInventoryForPharmacy("3"), 0.3120, 32.5880),
            "4" to PharmacyDetails("4", "First Care Pharmacy", "Kikuubo", "1.5 km", "4.2", "7:00 PM", getInventoryForPharmacy("4"), 0.3140, 32.5780),
            "5" to PharmacyDetails("5", "Vine Pharmacy", "Lugogo Mall", "3.2 km", "4.9", "11:00 PM", getInventoryForPharmacy("5"), 0.3250, 32.6020),
            "6" to PharmacyDetails("6", "Eco Pharmacy", "Kisementi", "2.8 km", "4.6", "10:30 PM", getInventoryForPharmacy("6"), 0.3350, 32.5950),
            "7" to PharmacyDetails("7", "Family Health Pharmacy", "Ntinda", "4.5 km", "4.4", "9:30 PM", getInventoryForPharmacy("7"), 0.3540, 32.6110)
        )
        // Simulating a backend lookup for the specific pharmacy
        return pharmacies[pharmacyId] ?: pharmacies["1"]!!
    }

    private fun getInventoryForPharmacy(pharmacyId: String): List<DrugItem> {
        return listOf(
            DrugItem("Paracetamol", "Pain Relief", "UGX 3,000", true, "High"),
            DrugItem("Amoxicillin", "Antibiotic", "UGX 12,000", true, "Medium"),
            DrugItem("Ibuprofen", "Pain Relief", "UGX 5,500", false, "Out of Stock"),
            DrugItem("Cetirizine", "Allergy", "UGX 4,000", true, "High"),
            DrugItem("Panadol Extra", "Pain Relief", "UGX 4,500", true, "Low"),
            DrugItem("Vitamin C", "Supplements", "UGX 15,000", true, "Medium"),
            DrugItem("Metformin", "Diabetes", "UGX 8,000", true, "Low"),
            DrugItem("Insulin Glargine", "Diabetes", "UGX 45,000", true, "Medium"),
            DrugItem("Aspirin 81mg", "Heart", "UGX 2,500", true, "High"),
            DrugItem("Atorvastatin", "Heart", "UGX 18,000", true, "Low"),
            DrugItem("Lisinopril", "Heart", "UGX 10,000", true, "Medium"),
            DrugItem("Salbutamol Inhaler", "General", "UGX 15,000", true, "High"),
            DrugItem("Omeprazole", "General", "UGX 7,000", true, "Medium"),
            DrugItem("Loratadine", "General", "UGX 3,500", true, "High"),
            DrugItem("Diclofenac Gel", "Pain Relief", "UGX 12,000", true, "Medium"),
            DrugItem("Azithromycin", "Antibiotic", "UGX 25,000", false, "Out of Stock"),
            DrugItem("ORS Sachet", "General", "UGX 1,500", true, "High"),
            DrugItem("Metronidazole", "Antibiotic", "UGX 6,000", true, "Medium"),
            DrugItem("Amlodipine", "Heart", "UGX 14,000", true, "High"),
            DrugItem("Ventolin Inhaler", "General", "UGX 22,000", true, "Low"),
            DrugItem("Gaviscon Liquid", "General", "UGX 35,000", true, "Medium"),
            DrugItem("Augustin 625mg", "Antibiotic", "UGX 30,000", true, "High"),
            DrugItem("Folic Acid", "Supplements", "UGX 5,000", true, "High"),
            DrugItem("Durex Condoms", "General", "UGX 10,000", true, "High"),
            DrugItem("Piriton", "Allergy", "UGX 2,500", true, "Medium")
        )
    }
}
