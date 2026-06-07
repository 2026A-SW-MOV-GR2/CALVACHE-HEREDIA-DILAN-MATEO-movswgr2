package com.example.proyecto_red_seguridad.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RestClient {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val baseUrl = "https://jsonplaceholder.typicode.com/posts"

    suspend fun getPost(id: Int): Post {
        return client.get("$baseUrl/$id").body()
    }

    suspend fun updatePost(id: Int, post: Post): Post {
        return client.put("$baseUrl/$id") {
            contentType(ContentType.Application.Json)
            setBody(post)
        }.body()
    }
}
