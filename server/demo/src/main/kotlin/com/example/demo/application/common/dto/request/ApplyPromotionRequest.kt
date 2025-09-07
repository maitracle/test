package com.example.demo.application.common.dto.request

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.user.model.User

/**
 * 프로모션 적용 요청 DTO
 */
data class ApplyPromotionRequest(
    val cart: Cart,
    val user: User
)
