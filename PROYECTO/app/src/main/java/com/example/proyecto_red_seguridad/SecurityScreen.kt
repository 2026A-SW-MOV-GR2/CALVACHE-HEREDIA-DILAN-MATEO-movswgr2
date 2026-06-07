package com.example.proyecto_red_seguridad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SecurityScreen(viewModel: SecurityViewModel) {
    val secretKey by viewModel.secretKey.collectAsState()
    val secretValue by viewModel.secretValue.collectAsState()
    val selectedStorage by viewModel.selectedStorage.collectAsState()
    val message by viewModel.message.collectAsState()

    val storageOptions = listOf("SharedPreferences", "DataStore", "Encrypted")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gestión de Secretos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = secretKey,
            onValueChange = { viewModel.secretKey.value = it },
            label = { Text("Llave (Key)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = secretValue,
            onValueChange = { viewModel.secretValue.value = it },
            label = { Text("Valor (Value)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Seleccionar Almacenamiento:", style = MaterialTheme.typography.titleMedium)

        Column(Modifier.selectableGroup()) {
            storageOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (text == selectedStorage),
                            onClick = { viewModel.selectedStorage.value = text },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedStorage),
                        onClick = null // null because the row handle the click
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.saveSecret() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
            Button(
                onClick = { viewModel.recoverSecret() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Recuperar")
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (message.contains("éxito") || message.contains("exitosamente")) 
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
