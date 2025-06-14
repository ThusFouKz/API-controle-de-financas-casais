package com.API_controle_de_financas_casais.API_controle_de_financas_casais.controller

import com.API_controle_de_financas_casais.API_controle_de_financas_casais.model.Despesa
import com.API_controle_de_financas_casais.API_controle_de_financas_casais.service.CasalService
import com.API_controle_de_financas_casais.API_controle_de_financas_casais.service.DespesaService
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.Date

@RestController
@RequestMapping("/expenses")
class DespesaController(
    private val despesaService: DespesaService,
    private val casalService: CasalService
) {

    @PostMapping
    fun createDespesa(
        @RequestBody request: Despesa,
        httpRequest: HttpServletRequest
    ): ResponseEntity<String> {
        val firebaseUser = httpRequest.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseUser.uid

        val casalId = casalService.getCasalIdDoFirestore(userId)
            ?: return ResponseEntity.badRequest().body("casalId não encontrado para o usuário")

        val id = despesaService.addDespesa(request.copy(
            casalId = casalId,
            userId = userId
        ))
        return ResponseEntity.ok(id)
    }

    @GetMapping("/buscar-por-casal")
    fun listDespesasCasal(
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<Despesa>> {
        val firebaseUser = httpRequest.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseUser.uid

        val casalId = casalService.getCasalIdDoFirestore(userId)
            ?: throw IllegalArgumentException("casalId não encontrado para o usuário")

        val despesas = despesaService.listExpensesByCasal(casalId)
        return ResponseEntity.ok(despesas)
    }

    @GetMapping("/buscar-por-user")
    fun listDespesasUser(
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<Despesa>> {
        val firebaseUser = httpRequest.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseUser.uid

        val despesas = despesaService.listExpensesByUser(userId)
        return ResponseEntity.ok(despesas)
    }

    @PutMapping
    fun updateDespesa(
        @RequestBody despesa: Despesa,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        val firebaseUser = httpRequest.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseUser.uid

        val casalId = casalService.getCasalIdDoFirestore(userId)
            ?: return ResponseEntity.badRequest().body("casalId não encontrado para o usuário")

        if (despesa.id == null) {
            return ResponseEntity.badRequest().body("ID da despesa é obrigatório")
        }

        val despesaAtualizada = despesa.copy(
            casalId = casalId,
            userId = userId
        )

        despesaService.updateExpenseIfBelongsToCasal(despesaAtualizada)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{despesaId}")
    fun deleteDespesa(
        @PathVariable despesaId: String,
    ): ResponseEntity<Any> {
        despesaService.deleteExpense(despesaId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/filter")
    fun filterDespesasByDateAndUserOrCasal(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) casalId: String?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<Despesa>> {

        val firebaseUser = httpRequest.getAttribute("firebaseUser") as FirebaseToken
        val userIdX = userId ?: firebaseUser.uid
        val casalIdX = casalId ?: casalService.getCasalIdDoFirestore(userIdX)
            ?: throw IllegalArgumentException("casalId não encontrado para o usuário")

        if (userIdX == null && casalIdX == null) {
            return ResponseEntity.badRequest().build()
        }

        val despesas = despesaService.listExpensesByDateAndUserOrCasal(
            startDate.toString(),
            endDate.toString(),
            userIdX,
            casalIdX
        )
        return ResponseEntity.ok(despesas)
    }

}
