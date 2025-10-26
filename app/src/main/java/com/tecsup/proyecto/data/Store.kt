package com.tecsup.proyecto.data

import androidx.compose.runtime.mutableStateListOf
import java.time.Instant

data class Product(
    val id: Int,
    val name: String,
    var stock: Int,
    val unitPrice: Double
)

data class Sale(
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double,
    val timestamp: Long = Instant.now().toEpochMilli()
)

data class Purchase(
    val productId: Int,
    val quantity: Int,
    val unitCost: Double,
    val timestamp: Long = Instant.now().toEpochMilli()
)

object InMemoryStore {
    val products = mutableStateListOf(
        Product(1, "Producto A", 20, 10.0),
        Product(2, "Producto B", 15, 15.5),
        Product(3, "Producto C", 8, 7.25)
    )
    val sales = mutableStateListOf<Sale>()
    val purchases = mutableStateListOf<Purchase>()

    fun registerPurchase(productId: Int, quantity: Int, unitCost: Double) {
        if (quantity <= 0 || unitCost < 0.0) return
        val pIndex = products.indexOfFirst { it.id == productId }
        if (pIndex >= 0) {
            val p = products[pIndex]
            val updated = p.copy(stock = p.stock + quantity)
            products[pIndex] = updated
            purchases.add(Purchase(productId, quantity, unitCost))
        }
    }

    fun totalSalesAmountForDay(epochDayStart: Long, epochDayEnd: Long): Double {
        return sales.filter { it.timestamp in epochDayStart..epochDayEnd }
            .sumOf { it.quantity * it.unitPrice }
    }

    fun totalPurchasesAmountForDay(epochDayStart: Long, epochDayEnd: Long): Double {
        return purchases.filter { it.timestamp in epochDayStart..epochDayEnd }
            .sumOf { it.quantity * it.unitCost }
    }
}
