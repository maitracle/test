package com.example.demo.domain.promotion.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.user.model.User
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 프로모션 규칙 평가 결과
 * 
 * 프로모션 규칙 평가의 상세 결과를 나타냅니다.
 */
data class RuleEvaluationResult(
    val isEligible: Boolean,
    val failedRules: List<String> = emptyList(),
    val passedRules: List<String> = emptyList()
) {
    /**
     * 모든 규칙이 통과했는지 확인합니다.
     * 
     * @return 모든 규칙 통과 여부
     */
    fun allRulesPassed(): Boolean = failedRules.isEmpty()
    
    /**
     * 실패한 규칙이 있는지 확인합니다.
     * 
     * @return 실패한 규칙 존재 여부
     */
    fun hasFailedRules(): Boolean = failedRules.isNotEmpty()
    
    /**
     * 통과한 규칙의 개수를 반환합니다.
     * 
     * @return 통과한 규칙 개수
     */
    fun getPassedRuleCount(): Int = passedRules.size
    
    /**
     * 실패한 규칙의 개수를 반환합니다.
     * 
     * @return 실패한 규칙 개수
     */
    fun getFailedRuleCount(): Int = failedRules.size
}

/**
 * 프로모션 규칙 엔진
 * 
 * 프로모션의 적용 규칙을 평가하고 검증하는 도메인 서비스입니다.
 * 다양한 비즈니스 규칙을 체계적으로 평가하여 프로모션 적용 가능성을 판단합니다.
 */
class PromotionRuleEngine {
    
    /**
     * 프로모션의 모든 규칙을 평가합니다.
     * 
     * @param promotion 평가할 프로모션
     * @param cart 장바구니
     * @param user 사용자
     * @return 규칙 평가 결과
     */
    fun evaluateRules(promotion: Promotion, cart: Cart, user: User): RuleEvaluationResult {
        val failedRules = mutableListOf<String>()
        val passedRules = mutableListOf<String>()
        
        // 기본 활성화 상태 확인
        if (!promotion.isActive) {
            failedRules.add("프로모션이 비활성화되어 있습니다")
        } else {
            passedRules.add("프로모션이 활성화되어 있습니다")
        }
        
        // 기간 유효성 확인
        if (!promotion.period.isValid()) {
            failedRules.add("프로모션 기간이 유효하지 않습니다 (${promotion.period})")
        } else {
            passedRules.add("프로모션 기간이 유효합니다")
        }
        
        // 조건 충족 확인
        if (!promotion.conditions.isSatisfiedBy(cart, user)) {
            failedRules.add("프로모션 조건을 충족하지 않습니다 (${promotion.conditions.getDescription()})")
        } else {
            passedRules.add("프로모션 조건을 충족합니다")
        }
        
        // 사용자 자격 확인
        if (!user.isEligibleForPromotion(promotion.conditions.targetUserLevel)) {
            failedRules.add("사용자가 프로모션 대상이 아닙니다")
        } else {
            passedRules.add("사용자가 프로모션 대상입니다")
        }
        
        val isEligible = failedRules.isEmpty()
        
        return RuleEvaluationResult(
            isEligible = isEligible,
            failedRules = failedRules,
            passedRules = passedRules
        )
    }
    
    /**
     * 프로모션 적용 가능성을 간단히 확인합니다.
     * 
     * @param promotion 평가할 프로모션
     * @param cart 장바구니
     * @param user 사용자
     * @return 적용 가능 여부
     */
    fun isEligibleForPromotion(promotion: Promotion, cart: Cart, user: User): Boolean {
        return promotion.isActive &&
               promotion.conditions.isSatisfiedBy(cart, user) &&
               promotion.period.isValid() &&
               user.isEligibleForPromotion(promotion.conditions.targetUserLevel)
    }
    
    /**
     * 여러 프로모션 중 적용 가능한 것들을 필터링합니다.
     * 
     * @param promotions 평가할 프로모션 목록
     * @param cart 장바구니
     * @param user 사용자
     * @return 적용 가능한 프로모션 목록
     */
    fun filterEligiblePromotions(
        promotions: List<Promotion>,
        cart: Cart,
        user: User
    ): List<Promotion> {
        return promotions.filter { promotion ->
            isEligibleForPromotion(promotion, cart, user)
        }
    }
    
    /**
     * 프로모션의 특정 조건을 개별적으로 평가합니다.
     * 
     * @param promotion 평가할 프로모션
     * @param cart 장바구니
     * @param user 사용자
     * @return 조건별 평가 결과
     */
    fun evaluateIndividualConditions(
        promotion: Promotion,
        cart: Cart,
        user: User
    ): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        // 활성화 상태
        results["isActive"] = promotion.isActive
        
