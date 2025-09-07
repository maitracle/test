package com.example.demo.domain.promotion.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.valueobject.Discount
import com.example.demo.domain.user.model.User
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 적용된 프로모션 정보
 * 
 * 실제로 적용된 프로모션의 상세 정보를 나타냅니다.
 */
data class AppliedPromotion(
    val promotionId: Long,
    val promotionName: String,
    val discountAmount: BigDecimal
)

/**
 * 프로모션 계산 결과
 * 
 * 프로모션 적용 후의 계산 결과를 나타냅니다.
 */
data class PromotionResult(
    val subtotal: BigDecimal,
    val totalDiscount: BigDecimal,
    val finalAmount: BigDecimal,
    val appliedPromotions: List<AppliedPromotion>
) {
    /**
     * 할인이 적용되었는지 확인합니다.
     * 
     * @return 할인 적용 여부
     */
    fun hasDiscount(): Boolean = totalDiscount > BigDecimal.ZERO
    
    /**
     * 적용된 프로모션의 개수를 반환합니다.
     * 
     * @return 적용된 프로모션 개수
     */
    fun getAppliedPromotionCount(): Int = appliedPromotions.size
    
    /**
     * 할인율을 계산합니다.
     * 
     * @return 할인율 (0.0 ~ 1.0)
     */
    fun getDiscountRatio(): Double {
        return if (subtotal > BigDecimal.ZERO) {
            totalDiscount.divide(subtotal, 4, RoundingMode.HALF_UP).toDouble()
        } else {
            0.0
        }
    }
}

/**
 * 프로모션 계산 서비스
 * 
 * 장바구니에 적용 가능한 프로모션들을 계산하고 할인 금액을 결정하는 도메인 서비스입니다.
 * 여러 프로모션이 동시에 적용될 때의 우선순위와 중복 적용을 관리합니다.
 */
class PromotionCalculator {
    
    /**
     * 장바구니와 사용자에 대해 프로모션 할인을 계산합니다.
     * 
     * @param cart 장바구니
     * @param promotions 적용할 프로모션 목록 (우선순위 순으로 정렬되어야 함)
     * @param user 사용자
     * @return 프로모션 계산 결과
     */
    fun calculateDiscounts(
        cart: Cart, 
        promotions: List<Promotion>,
        user: User
    ): PromotionResult {
        var totalDiscount = Discount.zero()
        val appliedPromotions = mutableListOf<AppliedPromotion>()
        
        for (promotion in promotions) {
            val discount = promotion.calculateDiscount(cart, user)
            if (discount.isApplied()) {
                totalDiscount += discount
                appliedPromotions.add(AppliedPromotion(
                    promotionId = promotion.id.value,
                    promotionName = promotion.name,
                    discountAmount = discount.toBigDecimal()
                ))
            }
        }
        
        // 최종 금액이 음수가 되지 않도록 보장 (할인 금액이 상품 가격보다 클 수 없음)
        val finalAmount = if (totalDiscount.amount > cart.totalAmount) {
            Money.zero()
        } else {
            cart.totalAmount - totalDiscount.amount
        }
        
        return PromotionResult(
            subtotal = cart.totalAmount.amount,
            totalDiscount = totalDiscount.amount.amount,
            finalAmount = finalAmount.amount,
            appliedPromotions = appliedPromotions
        )
    }
    
    /**
     * 단일 프로모션의 할인을 계산합니다.
     * 
     * @param cart 장바구니
     * @param promotion 프로모션
     * @param user 사용자
     * @return 계산된 할인 금액
     */
    fun calculateSinglePromotionDiscount(
        cart: Cart,
        promotion: Promotion,
        user: User
    ): Discount {
        return promotion.calculateDiscount(cart, user)
    }
    
    /**
     * 여러 프로모션의 할인을 비교합니다.
     * 
     * @param cart 장바구니
     * @param promotions 비교할 프로모션 목록
     * @param user 사용자
     * @return 프로모션별 할인 금액 목록
     */
    fun comparePromotionDiscounts(
        cart: Cart,
        promotions: List<Promotion>,
        user: User
    ): List<Pair<Promotion, Discount>> {
        return promotions.map { promotion ->
            promotion to promotion.calculateDiscount(cart, user)
        }.sortedByDescending { (_, discount) -> discount.amount.amount }
    }
    
    /**
     * 최적의 프로모션 조합을 찾습니다.
     * 
     * @param cart 장바구니
     * @param availablePromotions 사용 가능한 프로모션 목록
     * @param user 사용자
     * @param maxPromotions 최대 적용 가능한 프로모션 개수
     * @return 최적의 프로모션 조합 결과
     */
    fun findOptimalPromotionCombination(
        cart: Cart,
        availablePromotions: List<Promotion>,
        user: User,
        maxPromotions: Int = 3
    ): PromotionResult {
        val applicablePromotions = availablePromotions.filter { promotion ->
            promotion.isApplicableTo(cart, user)
        }.sortedBy { it.priority }
        
        return calculateDiscounts(cart, applicablePromotions.take(maxPromotions), user)
    }
    
    /**
     * 프로모션 적용 가능성을 미리 확인합니다.
     * 
     * @param cart 장바구니
     * @param promotion 프로모션
     * @param user 사용자
     * @return 적용 가능성과 예상 할인 금액
     */
    fun previewPromotionApplication(
        cart: Cart,
        promotion: Promotion,
        user: User
    ): Pair<Boolean, Discount> {
        val isApplicable = promotion.isApplicableTo(cart, user)
        val expectedDiscount = if (isApplicable) {
            promotion.calculateDiscount(cart, user)
        } else {
            Discount.zero()
        }
        return isApplicable to expectedDiscount
    }
    
    /**
     * 프로모션 적용 후 최종 금액을 계산합니다.
     * 
     * @param cart 장바구니
     * @param promotions 적용할 프로모션 목록
     * @param user 사용자
     * @return 최종 금액
     */
    fun calculateFinalAmount(
        cart: Cart,
        promotions: List<Promotion>,
        user: User
    ): BigDecimal {
        val result = calculateDiscounts(cart, promotions, user)
        return result.finalAmount
    }
    
    /**
     * 프로모션 적용으로 인한 절약 금액을 계산합니다.
     * 
     * @param cart 장바구니
     * @param promotions 적용할 프로모션 목록
     * @param user 사용자
     * @return 절약 금액
     */
    fun calculateSavings(
        cart: Cart,
        promotions: List<Promotion>,
        user: User
    ): BigDecimal {
        val result = calculateDiscounts(cart, promotions, user)
        return result.totalDiscount
    }
}
