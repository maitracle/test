package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.response.PromotionResponse
import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import com.example.demo.domain.promotion.model.PromotionType
import com.example.demo.domain.promotion.valueobject.DiscountPercentage
import com.example.demo.domain.promotion.valueobject.PromotionBenefits
import com.example.demo.domain.promotion.valueobject.PromotionConditions
import com.example.demo.domain.promotion.valueobject.PromotionPeriod
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * ManagePromotionUseCase 테스트
 * 프로모션 관리 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class ManagePromotionUseCaseTest : FunSpec({
    
    val promotionRepository = mockk<PromotionRepository>()
    val managePromotionUseCase = ManagePromotionUseCase(promotionRepository)
    
    beforeEach {
        clearMocks(promotionRepository)
    }
    
    val samplePromotion = Promotion(
        id = PromotionId.of(1L),
        name = "테스트 프로모션",
        description = "테스트용 프로모션입니다",
        type = PromotionType.PERCENTAGE_DISCOUNT,
        priority = 1,
        isActive = true,
        period = PromotionPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
        conditions = PromotionConditions(
            targetCategory = "전자제품",
            minCartAmount = com.example.demo.domain.common.valueobject.Money(BigDecimal("10000")),
            minQuantity = com.example.demo.domain.common.valueobject.Quantity(1),
            targetUserLevel = MembershipLevel.REGULAR
        ),
        benefits = PromotionBenefits(
            discountPercentage = DiscountPercentage(BigDecimal("10")),
            discountAmount = null,
            maxDiscountAmount = com.example.demo.domain.common.valueobject.Money(BigDecimal("5000"))
        ),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val inactivePromotion = samplePromotion.copy(
        id = PromotionId.of(2L),
        name = "비활성 프로모션",
        isActive = false
    )
    
    val highPriorityPromotion = samplePromotion.copy(
        id = PromotionId.of(3L),
        name = "고우선순위 프로모션",
        priority = 10
    )
    
    context("프로모션 조회 성공") {
        test("모든 프로모션을 조회할 수 있어야 한다") {
            // Given
            val allPromotions = listOf(samplePromotion, inactivePromotion, highPriorityPromotion)
            every { promotionRepository.findAll() } returns allPromotions
            
            // When
            val result = managePromotionUseCase.getAllPromotions()
            
            // Then
            result.size shouldBe 3
            result[0].id shouldBe 1L
            result[0].name shouldBe "테스트 프로모션"
            result[0].isActive shouldBe true
            
            result[1].id shouldBe 2L
            result[1].name shouldBe "비활성 프로모션"
            result[1].isActive shouldBe false
            
            result[2].id shouldBe 3L
            result[2].name shouldBe "고우선순위 프로모션"
            result[2].priority shouldBe 10
            
            verify { promotionRepository.findAll() }
        }
        
        test("활성 프로모션만 조회할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion, highPriorityPromotion)
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            
            // When
            val result = managePromotionUseCase.getActivePromotions()
            
            // Then
            result.size shouldBe 2
            result[0].id shouldBe 1L
            result[0].name shouldBe "테스트 프로모션"
            result[0].isActive shouldBe true
            
            result[1].id shouldBe 3L
            result[1].name shouldBe "고우선순위 프로모션"
            result[1].isActive shouldBe true
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
        }
        
        test("프로모션 ID로 프로모션을 조회할 수 있어야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            
            // When
            val result = managePromotionUseCase.getPromotionById(1L)
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.description shouldBe "테스트용 프로모션입니다"
            result.type shouldBe "PERCENTAGE_DISCOUNT"
            result.priority shouldBe 1
            result.isActive shouldBe true
            result.targetCategory shouldBe "전자제품"
            result.minCartAmount shouldBe BigDecimal("10000")
            result.minQuantity shouldBe 1
            result.targetUserLevel shouldBe "REGULAR"
            result.discountPercentage shouldBe BigDecimal("10")
            result.maxDiscountAmount shouldBe BigDecimal("5000")
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
        }
        
        test("빈 프로모션 목록을 조회할 수 있어야 한다") {
            // Given
            every { promotionRepository.findAll() } returns emptyList()
            
            // When
            val result = managePromotionUseCase.getAllPromotions()
            
            // Then
            result shouldBe emptyList()
            
            verify { promotionRepository.findAll() }
        }
    }
    
    context("프로모션 조회 실패") {
        test("존재하지 않는 프로모션 ID로 조회 시 실패해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.getPromotionById(999L)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(999L)) }
        }
        
        test("0 ID로 프로모션 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = 0L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.getPromotionById(promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
        }
        
        test("음수 ID로 프로모션 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = -1L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.getPromotionById(promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
        }
    }
    
    context("프로모션 활성화/비활성화") {
        test("프로모션을 활성화할 수 있어야 한다") {
            // Given
            val activatedPromotion = inactivePromotion.copy(isActive = true)
            every { promotionRepository.findById(PromotionId.of(2L)) } returns inactivePromotion
            every { promotionRepository.save(any()) } returns activatedPromotion
            
            // When
            val result = managePromotionUseCase.activatePromotion(2L)
            
            // Then
            result.id shouldBe 2L
            result.name shouldBe "비활성 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.findById(PromotionId.of(2L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션을 비활성화할 수 있어야 한다") {
            // Given
            val deactivatedPromotion = samplePromotion.copy(isActive = false)
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns deactivatedPromotion
            
            // When
            val result = managePromotionUseCase.deactivatePromotion(1L)
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe false
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("존재하지 않는 프로모션 활성화 시 실패해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.activatePromotion(999L)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(999L)) }
        }
        
        test("존재하지 않는 프로모션 비활성화 시 실패해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.deactivatePromotion(999L)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(999L)) }
        }
    }
    
    context("프로모션 수정") {
        test("프로모션 이름을 수정할 수 있어야 한다") {
            // Given
            val updatedPromotion = samplePromotion.copy(
                name = "수정된 프로모션",
                updatedAt = LocalDateTime.now()
            )
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns updatedPromotion
            
            // When
            val result = managePromotionUseCase.updatePromotion(
                promotionId = 1L,
                name = "수정된 프로모션"
            )
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "수정된 프로모션"
            result.description shouldBe "테스트용 프로모션입니다"
            result.priority shouldBe 1
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션 설명을 수정할 수 있어야 한다") {
            // Given
            val updatedPromotion = samplePromotion.copy(
                description = "수정된 설명",
                updatedAt = LocalDateTime.now()
            )
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns updatedPromotion
            
            // When
            val result = managePromotionUseCase.updatePromotion(
                promotionId = 1L,
                description = "수정된 설명"
            )
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.description shouldBe "수정된 설명"
            result.priority shouldBe 1
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션 우선순위를 수정할 수 있어야 한다") {
            // Given
            val updatedPromotion = samplePromotion.copy(
                priority = 5,
                updatedAt = LocalDateTime.now()
            )
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns updatedPromotion
            
            // When
            val result = managePromotionUseCase.updatePromotion(
                promotionId = 1L,
                priority = 5
            )
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.description shouldBe "테스트용 프로모션입니다"
            result.priority shouldBe 5
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션의 여러 필드를 동시에 수정할 수 있어야 한다") {
            // Given
            val updatedPromotion = samplePromotion.copy(
                name = "완전 수정된 프로모션",
                description = "완전 수정된 설명",
                priority = 10,
                updatedAt = LocalDateTime.now()
            )
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns updatedPromotion
            
            // When
            val result = managePromotionUseCase.updatePromotion(
                promotionId = 1L,
                name = "완전 수정된 프로모션",
                description = "완전 수정된 설명",
                priority = 10
            )
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "완전 수정된 프로모션"
            result.description shouldBe "완전 수정된 설명"
            result.priority shouldBe 10
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("수정할 필드가 없을 때 기존 값을 유지해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = managePromotionUseCase.updatePromotion(promotionId = 1L)
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.description shouldBe "테스트용 프로모션입니다"
            result.priority shouldBe 1
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("존재하지 않는 프로모션 수정 시 실패해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.updatePromotion(
                    promotionId = 999L,
                    name = "수정된 이름"
                )
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(999L)) }
        }
    }
    
    context("프로모션 삭제") {
        test("프로모션을 삭제할 수 있어야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.delete(PromotionId.of(1L)) } returns Unit
            
            // When
            managePromotionUseCase.deletePromotion(1L)
            
            // Then
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.delete(PromotionId.of(1L)) }
        }
        
        test("존재하지 않는 프로모션 삭제 시 실패해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                managePromotionUseCase.deletePromotion(999L)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(999L)) }
        }
    }
    
    context("경계값 테스트") {
        test("최소 우선순위로 프로모션을 조회할 수 있어야 한다") {
            // Given
            val minPriorityPromotion = samplePromotion.copy(priority = 1)
            every { promotionRepository.findById(PromotionId.of(1L)) } returns minPriorityPromotion
            
            // When
            val result = managePromotionUseCase.getPromotionById(1L)
            
            // Then
            result.priority shouldBe 1
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
        }
        
        test("최대 우선순위로 프로모션을 조회할 수 있어야 한다") {
            // Given
            val maxPriorityPromotion = samplePromotion.copy(priority = Int.MAX_VALUE)
            every { promotionRepository.findById(PromotionId.of(1L)) } returns maxPriorityPromotion
            
            // When
            val result = managePromotionUseCase.getPromotionById(1L)
            
            // Then
            result.priority shouldBe Int.MAX_VALUE
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
        }
    }
    
    context("리포지토리 연동 테스트") {
        test("프로모션 조회 시 리포지토리에 올바른 파라미터가 전달되어야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            
            // When
            managePromotionUseCase.getPromotionById(1L)
            
            // Then
            verify { promotionRepository.findById(PromotionId.of(1L)) }
        }
        
        test("프로모션 저장 시 리포지토리에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val updatedPromotion = samplePromotion.copy(name = "수정된 이름")
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.save(any()) } returns updatedPromotion
            
            // When
            managePromotionUseCase.updatePromotion(promotionId = 1L, name = "수정된 이름")
            
            // Then
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션 삭제 시 리포지토리에 올바른 파라미터가 전달되어야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            every { promotionRepository.delete(PromotionId.of(1L)) } returns Unit
            
            // When
            managePromotionUseCase.deletePromotion(1L)
            
            // Then
            verify { promotionRepository.findById(PromotionId.of(1L)) }
            verify { promotionRepository.delete(PromotionId.of(1L)) }
        }
    }
    
    context("응답 변환 테스트") {
        test("도메인 모델을 응답 DTO로 올바르게 변환해야 한다") {
            // Given
            every { promotionRepository.findById(PromotionId.of(1L)) } returns samplePromotion
            
            // When
            val result = managePromotionUseCase.getPromotionById(1L)
            
            // Then
            result.id shouldBe samplePromotion.id.value
            result.name shouldBe samplePromotion.name
            result.description shouldBe samplePromotion.description
            result.type shouldBe samplePromotion.type.name
            result.priority shouldBe samplePromotion.priority
            result.isActive shouldBe samplePromotion.isActive
            result.startDate shouldBe samplePromotion.period.startDate
            result.endDate shouldBe samplePromotion.period.endDate
            result.targetCategory shouldBe samplePromotion.conditions.targetCategory
            result.minCartAmount shouldBe samplePromotion.conditions.minCartAmount?.amount
            result.minQuantity shouldBe samplePromotion.conditions.minQuantity?.value
            result.targetUserLevel shouldBe samplePromotion.conditions.targetUserLevel?.name
            result.discountPercentage shouldBe samplePromotion.benefits.discountPercentage?.value
            result.discountAmount shouldBe samplePromotion.benefits.discountAmount?.amount
            result.maxDiscountAmount shouldBe samplePromotion.benefits.maxDiscountAmount?.amount
            result.createdAt shouldBe samplePromotion.createdAt
            result.updatedAt shouldBe samplePromotion.updatedAt
            
            verify { promotionRepository.findById(PromotionId.of(1L)) }
        }
    }
})
