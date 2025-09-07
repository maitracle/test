package com.example.demo.infrastructure.web.promotion.dto

/**
 * 프로모션 적용 웹 요청 DTO
 */
data class ApplyPromotionWebRequest(
    val cartId: Long,
    val userId: Long
)
