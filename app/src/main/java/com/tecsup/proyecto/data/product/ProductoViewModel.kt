package com.tecsup.proyecto.data.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.proyecto.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ProductoRepository(AppDatabase.getInstance(application))

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAll().collectLatest { list ->
                _productos.value = list
            }
        }
    }

    fun agregar(nombre: String, precio: Double, stock: Int) {
        viewModelScope.launch {
            repo.insert(nombre, precio, stock)
        }
    }

    fun actualizar(producto: Producto) {
        viewModelScope.launch {
            repo.update(producto)
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            repo.delete(producto)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                throw IllegalStateException("Use ViewModelProvider with AndroidX default factory in a Composable using viewModel() without custom factory")
            }
        }
    }
}
