package com.tecsup.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tecsup.proyecto.ui.theme.ProyectoTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tecsup.proyecto.screens.LoginScreen
import com.tecsup.proyecto.screens.RegisterScreen
import com.tecsup.proyecto.screens.HomeScreen
import com.tecsup.proyecto.screens.ComprasScreen
import com.tecsup.proyecto.screens.ReportesScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainComponent()
        }
    }
}

@Composable
fun MainComponent() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "route_login"){
        composable("route_login") { LoginScreen(navController) }
        composable("route_register") { RegisterScreen(navController) }
        composable("route_home") { HomeScreen(navController) }
        composable("route_compras") { ComprasScreen(navController) }
        composable("route_reportes") { ReportesScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainComponent()
}