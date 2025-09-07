package com.example.demo.domain.promotion.model

/**
 * 프로모션 타입 열거형
 * 
 * 시스템에서 지원하는 다양한 프로모션 유형을 정의합니다.
 * 각 타입은 고유한 비즈니스 의미와 할인 계산 방식을 가집니다.
 */
enum class PromotionType(
    val displayName: String,
    val description: String
) {
    /**
     * 퍼센트 할인
     * 주문 금액의 일정 비율을 할인합니다.
     */
    PERCENTAGE_DISCOUNT("퍼센트 할인", "주문 금액의 일정 비율을 할인하는 프로모션"),
    
    /**
     * 고정 금액 할인
     * 고정된 금액을 할인합니다.
     */
    FIXED_DISCOUNT("고정 금액 할인", "고정된 금액을 할인하는 프로모션"),
    
    /**
     * 1+1 프로모션
     * 하나를 구매하면 하나를 무료로 제공합니다.
     */
    BUY_ONE_GET_ONE("1+1 프로모션", "하나를 구매하면 하나를 무료로 제공하는 프로모션"),
    
    /**
     * 무료 배송
     * 배송비를 무료로 제공합니다.
     */
    FREE_SHIPPING("무료 배송", "배송비를 무료로 제공하는 프로모션"),
    
    /**
     * 캐시백
     * 구매 금액의 일정 비율을 캐시백으로 제공합니다.
     */
    CASHBACK("캐시백", "구매 금액의 일정 비율을 캐시백으로 제공하는 프로모션");
    
    /**
     * 할인 계산이 가능한 프로모션 타입인지 확인합니다.
     * 
     * @return 할인 계산 가능 여부
     */
    fun supportsDiscountCalculation(): Boolean {
        return when (this) {
            PERCENTAGE_DISCOUNT, FIXED_DISCOUNT -> true
            BUY_ONE_GET_ONE, FREE_SHIPPING, CASHBACK -> false
        }
    }
    
    /**
     * 수량 기반 프로모션인지 확인합니다.
     * 
     * @return 수량 기반 프로모션 여부
     */
    fun isQuantityBased(): Boolean {
        return this == BUY_ONE_GET_ONE
    }
    
    /**
     * 배송 관련 프로모션인지 확인합니다.
     * 
     * @return 배송 관련 프로모션 여부
     */
    fun isShippingRelated(): Boolean {
        return this == FREE_SHIPPING
    }
    
    /**
     * 캐시백 프로모션인지 확인합니다.
     * 
     * @return 캐시백 프로모션 여부
     */
    fun isCashback(): Boolean {
        return this == CASHBACK
    }
    
    override fun toString(): String = displayName
}
