package com.example.demo.infrastructure.web.user.dto

import java.time.LocalDateTime

/**
 * 사용자 웹 응답 DTO
 */
data class UserWebResponse(
    val id: Long,
    val email: String,
    val membershipLevel: String,
    val isNewCustomer: Boolean,
    val createdAt: LocalDateTime
)
