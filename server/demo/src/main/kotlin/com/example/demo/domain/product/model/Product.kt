package com.example.demo.domain.product.model

import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.valueobject.Price
import java.time.LocalDateTime

/**
 * 상품 도메인 모델
 * 
 * 상품의 핵심 비즈니스 로직과 규칙을 포함합니다.
 * 재고 관리, 가용성 검증 등의 비즈니스 규칙을 캡슐화합니다.
 * 
 * @param id 상품 식별자
 * @param name 상품명
 * @param description 상품 설명
 * @param price 상품 가격
 * @param stock 재고 수량
 * @param category 상품 카테고리
 * @param brand 브랜드
 * @param imageUrl 상품 이미지 URL
 * @param isActive 활성화 여부
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
data class Product(
    val id: ProductId,
    val name: String,
    val description: String?,
    val price: Price,
    val stock: Stock,
    val category: String?,
    val brand: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    
    init {
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(name.length <= 100) { "Product name cannot exceed 100 characters" }
        description?.let { 
            require(it.length <= 500) { "Product description cannot exceed 500 characters" }
        }
        category?.let { 
            require(it.length <= 50) { "Product category cannot exceed 50 characters" }
        }
        brand?.let { 
            require(it.length <= 100) { "Product brand cannot exceed 100 characters" }
        }
        imageUrl?.let { 
            require(it.length <= 200) { "Product imageUrl cannot exceed 200 characters" }
        }
    }
    
    /**
     * 상품이 주어진 수량만큼 구매 가능한지 확인합니다.
     * 
     * @param quantity 구매하려는 수량
     * @return 구매 가능 여부
     */
    fun isAvailable(quantity: Quantity): Boolean {
        return isActive && stock.hasEnough(quantity)
    }
    
    /**
     * 상품이 활성화되어 있는지 확인합니다.
     * 
     * @return 활성화 여부
     */
    fun isProductActive(): Boolean = isActive
    
    /**
     * 상품이 재고가 있는지 확인합니다.
     * 
     * @return 재고 보유 여부
     */
    fun hasStock(): Boolean = !stock.isEmpty()
    
    /**
     * 상품의 총 가격을 계산합니다.
     * 
     * @param quantity 수량
     * @return 총 가격
     */
    fun calculateTotalPrice(quantity: Quantity): Price {
        return price * quantity.value
    }
    
    /**
     * 재고를 차감합니다.
     * 
     * @param quantity 차감할 수량
     * @return 재고가 차감된 새로운 Product 인스턴스
     * @throws IllegalArgumentException 재고가 부족한 경우
     */
    fun reduceStock(quantity: Quantity): Product {
        require(stock.hasEnough(quantity)) { 
            "Insufficient stock. Available: ${stock.quantity}, Required: ${quantity.value}" 
        }
        return copy(
            stock = stock.reduce(quantity),
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 재고를 증가시킵니다.
     * 
     * @param quantity 증가할 수량
     * @return 재고가 증가된 새로운 Product 인스턴스
     */
    fun increaseStock(quantity: Quantity): Product {
        return copy(
            stock = stock.increase(quantity),
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 상품을 활성화합니다.
     * 
     * @return 활성화된 새로운 Product 인스턴스
     */
    fun activate(): Product {
        return copy(
            isActive = true,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 상품을 비활성화합니다.
     * 
     * @return 비활성화된 새로운 Product 인스턴스
     */
    fun deactivate(): Product {
        return copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 상품 정보를 업데이트합니다.
     * 
     * @param name 새로운 상품명
     * @param description 새로운 상품 설명
     * @param price 새로운 가격
     * @param category 새로운 카테고리
     * @param brand 새로운 브랜드
     * @param imageUrl 새로운 이미지 URL
     * @return 업데이트된 새로운 Product 인스턴스
     */
    fun updateInfo(
        name: String? = null,
        description: String? = null,
        price: Price? = null,
        category: String? = null,
        brand: String? = null,
        imageUrl: String? = null
    ): Product {
        return copy(
            name = name ?: this.name,
            description = description ?: this.description,
            price = price ?: this.price,
            category = category ?: this.category,
            brand = brand ?: this.brand,
            imageUrl = imageUrl ?: this.imageUrl,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 상품이 특정 카테고리에 속하는지 확인합니다.
     * 
     * @param targetCategory 확인할 카테고리
     * @return 카테고리 일치 여부
     */
    fun belongsToCategory(targetCategory: String): Boolean {
        return category?.equals(targetCategory, ignoreCase = true) ?: false
    }
    
    /**
     * 상품이 특정 브랜드에 속하는지 확인합니다.
     * 
     * @param targetBrand 확인할 브랜드
     * @return 브랜드 일치 여부
     */
    fun belongsToBrand(targetBrand: String): Boolean {
        return brand?.equals(targetBrand, ignoreCase = true) ?: false
    }
    
    companion object {
        /**
         * 새로운 상품을 생성합니다.
         * 
         * @param id 상품 식별자
         * @param name 상품명
         * @param description 상품 설명
         * @param price 상품 가격
         * @param stock 재고 수량
         * @param category 상품 카테고리
         * @param brand 브랜드
         * @param imageUrl 상품 이미지 URL
         * @return 새로운 Product 인스턴스
         */
        fun createNew(
            id: ProductId,
            name: String,
            description: String? = null,
            price: Price,
            stock: Stock,
            category: String? = null,
            brand: String? = null,
            imageUrl: String? = null
        ): Product {
            val now = LocalDateTime.now()
            return Product(
                id = id,
                name = name,
                description = description,
                price = price,
                stock = stock,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 상품을 생성합니다.
         * 
         * @param id 상품 식별자
         * @param name 상품명
         * @param description 상품 설명
         * @param price 상품 가격
         * @param stock 재고 수량
         * @param category 상품 카테고리
         * @param brand 브랜드
         * @param imageUrl 상품 이미지 URL
         * @param isActive 활성화 여부
         * @param createdAt 생성일시
         * @param updatedAt 수정일시
         * @return 새로운 Product 인스턴스
         */
        fun createExisting(
            id: ProductId,
            name: String,
            description: String? = null,
            price: Price,
            stock: Stock,
            category: String? = null,
            brand: String? = null,
            imageUrl: String? = null,
            isActive: Boolean = true,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Product {
            return Product(
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
