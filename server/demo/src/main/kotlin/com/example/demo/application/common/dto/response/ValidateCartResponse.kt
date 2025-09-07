package com.example.demo.application.common.dto.response

/**
 * 장바구니 검증 응답 DTO
 */
data class ValidateCartResponse(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)
