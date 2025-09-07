package com.example.demo.application.common.dto.response

/**
 * 프로모션 생성 응답 DTO
 */
data class CreatePromotionResponse(
    val promotionId: Long,
    val name: String,
    val isActive: Boolean
)
