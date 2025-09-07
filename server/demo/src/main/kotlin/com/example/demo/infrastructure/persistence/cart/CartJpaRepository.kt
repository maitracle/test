package com.example.demo.infrastructure.persistence.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 장바구니 JPA Repository 인터페이스
 * 
 * 장바구니 엔티티에 대한 데이터 접근을 위한 JPA Repository입니다.
 * Spring Data JPA의 기본 기능과 커스텀 쿼리를 제공합니다.
 */
@Repository
interface CartJpaRepository : JpaRepository<CartEntity, Long> {
    
    /**
     * 사용자 ID로 장바구니를 조회합니다.
     * @param userId 사용자 ID
     * @return 장바구니 엔티티, 없으면 null
     */
    fun findByUserId(userId: Long): CartEntity?
    
    /**
     * 특정 기간에 생성된 장바구니를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 생성된 장바구니 엔티티 목록
     */
    @Query("SELECT c FROM CartEntity c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    fun findByCreatedAtBetween(
        @Param("startDate") startDate: java.time.LocalDateTime,
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<CartEntity>
    
    /**
     * 특정 기간에 업데이트된 장바구니를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 업데이트된 장바구니 엔티티 목록
     */
    @Query("SELECT c FROM CartEntity c WHERE c.updatedAt BETWEEN :startDate AND :endDate")
    fun findByUpdatedAtBetween(
        @Param("startDate") startDate: java.time.LocalDateTime,
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<CartEntity>
    
    /**
     * 사용자 ID 목록으로 장바구니들을 조회합니다.
     * @param userIds 사용자 ID 목록
     * @return 해당 사용자들의 장바구니 엔티티 목록
     */
    fun findByUserIdIn(userIds: List<Long>): List<CartEntity>
    
    /**
     * 장바구니 아이템이 있는 장바구니만 조회합니다.
     * @return 아이템이 있는 장바구니 엔티티 목록
     */
    @Query("SELECT c FROM CartEntity c WHERE SIZE(c.items) > 0")
    fun findCartsWithItems(): List<CartEntity>
    
    /**
     * 빈 장바구니만 조회합니다.
     * @return 빈 장바구니 엔티티 목록
     */
    @Query("SELECT c FROM CartEntity c WHERE SIZE(c.items) = 0")
    fun findEmptyCarts(): List<CartEntity>
    
    /**
     * 특정 상품이 포함된 장바구니들을 조회합니다.
     * @param productId 상품 ID
     * @return 해당 상품이 포함된 장바구니 엔티티 목록
     */
    @Query("SELECT c FROM CartEntity c JOIN c.items i WHERE i.productId = :productId")
    fun findByItemsProductId(@Param("productId") productId: Long): List<CartEntity>
    
    /**
     * 장바구니 수를 조회합니다.
     * @return 장바구니 수
     */
    @Query("SELECT COUNT(c) FROM CartEntity c")
    fun countCarts(): Long
    
    /**
     * 아이템이 있는 장바구니 수를 조회합니다.
     * @return 아이템이 있는 장바구니 수
     */
    @Query("SELECT COUNT(c) FROM CartEntity c WHERE SIZE(c.items) > 0")
    fun countCartsWithItems(): Long
}

/**
 * 장바구니 아이템 JPA Repository 인터페이스
 * 
 * 장바구니 아이템 엔티티에 대한 데이터 접근을 위한 JPA Repository입니다.
 */
@Repository
interface CartItemJpaRepository : JpaRepository<CartItemEntity, Long> {
    
    /**
     * 장바구니 ID로 장바구니 아이템들을 조회합니다.
     * @param cartId 장바구니 ID
     * @return 해당 장바구니의 아이템 엔티티 목록
     */
    @Query("SELECT ci FROM CartItemEntity ci WHERE ci.cart.id = :cartId")
    fun findByCartId(@Param("cartId") cartId: Long): List<CartItemEntity>
    
    /**
     * 상품 ID로 장바구니 아이템들을 조회합니다.
     * @param productId 상품 ID
     * @return 해당 상품이 포함된 아이템 엔티티 목록
     */
    fun findByProductId(productId: Long): List<CartItemEntity>
    
    /**
     * 장바구니 ID와 상품 ID로 장바구니 아이템을 조회합니다.
     * @param cartId 장바구니 ID
     * @param productId 상품 ID
     * @return 장바구니 아이템 엔티티, 없으면 null
     */
    @Query("SELECT ci FROM CartItemEntity ci WHERE ci.cart.id = :cartId AND ci.productId = :productId")
    fun findByCartIdAndProductId(@Param("cartId") cartId: Long, @Param("productId") productId: Long): CartItemEntity?
    
    /**
     * 특정 수량 이상의 장바구니 아이템들을 조회합니다.
     * @param quantity 수량
     * @return 해당 수량 이상의 아이템 엔티티 목록
     */
    fun findByQuantityGreaterThanEqual(quantity: Int): List<CartItemEntity>
    
    /**
     * 특정 가격 이상의 장바구니 아이템들을 조회합니다.
     * @param unitPrice 단가
     * @return 해당 가격 이상의 아이템 엔티티 목록
     */
    fun findByUnitPriceGreaterThanEqual(unitPrice: java.math.BigDecimal): List<CartItemEntity>
    
    /**
     * 장바구니 아이템 수를 조회합니다.
     * @return 장바구니 아이템 수
     */
    @Query("SELECT COUNT(ci) FROM CartItemEntity ci")
    fun countCartItems(): Long
    
    /**
     * 특정 장바구니의 아이템 수를 조회합니다.
     * @param cartId 장바구니 ID
     * @return 해당 장바구니의 아이템 수
     */
    @Query("SELECT COUNT(ci) FROM CartItemEntity ci WHERE ci.cart.id = :cartId")
    fun countItemsByCartId(@Param("cartId") cartId: Long): Long
}
