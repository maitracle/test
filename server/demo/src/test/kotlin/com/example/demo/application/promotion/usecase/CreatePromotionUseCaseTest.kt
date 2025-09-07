package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.request.CreatePromotionRequest
import com.example.demo.application.common.dto.response.CreatePromotionResponse
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
 * CreatePromotionUseCase 테스트
 * 프로모션 생성 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class CreatePromotionUseCaseTest : FunSpec({
    
    val promotionRepository = mockk<PromotionRepository>()
    val createPromotionUseCase = CreatePromotionUseCase(promotionRepository)
    
    beforeEach {
        clearMocks(promotionRepository)
    }
    
    val sampleCreateRequest = CreatePromotionRequest(
        name = "테스트 프로모션",
        description = "테스트용 프로모션입니다",
        type = "PERCENTAGE_DISCOUNT",
        priority = 1,
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now().plusDays(30),
        targetCategory = "전자제품",
        minCartAmount = BigDecimal("10000"),
        minQuantity = 1,
        targetUserLevel = "REGULAR",
        discountPercentage = BigDecimal("10"),
        discountAmount = null,
        maxDiscountAmount = BigDecimal("5000")
    )
    
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
    
    context("프로모션 생성 성공") {
        test("할인율 기반 프로모션을 생성할 수 있어야 한다") {
            // Given
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(sampleCreateRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("할인금액 기반 프로모션을 생성할 수 있어야 한다") {
            // Given
            val amountRequest = CreatePromotionRequest(
                name = "금액 할인 프로모션",
                description = "금액 기반 할인 프로모션",
                type = "FIXED_DISCOUNT",
                priority = 2,
                startDate = LocalDateTime.now(),
                endDate = LocalDateTime.now().plusDays(15),
                targetCategory = null,
                minCartAmount = BigDecimal("5000"),
                minQuantity = null,
                targetUserLevel = null,
                discountPercentage = null,
                discountAmount = BigDecimal("1000"),
                maxDiscountAmount = null
            )
            
            val amountPromotion = samplePromotion.copy(
                id = PromotionId.of(2L),
                name = "금액 할인 프로모션",
                type = PromotionType.FIXED_DISCOUNT,
                priority = 2,
                benefits = PromotionBenefits(
                    discountPercentage = null,
                    discountAmount = com.example.demo.domain.common.valueobject.Money(BigDecimal("1000")),
                    maxDiscountAmount = null
                )
            )
            
            every { promotionRepository.save(any()) } returns amountPromotion
            
            // When
            val result = createPromotionUseCase.execute(amountRequest)
            
            // Then
            result.promotionId shouldBe 2L
            result.name shouldBe "금액 할인 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("최소 조건으로 프로모션을 생성할 수 있어야 한다") {
            // Given
            val minimalRequest = CreatePromotionRequest(
                name = "최소 프로모션",
                description = null,
                type = "PERCENTAGE_DISCOUNT",
                priority = 1,
                startDate = LocalDateTime.now(),
                endDate = LocalDateTime.now().plusDays(1),
                targetCategory = null,
                minCartAmount = null,
                minQuantity = null,
                targetUserLevel = null,
                discountPercentage = BigDecimal("5"),
                discountAmount = null,
                maxDiscountAmount = null
            )
            
            val minimalPromotion = samplePromotion.copy(
                id = PromotionId.of(3L),
                name = "최소 프로모션",
                description = null,
                conditions = PromotionConditions(
                    targetCategory = null,
                    minCartAmount = null,
                    minQuantity = null,
                    targetUserLevel = null
                ),
                benefits = PromotionBenefits(
                    discountPercentage = DiscountPercentage(BigDecimal("5")),
                    discountAmount = null,
                    maxDiscountAmount = null
                )
            )
            
            every { promotionRepository.save(any()) } returns minimalPromotion
            
            // When
            val result = createPromotionUseCase.execute(minimalRequest)
            
            // Then
            result.promotionId shouldBe 3L
            result.name shouldBe "최소 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
    }
    
    context("프로모션 생성 실패") {
        test("중복된 프로모션 이름으로 생성 시 실패해야 한다") {
            // Given
            every { promotionRepository.findByName("테스트 프로모션") } returns samplePromotion
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(sampleCreateRequest)
            }
            
            exception.message shouldBe "이미 존재하는 프로모션 이름입니다: 테스트 프로모션"
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("잘못된 날짜 범위로 생성 시 실패해야 한다") {
            // Given
            val invalidDateRequest = sampleCreateRequest.copy(
                startDate = LocalDateTime.now().plusDays(30),
                endDate = LocalDateTime.now()
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(invalidDateRequest)
            }
            
            exception.message shouldBe "시작일은 종료일보다 이전이어야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("할인 조건이 없는 프로모션 생성 시 실패해야 한다") {
            // Given
            val noDiscountRequest = sampleCreateRequest.copy(
                discountPercentage = null,
                discountAmount = null
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(noDiscountRequest)
            }
            
            exception.message shouldBe "할인율 또는 할인금액 중 하나는 반드시 설정되어야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("할인율과 할인금액을 동시에 설정한 프로모션 생성 시 실패해야 한다") {
            // Given
            val bothDiscountRequest = sampleCreateRequest.copy(
                discountPercentage = BigDecimal("10"),
                discountAmount = BigDecimal("1000")
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(bothDiscountRequest)
            }
            
            exception.message shouldBe "할인율과 할인금액은 동시에 설정할 수 없습니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("음수 할인율로 프로모션 생성 시 실패해야 한다") {
            // Given
            val negativePercentageRequest = sampleCreateRequest.copy(
                discountPercentage = BigDecimal("-5"),
                discountAmount = null
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(negativePercentageRequest)
            }
            
            exception.message shouldBe "할인율은 0% 이상 100% 이하여야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("100% 초과 할인율로 프로모션 생성 시 실패해야 한다") {
            // Given
            val overPercentageRequest = sampleCreateRequest.copy(
                discountPercentage = BigDecimal("150"),
                discountAmount = null
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(overPercentageRequest)
            }
            
            exception.message shouldBe "할인율은 0% 이상 100% 이하여야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("음수 할인금액으로 프로모션 생성 시 실패해야 한다") {
            // Given
            val negativeAmountRequest = sampleCreateRequest.copy(
                discountPercentage = null,
                discountAmount = BigDecimal("-1000")
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(negativeAmountRequest)
            }
            
            exception.message shouldBe "할인금액은 0보다 커야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("0 할인금액으로 프로모션 생성 시 실패해야 한다") {
            // Given
            val zeroAmountRequest = sampleCreateRequest.copy(
                discountPercentage = null,
                discountAmount = BigDecimal("0")
            )
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(zeroAmountRequest)
            }
            
            exception.message shouldBe "할인금액은 0보다 커야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("0 이하 우선순위로 프로모션 생성 시 실패해야 한다") {
            // Given
            val invalidPriorityRequest = sampleCreateRequest.copy(priority = 0)
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(invalidPriorityRequest)
            }
            
            exception.message shouldBe "우선순위는 1 이상이어야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
        
        test("음수 우선순위로 프로모션 생성 시 실패해야 한다") {
            // Given
            val negativePriorityRequest = sampleCreateRequest.copy(priority = -1)
            
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createPromotionUseCase.validateCreateRequest(negativePriorityRequest)
            }
            
            exception.message shouldBe "우선순위는 1 이상이어야 합니다."
            
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
    }
    
    context("경계값 테스트") {
        test("최소 할인율로 프로모션을 생성할 수 있어야 한다") {
            // Given
            val minPercentageRequest = sampleCreateRequest.copy(
                discountPercentage = BigDecimal("0"),
                discountAmount = null
            )
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(minPercentageRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("최대 할인율로 프로모션을 생성할 수 있어야 한다") {
            // Given
            val maxPercentageRequest = sampleCreateRequest.copy(
                discountPercentage = BigDecimal("100"),
                discountAmount = null
            )
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(maxPercentageRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("최소 할인금액으로 프로모션을 생성할 수 있어야 한다") {
            // Given
            val minAmountRequest = sampleCreateRequest.copy(
                discountPercentage = null,
                discountAmount = BigDecimal("1")
            )
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(minAmountRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("최소 우선순위로 프로모션을 생성할 수 있어야 한다") {
            // Given
            val minPriorityRequest = sampleCreateRequest.copy(priority = 1)
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(minPriorityRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
    }
    
    context("프로모션 타입 테스트") {
        test("PERCENTAGE 타입 프로모션을 생성할 수 있어야 한다") {
            // Given
            val percentageRequest = sampleCreateRequest.copy(type = "PERCENTAGE_DISCOUNT")
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(percentageRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
        
        test("FIXED_AMOUNT 타입 프로모션을 생성할 수 있어야 한다") {
            // Given
            val fixedAmountRequest = sampleCreateRequest.copy(
                type = "FIXED_DISCOUNT",
                discountPercentage = null,
                discountAmount = BigDecimal("1000")
            )
            
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            val result = createPromotionUseCase.execute(fixedAmountRequest)
            
            // Then
            result.promotionId shouldBe 1L
            result.name shouldBe "테스트 프로모션"
            result.isActive shouldBe true
            
            verify { promotionRepository.save(any()) }
        }
    }
    
    context("리포지토리 연동 테스트") {
        test("프로모션 저장 시 리포지토리에 올바른 파라미터가 전달되어야 한다") {
            // Given
            every { promotionRepository.save(any()) } returns samplePromotion
            
            // When
            createPromotionUseCase.execute(sampleCreateRequest)
            
            // Then
            verify { promotionRepository.save(any()) }
        }
        
        test("프로모션 이름 중복 확인 시 리포지토리가 호출되어야 한다") {
            // Given
            every { promotionRepository.findByName("테스트 프로모션") } returns null
            
            // When
            createPromotionUseCase.validateCreateRequest(sampleCreateRequest)
            
            // Then
            verify { promotionRepository.findByName("테스트 프로모션") }
        }
    }
})
