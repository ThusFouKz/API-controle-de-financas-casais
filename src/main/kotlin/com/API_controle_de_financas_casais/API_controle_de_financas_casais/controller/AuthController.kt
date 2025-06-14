package com.API_controle_de_financas_casais.API_controle_de_financas_casais.controller

import com.API_controle_de_financas_casais.API_controle_de_financas_casais.model.LoginRequest
import com.API_controle_de_financas_casais.API_controle_de_financas_casais.model.SignupRequest
import com.API_controle_de_financas_casais.API_controle_de_financas_casais.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<Map<String, String>> {
        println("Signup chamado com: ${request.email}")
        val token = authService.signup(request.email, request.password, request.name)
        return ResponseEntity.ok(mapOf("token" to token))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        val tokens = authService.login(request.email, request.password)
        return ResponseEntity.ok(tokens)
    }
}
