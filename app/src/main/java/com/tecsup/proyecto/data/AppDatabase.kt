package com.tecsup.proyecto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.proyecto.data.product.Producto
import com.tecsup.proyecto.data.product.ProductoDao

@Database(
    entities = [Producto::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao

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
