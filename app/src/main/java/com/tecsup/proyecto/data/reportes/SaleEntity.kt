package com.tecsup.proyecto.data.reportes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double,
    val timestamp: Long
)
