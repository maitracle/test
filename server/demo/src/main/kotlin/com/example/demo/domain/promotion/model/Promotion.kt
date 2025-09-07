package com.example.demo.domain.promotion.model

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.promotion.valueobject.Discount
import com.example.demo.domain.promotion.valueobject.PromotionBenefits
import com.example.demo.domain.promotion.valueobject.PromotionConditions
import com.example.demo.domain.promotion.valueobject.PromotionPeriod
import com.example.demo.domain.user.model.User
import java.time.LocalDateTime

/**
 * 프로모션 도메인 모델
 * 
 * 시스템에서 제공하는 프로모션을 나타냅니다.
 * 프로모션의 기본 정보, 적용 조건, 혜택, 기간 등을 포함합니다.
 */
data class Promotion(
    val id: PromotionId,
    val name: String,
    val description: String?,
    val type: PromotionType,
    val priority: Int,
    val isActive: Boolean,
    val period: PromotionPeriod,
    val conditions: PromotionConditions,
    val benefits: PromotionBenefits,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    
    init {
        require(name.isNotBlank()) { "Promotion name cannot be blank" }
        require(priority >= 0) { "Priority must be non-negative: $priority" }
    }
    
    /**
     * 프로모션이 장바구니와 사용자에게 적용 가능한지 확인합니다.
     * 
     * @param cart 장바구니
     * @param user 사용자
     * @return 적용 가능 여부
     */
    fun isApplicableTo(cart: Cart, user: User): Boolean {
        return isActive && 
               period.isValid() && 
               conditions.isSatisfiedBy(cart, user)
    }
    
    /**
     * 장바구니와 사용자에 대한 할인 금액을 계산합니다.
     * 
     * @param cart 장바구니
     * @param user 사용자
     * @return 계산된 할인 금액
     */
    fun calculateDiscount(cart: Cart, user: User): Discount {
        if (!isApplicableTo(cart, user)) {
            return Discount.zero()
        }
        return benefits.calculateDiscount(cart)
    }
    
    /**
     * 프로모션을 활성화합니다.
     * 
     * @return 활성화된 프로모션
     */
    fun activate(): Promotion = copy(isActive = true, updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션을 비활성화합니다.
     * 
     * @return 비활성화된 프로모션
     */
    fun deactivate(): Promotion = copy(isActive = false, updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션이 현재 유효한지 확인합니다.
     * 
     * @return 프로모션 유효 여부
     */
    fun isValid(): Boolean = isActive && period.isValid()
    
    /**
     * 프로모션이 시작되었는지 확인합니다.
     * 
     * @return 프로모션 시작 여부
     */
    fun hasStarted(): Boolean = period.hasStarted()
    
    /**
     * 프로모션이 종료되었는지 확인합니다.
     * 
     * @return 프로모션 종료 여부
     */
    fun hasEnded(): Boolean = period.hasEnded()
    
    /**
     * 프로모션이 시작되기 전인지 확인합니다.
     * 
     * @return 프로모션 시작 전 여부
     */
    fun isBeforeStart(): Boolean = period.isBeforeStart()
    
    /**
     * 프로모션의 우선순위를 변경합니다.
     * 
     * @param newPriority 새로운 우선순위
     * @return 우선순위가 변경된 프로모션
     */
    fun changePriority(newPriority: Int): Promotion {
        require(newPriority >= 0) { "Priority must be non-negative: $newPriority" }
        return copy(priority = newPriority, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 프로모션의 설명을 업데이트합니다.
     * 
     * @param newDescription 새로운 설명
     * @return 설명이 업데이트된 프로모션
     */
    fun updateDescription(newDescription: String?): Promotion = 
        copy(description = newDescription, updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션의 기간을 연장합니다.
     * 
     * @param days 연장할 일수
     * @return 기간이 연장된 프로모션
     */
    fun extendPeriod(days: Long): Promotion = 
        copy(period = period.extend(days), updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션의 기간을 단축합니다.
     * 
     * @param days 단축할 일수
     * @return 기간이 단축된 프로모션
     */
    fun shortenPeriod(days: Long): Promotion = 
        copy(period = period.shorten(days), updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션의 조건을 업데이트합니다.
     * 
     * @param newConditions 새로운 조건
     * @return 조건이 업데이트된 프로모션
     */
    fun updateConditions(newConditions: PromotionConditions): Promotion = 
        copy(conditions = newConditions, updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션의 혜택을 업데이트합니다.
     * 
     * @param newBenefits 새로운 혜택
     * @return 혜택이 업데이트된 프로모션
     */
    fun updateBenefits(newBenefits: PromotionBenefits): Promotion = 
        copy(benefits = newBenefits, updatedAt = LocalDateTime.now())
    
    /**
     * 프로모션의 상태를 설명하는 문자열을 반환합니다.
     * 
     * @return 상태 설명
     */
    fun getStatusDescription(): String {
        return when {
            !isActive -> "비활성화됨"
            isBeforeStart() -> "시작 전"
            hasEnded() -> "종료됨"
            isValid() -> "진행 중"
            else -> "알 수 없음"
        }
    }
    
    /**
     * 프로모션의 요약 정보를 반환합니다.
     * 
     * @return 요약 정보
     */
    fun getSummary(): String {
        return "$name (${type.displayName}) - ${benefits.getDescription()}"
    }
    
    override fun toString(): String = "Promotion(id=$id, name='$name', type=$type, priority=$priority, active=$isActive)"
    
    companion object {
        /**
         * 새로운 프로모션을 생성합니다.
         * 
         * @param name 프로모션 이름
         * @param description 프로모션 설명
         * @param type 프로모션 타입
         * @param priority 우선순위
         * @param period 프로모션 기간
         * @param conditions 적용 조건
         * @param benefits 제공 혜택
         * @return 새로운 프로모션
         */
        fun create(
            name: String,
            description: String?,
            type: PromotionType,
            priority: Int,
            period: PromotionPeriod,
            conditions: PromotionConditions,
            benefits: PromotionBenefits
        ): Promotion {
            val now = LocalDateTime.now()
            return Promotion(
                id = PromotionId.of(1L), // 임시 ID, 저장 시 실제 ID로 변경됨
                name = name,
                description = description,
                type = type,
                priority = priority,
                isActive = true,
                period = period,
                conditions = conditions,
                benefits = benefits,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 프로모션을 복사하여 새로운 프로모션을 생성합니다.
         * 
         * @param original 원본 프로모션
         * @param newName 새로운 이름
         * @param newDescription 새로운 설명
         * @return 복사된 프로모션
         */
        fun copyFrom(
            original: Promotion,
            newName: String,
            newDescription: String? = original.description
        ): Promotion {
            val now = LocalDateTime.now()
            return original.copy(
                id = PromotionId.of(1L), // 임시 ID
                name = newName,
                description = newDescription,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}
