package com.example.demo.application.common.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 프로모션 응답 DTO
 */
data class PromotionResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val type: String,
    val priority: Int,
    val isActive: Boolean,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val targetCategory: String?,
    val minCartAmount: BigDecimal?,
    val minQuantity: Int?,
    val targetUserLevel: String?,
    val discountPercentage: BigDecimal?,
    val discountAmount: BigDecimal?,
    val maxDiscountAmount: BigDecimal?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
