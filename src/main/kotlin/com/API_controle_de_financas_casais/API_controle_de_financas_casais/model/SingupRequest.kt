package com.API_controle_de_financas_casais.API_controle_de_financas_casais.model

data class SignupRequest(
    val email: String,
    val password: String,
    val name: String
)