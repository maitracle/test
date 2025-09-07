package com.example.demo.application.common.dto.response

import java.math.BigDecimal

/**
 * 장바구니 계산 응답 DTO
 */
data class CalculateCartResponse(
    val items: List<CartItemResponse>,
    val subtotal: BigDecimal,
    val totalDiscount: BigDecimal,
    val finalAmount: BigDecimal,
    val appliedPromotions: List<AppliedPromotionResponse>
)

/**
 * 장바구니 아이템 응답 DTO
 */
data class CartItemResponse(
    val productId: Long,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)

/**
 * 적용된 프로모션 응답 DTO
 */
data class AppliedPromotionResponse(
    val promotionId: Long,
    val promotionName: String,
    val discountAmount: BigDecimal
)
