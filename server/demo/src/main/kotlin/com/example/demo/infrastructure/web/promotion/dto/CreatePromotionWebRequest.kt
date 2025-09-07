package com.example.demo.infrastructure.web.promotion.dto

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 프로모션 생성 웹 요청 DTO
 */
data class CreatePromotionWebRequest(
    val name: String,
    val description: String?,
    val type: String,
    val priority: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val targetCategory: String?,
    val minCartAmount: BigDecimal?,
    val minQuantity: Int?,
    val targetUserLevel: String?,
    val discountPercentage: BigDecimal?,
    val discountAmount: BigDecimal?,
    val maxDiscountAmount: BigDecimal?
)
