package com.example.demo.infrastructure.web.cart.dto

import java.math.BigDecimal

/**
 * 장바구니 웹 응답 DTO
 */
data class CartResponse(
    val items: List<CartItemWebResponse>,
    val subtotal: BigDecimal,
    val totalDiscount: BigDecimal,
    val shippingFee: BigDecimal,
    val finalAmount: BigDecimal,
    val appliedPromotions: List<AppliedPromotionWebResponse>
)

/**
 * 장바구니 아이템 웹 응답 DTO
 */
data class CartItemWebResponse(
    val productId: Long,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)

/**
 * 적용된 프로모션 웹 응답 DTO
 */
data class AppliedPromotionWebResponse(
    val promotionId: Long,
    val promotionName: String,
    val discountAmount: BigDecimal
)
