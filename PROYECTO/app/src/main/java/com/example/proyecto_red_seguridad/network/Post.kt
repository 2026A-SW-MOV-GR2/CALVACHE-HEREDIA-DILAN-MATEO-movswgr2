package com.example.proyecto_red_seguridad.network

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: Int = 1,
    val id: Int = 0,
    val title: String,
    val body: String
)
