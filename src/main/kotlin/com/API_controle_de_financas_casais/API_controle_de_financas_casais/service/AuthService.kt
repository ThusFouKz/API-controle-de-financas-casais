package com.API_controle_de_financas_casais.API_controle_de_financas_casais.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class AuthService(
    private val firebaseAuth: FirebaseAuth
) {

    fun signup(email: String, password: String, name: String): String {
        val userRecord = firebaseAuth.createUser(
            UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(name)
        )
        return firebaseAuth.createCustomToken(userRecord.uid)
    }

    fun login(email: String, password: String): Map<String, Any> {
        val client = HttpClient.newHttpClient()
        val reqBody = mapOf(
            "email" to email,
            "password" to password,
            "returnSecureToken" to true
        )
        val jsonBody = ObjectMapper().writeValueAsString(reqBody)

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyDpNMxcmsxHLmlOXHXnMznpkxhSH3UiP64"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw RuntimeException("Login failed: ${response.body()}")
        }

        val jsonResponse = ObjectMapper().readValue(response.body(), Map::class.java) as Map<String, Any>
        return jsonResponse
    }
}
