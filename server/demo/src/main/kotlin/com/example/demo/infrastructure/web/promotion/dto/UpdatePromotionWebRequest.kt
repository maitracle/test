package com.example.demo.infrastructure.web.promotion.dto

/**
 * 프로모션 수정 웹 요청 DTO
 */
data class UpdatePromotionWebRequest(
    val name: String?,
    val description: String?,
    val priority: Int?
)
