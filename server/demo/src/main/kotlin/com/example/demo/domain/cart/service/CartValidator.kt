package com.example.demo.domain.cart.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity

/**
 * 장바구니 검증 결과를 나타내는 데이터 클래스
 * 
 * @param isValid 검증 통과 여부
 * @param errors 검증 실패 오류 목록
 * @param warnings 경고 메시지 목록
 */
data class CartValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
) {
    /**
     * 검증이 성공했는지 확인합니다.
     */
    fun isSuccess(): Boolean = isValid && errors.isEmpty()
    
    /**
     * 검증이 실패했는지 확인합니다.
     */
    fun isFailure(): Boolean = !isValid || errors.isNotEmpty()
    
    /**
     * 경고가 있는지 확인합니다.
     */
    fun hasWarnings(): Boolean = warnings.isNotEmpty()
}

/**
 * 장바구니 검증 규칙을 나타내는 열거형
 */
enum class CartValidationRule {
    /**
     * 장바구니가 비어있지 않은지 확인
     */
    NOT_EMPTY,
    
    /**
     * 모든 아이템이 유효한지 확인
     */
    ALL_ITEMS_VALID,
    
    /**
     * 상품이 활성화되어 있는지 확인
     */
    PRODUCTS_ACTIVE,
    
    /**
     * 재고가 충분한지 확인
     */
    SUFFICIENT_STOCK,
    
    /**
     * 수량이 양수인지 확인
     */
    POSITIVE_QUANTITY,
    
    /**
     * 가격이 양수인지 확인
     */
    POSITIVE_PRICE,
    
    /**
     * 최소 주문 금액 확인
     */
    MINIMUM_ORDER_AMOUNT,
    
    /**
     * 최대 주문 금액 확인
     */
    MAXIMUM_ORDER_AMOUNT,
    
    /**
     * 최대 아이템 개수 확인
     */
    MAXIMUM_ITEM_COUNT
}

/**
 * 장바구니 검증 서비스
 * 
 * 장바구니의 비즈니스 규칙을 검증하는 도메인 서비스입니다.
 * 다양한 검증 규칙을 적용하여 장바구니의 유효성을 확인합니다.
 */
class CartValidator {
    
    /**
     * 장바구니를 검증합니다.
     * 
     * @param cart 검증할 장바구니
     * @param rules 적용할 검증 규칙 목록
     * @param minOrderAmount 최소 주문 금액 (선택사항)
     * @param maxOrderAmount 최대 주문 금액 (선택사항)
     * @param maxItemCount 최대 아이템 개수 (선택사항)
     * @return 검증 결과
     */
    fun validate(
        cart: Cart,
        rules: List<CartValidationRule> = CartValidationRule.values().toList(),
        minOrderAmount: Money? = null,
        maxOrderAmount: Money? = null,
        maxItemCount: Int? = null
    ): CartValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        for (rule in rules) {
            when (rule) {
                CartValidationRule.NOT_EMPTY -> {
                    if (cart.isEmpty) {
                        errors.add("장바구니가 비어있습니다.")
                    }
                }
                
                CartValidationRule.ALL_ITEMS_VALID -> {
                    val invalidItems = cart.getInvalidItems()
                    if (invalidItems.isNotEmpty()) {
                        errors.add("유효하지 않은 아이템이 있습니다: ${invalidItems.map { it.product.name }}")
                    }
                }
                
                CartValidationRule.PRODUCTS_ACTIVE -> {
                    val inactiveProducts = cart.items.filter { !it.product.isActive }
                    if (inactiveProducts.isNotEmpty()) {
                        errors.add("비활성화된 상품이 있습니다: ${inactiveProducts.map { it.product.name }}")
                    }
                }
                
                CartValidationRule.SUFFICIENT_STOCK -> {
                    val insufficientStockItems = cart.items.filter { !it.product.isAvailable(it.quantity) }
                    if (insufficientStockItems.isNotEmpty()) {
                        errors.add("재고가 부족한 상품이 있습니다: ${insufficientStockItems.map { "${it.product.name} (요청: ${it.quantity}, 재고: ${it.product.stock})" }}")
                    }
                }
                
                CartValidationRule.POSITIVE_QUANTITY -> {
                    val invalidQuantityItems = cart.items.filter { it.quantity.value <= 0 }
                    if (invalidQuantityItems.isNotEmpty()) {
                        errors.add("수량이 유효하지 않은 상품이 있습니다: ${invalidQuantityItems.map { it.product.name }}")
                    }
                }
                
                CartValidationRule.POSITIVE_PRICE -> {
                    val invalidPriceItems = cart.items.filter { it.unitPrice <= Money.zero() }
                    if (invalidPriceItems.isNotEmpty()) {
                        errors.add("가격이 유효하지 않은 상품이 있습니다: ${invalidPriceItems.map { it.product.name }}")
                    }
                }
                
                CartValidationRule.MINIMUM_ORDER_AMOUNT -> {
                    minOrderAmount?.let { minAmount ->
                        if (!cart.meetsMinimumAmount(minAmount)) {
                            errors.add("최소 주문 금액을 충족하지 않습니다. 현재: ${cart.totalAmount}, 최소: $minAmount")
                        }
                    }
                }
                
                CartValidationRule.MAXIMUM_ORDER_AMOUNT -> {
                    maxOrderAmount?.let { maxAmount ->
                        if (cart.totalAmount > maxAmount) {
                            errors.add("최대 주문 금액을 초과했습니다. 현재: ${cart.totalAmount}, 최대: $maxAmount")
                        }
                    }
                }
                
                CartValidationRule.MAXIMUM_ITEM_COUNT -> {
                    maxItemCount?.let { maxCount ->
                        if (cart.itemCount > maxCount) {
                            errors.add("최대 아이템 개수를 초과했습니다. 현재: ${cart.itemCount}, 최대: $maxCount")
                        }
                    }
                }
            }
        }
        
