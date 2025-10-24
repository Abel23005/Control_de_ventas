package com.tecsup.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.proyecto.data.InMemoryStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cierre de Caja / Reportes", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        ReportesContent(Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesContent(modifier: Modifier = Modifier) {
    // Date handling using Calendar for minSdk 24 compatibility
    val tz = remember { TimeZone.getDefault() }
    var calendar by remember { mutableStateOf(Calendar.getInstance(tz)) }

    fun dayBoundsMillis(cal: Calendar): Pair<Long, Long> {
        val start = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
        return start.timeInMillis to end.timeInMillis
    }

    val (startOfDay, endOfDay) = dayBoundsMillis(calendar)
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateLabel = remember(calendar.timeInMillis) { dateFormat.format(Date(calendar.timeInMillis)) }

    val totalVentas = InMemoryStore.totalSalesAmountForDay(startOfDay, endOfDay)
    val totalCompras = InMemoryStore.totalPurchasesAmountForDay(startOfDay, endOfDay)
    val utilidad = totalVentas - totalCompras

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Resumen general", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                calendar = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
            }) { Text("◀ Día anterior") }
            OutlinedButton(onClick = {
                calendar = Calendar.getInstance(tz)
            }) { Text("Hoy") }
            OutlinedButton(onClick = {
                calendar = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
            }) { Text("Día siguiente ▶") }
        }
        Text("Fecha: ${dateLabel}")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Total de ventas del día: S/ ${"%.2f".format(totalVentas)}", fontWeight = FontWeight.Medium)
                Text("Total de compras del día: S/ ${"%.2f".format(totalCompras)}", fontWeight = FontWeight.Medium)
                Divider()
                Text("Utilidad (ventas – compras): S/ ${"%.2f".format(utilidad)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        // Optional: filter by producto (basic)
        var filterExpanded by remember { mutableStateOf(false) }
        var selectedProductIndex by remember { mutableStateOf(-1) } // -1 for all
        val products = InMemoryStore.products

        ExposedDropdownMenuBox(expanded = filterExpanded, onExpandedChange = { filterExpanded = !filterExpanded }) {
            OutlinedTextField(
                value = if (selectedProductIndex == -1) "Todos los productos" else products.getOrNull(selectedProductIndex)?.name ?: "",
                onValueChange = {},
                label = { Text("Filtrar por producto (opcional)") },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                DropdownMenuItem(text = { Text("Todos") }, onClick = { selectedProductIndex = -1; filterExpanded = false })
                products.forEachIndexed { idx, p ->
                    DropdownMenuItem(text = { Text(p.name) }, onClick = {
                        selectedProductIndex = idx
                        filterExpanded = false
                    })
                }
            }
        }

        val filteredProductId = if (selectedProductIndex >= 0) products.getOrNull(selectedProductIndex)?.id else null
        val ventasFiltradas = InMemoryStore.sales.filter { it.timestamp in startOfDay..endOfDay && (filteredProductId == null || it.productId == filteredProductId) }
        val comprasFiltradas = InMemoryStore.purchases.filter { it.timestamp in startOfDay..endOfDay && (filteredProductId == null || it.productId == filteredProductId) }
        val totalVentasFiltro = ventasFiltradas.sumOf { it.quantity * it.unitPrice }
        val totalComprasFiltro = comprasFiltradas.sumOf { it.quantity * it.unitCost }
        val utilidadFiltro = totalVentasFiltro - totalComprasFiltro

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Totales con filtro", fontWeight = FontWeight.SemiBold)
                Text("Ventas: S/ ${"%.2f".format(totalVentasFiltro)}")
                Text("Compras: S/ ${"%.2f".format(totalComprasFiltro)}")
                Divider()
                Text("Utilidad: S/ ${"%.2f".format(utilidadFiltro)}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
