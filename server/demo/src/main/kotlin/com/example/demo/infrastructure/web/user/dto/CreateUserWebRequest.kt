package com.example.demo.infrastructure.web.user.dto

/**
 * 사용자 생성 웹 요청 DTO
 */
data class CreateUserWebRequest(
    val email: String,
    val membershipLevel: String,
    val isNewCustomer: Boolean
)
