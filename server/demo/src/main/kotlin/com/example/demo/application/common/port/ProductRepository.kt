package com.example.demo.application.common.port

import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId

/**
 * 상품 데이터 접근을 위한 포트 인터페이스
 * 클린 아키텍처의 의존성 역전 원칙에 따라 애플리케이션 레이어에서 정의
 */
interface ProductRepository {
    
    /**
     * 상품 ID로 상품을 조회합니다.
     * @param productId 상품 ID
     * @return 상품 정보, 없으면 null
     */
    fun findById(productId: ProductId): Product?
    
    /**
     * 모든 활성 상품을 조회합니다.
     * @return 활성 상품 목록
     */
    fun findAll(): List<Product>
    
    /**
     * 활성 상품만 조회합니다.
     * @return 활성 상품 목록
     */
    fun findActiveProducts(): List<Product>
    
    /**
     * 카테고리로 상품을 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    fun findByCategory(category: String): List<Product>
    
    /**
     * 상품명으로 상품을 검색합니다.
     * @param name 검색할 상품명 (부분 일치)
     * @return 검색된 상품 목록
     */
    fun findByNameContaining(name: String): List<Product>
    
    /**
     * 상품을 저장합니다.
     * @param product 저장할 상품
     * @return 저장된 상품
     */
    fun save(product: Product): Product
    
    /**
     * 상품을 삭제합니다.
     * @param productId 삭제할 상품 ID
     */
    fun delete(productId: ProductId)
}
