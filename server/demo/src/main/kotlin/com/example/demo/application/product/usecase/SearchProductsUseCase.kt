package com.example.demo.application.product.usecase

import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.common.port.ProductRepository
import org.springframework.stereotype.Component

/**
 * 상품 검색 유스케이스
 * 다양한 조건으로 상품을 검색합니다.
 */
@Component
class SearchProductsUseCase(
    private val productRepository: ProductRepository
) {
    
    /**
     * 상품 검색을 실행합니다.
     * @param request 상품 검색 요청
     * @return 검색된 상품 목록
     */
    fun execute(request: ProductSearchRequest): List<ProductResponse> {
        val products = when {
            request.name != null && request.category != null -> {
                // 이름과 카테고리 모두로 검색
                val categoryProducts = productRepository.findByCategory(request.category)
                categoryProducts.filter { it.name.contains(request.name, ignoreCase = true) }
            }
            request.name != null -> {
                // 이름으로 검색
                productRepository.findByNameContaining(request.name)
            }
            request.category != null -> {
                // 카테고리로 검색
                productRepository.findByCategory(request.category)
            }
            request.brand != null -> {
                // 브랜드로 검색 (현재는 모든 상품에서 필터링)
                productRepository.findAll().filter { it.brand?.equals(request.brand, ignoreCase = true) == true }
            }
            request.minPrice != null || request.maxPrice != null -> {
                // 가격 범위로 검색
                productRepository.findAll().filter { product ->
                    val price = product.price.amount.amount
                    val minPrice = request.minPrice ?: java.math.BigDecimal.ZERO
                    val maxPrice = request.maxPrice ?: java.math.BigDecimal.valueOf(Long.MAX_VALUE)
                    price >= minPrice && price <= maxPrice
                }
            }
            request.activeOnly == true -> {
                // 활성 상품만 조회
                productRepository.findActiveProducts()
            }
            else -> {
                // 모든 상품 조회
                productRepository.findAll()
            }
        }
        
        // 정렬 적용
        val sortedProducts = when (request.sortBy) {
            "name" -> products.sortedBy { it.name }
            "price" -> products.sortedBy { it.price.amount.amount }
            "createdAt" -> products.sortedByDescending { it.createdAt }
            else -> products
        }
        
        // 활성 상품만 필터링 (요청된 경우)
        val filteredProducts = if (request.activeOnly == true) {
            sortedProducts.filter { it.isActive }
        } else {
            sortedProducts
        }
        
        return filteredProducts.map { product ->
            toProductResponse(product)
        }
    }
    
    /**
     * 인기 상품을 조회합니다 (재고가 많은 순).
     * @param limit 조회할 상품 수 (기본값: 10)
     * @return 인기 상품 목록
     */
    fun getPopularProducts(limit: Int = 10): List<ProductResponse> {
        return productRepository.findActiveProducts()
            .sortedByDescending { it.stock.quantity }
            .take(limit)
            .map { product ->
                toProductResponse(product)
            }
    }
    
    /**
     * 신상품을 조회합니다.
     * @param limit 조회할 상품 수 (기본값: 10)
     * @return 신상품 목록
     */
    fun getNewProducts(limit: Int = 10): List<ProductResponse> {
        return productRepository.findActiveProducts()
            .sortedByDescending { it.createdAt }
            .take(limit)
            .map { product ->
                toProductResponse(product)
            }
    }
    
    /**
     * 재고 부족 상품을 조회합니다.
     * @param threshold 재고 부족 기준 (기본값: 10)
     * @return 재고 부족 상품 목록
     */
    fun getLowStockProducts(threshold: Int = 10): List<ProductResponse> {
        return productRepository.findAll()
            .filter { it.stock.quantity <= threshold && it.stock.quantity > 0 }
            .map { product ->
                toProductResponse(product)
            }
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
 * 상품 검색 요청 DTO
 */
data class ProductSearchRequest(
    val name: String? = null,
    val category: String? = null,
    val brand: String? = null,
    val minPrice: java.math.BigDecimal? = null,
    val maxPrice: java.math.BigDecimal? = null,
    val activeOnly: Boolean? = null,
    val sortBy: String? = null
)
