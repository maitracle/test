package com.example.demo.infrastructure.web.product.dto

import java.math.BigDecimal

/**
 * 상품 생성 웹 요청 DTO
 */
data class CreateProductWebRequest(
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val category: String?,
    val brand: String?,
    val imageUrl: String?
)
