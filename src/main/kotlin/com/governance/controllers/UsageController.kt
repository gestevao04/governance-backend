package com.governance.controllers

import com.governance.dto.DailyUsageResponse
import com.governance.services.UsageService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/usage")
class UsageController(
    private val usageService: UsageService
) {
    
    @GetMapping("/daily")
    fun getDailyUsage(
        @RequestParam(required = false) date: String?
    ): ResponseEntity<DailyUsageResponse> {
        val response = usageService.getDailyUsage(date)
        return ResponseEntity.ok(response)
    }
}