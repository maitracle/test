package com.example.demo.infrastructure.persistence.promotion

import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import com.example.demo.domain.promotion.model.PromotionType
import com.example.demo.domain.promotion.valueobject.DiscountPercentage
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

class PromotionRepositoryImplTest : FunSpec() {
    
    init {
        context("프로모션 Repository 테스트") {
            test("ID로 프로모션을 조회할 수 있어야 한다") {
                // Given
                val promotionJpaRepository = mockk<PromotionJpaRepository>()
                val promotionRepository = PromotionRepositoryImpl(promotionJpaRepository)
                
                val promotionEntity = com.example.demo.infrastructure.persistence.promotion.PromotionEntity.createExisting(
                    id = 1L,
                    name = "테스트 프로모션",
                    description = "테스트 설명",
                    type = "PERCENTAGE_DISCOUNT",
                    priority = 1,
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now().plusDays(30),
                    targetCategory = "전자제품",
                    minCartAmount = BigDecimal("50000"),
                    minQuantity = 2,
                    targetUserLevel = "VIP",
                    isNewCustomerOnly = false,
                    discountPercentage = BigDecimal("10"),
                    discountAmount = null,
                    maxDiscountAmount = BigDecimal("10000"),
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                val promotionId = PromotionId.of(1L)
                
                every { promotionJpaRepository.findById(1L) } returns java.util.Optional.of(promotionEntity)
                
                // When
                val foundPromotion = promotionRepository.findById(promotionId)
                
                // Then
                foundPromotion.shouldNotBeNull()
                foundPromotion!!.name shouldBe "테스트 프로모션"
                foundPromotion.description shouldBe "테스트 설명"
                foundPromotion.type shouldBe PromotionType.PERCENTAGE_DISCOUNT
                foundPromotion.priority shouldBe 1
                foundPromotion.isActive shouldBe true
                
                verify { promotionJpaRepository.findById(1L) }
            }
            
            test("존재하지 않는 ID로 조회하면 null을 반환해야 한다") {
                // Given
                val promotionJpaRepository = mockk<PromotionJpaRepository>()
                val promotionRepository = PromotionRepositoryImpl(promotionJpaRepository)
                
                val promotionId = PromotionId.of(999L)
                
                every { promotionJpaRepository.findById(999L) } returns java.util.Optional.empty()
                
                // When
                val foundPromotion = promotionRepository.findById(promotionId)
                
                // Then
                foundPromotion shouldBe null
                
                verify { promotionJpaRepository.findById(999L) }
            }
            
            test("새로운 프로모션을 저장할 수 있어야 한다") {
                // Given
                val promotionJpaRepository = mockk<PromotionJpaRepository>()
                val promotionRepository = PromotionRepositoryImpl(promotionJpaRepository)
                
                val promotion = Promotion.create(
                    name = "새 프로모션",
                    description = "새 설명",
                    type = PromotionType.PERCENTAGE_DISCOUNT,
                    priority = 1,
                    period = com.example.demo.domain.promotion.valueobject.PromotionPeriod(
                        startDate = LocalDateTime.now(),
                        endDate = LocalDateTime.now().plusDays(30)
                    ),
                    conditions = com.example.demo.domain.promotion.valueobject.PromotionConditions(
                        targetCategory = "의류",
                        minCartAmount = com.example.demo.domain.common.valueobject.Money.of(BigDecimal("30000")),
                        minQuantity = com.example.demo.domain.common.valueobject.Quantity.of(1),
                        targetUserLevel = MembershipLevel.REGULAR,
                        isNewCustomerOnly = true
                    ),
                    benefits = com.example.demo.domain.promotion.valueobject.PromotionBenefits(
                        discountPercentage = DiscountPercentage.of(BigDecimal("15")),
                        discountAmount = null,
                        maxDiscountAmount = com.example.demo.domain.common.valueobject.Money.of(BigDecimal("5000"))
                    )
                )
                
                val savedPromotionEntity = com.example.demo.infrastructure.persistence.promotion.PromotionEntity.createExisting(
                    id = 1L,
                    name = "새 프로모션",
                    description = "새 설명",
                    type = "PERCENTAGE_DISCOUNT",
                    priority = 1,
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now().plusDays(30),
                    targetCategory = "의류",
                    minCartAmount = BigDecimal("30000"),
                    minQuantity = 1,
                    targetUserLevel = "REGULAR",
                    isNewCustomerOnly = true,
                    discountPercentage = BigDecimal("15"),
                    discountAmount = null,
                    maxDiscountAmount = BigDecimal("5000"),
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { promotionJpaRepository.save(any()) } returns savedPromotionEntity
                
                // When
                val savedPromotion = promotionRepository.save(promotion)
                
                // Then
                savedPromotion.shouldNotBeNull()
                savedPromotion!!.name shouldBe "새 프로모션"
                savedPromotion.description shouldBe "새 설명"
                savedPromotion.type shouldBe PromotionType.PERCENTAGE_DISCOUNT
                savedPromotion.priority shouldBe 1
                savedPromotion.isActive shouldBe true
                
                verify { promotionJpaRepository.save(any()) }
            }
            
            test("프로모션을 삭제할 수 있어야 한다") {
                // Given
                val promotionJpaRepository = mockk<PromotionJpaRepository>()
                val promotionRepository = PromotionRepositoryImpl(promotionJpaRepository)
                
                val promotionId = PromotionId.of(1L)
                
                every { promotionJpaRepository.deleteById(1L) } returns Unit
                
                // When
                promotionRepository.delete(promotionId)
                
                // Then
                verify { promotionJpaRepository.deleteById(1L) }
            }
        }
    }
}