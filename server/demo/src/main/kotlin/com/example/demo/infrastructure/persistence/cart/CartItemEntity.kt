package com.example.demo.infrastructure.persistence.cart

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 장바구니 아이템 JPA 엔티티
 * 
 * 장바구니 아이템 도메인 모델을 데이터베이스에 매핑하기 위한 JPA 엔티티입니다.
 * 상품 정보와 수량, 가격 정보를 저장합니다.
 */
@Entity
@Table(
    name = "cart_items",
    indexes = [
        Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
        Index(name = "idx_cart_item_product_id", columnList = "product_id"),
        Index(name = "idx_cart_item_created_at", columnList = "created_at")
    ]
)
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    val cart: CartEntity? = null,
    
    @Column(nullable = false)
    val productId: Long,
    
    @Column(nullable = false, length = 200)
    val productName: String,
    
    @Column(length = 1000)
    val productDescription: String? = null,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,
    
    @Column(nullable = false)
    val quantity: Int,
    
    @Column(length = 100)
    val category: String? = null,
    
    @Column(length = 100)
    val brand: String? = null,
    
    @Column(length = 500)
    val imageUrl: String? = null,
    
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
     * 장바구니 아이템의 총 가격 계산
     */
    fun getTotalPrice(): BigDecimal = unitPrice.multiply(BigDecimal(quantity))
    
    /**
     * 상품이 특정 카테고리에 속하는지 확인
     */
    fun belongsToCategory(category: String): Boolean {
        return this.category?.equals(category, ignoreCase = true) ?: false
    }
    
    /**
     * 상품이 특정 브랜드에 속하는지 확인
     */
    fun belongsToBrand(brand: String): Boolean {
        return this.brand?.equals(brand, ignoreCase = true) ?: false
    }
    
    /**
     * 수량 추가
     */
    fun addQuantity(additionalQuantity: Int): CartItemEntity {
        require(additionalQuantity > 0) { "Additional quantity must be positive: $additionalQuantity" }
        return copy(quantity = quantity + additionalQuantity, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 수량 업데이트
     */
    fun updateQuantity(newQuantity: Int): CartItemEntity {
        require(newQuantity > 0) { "Quantity must be positive: $newQuantity" }
        return copy(quantity = newQuantity, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 단위 가격 업데이트
     */
    fun updateUnitPrice(newUnitPrice: BigDecimal): CartItemEntity {
        require(newUnitPrice >= BigDecimal.ZERO) { "Unit price cannot be negative: $newUnitPrice" }
        return copy(unitPrice = newUnitPrice, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 할인된 가격 계산
     */
    fun calculateDiscountedPrice(discountRate: Double): BigDecimal {
        require(discountRate >= 0.0 && discountRate <= 1.0) { 
            "Discount rate must be between 0.0 and 1.0: $discountRate" 
        }
        val discountAmount = getTotalPrice().multiply(BigDecimal.valueOf(discountRate))
        return getTotalPrice().subtract(discountAmount)
    }
    
    /**
     * 할인 금액 적용
     */
    fun applyDiscount(discountAmount: BigDecimal): BigDecimal {
        require(discountAmount >= BigDecimal.ZERO) { 
            "Discount amount cannot be negative: $discountAmount" 
        }
        val finalDiscount = if (discountAmount > getTotalPrice()) getTotalPrice() else discountAmount
        return getTotalPrice().subtract(finalDiscount)
    }
    
    /**
     * 다른 아이템과 같은 상품인지 확인
     */
    fun isSameProduct(other: CartItemEntity): Boolean {
        return productId == other.productId
    }
    
    /**
     * 엔티티가 유효한지 확인
     */
    fun isValid(): Boolean {
        return productId > 0 &&
               productName.isNotBlank() &&
               unitPrice >= BigDecimal.ZERO &&
               quantity > 0 &&
               createdAt != null &&
               updatedAt != null &&
               updatedAt.isAfter(createdAt) || updatedAt.isEqual(createdAt)
    }
    
    /**
     * 엔티티를 문자열로 변환
     */
    override fun toString(): String {
        return "CartItemEntity(id=$id, productId=$productId, productName=$productName, quantity=$quantity, unitPrice=$unitPrice, totalPrice=${getTotalPrice()})"
    }
    
    companion object {
        /**
         * 새로운 장바구니 아이템 엔티티 생성
         */
        fun create(
            cart: CartEntity,
            productId: Long,
            productName: String,
            productDescription: String? = null,
            unitPrice: BigDecimal,
            quantity: Int,
            category: String? = null,
            brand: String? = null,
            imageUrl: String? = null
        ): CartItemEntity {
            val now = LocalDateTime.now()
            return CartItemEntity(
                cart = cart,
                productId = productId,
                productName = productName,
                productDescription = productDescription,
                unitPrice = unitPrice,
                quantity = quantity,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 장바구니 아이템 엔티티 생성
         */
        fun createExisting(
            id: Long,
            cart: CartEntity,
            productId: Long,
            productName: String,
            productDescription: String?,
            unitPrice: BigDecimal,
            quantity: Int,
            category: String?,
            brand: String?,
            imageUrl: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): CartItemEntity {
            return CartItemEntity(
                id = id,
                cart = cart,
                productId = productId,
                productName = productName,
                productDescription = productDescription,
                unitPrice = unitPrice,
                quantity = quantity,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
