package com.governance.services

import com.governance.dto.ProcessRequest
import com.governance.dto.ProcessResponse
import com.governance.repository.CacheService
import com.governance.repository.entities.RequestEntity
import com.governance.utils.DateUtils
import com.governance.utils.HashUtil
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

@Service
class ProcessService(
    private val costService: CostService,
    private val alertService: AlertService,
    private val cacheService: CacheService
) {
    
    fun processRequest(request: ProcessRequest, apiKey: String): ProcessResponse {
        val apiKeyHash = HashUtil.sha256(apiKey)
        val cost: Double
        
        val executionTime = measureTimeMillis {
            cost = costService.calculateCost(request.model, request.tokens)
            
            val entity = RequestEntity(
                timestamp = DateUtils.getCurrentDateTime(),
                model = request.model,
                tokens = request.tokens,
                cost = cost,
                inputText = request.input,
                outputText = request.output,
                apiKeyHash = apiKeyHash
            )

            cacheService.save(entity)
            
            alertService.sendCostAlert(entity)
        }

        logger.info {
            mapOf(
                "action" to "process_request",
                "route" to "/process",
                "apiKeyHash" to apiKeyHash,
                "model" to request.model,
                "tokens" to request.tokens,
                "cost" to cost,
                "executionTimeMs" to executionTime
            )
        }
        
        return ProcessResponse(
            requestCost = cost,
        )
    }
}