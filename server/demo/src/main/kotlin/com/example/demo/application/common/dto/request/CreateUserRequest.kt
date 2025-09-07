package com.example.demo.application.common.dto.request

/**
 * 사용자 생성 요청 DTO
 */
data class CreateUserRequest(
    val email: String,
    val membershipLevel: String = "REGULAR",
    val isNewCustomer: Boolean = true
)
