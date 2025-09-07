package com.example.demo.infrastructure.web.cart.dto

/**
 * 장바구니 검증 웹 응답 DTO
 */
data class ValidationResponse(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)
