package com.tecsup.proyecto.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.proyecto.data.InMemoryStore
import com.tecsup.proyecto.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compras / Insumos", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        ComprasContent(Modifier.padding(padding), navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasContent(modifier: Modifier = Modifier, navController: NavController) {
    val products = InMemoryStore.products
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var quantityText by remember { mutableStateOf("") }
    var unitCostText by remember { mutableStateOf("") }

    val quantity = quantityText.toIntOrNull() ?: 0
    val unitCost = unitCostText.toDoubleOrNull() ?: 0.0
    val total = quantity * unitCost

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Registrar compra o ingreso de stock", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = products.getOrNull(selectedIndex)?.name ?: "",
                onValueChange = {},
                label = { Text("Producto") },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                products.forEachIndexed { index, product ->
                    DropdownMenuItem(
                        text = { Text(product.name) },
                        onClick = {
                            selectedIndex = index
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Cantidad comprada") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = unitCostText,
            onValueChange = { unitCostText = it.filter { ch -> ch.isDigit() || ch == '.' }.replace(Regex("\\.(?=.*\\.)"), "") },
            label = { Text("Costo por unidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Total: S/ ${"%.2f".format(total)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        val canRegister = products.isNotEmpty() && quantity > 0 && unitCost >= 0.0
        Button(
            onClick = {
                products.getOrNull(selectedIndex)?.let { p ->
                    InMemoryStore.registerPurchase(p.id, quantity, unitCost)
                }
                quantityText = ""
                unitCostText = ""
            },
            enabled = canRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar compra")
        }
    }
}
