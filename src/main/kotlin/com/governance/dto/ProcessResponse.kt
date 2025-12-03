package com.governance.dto

data class ProcessResponse(
    val status: String = "ok",
    val requestCost: Double,
)