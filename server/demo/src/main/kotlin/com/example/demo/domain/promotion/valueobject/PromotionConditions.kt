package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.valueobject.MembershipLevel

/**
 * 프로모션 적용 조건 값 객체
 * 
 * 프로모션이 적용되기 위해 충족되어야 하는 조건들을 나타냅니다.
 * 카테고리, 최소 주문 금액, 최소 수량, 대상 사용자 등급 등의 조건을 포함합니다.
 */
data class PromotionConditions(
    val targetCategory: String? = null,
    val minCartAmount: Money? = null,
    val minQuantity: Quantity? = null,
    val targetUserLevel: MembershipLevel? = null
) {
    
    /**
     * 장바구니와 사용자가 프로모션 조건을 충족하는지 확인합니다.
     * 
     * @param cart 장바구니
     * @param user 사용자
     * @return 조건 충족 여부
     */
    fun isSatisfiedBy(cart: Cart, user: User): Boolean {
        return isCategoryConditionSatisfied(cart) &&
               isCartAmountConditionSatisfied(cart) &&
               isQuantityConditionSatisfied(cart) &&
               isUserLevelConditionSatisfied(user)
    }
    
    /**
     * 카테고리 조건을 확인합니다.
     * 
     * @param cart 장바구니
     * @return 카테고리 조건 충족 여부
     */
    private fun isCategoryConditionSatisfied(cart: Cart): Boolean {
        return targetCategory == null || cart.hasCategory(targetCategory)
    }
    
    /**
     * 최소 주문 금액 조건을 확인합니다.
     * 
     * @param cart 장바구니
     * @return 최소 주문 금액 조건 충족 여부
     */
    private fun isCartAmountConditionSatisfied(cart: Cart): Boolean {
        return minCartAmount == null || cart.totalAmount >= minCartAmount
    }
    
    /**
     * 최소 수량 조건을 확인합니다.
     * 
     * @param cart 장바구니
     * @return 최소 수량 조건 충족 여부
     */
    private fun isQuantityConditionSatisfied(cart: Cart): Boolean {
        return minQuantity == null || cart.totalQuantity >= minQuantity.value
    }
    
    /**
     * 사용자 등급 조건을 확인합니다.
     * 
     * @param user 사용자
     * @return 사용자 등급 조건 충족 여부
     */
    private fun isUserLevelConditionSatisfied(user: User): Boolean {
        return targetUserLevel == null || user.membershipLevel >= targetUserLevel
    }
    
    /**
     * 조건이 설정되어 있는지 확인합니다.
     * 
     * @return 조건 설정 여부
     */
    fun hasConditions(): Boolean {
        return targetCategory != null ||
               minCartAmount != null ||
               minQuantity != null ||
               targetUserLevel != null
    }
    
    /**
     * 카테고리 조건이 설정되어 있는지 확인합니다.
     * 
     * @return 카테고리 조건 설정 여부
     */
    fun hasCategoryCondition(): Boolean = targetCategory != null
    
    /**
     * 최소 주문 금액 조건이 설정되어 있는지 확인합니다.
     * 
     * @return 최소 주문 금액 조건 설정 여부
     */
    fun hasCartAmountCondition(): Boolean = minCartAmount != null
    
    /**
     * 최소 수량 조건이 설정되어 있는지 확인합니다.
     * 
     * @return 최소 수량 조건 설정 여부
     */
    fun hasQuantityCondition(): Boolean = minQuantity != null
    
    /**
     * 사용자 등급 조건이 설정되어 있는지 확인합니다.
     * 
     * @return 사용자 등급 조건 설정 여부
     */
    fun hasUserLevelCondition(): Boolean = targetUserLevel != null
    
    /**
     * 조건의 개수를 반환합니다.
     * 
     * @return 조건 개수
     */
    fun getConditionCount(): Int {
        var count = 0
        if (targetCategory != null) count++
        if (minCartAmount != null) count++
        if (minQuantity != null) count++
        if (targetUserLevel != null) count++
        return count
    }
    
    /**
     * 조건을 설명하는 문자열을 반환합니다.
     * 
     * @return 조건 설명
     */
    fun getDescription(): String {
        val conditions = mutableListOf<String>()
        
        targetCategory?.let { conditions.add("카테고리: $it") }
        minCartAmount?.let { conditions.add("최소 주문 금액: ${it.amount}원") }
        minQuantity?.let { conditions.add("최소 수량: ${it.value}개") }
        targetUserLevel?.let { conditions.add("사용자 등급: ${it.displayName}") }
        
        return if (conditions.isEmpty()) "조건 없음" else conditions.joinToString(", ")
    }
    
    override fun toString(): String = "PromotionConditions(${getDescription()})"
    
    companion object {
        /**
         * 조건 없는 프로모션 조건을 생성합니다.
         * 
         * @return 조건 없는 PromotionConditions
         */
        fun noConditions(): PromotionConditions = PromotionConditions()
        
        /**
         * 카테고리 조건만 있는 프로모션 조건을 생성합니다.
         * 
         * @param category 대상 카테고리
         * @return 카테고리 조건만 있는 PromotionConditions
         */
        fun forCategory(category: String): PromotionConditions = 
            PromotionConditions(targetCategory = category)
        
        /**
         * 최소 주문 금액 조건만 있는 프로모션 조건을 생성합니다.
         * 
         * @param minAmount 최소 주문 금액
         * @return 최소 주문 금액 조건만 있는 PromotionConditions
         */
        fun forMinAmount(minAmount: Money): PromotionConditions = 
            PromotionConditions(minCartAmount = minAmount)
        
        /**
         * 사용자 등급 조건만 있는 프로모션 조건을 생성합니다.
         * 
         * @param userLevel 대상 사용자 등급
         * @return 사용자 등급 조건만 있는 PromotionConditions
         */
        fun forUserLevel(userLevel: MembershipLevel): PromotionConditions = 
            PromotionConditions(targetUserLevel = userLevel)
        
        /**
         * 모든 조건을 포함하는 프로모션 조건을 생성합니다.
         * 
         * @param category 대상 카테고리
         * @param minAmount 최소 주문 금액
         * @param minQuantity 최소 수량
         * @param userLevel 대상 사용자 등급
         * @return 모든 조건을 포함하는 PromotionConditions
         */
        fun all(
            category: String? = null,
            minAmount: Money? = null,
            minQuantity: Quantity? = null,
            userLevel: MembershipLevel? = null
        ): PromotionConditions = PromotionConditions(
            targetCategory = category,
            minCartAmount = minAmount,
            minQuantity = minQuantity,
            targetUserLevel = userLevel
        )
    }
}
