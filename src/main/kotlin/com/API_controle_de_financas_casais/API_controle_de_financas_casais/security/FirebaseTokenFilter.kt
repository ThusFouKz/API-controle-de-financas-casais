package com.API_controle_de_financas_casais.API_controle_de_financas_casais.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseTokenFilter(
    val firebaseAuth: FirebaseAuth
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        println("FirebaseTokenFilter: path=${request.requestURI}")

        val path = request.requestURI

        // Pula autenticação para rotas públicas
        if (path.startsWith("/auth/") || path.startsWith("/casais")) {
            println("FirebaseTokenFilter: ignorando auth route")

            filterChain.doFilter(request, response)
            return
        }

        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        val token = header.removePrefix("Bearer ").trim()
        try {
            val decodedToken = firebaseAuth.verifyIdToken(token)
            request.setAttribute("firebaseUser", decodedToken)
            filterChain.doFilter(request, response)
        } catch (e: FirebaseAuthException) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
        }
    }

}
