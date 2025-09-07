package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.EntityId

/**
 * 장바구니 식별자를 나타내는 값 객체
 * EntityId를 래핑하여 장바구니 도메인의 의미를 명확히 합니다.
 * 
 * @param entityId 내부 EntityId 인스턴스
 */
@JvmInline
value class CartId(val entityId: EntityId) {
    
    /**
     * 장바구니 식별자 값
     */
    val value: Long get() = entityId.value
    
    /**
     * EntityId의 비교 기능을 위임
     */
    operator fun compareTo(other: CartId): Int = entityId.compareTo(other.entityId)
    
    /**
     * EntityId의 크기 비교 기능을 위임
     */
    fun isGreaterThan(other: CartId): Boolean = entityId.isGreaterThan(other.entityId)
    
    /**
     * EntityId의 크기 비교 기능을 위임
     */
    fun isLessThan(other: CartId): Boolean = entityId.isLessThan(other.entityId)
    
    /**
     * EntityId의 동등성 비교 기능을 위임
     */
    fun isEqualTo(other: CartId): Boolean = entityId.isEqualTo(other.entityId)
    
    /**
     * EntityId의 Int 변환 기능을 위임
     */
    fun toInt(): Int = entityId.toInt()
    
    /**
     * EntityId의 String 변환 기능을 위임
     */
    fun toStringValue(): String = entityId.toStringValue()
    
    override fun toString(): String = "CartId($value)"
    
    companion object {
        /**
         * CartId를 생성합니다.
         * 
         * @param value 장바구니 식별자 값
         * @return CartId 인스턴스
         * @throws IllegalArgumentException 값이 양수가 아닌 경우
         */
        fun of(value: Long): CartId = CartId(EntityId(value))
        
        /**
         * EntityId로부터 CartId를 생성합니다.
         * 
         * @param entityId EntityId 인스턴스
         * @return CartId 인스턴스
         */
        fun of(entityId: EntityId): CartId = CartId(entityId)
        
        /**
         * 안전하게 CartId를 생성합니다.
         * 
         * @param value 장바구니 식별자 값 (null 가능)
         * @return CartId 인스턴스 또는 null
         */
        fun safeOf(value: Long?): CartId? = value?.let { 
            if (it > 0) CartId(EntityId(it)) else null 
        }
        
        /**
         * 안전하게 CartId를 생성합니다.
         * 
         * @param entityId EntityId 인스턴스 (null 가능)
         * @return CartId 인스턴스 또는 null
         */
        fun safeOf(entityId: EntityId?): CartId? = entityId?.let { CartId(it) }
    }
}
