package com.example.proyecto_red_seguridad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RestScreen(viewModel: RestViewModel) {
    val postIdInput by viewModel.postIdInput.collectAsState()
    val postTitle by viewModel.postTitle.collectAsState()
    val postBody by viewModel.postBody.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = postIdInput,
            onValueChange = { viewModel.postIdInput.value = it },
            label = { Text("ID del Post") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Button(
            onClick = { viewModel.fetchPost() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Consultar (GET)")
        }

        OutlinedTextField(
            value = postTitle,
            onValueChange = { viewModel.postTitle.value = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = postBody,
            onValueChange = { viewModel.postBody.value = it },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Button(
            onClick = { viewModel.updatePost() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Actualizar (PUT)")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (message.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}
