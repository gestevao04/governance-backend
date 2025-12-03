package com.governance.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ProcessRequest(
    @field:NotBlank(message = "Model is required")
    val model: String,
    
    @field:Min(value = 1, message = "Tokens must be at least 1")
    val tokens: Int,
    
    val input: String? = null,
    val output: String? = null
)