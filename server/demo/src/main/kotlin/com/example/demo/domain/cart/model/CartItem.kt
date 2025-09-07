package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId

/**
 * 장바구니 아이템 도메인 모델
 * 
 * 장바구니에 담긴 상품의 정보와 수량, 가격을 관리합니다.
 * 상품의 가용성 검증과 수량 관리 등의 비즈니스 로직을 포함합니다.
 * 
 * @param product 상품 정보
 * @param quantity 수량
 * @param unitPrice 단위 가격 (상품 가격과 동일하지만 장바구니에서 변경될 수 있음)
 */
data class CartItem(
    val product: Product,
    val quantity: Quantity,
    val unitPrice: Money
) {
    
    init {
        require(unitPrice >= Money.zero()) { 
            "Unit price cannot be negative: $unitPrice" 
        }
    }
    
    /**
     * 장바구니 아이템의 총 가격을 계산합니다.
     * 
     * @return 총 가격 (단위 가격 × 수량)
     */
    val totalPrice: Money
        get() = unitPrice * quantity.value
    
    /**
     * 장바구니 아이템이 유효한지 확인합니다.
     * 상품이 활성화되어 있고 재고가 충분한지 검증합니다.
     * 
     * @return 유효성 여부
     */
    fun isValid(): Boolean {
        return product.isActive && product.isAvailable(quantity)
    }
    
    /**
     * 상품이 특정 카테고리에 속하는지 확인합니다.
     * 
     * @param category 확인할 카테고리
     * @return 카테고리 일치 여부
     */
    fun belongsToCategory(category: String): Boolean {
        return product.category?.equals(category, ignoreCase = true) ?: false
    }
    
    /**
     * 상품이 특정 브랜드에 속하는지 확인합니다.
     * 
     * @param brand 확인할 브랜드
     * @return 브랜드 일치 여부
     */
    fun belongsToBrand(brand: String): Boolean {
        return product.brand?.equals(brand, ignoreCase = true) ?: false
    }
    
    /**
     * 수량을 추가합니다.
     * 
     * @param additionalQuantity 추가할 수량
     * @return 수량이 추가된 새로운 CartItem 인스턴스
     * @throws IllegalArgumentException 추가 후 수량이 유효하지 않은 경우
     */
    fun addQuantity(additionalQuantity: Quantity): CartItem {
        val newQuantity = quantity + additionalQuantity
        return copy(quantity = newQuantity)
    }
    
    /**
     * 수량을 업데이트합니다.
     * 
     * @param newQuantity 새로운 수량
     * @return 수량이 업데이트된 새로운 CartItem 인스턴스
     * @throws IllegalArgumentException 새로운 수량이 유효하지 않은 경우
     */
    fun updateQuantity(newQuantity: Quantity): CartItem {
        return copy(quantity = newQuantity)
    }
    
    /**
     * 단위 가격을 업데이트합니다.
     * 
     * @param newUnitPrice 새로운 단위 가격
     * @return 단위 가격이 업데이트된 새로운 CartItem 인스턴스
     * @throws IllegalArgumentException 새로운 단위 가격이 음수인 경우
     */
    fun updateUnitPrice(newUnitPrice: Money): CartItem {
        require(newUnitPrice >= Money.zero()) { 
            "Unit price cannot be negative: $newUnitPrice" 
        }
        return copy(unitPrice = newUnitPrice)
    }
    
    /**
     * 장바구니 아이템의 할인된 가격을 계산합니다.
     * 
     * @param discountRate 할인율 (0.0 ~ 1.0)
     * @return 할인된 총 가격
     */
    fun calculateDiscountedPrice(discountRate: Double): Money {
        require(discountRate >= 0.0 && discountRate <= 1.0) { 
            "Discount rate must be between 0.0 and 1.0: $discountRate" 
        }
        val discountAmount = totalPrice * java.math.BigDecimal.valueOf(discountRate)
        return totalPrice - discountAmount
    }
    
    /**
     * 장바구니 아이템의 할인 금액을 계산합니다.
     * 
     * @param discountAmount 할인 금액
     * @return 할인된 총 가격
     */
    fun applyDiscount(discountAmount: Money): Money {
        require(discountAmount >= Money.zero()) { 
            "Discount amount cannot be negative: $discountAmount" 
        }
        val finalDiscount = if (discountAmount > totalPrice) totalPrice else discountAmount
        return totalPrice - finalDiscount
    }
    
    /**
     * 장바구니 아이템이 다른 아이템과 같은 상품인지 확인합니다.
     * 
     * @param other 다른 장바구니 아이템
     * @return 같은 상품 여부
     */
    fun isSameProduct(other: CartItem): Boolean {
        return product.id == other.product.id
    }
    
    /**
     * 장바구니 아이템을 문자열로 변환합니다.
     */
    override fun toString(): String {
        return "CartItem(product=${product.name}, quantity=$quantity, unitPrice=$unitPrice, totalPrice=$totalPrice)"
    }
    
    companion object {
        /**
         * 새로운 장바구니 아이템을 생성합니다.
         * 
         * @param product 상품 정보
         * @param quantity 수량
         * @return 새로운 CartItem 인스턴스
         */
        fun create(
            product: Product,
            quantity: Quantity
        ): CartItem {
            return CartItem(
                product = product,
                quantity = quantity,
                unitPrice = product.price.amount
            )
        }
        
        /**
         * 새로운 장바구니 아이템을 생성합니다 (단위 가격 지정).
         * 
         * @param product 상품 정보
         * @param quantity 수량
         * @param unitPrice 단위 가격
         * @return 새로운 CartItem 인스턴스
         */
        fun create(
            product: Product,
            quantity: Quantity,
            unitPrice: Money
        ): CartItem {
            return CartItem(
                product = product,
                quantity = quantity,
                unitPrice = unitPrice
            )
        }
    }
}
