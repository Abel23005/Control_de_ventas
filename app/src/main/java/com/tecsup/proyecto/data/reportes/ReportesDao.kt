package com.tecsup.proyecto.data.reportes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReportesDao {
    @Query("SELECT COALESCE(SUM(quantity * unitPrice), 0) FROM sales WHERE timestamp BETWEEN :start AND :end")
    suspend fun sumVentas(start: Long, end: Long): Double

    @Query("SELECT COALESCE(SUM(quantity * unitCost), 0) FROM purchases WHERE timestamp BETWEEN :start AND :end")
    suspend fun sumCompras(start: Long, end: Long): Double

    @Query("SELECT COUNT(*) FROM sales")
    suspend fun salesCount(): Long

    @Query("SELECT COUNT(*) FROM purchases")
    suspend fun purchasesCount(): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSales(items: List<SaleEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPurchases(items: List<PurchaseEntity>)
}
