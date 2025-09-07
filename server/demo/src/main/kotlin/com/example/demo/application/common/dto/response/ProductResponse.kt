package com.example.demo.application.common.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 상품 응답 DTO
 */
data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val category: String?,
    val brand: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
