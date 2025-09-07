package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.common.valueobject.Money
import java.math.BigDecimal

/**
 * 프로모션 혜택 값 객체
 * 
 * 프로모션에서 제공되는 혜택을 나타냅니다.
 * 할인 퍼센트, 고정 할인 금액, 최대 할인 금액 등의 혜택을 포함합니다.
 */
data class PromotionBenefits(
    val discountPercentage: DiscountPercentage? = null,
    val discountAmount: Money? = null,
    val maxDiscountAmount: Money? = null
) {
    
    init {
        require(discountPercentage != null || discountAmount != null) {
            "At least one discount type must be specified"
        }
        require(!(discountPercentage != null && discountAmount != null)) {
            "Cannot specify both percentage and fixed amount discount"
        }
    }
    
    /**
     * 장바구니에 대한 할인 금액을 계산합니다.
     * 
     * @param cart 장바구니
     * @return 계산된 할인 금액
     */
    fun calculateDiscount(cart: Cart): Discount {
        return when {
            discountPercentage != null -> {
                val discount = discountPercentage * cart.totalAmount
                val maxDiscount = maxDiscountAmount ?: discount
                Discount(if (discount <= maxDiscount) discount else maxDiscount)
            }
            discountAmount != null -> {
                Discount(if (discountAmount <= cart.totalAmount) discountAmount else cart.totalAmount)
            }
            else -> Discount.zero()
        }
    }
    
    /**
     * 퍼센트 할인인지 확인합니다.
     * 
     * @return 퍼센트 할인 여부
     */
    fun isPercentageDiscount(): Boolean = discountPercentage != null
    
    /**
     * 고정 금액 할인인지 확인합니다.
     * 
     * @return 고정 금액 할인 여부
     */
    fun isFixedAmountDiscount(): Boolean = discountAmount != null
    
    /**
     * 최대 할인 금액이 설정되어 있는지 확인합니다.
     * 
     * @return 최대 할인 금액 설정 여부
     */
    fun hasMaxDiscountLimit(): Boolean = maxDiscountAmount != null
    
    /**
     * 혜택이 설정되어 있는지 확인합니다.
     * 
     * @return 혜택 설정 여부
     */
    fun hasBenefits(): Boolean = discountPercentage != null || discountAmount != null
    
    /**
     * 혜택의 설명을 반환합니다.
     * 
     * @return 혜택 설명
     */
    fun getDescription(): String {
        return when {
            discountPercentage != null -> {
                val maxLimit = maxDiscountAmount?.let { " (최대 ${it.amount}원)" } ?: ""
                "${discountPercentage.toDisplayString()} 할인$maxLimit"
            }
            discountAmount != null -> "${discountAmount.amount}원 할인"
            else -> "혜택 없음"
        }
    }
    
    /**
     * 특정 금액에 대한 예상 할인 금액을 계산합니다.
     * 
     * @param amount 기준 금액
     * @return 예상 할인 금액
     */
    fun calculateExpectedDiscount(amount: Money): Money {
        return when {
            discountPercentage != null -> {
                val discount = discountPercentage * amount
                val maxDiscount = maxDiscountAmount ?: discount
                if (discount.amount <= maxDiscount.amount) discount else maxDiscount
            }
            discountAmount != null -> {
                if (discountAmount.amount <= amount.amount) discountAmount else amount
            }
            else -> Money.zero()
        }
    }
    
    /**
     * 혜택의 가치를 평가합니다.
     * 
     * @param cart 장바구니
     * @return 혜택 가치 (할인 금액)
     */
    fun evaluateValue(cart: Cart): Money {
        return calculateDiscount(cart).amount
    }
    
    /**
     * 혜택이 유효한지 확인합니다.
     * 
     * @return 혜택 유효 여부
     */
    fun isValid(): Boolean {
        return when {
            discountPercentage != null -> {
                discountPercentage.value > BigDecimal.ZERO &&
                (maxDiscountAmount == null || maxDiscountAmount.amount > BigDecimal.ZERO)
            }
            discountAmount != null -> {
                discountAmount.amount > BigDecimal.ZERO
            }
            else -> false
        }
    }
    
    override fun toString(): String = "PromotionBenefits(${getDescription()})"
    
    companion object {
        /**
         * 퍼센트 할인 혜택을 생성합니다.
         * 
         * @param percentage 할인 퍼센트
         * @param maxDiscount 최대 할인 금액 (선택사항)
         * @return 퍼센트 할인 혜택
         */
        fun percentageDiscount(
            percentage: DiscountPercentage,
            maxDiscount: Money? = null
        ): PromotionBenefits = PromotionBenefits(
            discountPercentage = percentage,
            maxDiscountAmount = maxDiscount
        )
        
        /**
         * 고정 금액 할인 혜택을 생성합니다.
         * 
         * @param amount 할인 금액
         * @return 고정 금액 할인 혜택
         */
        fun fixedAmountDiscount(amount: Money): PromotionBenefits = PromotionBenefits(
            discountAmount = amount
        )
        
        /**
         * 퍼센트 할인 혜택을 생성합니다 (Double 버전).
         * 
         * @param percentage 할인 퍼센트 (0.0-100.0)
         * @param maxDiscount 최대 할인 금액 (선택사항)
         * @return 퍼센트 할인 혜택
         */
        fun percentageDiscount(
            percentage: Double,
            maxDiscount: Money? = null
        ): PromotionBenefits = PromotionBenefits(
            discountPercentage = DiscountPercentage.of(percentage),
            maxDiscountAmount = maxDiscount
        )
        
        /**
         * 퍼센트 할인 혜택을 생성합니다 (Int 버전).
         * 
         * @param percentage 할인 퍼센트 (0-100)
         * @param maxDiscount 최대 할인 금액 (선택사항)
         * @return 퍼센트 할인 혜택
         */
        fun percentageDiscount(
            percentage: Int,
            maxDiscount: Money? = null
        ): PromotionBenefits = PromotionBenefits(
            discountPercentage = DiscountPercentage.of(percentage),
            maxDiscountAmount = maxDiscount
        )
        
        /**
         * 고정 금액 할인 혜택을 생성합니다 (BigDecimal 버전).
         * 
         * @param amount 할인 금액
         * @return 고정 금액 할인 혜택
         */
        fun fixedAmountDiscount(amount: BigDecimal): PromotionBenefits = PromotionBenefits(
            discountAmount = Money(amount)
        )
        
        /**
         * 고정 금액 할인 혜택을 생성합니다 (Long 버전).
         * 
         * @param amount 할인 금액
         * @return 고정 금액 할인 혜택
         */
        fun fixedAmountDiscount(amount: Long): PromotionBenefits = PromotionBenefits(
            discountAmount = Money.of(amount)
        )
    }
}
