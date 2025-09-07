package com.example.demo.infrastructure.persistence.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 상품 JPA Repository 인터페이스
 * 
 * 상품 엔티티에 대한 데이터 접근을 위한 JPA Repository입니다.
 * Spring Data JPA의 기본 기능과 커스텀 쿼리를 제공합니다.
 */
@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
    
    /**
     * 활성 상품만 조회합니다.
     * @return 활성 상품 엔티티 목록
     */
    fun findByIsActiveTrue(): List<ProductEntity>
    
    /**
     * 카테고리로 상품을 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 엔티티 목록
     */
    fun findByCategory(category: String): List<ProductEntity>
    
    /**
     * 브랜드로 상품을 조회합니다.
     * @param brand 브랜드명
     * @return 해당 브랜드의 상품 엔티티 목록
     */
    fun findByBrand(brand: String): List<ProductEntity>
    
    /**
     * 상품명으로 상품을 검색합니다 (부분 일치).
     * @param name 검색할 상품명
     * @return 검색된 상품 엔티티 목록
     */
    fun findByNameContainingIgnoreCase(name: String): List<ProductEntity>
    
    /**
     * 상품 설명으로 상품을 검색합니다 (부분 일치).
     * @param description 검색할 설명
     * @return 검색된 상품 엔티티 목록
     */
    fun findByDescriptionContainingIgnoreCase(description: String): List<ProductEntity>
    
    /**
     * 가격 범위로 상품을 조회합니다.
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 해당 가격 범위의 상품 엔티티 목록
     */
    fun findByPriceBetween(minPrice: java.math.BigDecimal, maxPrice: java.math.BigDecimal): List<ProductEntity>
    
    /**
     * 재고가 있는 상품만 조회합니다.
     * @return 재고가 있는 상품 엔티티 목록
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.stock > 0")
    fun findInStockProducts(): List<ProductEntity>
    
    /**
     * 재고가 부족한 상품을 조회합니다.
     * @param threshold 재고 임계값
     * @return 재고가 임계값 이하인 상품 엔티티 목록
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.stock <= :threshold")
    fun findLowStockProducts(@Param("threshold") threshold: Int): List<ProductEntity>
    
    /**
     * 카테고리와 활성 상태로 상품을 조회합니다.
     * @param category 카테고리명
     * @param isActive 활성 상태
     * @return 해당 조건의 상품 엔티티 목록
     */
    fun findByCategoryAndIsActive(category: String, isActive: Boolean): List<ProductEntity>
    
    /**
     * 브랜드와 활성 상태로 상품을 조회합니다.
     * @param brand 브랜드명
     * @param isActive 활성 상태
     * @return 해당 조건의 상품 엔티티 목록
     */
    fun findByBrandAndIsActive(brand: String, isActive: Boolean): List<ProductEntity>
    
    /**
     * 상품명으로 정확히 일치하는 상품을 조회합니다.
     * @param name 상품명
     * @return 상품 엔티티, 없으면 null
     */
    fun findByName(name: String): ProductEntity?
    
    /**
     * 특정 기간에 생성된 상품을 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 생성된 상품 엔티티 목록
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    fun findByCreatedAtBetween(
        @Param("startDate") startDate: java.time.LocalDateTime,
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<ProductEntity>
    
    /**
     * 활성 상품 수를 조회합니다.
     * @return 활성 상품 수
     */
    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.isActive = true")
    fun countActiveProducts(): Long
    
    /**
     * 카테고리별 상품 수를 조회합니다.
     * @return 카테고리별 상품 수 목록
     */
    @Query("SELECT p.category, COUNT(p) FROM ProductEntity p GROUP BY p.category")
    fun countByCategory(): List<Array<Any>>
    
    /**
     * 브랜드별 상품 수를 조회합니다.
     * @return 브랜드별 상품 수 목록
     */
    @Query("SELECT p.brand, COUNT(p) FROM ProductEntity p GROUP BY p.brand")
    fun countByBrand(): List<Array<Any>>
}
