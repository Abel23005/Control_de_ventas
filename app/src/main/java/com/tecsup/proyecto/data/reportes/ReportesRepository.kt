package com.tecsup.proyecto.data.reportes

import com.tecsup.proyecto.data.InMemoryStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportesRepository(private val dao: ReportesDao) {
    suspend fun sumVentas(start: Long, end: Long): Double = withContext(Dispatchers.IO) {
        dao.sumVentas(start, end)
    }

    suspend fun sumCompras(start: Long, end: Long): Double = withContext(Dispatchers.IO) {
        dao.sumCompras(start, end)
    }

    suspend fun seedIfEmptyFromInMemory() = withContext(Dispatchers.IO) {
        val salesCount = dao.salesCount()
        val purchasesCount = dao.purchasesCount()
        if (salesCount == 0L && purchasesCount == 0L) {
            if (InMemoryStore.sales.isNotEmpty()) {
                val sales = InMemoryStore.sales.map {
                    SaleEntity(
                        productId = it.productId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice,
                        timestamp = it.timestamp
                    )
                }
                dao.insertSales(sales)
            }
            if (InMemoryStore.purchases.isNotEmpty()) {
                val purchases = InMemoryStore.purchases.map {
                    PurchaseEntity(
                        productId = it.productId,
                        quantity = it.quantity,
                        unitCost = it.unitCost,
                        timestamp = it.timestamp
                    )
                }
                dao.insertPurchases(purchases)
            }
        }
    }
}
