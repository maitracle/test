package com.example.demo.domain.common.valueobject

/**
 * 재고를 나타내는 값 객체
 * 
 * 상품의 재고 수량을 관리하며,
 * 재고 확인, 차감, 증가 등의 재고 관리 기능을 제공합니다.
 * 
 * @property quantity 재고 수량 (0 이상)
 */
@JvmInline
value class Stock(val quantity: Int) {
    
    init {
        require(quantity >= 0) { 
            "Stock quantity cannot be negative: $quantity" 
        }
    }
    
    /**
     * 요청된 수량만큼 재고가 충분한지 확인
     */
    fun hasEnough(required: Quantity): Boolean = quantity >= required.value
    
    /**
     * 재고가 비어있는지 확인
     */
    fun isEmpty(): Boolean = quantity == 0
    
    /**
     * 재고가 있는지 확인
     */
    fun isNotEmpty(): Boolean = quantity > 0
    
    /**
     * 재고 차감
     * 
     * @param amount 차감할 수량
     * @return 차감 후 재고
     * @throws IllegalArgumentException 재고가 부족한 경우
     */
    fun reduce(amount: Quantity): Stock {
        require(hasEnough(amount)) { 
            "Insufficient stock: available=$quantity, required=${amount.value}" 
        }
        return Stock(quantity - amount.value)
    }
    
    /**
     * 재고 증가
     * 
     * @param amount 증가할 수량
     * @return 증가 후 재고
     */
    fun increase(amount: Quantity): Stock = Stock(quantity + amount.value)
    
    /**
     * 재고 설정
     * 
     * @param newQuantity 새로운 재고 수량
     * @return 설정된 재고
     */
    fun set(newQuantity: Int): Stock = Stock(newQuantity)
    
    /**
     * 재고 비교
     */
    operator fun compareTo(other: Stock): Int = quantity.compareTo(other.quantity)
    
    /**
     * 재고가 다른 재고보다 큰지 확인
     */
    fun isGreaterThan(other: Stock): Boolean = this > other
    
    /**
     * 재고가 다른 재고보다 작은지 확인
     */
    fun isLessThan(other: Stock): Boolean = this < other
    
    /**
     * 재고가 다른 재고와 같은지 확인
     */
    fun isEqualTo(other: Stock): Boolean = this == other
    
    /**
     * 재고가 최소 재고 이상인지 확인
     */
    fun isAtLeast(minStock: Stock): Boolean = this >= minStock
    
    /**
     * 재고가 최대 재고 이하인지 확인
     */
    fun isAtMost(maxStock: Stock): Boolean = this <= maxStock
    
    /**
     * 재고를 문자열로 변환
     */
    override fun toString(): String = quantity.toString()
    
    companion object {
        /**
         * 빈 재고 (0) 생성
         */
        fun empty(): Stock = Stock(0)
        
        /**
         * Int로부터 Stock 생성
         */
        fun of(quantity: Int): Stock = Stock(quantity)
        
        /**
         * Long으로부터 Stock 생성
         */
        fun of(quantity: Long): Stock = Stock(quantity.toInt())
        
        /**
         * String으로부터 Stock 생성
         */
        fun of(quantity: String): Stock = Stock(quantity.toInt())
        
        /**
         * 안전한 재고 생성 (음수일 경우 0으로 설정)
         */
        fun safeOf(quantity: Int): Stock = Stock(maxOf(0, quantity))
    }
}
