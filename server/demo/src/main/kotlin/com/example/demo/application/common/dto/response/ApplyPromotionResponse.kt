package com.example.demo.application.common.dto.response

import java.math.BigDecimal

/**
 * 프로모션 적용 응답 DTO
 */
data class ApplyPromotionResponse(
    val appliedPromotions: List<AppliedPromotionResponse>,
    val totalDiscount: BigDecimal,
    val finalAmount: BigDecimal
)
