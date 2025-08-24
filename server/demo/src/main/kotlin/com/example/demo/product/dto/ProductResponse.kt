package com.example.demo.product.dto

import com.example.demo.product.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int,
    val category: String?,
    val brand: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id!!,
                name = product.name,
                description = product.description,
                price = product.price,
                stockQuantity = product.stockQuantity,
                category = product.category,
                brand = product.brand,
                imageUrl = product.imageUrl,
                isActive = product.isActive,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
} 