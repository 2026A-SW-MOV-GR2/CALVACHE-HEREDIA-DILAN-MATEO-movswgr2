package com.example.proyecto_red_seguridad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto_red_seguridad.ui.theme.Proyecto_Red_SeguridadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_Red_SeguridadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("Security") }
                    
                    Column(modifier = Modifier.padding(top = 32.dp)) {
                        // Botón de navegación en la parte superior
                        Button(
                            onClick = { 
                                currentScreen = if (currentScreen == "Security") "Rest" else "Security" 
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Text(if (currentScreen == "Security") "Cambiar a Cliente REST" else "Cambiar a Gestión de Secretos")
                        }

                        if (currentScreen == "Security") {
                            val securityViewModel = SecurityViewModel(application)
                            SecurityScreen(viewModel = securityViewModel)
                        } else {
                            val restViewModel = RestViewModel()
                            RestScreen(viewModel = restViewModel)
                        }
                    }
                }
            }
        }
    }
}
