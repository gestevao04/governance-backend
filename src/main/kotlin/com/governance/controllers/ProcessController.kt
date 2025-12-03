package com.governance.controllers

import com.governance.dto.ProcessRequest
import com.governance.dto.ProcessResponse
import com.governance.services.ProcessService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/process")
class ProcessController(
    private val processService: ProcessService
) {
    
    @PostMapping
    fun process(
        @Valid @RequestBody request: ProcessRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ProcessResponse> {
        val apiKey = httpRequest.getAttribute("apiKey") as String
        val response = processService.processRequest(request, apiKey)
        return ResponseEntity.ok(response)
    }
}