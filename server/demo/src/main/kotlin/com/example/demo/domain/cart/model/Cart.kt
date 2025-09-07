package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.user.model.UserId
import java.time.LocalDateTime

/**
 * 장바구니 도메인 모델
 * 
 * 사용자의 장바구니를 나타내며, 장바구니 아이템 관리,
 * 총액 계산, 프로모션 적용 등의 핵심 비즈니스 로직을 포함합니다.
 * 
 * @param id 장바구니 식별자
 * @param userId 사용자 식별자
 * @param items 장바구니 아이템 목록
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
data class Cart(
    val id: CartId,
    val userId: UserId,
    val items: List<CartItem>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    
    init {
        require(items.isNotEmpty() || items.isEmpty()) { 
            "Cart items cannot be null" 
        }
    }
    
    /**
     * 장바구니의 총 금액을 계산합니다.
     * 
     * @return 총 금액
     */
    val totalAmount: Money
        get() = items.fold(Money.zero()) { acc, item -> acc + item.totalPrice }
    
    /**
     * 장바구니의 총 수량을 계산합니다.
     * 
     * @return 총 수량
     */
    val totalQuantity: Int
        get() = items.sumOf { it.quantity.value }
    
    /**
     * 장바구니가 비어있는지 확인합니다.
     * 
     * @return 비어있음 여부
     */
    val isEmpty: Boolean
        get() = items.isEmpty()
    
    /**
     * 장바구니에 아이템이 있는지 확인합니다.
     * 
     * @return 아이템 존재 여부
     */
    val isNotEmpty: Boolean
        get() = items.isNotEmpty()
    
    /**
     * 장바구니의 아이템 개수를 반환합니다.
     * 
     * @return 아이템 개수
     */
    val itemCount: Int
        get() = items.size
    
    /**
     * 장바구니에 특정 카테고리의 상품이 있는지 확인합니다.
     * 
     * @param category 확인할 카테고리
     * @return 카테고리 상품 존재 여부
     */
    fun hasCategory(category: String): Boolean {
        return items.any { it.belongsToCategory(category) }
    }
    
    /**
     * 장바구니에 특정 브랜드의 상품이 있는지 확인합니다.
     * 
     * @param brand 확인할 브랜드
     * @return 브랜드 상품 존재 여부
     */
    fun hasBrand(brand: String): Boolean {
        return items.any { it.belongsToBrand(brand) }
    }
    
    /**
     * 장바구니에 특정 상품이 있는지 확인합니다.
     * 
     * @param productId 확인할 상품 ID
     * @return 상품 존재 여부
     */
    fun hasProduct(productId: ProductId): Boolean {
        return items.any { it.product.id == productId }
    }
    
    /**
     * 장바구니에 상품을 추가합니다.
     * 이미 존재하는 상품인 경우 수량을 증가시킵니다.
     * 
     * @param product 추가할 상품
     * @param quantity 추가할 수량
     * @return 상품이 추가된 새로운 Cart 인스턴스
     * @throws IllegalArgumentException 상품이 유효하지 않은 경우
     */
    fun addItem(product: Product, quantity: Quantity): Cart {
        require(product.isActive) { 
            "Cannot add inactive product to cart: ${product.name}" 
        }
        require(product.isAvailable(quantity)) { 
            "Insufficient stock for product: ${product.name}" 
        }
        
        val existingItem = items.find { it.product.id == product.id }
        val updatedItems = if (existingItem != null) {
            items.map { item ->
                if (item.product.id == product.id) {
                    item.addQuantity(quantity)
                } else item
            }
        } else {
            items + CartItem.create(product, quantity)
        }
        
        return copy(items = updatedItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니에서 상품을 제거합니다.
     * 
     * @param productId 제거할 상품 ID
     * @return 상품이 제거된 새로운 Cart 인스턴스
     */
    fun removeItem(productId: ProductId): Cart {
        val updatedItems = items.filter { it.product.id != productId }
        return copy(items = updatedItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니에서 상품의 수량을 업데이트합니다.
     * 
     * @param productId 업데이트할 상품 ID
     * @param newQuantity 새로운 수량
     * @return 수량이 업데이트된 새로운 Cart 인스턴스
     * @throws IllegalArgumentException 상품이 존재하지 않거나 수량이 유효하지 않은 경우
     */
    fun updateItemQuantity(productId: ProductId, newQuantity: Quantity): Cart {
        val existingItem = items.find { it.product.id == productId }
            ?: throw IllegalArgumentException("Product not found in cart: $productId")
        
        require(existingItem.product.isAvailable(newQuantity)) { 
            "Insufficient stock for product: ${existingItem.product.name}" 
        }
        
        val updatedItems = items.map { item ->
            if (item.product.id == productId) {
                item.updateQuantity(newQuantity)
            } else item
        }
        
        return copy(items = updatedItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니를 비웁니다.
     * 
     * @return 비워진 새로운 Cart 인스턴스
     */
    fun clear(): Cart {
        return copy(items = emptyList(), updatedAt = LocalDateTime.now())
    }
    
    /**
     * 장바구니의 유효성을 검증합니다.
     * 모든 아이템이 유효한지 확인합니다.
     * 
     * @return 유효성 여부
     */
    fun isValid(): Boolean {
        return items.all { it.isValid() }
    }
    
    /**
     * 장바구니의 유효하지 않은 아이템들을 반환합니다.
     * 
     * @return 유효하지 않은 아이템 목록
     */
    fun getInvalidItems(): List<CartItem> {
        return items.filter { !it.isValid() }
    }
    
    /**
     * 장바구니의 특정 카테고리 아이템들을 반환합니다.
     * 
     * @param category 카테고리
     * @return 해당 카테고리의 아이템 목록
     */
    fun getItemsByCategory(category: String): List<CartItem> {
        return items.filter { it.belongsToCategory(category) }
    }
    
    /**
     * 장바구니의 특정 브랜드 아이템들을 반환합니다.
     * 
     * @param brand 브랜드
     * @return 해당 브랜드의 아이템 목록
     */
    fun getItemsByBrand(brand: String): List<CartItem> {
        return items.filter { it.belongsToBrand(brand) }
    }
    
    /**
     * 장바구니의 특정 상품 아이템을 반환합니다.
     * 
     * @param productId 상품 ID
     * @return 해당 상품의 아이템 또는 null
     */
    fun getItemByProductId(productId: ProductId): CartItem? {
        return items.find { it.product.id == productId }
    }
    
    /**
     * 장바구니의 총 가격이 최소 금액 이상인지 확인합니다.
     * 
     * @param minAmount 최소 금액
     * @return 최소 금액 이상 여부
     */
    fun meetsMinimumAmount(minAmount: Money): Boolean {
        return totalAmount >= minAmount
    }
    
    /**
     * 장바구니의 총 수량이 최소 수량 이상인지 확인합니다.
     * 
     * @param minQuantity 최소 수량
     * @return 최소 수량 이상 여부
     */
    fun meetsMinimumQuantity(minQuantity: Quantity): Boolean {
        return totalQuantity >= minQuantity.value
    }
    
    /**
     * 장바구니를 문자열로 변환합니다.
     */
    override fun toString(): String {
        return "Cart(id=$id, userId=$userId, itemCount=$itemCount, totalAmount=$totalAmount, totalQuantity=$totalQuantity)"
    }
    
    companion object {
        /**
         * 새로운 빈 장바구니를 생성합니다.
         * 
         * @param id 장바구니 식별자
         * @param userId 사용자 식별자
         * @return 새로운 Cart 인스턴스
         */
        fun createEmpty(
            id: CartId,
            userId: UserId
        ): Cart {
            val now = LocalDateTime.now()
            return Cart(
                id = id,
                userId = userId,
                items = emptyList(),
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 장바구니를 생성합니다.
         * 
         * @param id 장바구니 식별자
         * @param userId 사용자 식별자
         * @param items 장바구니 아이템 목록
         * @param createdAt 생성일시
         * @param updatedAt 수정일시
         * @return 새로운 Cart 인스턴스
         */
        fun createExisting(
            id: CartId,
            userId: UserId,
            items: List<CartItem>,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Cart {
            return Cart(
                id = id,
                userId = userId,
                items = items,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
