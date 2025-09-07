package com.example.demo.infrastructure.persistence.promotion

import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 프로모션 Repository 구현체
 * 
 * PromotionRepository 포트의 구현체로, JPA를 사용하여 프로모션 데이터를 관리합니다.
 * 도메인 모델과 엔티티 간의 변환을 담당합니다.
 */
@Component
@Transactional(readOnly = true)
class PromotionRepositoryImpl(
    private val promotionJpaRepository: PromotionJpaRepository
) : PromotionRepository {
    
    override fun findActivePromotions(): List<Promotion> {
        return promotionJpaRepository.findByIsActiveTrue()
            .map { it.toDomain() }
    }
    
    override fun findActivePromotionsAt(dateTime: LocalDateTime): List<Promotion> {
        return promotionJpaRepository.findActivePromotionsAt(dateTime)
            .map { it.toDomain() }
    }
    
    override fun findById(promotionId: PromotionId): Promotion? {
        return promotionJpaRepository.findById(promotionId.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findByName(name: String): Promotion? {
        return promotionJpaRepository.findByName(name)
            ?.toDomain()
    }
    
    @Transactional
    override fun save(promotion: Promotion): Promotion {
        val entity = promotion.toEntity()
        val savedEntity = promotionJpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    @Transactional
    override fun delete(promotionId: PromotionId) {
        promotionJpaRepository.deleteById(promotionId.value)
    }
    
    override fun findAll(): List<Promotion> {
        return promotionJpaRepository.findAll()
            .map { it.toDomain() }
    }
    
    override fun findActivePromotionsOrderByPriority(): List<Promotion> {
        return promotionJpaRepository.findByIsActiveTrueOrderByPriorityAsc()
            .map { it.toDomain() }
    }
}

/**
 * PromotionEntity를 Promotion 도메인 모델로 변환하는 확장 함수
 */
private fun PromotionEntity.toDomain(): Promotion {
    val promotion = Promotion.create(
        name = name,
        description = description,
        type = com.example.demo.domain.promotion.model.PromotionType.valueOf(type),
        priority = priority,
        period = com.example.demo.domain.promotion.valueobject.PromotionPeriod(startDate, endDate),
        conditions = com.example.demo.domain.promotion.valueobject.PromotionConditions(
            targetCategory = targetCategory,
            minCartAmount = minCartAmount?.let { com.example.demo.domain.common.valueobject.Money.of(it) },
            minQuantity = minQuantity?.let { com.example.demo.domain.common.valueobject.Quantity.of(it) },
            targetUserLevel = targetUserLevel?.let { com.example.demo.domain.user.valueobject.MembershipLevel.valueOf(it) },
            isNewCustomerOnly = isNewCustomerOnly
        ),
        benefits = com.example.demo.domain.promotion.valueobject.PromotionBenefits(
            discountPercentage = discountPercentage?.let { com.example.demo.domain.promotion.valueobject.DiscountPercentage.of(it) },
            discountAmount = discountAmount?.let { com.example.demo.domain.common.valueobject.Money.of(it) },
            maxDiscountAmount = maxDiscountAmount?.let { com.example.demo.domain.common.valueobject.Money.of(it) }
        )
    )
    
    return promotion.copy(
        id = PromotionId.of(id ?: throw IllegalStateException("Promotion ID cannot be null")),
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Promotion 도메인 모델을 PromotionEntity로 변환하는 확장 함수
 */
private fun Promotion.toEntity(): PromotionEntity {
    return PromotionEntity.createExisting(
        id = id.value,
        name = name,
        description = description,
        type = type.name,
        priority = priority,
        isActive = isActive,
        startDate = period.startDate,
        endDate = period.endDate,
        targetCategory = conditions.targetCategory,
        minCartAmount = conditions.minCartAmount?.amount,
        minQuantity = conditions.minQuantity?.value,
        targetUserLevel = conditions.targetUserLevel?.name,
        isNewCustomerOnly = conditions.isNewCustomerOnly,
        discountPercentage = benefits.discountPercentage?.value,
        discountAmount = benefits.discountAmount?.amount,
        maxDiscountAmount = benefits.maxDiscountAmount?.amount,
        createdAt = createdAt,
        updatedAt = LocalDateTime.now()
    )
}
