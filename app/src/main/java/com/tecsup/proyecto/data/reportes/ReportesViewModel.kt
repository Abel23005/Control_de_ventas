package com.tecsup.proyecto.data.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportesViewModel(private val repo: ReportesRepository) : ViewModel() {
    private val _ventas = MutableStateFlow(0.0)
    val ventas: StateFlow<Double> = _ventas

    private val _compras = MutableStateFlow(0.0)
    val compras: StateFlow<Double> = _compras

    val balance: StateFlow<Double> = MutableStateFlow(0.0).also { balFlow ->
        viewModelScope.launch {
            // keep balance updated when ventas/compras change
            ventas.collect { v ->
                balFlow.value = v - compras.value
            }
        }
        viewModelScope.launch {
            compras.collect { c ->
                balFlow.value = ventas.value - c
            }
        }
    }

    fun seedIfEmpty() {
        viewModelScope.launch { repo.seedIfEmptyFromInMemory() }
    }

    fun loadSums(start: Long, end: Long) {
        viewModelScope.launch {
            val v = repo.sumVentas(start, end)
            val c = repo.sumCompras(start, end)
            _ventas.value = v
            _compras.value = c
        }
    }
}
