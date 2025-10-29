package com.tecsup.proyecto.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.proyecto.data.product.Producto
import androidx.compose.ui.platform.LocalContext
import com.tecsup.proyecto.data.AppDatabase
import com.tecsup.proyecto.data.reportes.PurchaseEntity
import kotlinx.coroutines.launch

data class Compra(
    val id: Int,
    val producto: String,
    val cantidad: Int,
    val costoUnitario: Double,
    val total: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ComprasScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val dao = remember { db.reportesDao() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🧾 Compras ",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF59E0B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        ComprasContent(navController, paddingValues, onRegistrarCompra = { p, cantidad, total ->
            scope.launch {
                dao.insertPurchases(
                    listOf(
                        PurchaseEntity(
                            productId = p.id,
                            quantity = cantidad,
                            unitCost = p.precio,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                )
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasContent(
    navController: NavController,
    paddingValues: PaddingValues,
    onRegistrarCompra: (Producto, Int, Double) -> Unit
) {
    var compras by remember { mutableStateOf(listOf<Compra>()) }

    val productosDisponibles = listOf(
        Producto(1, "Harina (kg)", 3.50, 20),
        Producto(2, "Azúcar (kg)", 4.00, 15),
        Producto(3, "Aceite (litro)", 8.50, 10),
        Producto(4, "Sal (kg)", 2.00, 25),
        Producto(5, "Levadura (sobre)", 1.50, 30),
        Producto(6, "Mantequilla (kg)", 12.00, 8)
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var cantidadText by remember { mutableStateOf("") }

    val cantidad = cantidadText.toIntOrNull() ?: 0
    val productoSeleccionado = productosDisponibles.getOrNull(selectedIndex)
    val total = cantidad * (productoSeleccionado?.precio ?: 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen de compras",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total compras: ${compras.size}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "Total S/ ${String.format("%.2f", compras.sumOf { it.total })}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (productoSeleccionado != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Precio por unidad:",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "S/ ${String.format("%.2f", productoSeleccionado.precio)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Registrar compra ",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp ))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = productoSeleccionado?.nombre ?: "",
                onValueChange = {},
                label = { Text("Producto") },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF59E0B),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                productosDisponibles.forEachIndexed { index, producto ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", producto.precio)} c/u - Stock: ${producto.stock}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        },
                        onClick = {
                            selectedIndex = index
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = cantidadText,
            onValueChange = { cantidadText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Cantidad comprada") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
        if (productoSeleccionado != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF59E0B).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Precio por unidad: S/ ${String.format("%.2f", productoSeleccionado.precio)}",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total: S/ ${String.format("%.2f", total)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))


        val canRegister = productosDisponibles.isNotEmpty() && cantidad > 0

        Button(
            onClick = {
                productoSeleccionado?.let { p ->
                    onRegistrarCompra(p, cantidad, total)
                    compras = compras + Compra(
                        id = compras.size + 1,
                        producto = p.nombre,
                        cantidad = cantidad,
                        costoUnitario = p.precio,
                        total = total
                    )
                }
                cantidadText = ""
            },

            enabled = canRegister,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF59E0B)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Registrar compra",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (compras.isNotEmpty()) {
            Text(
                text = "Historial de compras",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(compras) { compra ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🧾", fontSize = 24.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = compra.producto,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Cantidad: ${compra.cantidad} x S/ ${String.format("%.2f", compra.costoUnitario)}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Text(
                                text = "S/ ${String.format("%.2f", compra.total)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }
        }
    }
}