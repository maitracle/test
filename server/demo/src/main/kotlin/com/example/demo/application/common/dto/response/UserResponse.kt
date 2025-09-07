package com.example.demo.application.common.dto.response

import java.time.LocalDateTime

/**
 * 사용자 응답 DTO
 */
data class UserResponse(
    val id: Long,
    val email: String,
    val membershipLevel: String,
    val isNewCustomer: Boolean,
    val createdAt: LocalDateTime
)
