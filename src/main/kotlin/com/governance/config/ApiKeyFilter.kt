package com.governance.config

import com.governance.utils.HashUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class ApiKeyFilter : OncePerRequestFilter() {
    
    @Value("\${governanca.api-key}")
    private lateinit var configuredApiKey: String
    
    private val publicPaths = listOf("/actuator", "/health")
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        
        if (publicPaths.any { path.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }
        
        val apiKey = request.getHeader("x-api-key")
        
        if (apiKey.isNullOrBlank()) {
            logger.warn { 
                mapOf(
                    "action" to "auth_failed",
                    "reason" to "missing_api_key",
                    "path" to path
                )
            }
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "API Key is required")
            return
        }
        
        if (apiKey != configuredApiKey) {
            val apiKeyHash = HashUtil.sha256(apiKey)
            logger.warn { 
                mapOf(
                    "action" to "auth_failed",
                    "reason" to "invalid_api_key",
                    "apiKeyHash" to apiKeyHash,
                    "path" to path
                )
            }
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid API Key")
            return
        }
        
        logger.debug { 
            mapOf(
                "action" to "auth_success",
                "apiKeyHash" to HashUtil.sha256(apiKey),
                "path" to path
            )
        }
        
        request.setAttribute("apiKey", apiKey)
        filterChain.doFilter(request, response)
    }
}