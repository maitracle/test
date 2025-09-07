package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.common.valueobject.Money
import java.math.BigDecimal

/**
 * 할인 금액 값 객체
 * 
 * 프로모션에 의해 적용되는 할인 금액을 나타냅니다.
 * Money를 래핑하여 할인 도메인에 특화된 의미를 제공합니다.
 */
@JvmInline
value class Discount(val amount: Money) {
    init {
        require(amount.amount >= BigDecimal.ZERO) { "Discount cannot be negative: ${amount.amount}" }
    }
    
    /**
     * 다른 할인과 더합니다.
     */
    operator fun plus(other: Discount): Discount = Discount(amount + other.amount)
    
    /**
     * 다른 할인을 뺍니다.
     */
    operator fun minus(other: Discount): Discount = Discount(amount - other.amount)
    
    /**
     * 할인 금액을 비교합니다.
     */
    operator fun compareTo(other: Discount): Int = amount.compareTo(other.amount)
    
    /**
     * 할인이 적용되었는지 확인합니다.
     * 
     * @return 할인 적용 여부
     */
    fun isApplied(): Boolean = amount.amount > BigDecimal.ZERO
    
    /**
     * 할인 금액이 0인지 확인합니다.
     * 
     * @return 할인 금액이 0인지 여부
     */
    fun isZero(): Boolean = amount.amount == BigDecimal.ZERO
    
    /**
     * 할인 금액을 BigDecimal로 반환합니다.
     * 
     * @return 할인 금액
     */
    fun toBigDecimal(): BigDecimal = amount.amount
    
    override fun toString(): String = "Discount(${amount.amount})"
    
    companion object {
        /**
         * 0원 할인을 생성합니다.
         * 
         * @return 0원 할인
         */
        fun zero(): Discount = Discount(Money.zero())
        
        /**
         * Money로부터 할인을 생성합니다.
         * 
         * @param money 할인 금액
         * @return Discount 인스턴스
         */
        fun of(money: Money): Discount = Discount(money)
        
        /**
         * BigDecimal로부터 할인을 생성합니다.
         * 
         * @param amount 할인 금액
         * @return Discount 인스턴스
         */
        fun of(amount: BigDecimal): Discount = Discount(Money(amount))
        
        /**
         * Long으로부터 할인을 생성합니다.
         * 
         * @param amount 할인 금액
         * @return Discount 인스턴스
         */
        fun of(amount: Long): Discount = Discount(Money.of(amount))
        
        /**
         * 안전하게 할인을 생성합니다.
         * 값이 null이거나 음수면 0원 할인을 반환합니다.
         * 
         * @param amount 할인 금액 (null 가능)
         * @return Discount 인스턴스
         */
        fun safeOf(amount: BigDecimal?): Discount = amount?.let {
            if (it >= BigDecimal.ZERO) Discount(Money(it)) else zero()
        } ?: zero()
    }
}
