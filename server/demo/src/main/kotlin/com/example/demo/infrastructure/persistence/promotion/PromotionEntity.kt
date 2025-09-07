package com.example.demo.infrastructure.persistence.promotion

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 프로모션 JPA 엔티티
 * 
 * 프로모션 도메인 모델을 데이터베이스에 매핑하기 위한 JPA 엔티티입니다.
 * 프로모션의 기본 정보, 조건, 혜택, 기간 등의 정보를 저장합니다.
 */
@Entity
@Table(
    name = "promotions",
    indexes = [
        Index(name = "idx_promotion_name", columnList = "name"),
        Index(name = "idx_promotion_type", columnList = "type"),
        Index(name = "idx_promotion_priority", columnList = "priority"),
        Index(name = "idx_promotion_is_active", columnList = "is_active"),
        Index(name = "idx_promotion_start_date", columnList = "start_date"),
        Index(name = "idx_promotion_end_date", columnList = "end_date"),
        Index(name = "idx_promotion_target_category", columnList = "target_category"),
        Index(name = "idx_promotion_target_user_level", columnList = "target_user_level"),
        Index(name = "idx_promotion_created_at", columnList = "created_at")
    ]
)
data class PromotionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 200)
    val name: String,
    
    @Column(length = 1000)
    val description: String? = null,
    
    @Column(nullable = false, length = 50)
    val type: String,
    
    @Column(nullable = false)
    val priority: Int,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val startDate: LocalDateTime,
    
    @Column(nullable = false)
    val endDate: LocalDateTime,
    
    @Column(length = 100)
    val targetCategory: String? = null,
    
    @Column(precision = 10, scale = 2)
    val minCartAmount: BigDecimal? = null,
    
    @Column
    val minQuantity: Int? = null,
    
    @Column(length = 20)
    val targetUserLevel: String? = null,
    
    @Column
    val isNewCustomerOnly: Boolean? = false,
    
    @Column(precision = 5, scale = 2)
    val discountPercentage: BigDecimal? = null,
    
    @Column(precision = 10, scale = 2)
    val discountAmount: BigDecimal? = null,
    
    @Column(precision = 10, scale = 2)
    val maxDiscountAmount: BigDecimal? = null,
    
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * 엔티티 생성 시점에 updatedAt 설정
     */
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        // createdAt은 생성자에서 이미 설정되므로 여기서는 updatedAt만 설정
    }
    
    /**
     * 엔티티 업데이트 시점에 updatedAt 갱신
     */
    @PreUpdate
    fun preUpdate() {
        // updatedAt은 생성자에서 이미 설정되므로 여기서는 추가 작업 없음
    }
    
    /**
     * 프로모션이 현재 유효한지 확인
     */
    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return isActive && 
               !now.isBefore(startDate) && 
               !now.isAfter(endDate)
    }
    
    /**
     * 프로모션이 시작되었는지 확인
     */
    fun hasStarted(): Boolean {
        return LocalDateTime.now().isAfter(startDate) || LocalDateTime.now().isEqual(startDate)
    }
    
    /**
     * 프로모션이 종료되었는지 확인
     */
    fun hasEnded(): Boolean {
        return LocalDateTime.now().isAfter(endDate)
    }
    
    /**
     * 프로모션이 시작되기 전인지 확인
     */
    fun isBeforeStart(): Boolean {
        return LocalDateTime.now().isBefore(startDate)
    }
    
    /**
     * 프로모션을 활성화
     */
    fun activate(): PromotionEntity {
        return copy(isActive = true, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 프로모션을 비활성화
     */
    fun deactivate(): PromotionEntity {
        return copy(isActive = false, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 프로모션 기간 연장
     */
    fun extend(days: Long): PromotionEntity {
        require(days > 0) { "Extension days must be positive: $days" }
        return copy(endDate = endDate.plusDays(days), updatedAt = LocalDateTime.now())
    }
    
    /**
     * 프로모션 기간 단축
     */
    fun shorten(days: Long): PromotionEntity {
        require(days > 0) { "Shortening days must be positive: $days" }
        val newEndDate = endDate.minusDays(days)
        require(newEndDate.isAfter(startDate)) { "Cannot shorten period beyond start date" }
        return copy(endDate = newEndDate, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 우선순위 업데이트
     */
    fun updatePriority(newPriority: Int): PromotionEntity {
        require(newPriority > 0) { "Priority must be positive: $newPriority" }
        return copy(priority = newPriority, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 할인 퍼센트 업데이트
     */
    fun updateDiscountPercentage(percentage: BigDecimal?): PromotionEntity {
        percentage?.let {
            require(it >= BigDecimal.ZERO && it <= BigDecimal(100)) { 
                "Discount percentage must be between 0 and 100: $it" 
            }
        }
        return copy(discountPercentage = percentage, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 할인 금액 업데이트
     */
    fun updateDiscountAmount(amount: BigDecimal?): PromotionEntity {
        amount?.let {
            require(it >= BigDecimal.ZERO) { "Discount amount cannot be negative: $it" }
        }
        return copy(discountAmount = amount, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 최대 할인 금액 업데이트
     */
    fun updateMaxDiscountAmount(amount: BigDecimal?): PromotionEntity {
        amount?.let {
            require(it >= BigDecimal.ZERO) { "Max discount amount cannot be negative: $it" }
        }
        return copy(maxDiscountAmount = amount, updatedAt = LocalDateTime.now())
    }
    
    /**
     * 엔티티가 유효한지 확인
     */
    fun isEntityValid(): Boolean {
        return name.isNotBlank() &&
               type.isNotBlank() &&
               priority > 0 &&
               startDate.isBefore(endDate) &&
               createdAt != null &&
               updatedAt != null &&
               (updatedAt.isAfter(createdAt) || updatedAt.isEqual(createdAt)) &&
               (discountPercentage?.let { it >= BigDecimal.ZERO && it <= BigDecimal(100) } ?: true) &&
               (discountAmount?.let { it >= BigDecimal.ZERO } ?: true) &&
               (maxDiscountAmount?.let { it >= BigDecimal.ZERO } ?: true) &&
               (minCartAmount?.let { it >= BigDecimal.ZERO } ?: true) &&
               (minQuantity?.let { it > 0 } ?: true)
    }
    
    /**
     * 엔티티를 문자열로 변환
     */
    override fun toString(): String {
        return "PromotionEntity(id=$id, name=$name, type=$type, priority=$priority, isActive=$isActive, startDate=$startDate, endDate=$endDate)"
    }
    
    companion object {
        /**
         * 새로운 프로모션 엔티티 생성
         */
        fun create(
            name: String,
            description: String? = null,
            type: String,
            priority: Int,
            startDate: LocalDateTime,
            endDate: LocalDateTime,
            targetCategory: String? = null,
            minCartAmount: BigDecimal? = null,
            minQuantity: Int? = null,
            targetUserLevel: String? = null,
            isNewCustomerOnly: Boolean? = false,
            discountPercentage: BigDecimal? = null,
            discountAmount: BigDecimal? = null,
            maxDiscountAmount: BigDecimal? = null,
            isActive: Boolean = true
        ): PromotionEntity {
            val now = LocalDateTime.now()
            return PromotionEntity(
                name = name,
                description = description,
                type = type,
                priority = priority,
                isActive = isActive,
                startDate = startDate,
                endDate = endDate,
                targetCategory = targetCategory,
                minCartAmount = minCartAmount,
                minQuantity = minQuantity,
                targetUserLevel = targetUserLevel,
                isNewCustomerOnly = isNewCustomerOnly,
                discountPercentage = discountPercentage,
                discountAmount = discountAmount,
                maxDiscountAmount = maxDiscountAmount,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 프로모션 엔티티 생성
         */
        fun createExisting(
            id: Long,
            name: String,
            description: String?,
            type: String,
            priority: Int,
            isActive: Boolean,
            startDate: LocalDateTime,
            endDate: LocalDateTime,
            targetCategory: String?,
            minCartAmount: BigDecimal?,
            minQuantity: Int?,
            targetUserLevel: String?,
            isNewCustomerOnly: Boolean?,
            discountPercentage: BigDecimal?,
            discountAmount: BigDecimal?,
            maxDiscountAmount: BigDecimal?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): PromotionEntity {
            return PromotionEntity(
                id = id,
                name = name,
                description = description,
                type = type,
                priority = priority,
                isActive = isActive,
                startDate = startDate,
                endDate = endDate,
                targetCategory = targetCategory,
                minCartAmount = minCartAmount,
                minQuantity = minQuantity,
                targetUserLevel = targetUserLevel,
                isNewCustomerOnly = isNewCustomerOnly,
                discountPercentage = discountPercentage,
                discountAmount = discountAmount,
                maxDiscountAmount = maxDiscountAmount,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