        // 기간 유효성
        results["isPeriodValid"] = promotion.period.isValid()
        
        // 카테고리 조건
        results["isCategorySatisfied"] = promotion.conditions.targetCategory?.let { category ->
            cart.hasCategory(category)
        } ?: true
        
        // 최소 주문 금액 조건
        results["isMinAmountSatisfied"] = promotion.conditions.minCartAmount?.let { minAmount ->
            cart.totalAmount >= minAmount
        } ?: true
        
        // 최소 수량 조건
        results["isMinQuantitySatisfied"] = promotion.conditions.minQuantity?.let { minQuantity ->
            cart.totalQuantity >= minQuantity.value
        } ?: true
        
        // 사용자 등급 조건
        results["isUserLevelSatisfied"] = promotion.conditions.targetUserLevel?.let { targetLevel ->
            user.membershipLevel.priority >= targetLevel.priority
        } ?: true
        
        return results
    }
    
    /**
     * 프로모션 충돌을 확인합니다.
     * 
     * @param promotions 확인할 프로모션 목록
     * @return 충돌하는 프로모션 쌍 목록
     */
    fun detectPromotionConflicts(promotions: List<Promotion>): List<Pair<Promotion, Promotion>> {
        val conflicts = mutableListOf<Pair<Promotion, Promotion>>()
        
        for (i in promotions.indices) {
            for (j in i + 1 until promotions.size) {
                val promotion1 = promotions[i]
                val promotion2 = promotions[j]
                
                if (hasConflict(promotion1, promotion2)) {
                    conflicts.add(promotion1 to promotion2)
                }
            }
        }
        
        return conflicts
    }
    
    /**
     * 두 프로모션 간의 충돌 여부를 확인합니다.
     * 
     * @param promotion1 첫 번째 프로모션
     * @param promotion2 두 번째 프로모션
     * @return 충돌 여부
     */
    private fun hasConflict(promotion1: Promotion, promotion2: Promotion): Boolean {
        // 같은 타입의 프로모션이 동시에 적용되는 경우 충돌로 간주
        if (promotion1.type == promotion2.type) {
            return true
        }
        
        // 기간이 겹치는 경우
        if (promotion1.period.overlapsWith(promotion2.period)) {
            return true
        }
        
        // 같은 우선순위를 가진 경우
        if (promotion1.priority == promotion2.priority) {
            return true
        }
        
        return false
    }
    
    /**
     * 프로모션의 우선순위를 기준으로 정렬합니다.
     * 
     * @param promotions 정렬할 프로모션 목록
     * @return 우선순위 순으로 정렬된 프로모션 목록
     */
    fun sortByPriority(promotions: List<Promotion>): List<Promotion> {
        return promotions.sortedBy { it.priority }
    }
    
    /**
     * 프로모션의 할인 금액을 기준으로 정렬합니다.
     * 
     * @param promotions 정렬할 프로모션 목록
     * @param cart 장바구니
     * @param user 사용자
     * @return 할인 금액 순으로 정렬된 프로모션 목록
     */
    fun sortByDiscountAmount(
        promotions: List<Promotion>,
        cart: Cart,
        user: User
    ): List<Promotion> {
        return promotions.sortedByDescending { promotion ->
            promotion.calculateDiscount(cart, user).amount.amount
        }
    }
    
    /**
     * 프로모션의 효율성을 평가합니다.
     * 
     * @param promotion 평가할 프로모션
     * @param cart 장바구니
     * @param user 사용자
     * @return 효율성 점수 (0.0 ~ 1.0)
     */
    fun evaluatePromotionEfficiency(
        promotion: Promotion,
        cart: Cart,
        user: User
    ): Double {
        if (!isEligibleForPromotion(promotion, cart, user)) {
            return 0.0
        }
        
        val discount = promotion.calculateDiscount(cart, user)
        val discountRatio = if (cart.totalAmount.amount > BigDecimal.ZERO) {
            discount.amount.amount.divide(cart.totalAmount.amount, 4, RoundingMode.HALF_UP).toDouble()
        } else {
            0.0
        }
        
        // 우선순위가 높을수록(숫자가 작을수록) 효율성 증가
        val priorityScore = 1.0 - (promotion.priority / 100.0)
        
        // 할인 비율과 우선순위를 조합하여 효율성 계산
        // 할인 비율 70%, 우선순위 30% 가중치
        return (discountRatio * DISCOUNT_WEIGHT + priorityScore * PRIORITY_WEIGHT).coerceIn(0.0, 1.0)
    }
    
    companion object {
        private const val DISCOUNT_WEIGHT = 0.7
        private const val PRIORITY_WEIGHT = 0.3
    }
}
