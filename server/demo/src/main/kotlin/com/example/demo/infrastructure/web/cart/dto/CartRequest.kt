package com.example.demo.infrastructure.web.cart.dto

/**
 * 장바구니 웹 요청 DTO
 */
data class CartRequest(
    val userId: Long,
    val items: List<CartItemWebRequest>
)

/**
 * 장바구니 아이템 웹 요청 DTO
 */
data class CartItemWebRequest(
    val productId: Long,
    val quantity: Int
)
