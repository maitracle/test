package com.example.demo.infrastructure.persistence.product

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 상품 JPA 엔티티
 * 
 * 상품 도메인 모델을 데이터베이스에 매핑하기 위한 JPA 엔티티입니다.
 * 상품의 기본 정보, 가격, 재고, 카테고리 등의 정보를 저장합니다.
 */
@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_product_name", columnList = "name"),
        Index(name = "idx_product_category", columnList = "category"),
        Index(name = "idx_product_brand", columnList = "brand"),
        Index(name = "idx_product_is_active", columnList = "is_active"),
        Index(name = "idx_product_created_at", columnList = "created_at")
    ]
)
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 200)
    val name: String,
    
    @Column(length = 1000)
    val description: String? = null,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @Column(nullable = false)
    val stock: Int,
    
    @Column(length = 100)
    val category: String? = null,
    
    @Column(length = 100)
    val brand: String? = null,
    
    @Column(length = 500)
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
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
     * 상품이 활성화되어 있는지 확인
     */
    fun isProductActive(): Boolean = isActive
    
    /**
     * 재고가 충분한지 확인
     */
    fun hasEnoughStock(requiredQuantity: Int): Boolean = stock >= requiredQuantity
    
    /**
     * 재고가 비어있는지 확인
     */
    fun isOutOfStock(): Boolean = stock <= 0
    
    /**
     * 상품이 유효한지 확인
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && 
               price >= BigDecimal.ZERO &&
               stock >= 0 &&
               createdAt != null &&
               updatedAt != null &&
               updatedAt.isAfter(createdAt) || updatedAt.isEqual(createdAt)
    }
    
    /**
     * 재고를 차감
     */
    fun reduceStock(quantity: Int): ProductEntity {
        require(quantity > 0) { "Quantity must be positive: $quantity" }
        require(stock >= quantity) { "Insufficient stock: available=$stock, required=$quantity" }
        return copy(stock = stock - quantity, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 재고를 증가
     */
    fun increaseStock(quantity: Int): ProductEntity {
        require(quantity > 0) { "Quantity must be positive: $quantity" }
        return copy(stock = stock + quantity, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 상품을 활성화
     */
    fun activate(): ProductEntity {
        return copy(isActive = true, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 상품을 비활성화
     */
    fun deactivate(): ProductEntity {
        return copy(isActive = false, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 가격을 업데이트
     */
    fun updatePrice(newPrice: BigDecimal): ProductEntity {
        require(newPrice >= BigDecimal.ZERO) { "Price cannot be negative: $newPrice" }
        return copy(price = newPrice, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 엔티티를 문자열로 변환
     */
    override fun toString(): String {
        return "ProductEntity(id=$id, name=$name, price=$price, stock=$stock, category=$category, brand=$brand, isActive=$isActive)"
    }
    
    companion object {
        /**
         * 새로운 상품 엔티티 생성
         */
        fun create(
            name: String,
            description: String? = null,
            price: BigDecimal,
            stock: Int,
            category: String? = null,
            brand: String? = null,
            imageUrl: String? = null,
            isActive: Boolean = true
        ): ProductEntity {
            val now = LocalDateTime.now()
            return ProductEntity(
                name = name,
                description = description,
                price = price,
                stock = stock,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                isActive = isActive,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 상품 엔티티 생성
         */
        fun createExisting(
            id: Long,
            name: String,
            description: String?,
            price: BigDecimal,
            stock: Int,
            category: String?,
            brand: String?,
            imageUrl: String?,
            isActive: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): ProductEntity {
            return ProductEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                stock = stock,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                isActive = isActive,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
