package com.tecsup.proyecto.data.reportes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Int,
    val quantity: Int,
    val unitCost: Double,
    val timestamp: Long
)
