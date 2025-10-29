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
import com.tecsup.proyecto.data.InMemoryStore
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.proyecto.data.AppDatabase
import com.tecsup.proyecto.data.reportes.ReportesRepository
import com.tecsup.proyecto.data.reportes.ReportesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReportesScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📊 Cierre de Caja ",
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        ReportesContent(navController, paddingValues, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesContent(navController: NavController, paddingValues: PaddingValues, snackbarHostState: SnackbarHostState) {
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

    // Room + ViewModel (mínimo acoplamiento)
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val repo = remember { ReportesRepository(db.reportesDao()) }
    val vm: ReportesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReportesViewModel(repo) as T
        }
    })
    LaunchedEffect(Unit) { vm.seedIfEmpty() }
    LaunchedEffect(startOfDay, endOfDay) { vm.loadSums(startOfDay, endOfDay) }
    val montoVentas by vm.ventas.collectAsState(initial = 0.0)
    val montoCompras by vm.compras.collectAsState(initial = 0.0)

    val ventasDia = remember(InMemoryStore.sales.size, calendar.timeInMillis) {
        InMemoryStore.sales.filter { it.timestamp in startOfDay..endOfDay }
    }
    val comprasDia = remember(InMemoryStore.purchases.size, calendar.timeInMillis) {
        InMemoryStore.purchases.filter { it.timestamp in startOfDay..endOfDay }
    }

    val totalVentas = ventasDia.size
    val totalCompras = comprasDia.size
    val balanceGeneral = montoVentas - montoCompras

    // Filtro por producto eliminado según solicitud
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) } }) { Text("◀ Día anterior") }
            OutlinedButton(onClick = { calendar = Calendar.getInstance(tz) }) { Text("Hoy") }
            OutlinedButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) } }) { Text("Día siguiente ▶") }
        }
        Text(
            text = "Fecha: ${dateLabel}",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Text(
            text = "Resumen del día",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "💰", fontSize = 36.sp)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total de Ventas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalVentas ventas",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Text(
                    text = "S/ ${String.format("%.2f", montoVentas)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🧾", fontSize = 36.sp)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total de Compras",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalCompras compras",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Text(
                    text = "S/ ${String.format("%.2f", montoCompras)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (balanceGeneral >= 0)
                    Color(0xFF10B981).copy(alpha = 0.1f)
                else
                    Color(0xFFEF4444).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Balance General",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (balanceGeneral >= 0) "Ganancia" else "Pérdida",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/ ${String.format("%.2f", balanceGeneral)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (balanceGeneral >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ingresos:",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", montoVentas)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Egresos:",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", montoCompras)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF59E0B)
                    )
                }
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFE5E7EB)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Balance:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", balanceGeneral)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (balanceGeneral >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }

        // Se removió el filtro por producto y su tarjeta de totales

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val fechaHora = SimpleDateFormat("EEEE d 'de' MMMM yyyy HH:mm", Locale("es", "ES")).format(Date())
                val textoPosNeg = if (balanceGeneral >= 0) "positivo" else "negativo"
                val mensaje = "Cierre ${fechaHora} — Balance S/ ${"%.2f".format(balanceGeneral)} ${textoPosNeg}"
                // Persistir mensaje para mostrarlo de forma permanente
                val prefs = context.getSharedPreferences("cash_close_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("last_closure_message", mensaje).apply()
                navController.navigate("route_cierre")
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
                text = "Cerrar Caja",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}