package com.example.demo.product.dto

import java.math.BigDecimal

data class ProductRequest(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val stockQuantity: Int,
    val category: String? = null,
    val brand: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean = true
) 