package com.example.demo.application.product.usecase

import com.example.demo.application.common.dto.request.CreateProductRequest
import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 상품 생성 유스케이스
 * 새로운 상품을 생성하고 저장합니다.
 */
@Component
class CreateProductUseCase(
    private val productRepository: ProductRepository
) {
    
    /**
     * 상품 생성을 실행합니다.
     * @param request 상품 생성 요청
     * @return 생성된 상품 정보
     */
    fun execute(request: CreateProductRequest): ProductResponse {
        // 1. 요청 유효성 검증
        validateCreateRequest(request)
        
        // 2. 상품 도메인 모델 생성
        val product = Product(
            id = ProductId.of(1L), // 임시 ID (실제로는 저장 시 생성됨)
            name = request.name,
            description = request.description,
            price = com.example.demo.domain.product.valueobject.Price.of(Money(request.price)),
            stock = Stock(request.stock),
            category = request.category,
            brand = request.brand,
            imageUrl = request.imageUrl,
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 3. 상품 저장
        val savedProduct = productRepository.save(product)
        
        // 4. 응답 생성
        return toProductResponse(savedProduct)
    }
    
    /**
     * 상품 생성 요청 유효성 검증을 수행합니다.
     * @param request 상품 생성 요청
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    private fun validateCreateRequest(request: CreateProductRequest) {
        // 이름 필수 확인
        if (request.name.isBlank()) {
            throw IllegalArgumentException("상품명은 필수입니다.")
        }
        
        // 가격 양수 확인
        if (request.price <= java.math.BigDecimal.ZERO) {
            throw IllegalArgumentException("상품 가격은 0보다 커야 합니다.")
        }
        
        // 재고 음수 확인
        if (request.stock < 0) {
            throw IllegalArgumentException("재고는 0 이상이어야 합니다.")
        }
        
        // 이름 길이 확인
        if (request.name.length > 100) {
            throw IllegalArgumentException("상품명은 100자를 초과할 수 없습니다.")
        }
        
        // 설명 길이 확인
        request.description?.let { description ->
            if (description.length > 500) {
                throw IllegalArgumentException("상품 설명은 500자를 초과할 수 없습니다.")
            }
        }
        
        // 카테고리 길이 확인
        request.category?.let { category ->
            if (category.length > 50) {
                throw IllegalArgumentException("카테고리는 50자를 초과할 수 없습니다.")
            }
        }
        
        // 브랜드 길이 확인
        request.brand?.let { brand ->
            if (brand.length > 50) {
                throw IllegalArgumentException("브랜드는 50자를 초과할 수 없습니다.")
            }
        }
    }
    
    /**
     * 도메인 모델을 응답 DTO로 변환합니다.
     * @param product 상품 도메인 모델
     * @return 상품 응답 DTO
     */
    private fun toProductResponse(product: Product): ProductResponse {
        return ProductResponse(
            id = product.id.value,
            name = product.name,
            description = product.description,
            price = product.price.amount.amount,
            stock = product.stock.quantity,
            category = product.category,
            brand = product.brand,
            imageUrl = product.imageUrl,
            isActive = product.isActive,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}
