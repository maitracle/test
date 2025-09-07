package com.example.demo.domain.common.valueobject

/**
 * 수량을 나타내는 값 객체
 * 
 * 상품의 수량, 주문 수량 등을 나타내며,
 * 양수만 허용하고 수량 관련 연산을 제공합니다.
 * 
 * @property value 수량 (양수)
 */
@JvmInline
value class Quantity(val value: Int) {
    
    init {
        require(value > 0) { 
            "Quantity must be positive: $value" 
        }
    }
    
    /**
     * 수량 덧셈
     */
    operator fun plus(other: Quantity): Quantity = Quantity(value + other.value)
    
    /**
     * 수량 뺄셈
     */
    operator fun minus(other: Quantity): Quantity = Quantity(value - other.value)
    
    /**
     * 수량 곱셈
     */
    operator fun times(multiplier: Int): Quantity = Quantity(value * multiplier)
    
    /**
     * 수량 비교
     */
    operator fun compareTo(other: Quantity): Int = value.compareTo(other.value)
    
    /**
     * 수량이 다른 수량보다 큰지 확인
     */
    fun isGreaterThan(other: Quantity): Boolean = this > other
    
    /**
     * 수량이 다른 수량보다 작은지 확인
     */
    fun isLessThan(other: Quantity): Boolean = this < other
    
    /**
     * 수량이 다른 수량과 같은지 확인
     */
    fun isEqualTo(other: Quantity): Boolean = this == other
    
    /**
     * 수량이 다른 수량보다 크거나 같은지 확인
     */
    fun isGreaterThanOrEqualTo(other: Quantity): Boolean = this >= other
    
    /**
     * 수량이 다른 수량보다 작거나 같은지 확인
     */
    fun isLessThanOrEqualTo(other: Quantity): Boolean = this <= other
    
    /**
     * 수량이 최소 수량 이상인지 확인
     */
    fun isAtLeast(minQuantity: Quantity): Boolean = this >= minQuantity
    
    /**
     * 수량이 최대 수량 이하인지 확인
     */
    fun isAtMost(maxQuantity: Quantity): Boolean = this <= maxQuantity
    
    /**
     * 수량을 문자열로 변환
     */
    override fun toString(): String = value.toString()
    
    companion object {
        /**
         * 최소 수량 (1) 생성
         */
        fun one(): Quantity = Quantity(1)
        
        /**
         * Int로부터 Quantity 생성
         */
        fun of(value: Int): Quantity = Quantity(value)
        
        /**
         * Long으로부터 Quantity 생성
         */
        fun of(value: Long): Quantity = Quantity(value.toInt())
        
        /**
         * String으로부터 Quantity 생성
         */
        fun of(value: String): Quantity = Quantity(value.toInt())
        
        /**
         * 안전한 수량 생성 (0 이하일 경우 예외 발생)
         */
        fun safeOf(value: Int): Quantity? = if (value > 0) Quantity(value) else null
    }
}
