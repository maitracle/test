package com.example.demo.infrastructure.web.product.dto

/**
 * 상품 검색 웹 요청 DTO
 */
data class SearchProductsWebRequest(
    val category: String?,
    val keyword: String?
)
