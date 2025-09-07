package com.example.demo.domain.common.valueobject

/**
 * 엔티티의 ID를 나타내는 공통 값 객체
 * 
 * 모든 도메인 엔티티의 ID를 나타내며,
 * 타입 안전성과 ID 검증을 제공합니다.
 * 
 * @property value ID 값 (양수)
 */
@JvmInline
value class EntityId(val value: Long) {
    
    init {
        require(value > 0) { 
            "EntityId must be positive: $value" 
        }
    }
    
    /**
     * ID 비교
     */
    operator fun compareTo(other: EntityId): Int = value.compareTo(other.value)
    
    /**
     * ID가 다른 ID보다 큰지 확인
     */
    fun isGreaterThan(other: EntityId): Boolean = this > other
    
    /**
     * ID가 다른 ID보다 작은지 확인
     */
    fun isLessThan(other: EntityId): Boolean = this < other
    
    /**
     * ID가 다른 ID와 같은지 확인
     */
    fun isEqualTo(other: EntityId): Boolean = this == other
    
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
         * Long으로부터 EntityId 생성
         */
        fun of(value: Long): EntityId = EntityId(value)
        
        /**
         * Int로부터 EntityId 생성
         */
        fun of(value: Int): EntityId = EntityId(value.toLong())
        
        /**
         * String으로부터 EntityId 생성
         */
        fun of(value: String): EntityId = EntityId(value.toLong())
        
        /**
         * 안전한 EntityId 생성 (0 이하일 경우 null 반환)
         */
        fun safeOf(value: Long): EntityId? = if (value > 0) EntityId(value) else null
        
        /**
         * 안전한 EntityId 생성 (Int)
         */
        fun safeOf(value: Int): EntityId? = if (value > 0) EntityId(value.toLong()) else null
        
        /**
         * 안전한 EntityId 생성 (String)
         */
        fun safeOf(value: String): EntityId? = try {
            val longValue = value.toLong()
            if (longValue > 0) EntityId(longValue) else null
        } catch (e: NumberFormatException) {
            null
        }
    }
}
