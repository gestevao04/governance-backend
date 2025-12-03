package com.governance.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    fun getCurrentDate(): String = LocalDate.now().format(dateFormatter)
    
    fun getCurrentDateTime(): String = LocalDateTime.now().format(dateTimeFormatter)
}