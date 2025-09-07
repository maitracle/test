package com.example.demo.domain.user.model

import com.example.demo.domain.common.valueobject.EntityId

/**
 * 사용자 ID를 나타내는 값 객체
 * 
 * 사용자의 고유 식별자를 나타내며,
 * EntityId를 상속하여 ID의 기본 기능을 제공합니다.
 * 
 * @property value 사용자 ID 값 (양수)
 */
@JvmInline
value class UserId(val value: Long) {
    
    init {
        require(value > 0) { 
            "UserId must be positive: $value" 
        }
    }
    
    /**
     * ID 비교
     */
    operator fun compareTo(other: UserId): Int = value.compareTo(other.value)
    
    /**
     * ID가 다른 ID보다 큰지 확인
     */
    fun isGreaterThan(other: UserId): Boolean = this > other
    
    /**
     * ID가 다른 ID보다 작은지 확인
     */
    fun isLessThan(other: UserId): Boolean = this < other
    
    /**
     * ID가 다른 ID와 같은지 확인
     */
    fun isEqualTo(other: UserId): Boolean = this == other
    
    /**
     * ID를 문자열로 변환
     */
    override fun toString(): String = value.toString()
    
    /**
     * ID를 Int로 변환 (주의: Long 범위 초과 시 예외 발생 가능)
     */
    fun toInt(): Int = value.toInt()
    
    /**
     * ID를 String으로 변환
     */
    fun toStringValue(): String = value.toString()
    
    companion object {
        /**
         * Long으로부터 UserId 생성
         */
        fun of(value: Long): UserId = UserId(value)
        
        /**
         * Int로부터 UserId 생성
         */
        fun of(value: Int): UserId = UserId(value.toLong())
        
        /**
         * String으로부터 UserId 생성
         */
        fun of(value: String): UserId = UserId(value.toLong())
        
        /**
         * 안전한 UserId 생성 (0 이하일 경우 null 반환)
         */
        fun safeOf(value: Long): UserId? = if (value > 0) UserId(value) else null
        
        /**
         * 안전한 UserId 생성 (Int)
         */
        fun safeOf(value: Int): UserId? = if (value > 0) UserId(value.toLong()) else null
        
        /**
         * 안전한 UserId 생성 (String)
         */
        fun safeOf(value: String): UserId? = try {
            val longValue = value.toLong()
            if (longValue > 0) UserId(longValue) else null
        } catch (e: NumberFormatException) {
            null
        }
    }
}
