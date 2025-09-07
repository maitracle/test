package com.example.demo.cart.dto

import java.math.BigDecimal

data class CartItemResponse(
    val productId: Long,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)
