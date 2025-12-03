package com.governance.repository.entities

data class RequestEntity(
    val id: Long? = null,
    val timestamp: String,
    val model: String,
    val tokens: Int,
    val cost: Double,
    val inputText: String? = null,
    val outputText: String? = null,
    val apiKeyHash: String
)