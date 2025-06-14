package com.API_controle_de_financas_casais.API_controle_de_financas_casais.service

import com.API_controle_de_financas_casais.API_controle_de_financas_casais.model.Despesa
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class DespesaService(
    val firestore: Firestore
) {
    fun addDespesa(despesa: Despesa): String {
        val docRef = firestore.collection("despesas").document()
        // Aguarda a operação ser concluída para garantir consistência
        try {
            docRef.set(despesa).get()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Erro ao adicionar despesa", e)
        } catch (e: ExecutionException) {
            throw RuntimeException("Erro ao adicionar despesa", e)
        }
        return docRef.id
    }

    fun listExpensesByCasal(casalId: String): List<Despesa> {
        val snapshot = firestore.collection("despesas")
            .whereEqualTo("casalId", casalId)  // campo alinhado ao seu modelo
            .get()
            .get()

        return snapshot.documents.mapNotNull {
            it.toObject(Despesa::class.java).copy(id = it.id)
        }
    }

    fun listExpensesByUser(userId: String): List<Despesa> {
        val snapshot = firestore.collection("despesas")
            .whereEqualTo("userId", userId)
            .get()
            .get()

        return snapshot.documents.mapNotNull {
            it.toObject(Despesa::class.java)?.copy(id = it.id)
        }
    }

    fun listExpensesByDateAndUserOrCasal(
        startDate: String,
        endDate: String,
        userId: String? = null,
        casalId: String? = null
    ): List<Despesa> {
        if (userId == null && casalId == null) {
            throw IllegalArgumentException("Deve ser informado userId ou casalId")
        }

        val collection = firestore.collection("despesas")
        var query = collection.whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)

        query = when {
            userId != null -> query.whereEqualTo("userId", userId)
            else -> query.whereEqualTo("casalId", casalId)
        }

        val snapshot = query.get().get()
        return snapshot.documents.mapNotNull {
            it.toObject(Despesa::class.java)?.copy(id = it.id)
        }
    }


    fun updateExpenseIfBelongsToCasal(despesa: Despesa) {
        if (despesa.id == null) {
            throw IllegalArgumentException("ID da despesa é obrigatório para atualização")
        }
        val docRef = firestore.collection("despesas").document(despesa.id)
        try {
            docRef.set(despesa).get()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Erro ao atualizar despesa", e)
        } catch (e: ExecutionException) {
            throw RuntimeException("Erro ao atualizar despesa", e)
        }
    }

    fun deleteExpense(despesaId: String) {
        val docRef = firestore.collection("despesas").document(despesaId)
        try {
            docRef.delete().get()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Erro ao deletar despesa", e)
        } catch (e: ExecutionException) {
            throw RuntimeException("Erro ao deletar despesa", e)
        }
    }
}
