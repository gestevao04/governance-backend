package com.governance.services

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

private val logger = KotlinLogging.logger {}

@Service
class CostService {
    
    private val modelPrices = mapOf(
        "gpt-4.1" to 0.000002,
        "gpt-4o-mini" to 0.0000006,
        "sonnet" to 0.000003
    )
    
    fun calculateCost(model: String, tokens: Int): Double {
        val pricePerToken = modelPrices[model.lowercase()] ?: modelPrices["gpt-4.1"]!!
        val cost = pricePerToken * tokens
        
        logger.debug { 
            mapOf(
                "action" to "calculate_cost",
                "model" to model,
                "tokens" to tokens,
                "pricePerToken" to pricePerToken,
                "cost" to cost
            )
        }
        
        return BigDecimal(cost).setScale(6, RoundingMode.HALF_UP).toDouble()
    }
    
    fun getSupportedModels(): Set<String> = modelPrices.keys
}