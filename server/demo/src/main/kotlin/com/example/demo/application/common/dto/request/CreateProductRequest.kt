package com.example.demo.application.common.dto.request

import java.math.BigDecimal

/**
 * 상품 생성 요청 DTO
 */
data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val stock: Int,
    val category: String? = null,
    val brand: String? = null,
    val imageUrl: String? = null
)
