package com.example.demo.infrastructure.persistence.cart

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 장바구니 JPA 엔티티
 * 
 * 장바구니 도메인 모델을 데이터베이스에 매핑하기 위한 JPA 엔티티입니다.
 * 사용자와 장바구니 아이템들 간의 관계를 관리합니다.
 */
@Entity
@Table(
    name = "carts",
    indexes = [
        Index(name = "idx_cart_user_id", columnList = "user_id"),
        Index(name = "idx_cart_created_at", columnList = "created_at"),
        Index(name = "idx_cart_updated_at", columnList = "updated_at")
    ]
)
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val userId: Long,
    
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: MutableList<CartItemEntity> = mutableListOf(),
    
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * 엔티티 생성 시점에 updatedAt 설정
     */
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        // createdAt은 생성자에서 이미 설정되므로 여기서는 updatedAt만 설정
    }
    
    /**
     * 엔티티 업데이트 시점에 updatedAt 갱신
     */
    @PreUpdate
    fun preUpdate() {
        // updatedAt은 생성자에서 이미 설정되므로 여기서는 추가 작업 없음
    }
    
    /**
     * 장바구니가 비어있는지 확인
     */
    fun isEmpty(): Boolean = items.isEmpty()
    
    /**
     * 장바구니에 아이템이 있는지 확인
     */
    fun isNotEmpty(): Boolean = items.isNotEmpty()
    
    /**
     * 장바구니의 아이템 개수 반환
     */
    fun getItemCount(): Int = items.size
    
    /**
     * 장바구니의 총 수량 계산
     */
    fun getTotalQuantity(): Int = items.sumOf { it.quantity }
    
    /**
     * 장바구니의 총 금액 계산
     */
    fun getTotalAmount(): java.math.BigDecimal = items.sumOf { it.getTotalPrice() }
    
    /**
     * 특정 상품이 장바구니에 있는지 확인
     */
    fun hasProduct(productId: Long): Boolean = items.any { it.productId == productId }
    
    /**
     * 특정 카테고리의 상품이 있는지 확인
     */
    fun hasCategory(category: String): Boolean = items.any { it.category == category }
    
    /**
     * 특정 브랜드의 상품이 있는지 확인
     */
    fun hasBrand(brand: String): Boolean = items.any { it.brand == brand }
    
    /**
     * 장바구니 아이템 추가
     */
    fun addItem(item: CartItemEntity): CartEntity {
        val existingItem = items.find { it.productId == item.productId }
        return if (existingItem != null) {
            val updatedItems = items.map { cartItem ->
                if (cartItem.productId == item.productId) {
                    cartItem.copy(quantity = cartItem.quantity + item.quantity, updatedAt = LocalDateTime.now())
                } else cartItem
            }.toMutableList()
            copy(items = updatedItems, updatedAt = LocalDateTime.now())
        } else {
            val newItems = items.toMutableList()
            newItems.add(item)
            copy(items = newItems, updatedAt = LocalDateTime.now())
        }
    }
    
    /**
     * 장바구니 아이템 제거
     */
    fun removeItem(productId: Long): CartEntity {
        val updatedItems = items.filter { it.productId != productId }.toMutableList()
        return copy(items = updatedItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니 아이템 수량 업데이트
     */
    fun updateItemQuantity(productId: Long, newQuantity: Int): CartEntity {
        val updatedItems = items.map { item ->
            if (item.productId == productId) {
                item.copy(quantity = newQuantity, updatedAt = LocalDateTime.now())
            } else item
        }.toMutableList()
        return copy(items = updatedItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니 비우기
     */
    fun clear(): CartEntity {
        return copy(items = mutableListOf(), updatedAt = LocalDateTime.now())
    }
    
    /**
     * 엔티티가 유효한지 확인
     */
    fun isValid(): Boolean {
        return userId > 0 &&
               createdAt != null &&
               updatedAt != null &&
               updatedAt.isAfter(createdAt) || updatedAt.isEqual(createdAt) &&
               items.all { it.isValid() }
    }
    
    /**
     * 엔티티를 문자열로 변환
     */
    override fun toString(): String {
        return "CartEntity(id=$id, userId=$userId, itemCount=${items.size}, totalAmount=${getTotalAmount()}, totalQuantity=${getTotalQuantity()})"
    }
    
    companion object {
        /**
         * 새로운 빈 장바구니 엔티티 생성
         */
        fun createEmpty(userId: Long): CartEntity {
            val now = LocalDateTime.now()
            return CartEntity(
                userId = userId,
                items = mutableListOf(),
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 장바구니 엔티티 생성
         */
        fun createExisting(
            id: Long,
            userId: Long,
            items: List<CartItemEntity>,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): CartEntity {
            return CartEntity(
                id = id,
                userId = userId,
                items = items.toMutableList(),
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
