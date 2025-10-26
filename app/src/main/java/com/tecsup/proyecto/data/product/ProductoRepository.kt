package com.tecsup.proyecto.data.product

import com.tecsup.proyecto.data.AppDatabase
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val db: AppDatabase) {
    private val dao = db.productoDao()

    fun getAll(): Flow<List<Producto>> = dao.getAll()

    suspend fun insert(nombre: String, precio: Double, stock: Int) {
        dao.insert(Producto(nombre = nombre, precio = precio, stock = stock))
    }

    suspend fun update(producto: Producto) {
        dao.update(producto)
    }

    suspend fun delete(producto: Producto) {
        dao.delete(producto)
    }
}
