package com.example.medilink2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pharmacies")
data class PharmacyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val distance: String,
    val rating: String,
    val closingTime: String,
    val isOpen: Boolean,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "drugs")
data class DrugEntity(
    @PrimaryKey val id: String,
    val pharmacyId: String,
    val name: String,
    val category: String,
    val price: String,
    val inStock: Boolean,
    val stockLevel: String
)
