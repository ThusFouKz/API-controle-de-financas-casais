package com.API_controle_de_financas_casais.API_controle_de_financas_casais.model

import java.time.LocalDate
import java.util.Date

data class Despesa(
    val id: String? = null,
    val casalId: String? = null,
    val userId: String? = null,
    val quantidade: Double = 0.0,
    val categoria: String = "",
    val description: String? = null,
    val date: String = LocalDate.now().toString()
)