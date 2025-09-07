package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.common.valueobject.Money
import java.math.BigDecimal

/**
 * 할인 퍼센트 값 객체
 * 
 * 프로모션에서 사용되는 할인 비율을 나타냅니다.
 * 0% ~ 100% 범위의 값을 가집니다.
 */
@JvmInline
value class DiscountPercentage(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO && value <= BigDecimal(100)) { 
            "Discount percentage must be between 0 and 100: $value" 
        }
    }
    
    /**
     * Money에 할인 퍼센트를 적용하여 할인 금액을 계산합니다.
     * 
     * @param money 원본 금액
     * @return 할인된 금액
     */
    operator fun times(money: Money): Money = Money(money.amount * (value / BigDecimal(100)))
    
    /**
     * 할인 퍼센트를 비교합니다.
     */
    operator fun compareTo(other: DiscountPercentage): Int = value.compareTo(other.value)
    
    /**
     * 할인이 적용되었는지 확인합니다.
     * 
     * @return 할인 적용 여부
     */
    fun isApplied(): Boolean = value > BigDecimal.ZERO
    
    /**
     * 할인 퍼센트가 0%인지 확인합니다.
     * 
     * @return 할인 퍼센트가 0%인지 여부
     */
    fun isZero(): Boolean = value == BigDecimal.ZERO
    
    /**
     * 할인 퍼센트를 Double로 반환합니다.
     * 
     * @return 할인 퍼센트
     */
    fun toDouble(): Double = value.toDouble()
    
    /**
     * 할인 퍼센트를 표시용 문자열로 반환합니다.
     * 
     * @return 표시용 문자열 (예: "10%")
     */
    fun toDisplayString(): String = "${value}%"
    
    override fun toString(): String = "${value}%"
    
    companion object {
        /**
         * BigDecimal로부터 할인 퍼센트를 생성합니다.
         * 
         * @param value 할인 퍼센트 (0-100)
         * @return DiscountPercentage 인스턴스
         */
        fun of(value: BigDecimal): DiscountPercentage = DiscountPercentage(value)
        
        /**
         * Double로부터 할인 퍼센트를 생성합니다.
         * 
         * @param value 할인 퍼센트 (0.0-100.0)
         * @return DiscountPercentage 인스턴스
         */
        fun of(value: Double): DiscountPercentage = DiscountPercentage(BigDecimal.valueOf(value))
        
        /**
         * Int로부터 할인 퍼센트를 생성합니다.
         * 
         * @param value 할인 퍼센트 (0-100)
         * @return DiscountPercentage 인스턴스
         */
        fun of(value: Int): DiscountPercentage = DiscountPercentage(BigDecimal.valueOf(value.toLong()))
        
        /**
         * 안전하게 할인 퍼센트를 생성합니다.
         * 값이 null이거나 범위를 벗어나면 null을 반환합니다.
         * 
         * @param value 할인 퍼센트 (null 가능)
         * @return DiscountPercentage 인스턴스 또는 null
         */
        fun safeOf(value: BigDecimal?): DiscountPercentage? = value?.let {
            if (it >= BigDecimal.ZERO && it <= BigDecimal(100)) DiscountPercentage(it) else null
        }
        
        /**
         * Double로부터 안전하게 할인 퍼센트를 생성합니다.
         * 
         * @param value 할인 퍼센트 (null 가능)
         * @return DiscountPercentage 인스턴스 또는 null
         */
        fun safeOf(value: Double?): DiscountPercentage? = value?.let {
            if (it >= 0.0 && it <= 100.0) DiscountPercentage(BigDecimal.valueOf(it)) else null
        }
        
        /**
         * 0% 할인을 생성합니다.
         * 
         * @return 0% 할인
         */
        fun zero(): DiscountPercentage = DiscountPercentage(BigDecimal.ZERO)
        
        /**
         * 100% 할인을 생성합니다.
         * 
         * @return 100% 할인
         */
        fun full(): DiscountPercentage = DiscountPercentage(BigDecimal(100))
    }
}
