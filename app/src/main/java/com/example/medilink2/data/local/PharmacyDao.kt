package com.example.medilink2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM pharmacies")
    fun getAllPharmacies(): Flow<List<PharmacyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacies(pharmacies: List<PharmacyEntity>)

    @Query("DELETE FROM pharmacies")
    suspend fun clearAll()

    @Query("SELECT * FROM drugs WHERE pharmacyId = :pharmacyId")
    fun getDrugsForPharmacy(pharmacyId: String): Flow<List<DrugEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(drugs: List<DrugEntity>)
}
