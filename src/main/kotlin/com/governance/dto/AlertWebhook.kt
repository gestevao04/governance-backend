package com.governance.dto

data class AlertWebhook(
    val type: String = "cost_alert",
    val amount: Double,
    val model: String,
    val tokens: Int,
)