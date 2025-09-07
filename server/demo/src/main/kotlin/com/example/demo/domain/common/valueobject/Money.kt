package com.example.demo.domain.common.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 금액을 나타내는 값 객체
 * 
 * 금액 계산과 관련된 모든 연산을 캡슐화하며, 
 * 음수 금액을 방지하고 정밀한 금액 계산을 보장합니다.
 * 
 * @property amount 금액 (BigDecimal)
 */
@JvmInline
value class Money(val amount: BigDecimal) {
    
    init {
        require(amount >= BigDecimal.ZERO) { 
            "Money amount cannot be negative: $amount" 
        }
    }
    
    /**
     * 금액 덧셈
     */
    operator fun plus(other: Money): Money = Money(amount + other.amount)
    
    /**
     * 금액 뺄셈
     */
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    
    /**
     * 금액 곱셈 (BigDecimal)
     */
    operator fun times(multiplier: BigDecimal): Money = Money(amount * multiplier)
    
    /**
     * 금액 곱셈 (Int)
     */
    operator fun times(multiplier: Int): Money = Money(amount * BigDecimal(multiplier))
    
    /**
     * 금액 비교
     */
    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)
    
    /**
     * 금액이 다른 금액보다 큰지 확인
     */
    operator fun compareTo(other: BigDecimal): Int = amount.compareTo(other)
    
    /**
     * 금액이 다른 금액보다 큰지 확인
     */
    fun isGreaterThan(other: Money): Boolean = this > other
    
    /**
     * 금액이 다른 금액보다 작은지 확인
     */
    fun isLessThan(other: Money): Boolean = this < other
    
    /**
     * 금액이 다른 금액과 같은지 확인
     */
    fun isEqualTo(other: Money): Boolean = this == other
    
    /**
     * 금액이 0인지 확인
     */
    fun isZero(): Boolean = amount == BigDecimal.ZERO
    
    /**
     * 금액이 양수인지 확인
     */
    fun isPositive(): Boolean = amount > BigDecimal.ZERO
    
    /**
     * 금액을 지정된 소수점 자리수로 반올림
     */
    fun roundTo(scale: Int): Money = Money(amount.setScale(scale, RoundingMode.HALF_UP))
    
    /**
     * 금액을 문자열로 변환
     */
    override fun toString(): String = amount.toString()
    
    companion object {
        /**
         * 0원 생성
         */
        fun zero(): Money = Money(BigDecimal.ZERO)
        
        /**
         * BigDecimal로부터 Money 생성
         */
        fun of(amount: BigDecimal): Money = Money(amount)
        
        /**
         * Long으로부터 Money 생성
         */
        fun of(amount: Long): Money = Money(BigDecimal.valueOf(amount))
        
        /**
         * Double로부터 Money 생성 (정밀도 주의)
         */
        fun of(amount: Double): Money = Money(BigDecimal.valueOf(amount))
        
        /**
         * String으로부터 Money 생성
         */
        fun of(amount: String): Money = Money(BigDecimal(amount))
    }
}
