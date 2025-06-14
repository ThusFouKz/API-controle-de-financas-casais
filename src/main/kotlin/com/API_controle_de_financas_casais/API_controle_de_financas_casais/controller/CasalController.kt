package com.API_controle_de_financas_casais.API_controle_de_financas_casais.controller

import com.API_controle_de_financas_casais.API_controle_de_financas_casais.model.Casal
import com.google.cloud.firestore.Firestore
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/casais")
class CasalController(
    private val firestore: Firestore
) {

    @PostMapping()
    fun createCasal(@RequestBody casal: Casal): ResponseEntity<String> {
        val docRef = firestore.collection("casais").document()
        val newCasal = casal.copy(id = docRef.id)
        docRef.set(newCasal)
        return ResponseEntity.ok(newCasal.id)
    }

}