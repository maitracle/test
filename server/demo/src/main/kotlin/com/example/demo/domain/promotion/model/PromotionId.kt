package com.example.demo.domain.promotion.model

import com.example.demo.domain.common.valueobject.EntityId

/**
 * 프로모션 식별자 값 객체
 * 
 * 프로모션을 고유하게 식별하는 타입 안전한 식별자입니다.
 * EntityId를 래핑하여 프로모션 도메인에 특화된 의미를 제공합니다.
 */
@JvmInline
value class PromotionId(val entityId: EntityId) {
    val value: Long get() = entityId.value
    
    init {
        require(entityId.value > 0) { "PromotionId must be positive: ${entityId.value}" }
    }
    
    
    override fun toString(): String = "PromotionId($value)"
    
    companion object {
        /**
         * Long 값으로부터 PromotionId를 생성합니다.
         * 
         * @param value 프로모션 ID 값 (양수여야 함)
         * @return PromotionId 인스턴스
         * @throws IllegalArgumentException 값이 양수가 아닌 경우
         */
        fun of(value: Long): PromotionId = PromotionId(EntityId(value))
        
        /**
         * EntityId로부터 PromotionId를 생성합니다.
         * 
         * @param entityId EntityId 인스턴스
         * @return PromotionId 인스턴스
         */
        fun of(entityId: EntityId): PromotionId = PromotionId(entityId)
        
        /**
         * 안전하게 PromotionId를 생성합니다.
         * 값이 null이거나 양수가 아니면 null을 반환합니다.
         * 
         * @param value 프로모션 ID 값 (null 가능)
         * @return PromotionId 인스턴스 또는 null
         */
        fun safeOf(value: Long?): PromotionId? = value?.let {
            if (it > 0) PromotionId(EntityId(it)) else null
        }
        
        /**
         * EntityId로부터 안전하게 PromotionId를 생성합니다.
         * 
         * @param entityId EntityId 인스턴스 (null 가능)
         * @return PromotionId 인스턴스 또는 null
         */
        fun safeOf(entityId: EntityId?): PromotionId? = entityId?.let { PromotionId(it) }
    }
}
