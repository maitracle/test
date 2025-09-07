package com.example.demo.cart.dto

import java.math.BigDecimal

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal,
    val totalItems: Int
)
