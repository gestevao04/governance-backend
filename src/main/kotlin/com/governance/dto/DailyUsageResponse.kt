package com.governance.dto

data class DailyUsageResponse(
    val date: String,
    val totalRequests: Int,
    val totalCost: Double,
    val costByModel: Map<String, Double>,
    val summary: String
)