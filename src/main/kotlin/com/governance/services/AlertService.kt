package com.governance.services

import com.governance.dto.AlertWebhook
import com.governance.repository.entities.RequestEntity
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class AlertService(
    private val webClient: WebClient = WebClient.builder().build()
) {
    
    @Value("\${governanca.webhook-url}")
    private lateinit var webhookUrl: String
    
    @Value("\${governanca.cost-threshold}")
    private var costThreshold: Double = 0.01
    
    fun sendCostAlert(entity: RequestEntity) {
        val alert = AlertWebhook(
            amount = entity.cost,
            model = entity.model,
            tokens = entity.tokens
        )
        
        logger.info { 
            mapOf(
                "action" to "send_alert",
                "cost" to entity.cost,
                "model" to entity.model,
                "webhookUrl" to webhookUrl
            )
        }
        
        webClient.post()
            .uri(webhookUrl)
            .bodyValue(alert)
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume { error ->
                logger.error { 
                    mapOf(
                        "action" to "alert_failed",
                        "error" to error.message
                    )
                }
                Mono.empty()
            }
            .subscribe { response ->
                logger.debug { 
                    mapOf(
                        "action" to "alert_sent",
                        "response" to response
                    )
                }
            }
    }
}