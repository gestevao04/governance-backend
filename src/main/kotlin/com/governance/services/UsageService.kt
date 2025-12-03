package com.governance.services

import com.governance.dto.DailyUsageResponse
import com.governance.repository.CacheService
import com.governance.utils.DateUtils
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

private val logger = KotlinLogging.logger {}

@Service
class UsageService(
    private val cacheService: CacheService
) {
    
    fun getDailyUsage(date: String? = null): DailyUsageResponse {
        val targetDate = date ?: DateUtils.getCurrentDate()
        val requests = cacheService.findByDate(targetDate)
        
        val totalRequests = requests.size
        val totalCost = requests.sumOf { it.cost }
        
        val costByModel = requests
            .groupBy { it.model }
            .mapValues { (_, reqs) -> 
                BigDecimal(reqs.sumOf { it.cost })
                    .setScale(6, RoundingMode.HALF_UP)
                    .toDouble()
            }
        
        val summary = generateSummary(targetDate, totalRequests, totalCost)
        
        logger.info { 
            mapOf(
                "action" to "get_daily_usage",
                "date" to targetDate,
                "totalRequests" to totalRequests,
                "totalCost" to totalCost,
                "models" to costByModel.keys
            )
        }
        
        return DailyUsageResponse(
            date = targetDate,
            totalRequests = totalRequests,
            totalCost = BigDecimal(totalCost).setScale(6, RoundingMode.HALF_UP).toDouble(),
            costByModel = costByModel,
            summary = summary
        )
    }
    
    private fun generateSummary(date: String, totalRequests: Int, totalCost: Double): String {
        val costFormatted = String.format("%.6f", totalCost)
        
        return when (totalRequests) {
            0 -> "No requests processed on $date."
            1 -> "On $date, the system processed 1 request costing \$$costFormatted."
            else -> "On $date, the system processed $totalRequests requests costing \$$costFormatted."
        }
    }
}