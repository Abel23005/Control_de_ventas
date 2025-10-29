package com.tecsup.proyecto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.proyecto.data.product.Producto
import com.tecsup.proyecto.data.product.ProductoDao
import com.tecsup.proyecto.data.reportes.PurchaseEntity
import com.tecsup.proyecto.data.reportes.ReportesDao
import com.tecsup.proyecto.data.reportes.SaleEntity

@Database(
    entities = [Producto::class, SaleEntity::class, PurchaseEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun reportesDao(): ReportesDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "control_ventas.db"
                ).fallbackToDestructiveMigration().build()
                    .also { INSTANCE = it }
            }
    }
}
