package com.example.demo.infrastructure.web.product

import com.example.demo.application.common.dto.request.CreateProductRequest
import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.infrastructure.web.product.dto.CreateProductWebRequest
import com.example.demo.infrastructure.web.product.dto.ProductWebResponse
import com.example.demo.infrastructure.web.product.dto.SearchProductsWebRequest
import org.springframework.stereotype.Component

/**
 * 상품 관련 웹 DTO와 유스케이스 DTO 간의 변환을 담당하는 매퍼
 */
@Component
class ProductWebMapper {
    
    /**
     * 웹 요청 DTO를 상품 생성 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 상품 생성 유스케이스 요청 DTO
     */
    fun toCreateProductRequest(request: CreateProductWebRequest): CreateProductRequest {
        return CreateProductRequest(
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock,
            category = request.category,
            brand = request.brand,
            imageUrl = request.imageUrl
        )
    }
    
    /**
     * 웹 요청 DTO를 상품 검색 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 상품 검색 유스케이스 요청 DTO
     */
    fun toSearchProductsRequest(request: SearchProductsWebRequest): com.example.demo.application.product.usecase.ProductSearchRequest {
        return com.example.demo.application.product.usecase.ProductSearchRequest(
            category = request.category,
            name = request.keyword
        )
    }
    
    /**
     * 상품 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 상품 유스케이스 응답 DTO
     * @return 상품 웹 응답 DTO
     */
    fun toProductWebResponse(response: ProductResponse): ProductWebResponse {
        return ProductWebResponse(
            id = response.id,
            name = response.name,
            description = response.description,
            price = response.price,
            stock = response.stock,
            category = response.category,
            brand = response.brand,
            imageUrl = response.imageUrl,
            isActive = response.isActive,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }
}
