package com.tecsup.proyecto.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CierreCajaScreen(navController: NavController) {
    val context = LocalContext.current
    val mensaje by remember {
        mutableStateOf(
            context.getSharedPreferences("cash_close_prefs", android.content.Context.MODE_PRIVATE)
                .getString("last_closure_message", null)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🧾 Cierre de Caja",
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
                    containerColor = Color(0xFF3B82F6),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumen del cierre",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = mensaje ?: "No hay cierre registrado",
                        fontSize = 16.sp,
                        color = Color(0xFF374151)
                    )
                    // Balance destacado con color segun signo
                    val balanceValue: Double? = remember(mensaje) {
                        mensaje?.let {
                            val regex = Regex("Balance\\s*S/\\s*([-+]?[0-9]*\\.?[0-9]+)")
                            val match = regex.find(it)
                            match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
                        }
                    }
                    if (balanceValue != null) {
                        val isPositive = balanceValue >= 0.0
                        val balanceColor = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
                        Text(
                            text = "Balance: S/ ${"%.2f".format(abs(balanceValue))} ${if (balanceValue >= 0) "(positivo)" else "(negativo)"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = balanceColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("route_home") {
                        popUpTo("route_home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Volver al inicio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
