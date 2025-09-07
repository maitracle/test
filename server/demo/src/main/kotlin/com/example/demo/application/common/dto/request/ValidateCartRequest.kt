package com.example.demo.application.common.dto.request

/**
 * 장바구니 검증 요청 DTO
 */
data class ValidateCartRequest(
    val userId: Long,
    val items: List<CartItemRequest>
)
