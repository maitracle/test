package com.example.demo.application.product.service

import com.example.demo.application.common.dto.request.CreateProductRequest
import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.product.usecase.CreateProductUseCase
import com.example.demo.application.product.usecase.GetProductUseCase
import com.example.demo.application.product.usecase.SearchProductsUseCase
import com.example.demo.application.product.usecase.ProductSearchRequest
import org.springframework.stereotype.Service

/**
 * 상품 서비스
 * 상품 관련 비즈니스 로직을 조합하고 제공합니다.
 */
@Service
class ProductService(
    private val createProductUseCase: CreateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) {
    
    /**
     * 상품을 생성합니다.
     * @param request 상품 생성 요청
     * @return 생성된 상품 정보
     */
    fun createProduct(request: CreateProductRequest): ProductResponse {
        return createProductUseCase.execute(request)
    }
    
    /**
     * 상품 ID로 상품을 조회합니다.
     * @param productId 상품 ID
     * @return 상품 정보
     */
    fun getProductById(productId: Long): ProductResponse {
        return getProductUseCase.getProductById(productId)
    }
    
    /**
     * 활성 상품만 조회합니다.
     * @return 활성 상품 목록
     */
    fun getActiveProducts(): List<ProductResponse> {
        return getProductUseCase.getActiveProducts()
    }
    
    /**
     * 모든 상품을 조회합니다.
     * @return 모든 상품 목록
     */
    fun getAllProducts(): List<ProductResponse> {
        return getProductUseCase.getAllProducts()
    }
    
    /**
     * 카테고리로 상품을 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    fun getProductsByCategory(category: String): List<ProductResponse> {
        return getProductUseCase.getProductsByCategory(category)
    }
    
    /**
     * 상품명으로 상품을 검색합니다.
     * @param name 검색할 상품명
     * @return 검색된 상품 목록
     */
    fun searchProductsByName(name: String): List<ProductResponse> {
        return getProductUseCase.searchProductsByName(name)
    }
    
    /**
     * 상품을 검색합니다.
     * @param request 상품 검색 요청
     * @return 검색된 상품 목록
     */
    fun searchProducts(request: ProductSearchRequest): List<ProductResponse> {
        return searchProductsUseCase.execute(request)
    }
    
    /**
     * 인기 상품을 조회합니다.
     * @param limit 조회할 상품 수
     * @return 인기 상품 목록
     */
    fun getPopularProducts(limit: Int = 10): List<ProductResponse> {
        return searchProductsUseCase.getPopularProducts(limit)
    }
    
    /**
     * 신상품을 조회합니다.
     * @param limit 조회할 상품 수
     * @return 신상품 목록
     */
    fun getNewProducts(limit: Int = 10): List<ProductResponse> {
        return searchProductsUseCase.getNewProducts(limit)
    }
    
    /**
     * 재고 부족 상품을 조회합니다.
     * @param threshold 재고 부족 기준
     * @return 재고 부족 상품 목록
     */
    fun getLowStockProducts(threshold: Int = 10): List<ProductResponse> {
        return searchProductsUseCase.getLowStockProducts(threshold)
    }
    
    /**
     * 상품의 재고 상태를 확인합니다.
     * @param productId 상품 ID
     * @return 재고 상태 정보
     */
    fun getProductStockStatus(productId: Long): com.example.demo.application.product.usecase.ProductStockStatus {
        return getProductUseCase.getProductStockStatus(productId)
    }
}
