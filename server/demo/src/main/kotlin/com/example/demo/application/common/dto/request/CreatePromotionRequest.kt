package com.example.demo.application.common.dto.request

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 프로모션 생성 요청 DTO
 */
data class CreatePromotionRequest(
    val name: String,
    val description: String? = null,
    val type: String,
    val priority: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val targetCategory: String? = null,
    val minCartAmount: BigDecimal? = null,
    val minQuantity: Int? = null,
    val targetUserLevel: String? = null,
    val discountPercentage: BigDecimal? = null,
    val discountAmount: BigDecimal? = null,
    val maxDiscountAmount: BigDecimal? = null
)
