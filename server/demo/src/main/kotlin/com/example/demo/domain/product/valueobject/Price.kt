package com.example.demo.domain.product.valueobject

import com.example.demo.domain.common.valueobject.Money
import java.math.BigDecimal

/**
 * 상품 가격을 나타내는 값 객체
 * Money를 래핑하여 상품 가격의 의미를 명확히 합니다.
 * 
 * @param amount 가격 금액
 */
@JvmInline
value class Price(val amount: Money) {
    
    /**
     * 가격 비교 연산자
     */
    operator fun compareTo(other: Price): Int = amount.compareTo(other.amount)
    
    /**
     * 가격 덧셈 연산자
     */
    operator fun plus(other: Price): Price = Price(amount + other.amount)
    
    /**
     * 가격 뺄셈 연산자
     */
    operator fun minus(other: Price): Price = Price(amount - other.amount)
    
    /**
     * 가격 곱셈 연산자 (수량과 곱할 때 사용)
     */
    operator fun times(multiplier: BigDecimal): Price = Price(amount * multiplier)
    
    /**
     * 가격 곱셈 연산자 (정수 수량과 곱할 때 사용)
     */
    operator fun times(multiplier: Int): Price = Price(amount * multiplier)
    
    override fun toString(): String = "Price(${amount.amount})"
    
    companion object {
        /**
         * Price를 생성합니다.
         * 
         * @param amount 가격 금액
         * @return Price 인스턴스
         * @throws IllegalArgumentException 금액이 음수인 경우
         */
        fun of(amount: BigDecimal): Price = Price(Money(amount))
        
        /**
         * Price를 생성합니다.
         * 
         * @param amount 가격 금액
         * @return Price 인스턴스
         * @throws IllegalArgumentException 금액이 음수인 경우
         */
        fun of(amount: Money): Price = Price(amount)
        
        /**
         * 안전하게 Price를 생성합니다.
         * 
         * @param amount 가격 금액 (null 가능)
         * @return Price 인스턴스 또는 null
         */
        fun safeOf(amount: BigDecimal?): Price? = amount?.let { Price(Money(it)) }
        
        /**
         * 안전하게 Price를 생성합니다.
         * 
         * @param amount 가격 금액 (null 가능)
         * @return Price 인스턴스 또는 null
         */
        fun safeOf(amount: Money?): Price? = amount?.let { Price(it) }
        
        /**
         * 무료 상품을 위한 Price를 생성합니다.
         * 
         * @return Price 인스턴스 (금액: 0)
         */
        fun free(): Price = Price(Money.zero())
    }
}
