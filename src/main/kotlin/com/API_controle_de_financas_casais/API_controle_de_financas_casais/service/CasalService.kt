package com.API_controle_de_financas_casais.API_controle_de_financas_casais.service

import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Service

@Service
class CasalService (
    val firestore: Firestore
){

    fun getCasalIdDoFirestore(userId: String): String? {
        val snapshot = firestore.collection("casais")
            .whereArrayContains("userIds", userId)
            .get()
            .get()

        val doc = snapshot.documents.firstOrNull()
        return doc?.id
    }
}