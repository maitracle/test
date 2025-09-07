package com.example.demo.infrastructure.web.promotion.dto

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 프로모션 웹 응답 DTO
 */
data class PromotionWebResponse(
    val promotionId: Long,
    val name: String,
    val description: String? = null,
    val type: String? = null,
    val priority: Int? = null,
    val isActive: Boolean? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val targetCategory: String? = null,
    val minCartAmount: BigDecimal? = null,
    val minQuantity: Int? = null,
    val targetUserLevel: String? = null,
    val discountPercentage: BigDecimal? = null,
    val discountAmount: BigDecimal? = null,
    val maxDiscountAmount: BigDecimal? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
