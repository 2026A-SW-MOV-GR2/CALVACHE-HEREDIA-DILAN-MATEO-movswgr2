package com.example.proyecto_red_seguridad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_red_seguridad.network.Post
import com.example.proyecto_red_seguridad.network.RestClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RestViewModel : ViewModel() {
    private val restClient = RestClient()

    val postIdInput = MutableStateFlow("")
    val postTitle = MutableStateFlow("")
    val postBody = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val message = MutableStateFlow("")

    fun fetchPost() {
        val id = postIdInput.value.toIntOrNull() ?: run {
            message.value = "ID inválido"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                val post = restClient.getPost(id)
                postTitle.value = post.title
                postBody.value = post.body
                message.value = "Post cargado con éxito"
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updatePost() {
        val id = postIdInput.value.toIntOrNull() ?: run {
            message.value = "ID inválido"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                val postToUpdate = Post(
                    id = id,
                    title = postTitle.value,
                    body = postBody.value
                )
                restClient.updatePost(id, postToUpdate)
                message.value = "HTTP 200 OK"
            } catch (e: Exception) {
                message.value = "Error al actualizar: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
