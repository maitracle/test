package com.example.demo.application.common.dto.request

/**
 * 장바구니 계산 요청 DTO
 */
data class CalculateCartRequest(
    val userId: Long,
    val items: List<CartItemRequest>
)

/**
 * 장바구니 아이템 요청 DTO
 */
data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)