        // 경고 메시지 추가
        if (cart.totalAmount > Money.of(1000000)) {
            warnings.add("주문 금액이 높습니다. 결제 시 추가 확인이 필요할 수 있습니다.")
        }
        
        if (cart.itemCount > 20) {
            warnings.add("아이템 개수가 많습니다. 배송비가 추가될 수 있습니다.")
        }
        
        return CartValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * 장바구니 아이템을 검증합니다.
     * 
     * @param item 검증할 장바구니 아이템
     * @return 검증 결과
     */
    fun validateItem(item: CartItem): CartValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // 상품 활성화 확인
        if (!item.product.isActive) {
            errors.add("상품이 비활성화되어 있습니다: ${item.product.name}")
        }
        
        // 재고 확인
        if (!item.product.isAvailable(item.quantity)) {
            errors.add("재고가 부족합니다: ${item.product.name} (요청: ${item.quantity}, 재고: ${item.product.stock})")
        }
        
        // 수량 확인
        if (item.quantity.value <= 0) {
            errors.add("수량이 유효하지 않습니다: ${item.product.name}")
        }
        
        // 가격 확인
        if (item.unitPrice <= Money.zero()) {
            errors.add("가격이 유효하지 않습니다: ${item.product.name}")
        }
        
        // 경고 메시지
        if (item.quantity.value > 10) {
            warnings.add("수량이 많습니다: ${item.product.name} (${item.quantity})")
        }
        
        return CartValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * 장바구니의 기본 검증을 수행합니다.
     * 
     * @param cart 검증할 장바구니
     * @return 검증 결과
     */
    fun validateBasic(cart: Cart): CartValidationResult {
        return validate(
            cart = cart,
            rules = listOf(
                CartValidationRule.NOT_EMPTY,
                CartValidationRule.ALL_ITEMS_VALID,
                CartValidationRule.PRODUCTS_ACTIVE,
                CartValidationRule.SUFFICIENT_STOCK
            )
        )
    }
    
    /**
     * 장바구니의 주문 전 검증을 수행합니다.
     * 
     * @param cart 검증할 장바구니
     * @param minOrderAmount 최소 주문 금액
     * @return 검증 결과
     */
    fun validateForOrder(cart: Cart, minOrderAmount: Money): CartValidationResult {
        return validate(
            cart = cart,
            rules = CartValidationRule.values().toList(),
            minOrderAmount = minOrderAmount
        )
    }
    
    companion object {
        /**
         * 기본 검증 규칙 목록
         */
        val DEFAULT_RULES = listOf(
            CartValidationRule.NOT_EMPTY,
            CartValidationRule.ALL_ITEMS_VALID,
            CartValidationRule.PRODUCTS_ACTIVE,
            CartValidationRule.SUFFICIENT_STOCK,
            CartValidationRule.POSITIVE_QUANTITY,
            CartValidationRule.POSITIVE_PRICE
        )
        
        /**
         * 주문 전 검증 규칙 목록
         */
        val ORDER_RULES = CartValidationRule.values().toList()
    }
}
