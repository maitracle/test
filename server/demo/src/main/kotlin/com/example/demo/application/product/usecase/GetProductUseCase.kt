package com.example.demo.application.product.usecase

import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.product.model.ProductId
import org.springframework.stereotype.Component

/**
 * 상품 조회 유스케이스
 * 상품 정보를 조회합니다.
 */
@Component
class GetProductUseCase(
    private val productRepository: ProductRepository
) {
    
    /**
     * 상품 ID로 상품을 조회합니다.
     * @param productId 상품 ID
     * @return 상품 정보
     * @throws IllegalArgumentException 상품을 찾을 수 없는 경우
     */
    fun getProductById(productId: Long): ProductResponse {
        val product = productRepository.findById(ProductId.of(productId))
            ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: $productId")
        
        return toProductResponse(product)
    }
    
    /**
     * 활성 상품만 조회합니다.
     * @return 활성 상품 목록
     */
    fun getActiveProducts(): List<ProductResponse> {
        return productRepository.findActiveProducts().map { product ->
            toProductResponse(product)
        }
    }
    
    /**
     * 모든 상품을 조회합니다.
     * @return 모든 상품 목록
     */
    fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { product ->
            toProductResponse(product)
        }
    }
    
    /**
     * 카테고리로 상품을 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    fun getProductsByCategory(category: String): List<ProductResponse> {
        return productRepository.findByCategory(category).map { product ->
            toProductResponse(product)
        }
    }
    
    /**
     * 상품명으로 상품을 검색합니다.
     * @param name 검색할 상품명 (부분 일치)
     * @return 검색된 상품 목록
     */
    fun searchProductsByName(name: String): List<ProductResponse> {
        if (name.isBlank()) {
            return emptyList()
        }
        
        return productRepository.findByNameContaining(name).map { product ->
            toProductResponse(product)
        }
    }
    
    /**
     * 상품의 재고 상태를 확인합니다.
     * @param productId 상품 ID
     * @return 재고 상태 정보
     */
    fun getProductStockStatus(productId: Long): ProductStockStatus {
        val product = productRepository.findById(ProductId.of(productId))
            ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: $productId")
        
        return ProductStockStatus(
            productId = product.id.value,
            productName = product.name,
            currentStock = product.stock.quantity,
            isInStock = !product.stock.isEmpty(),
            isLowStock = product.stock.quantity < 10,
            isOutOfStock = product.stock.isEmpty()
        )
    }
    
    /**
     * 도메인 모델을 응답 DTO로 변환합니다.
     * @param product 상품 도메인 모델
     * @return 상품 응답 DTO
     */
    private fun toProductResponse(product: com.example.demo.domain.product.model.Product): ProductResponse {
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

/**
 * 상품 재고 상태 정보
 */
data class ProductStockStatus(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val isInStock: Boolean,
    val isLowStock: Boolean,
    val isOutOfStock: Boolean
)
