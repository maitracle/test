package com.example.demo.application.promotion.service

import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import com.example.demo.domain.promotion.model.PromotionType
import com.example.demo.domain.promotion.service.PromotionCalculator
import com.example.demo.domain.promotion.service.PromotionRuleEngine
import com.example.demo.domain.promotion.service.PromotionResult
import com.example.demo.domain.promotion.service.RuleEvaluationResult
import com.example.demo.domain.promotion.valueobject.DiscountPercentage
import com.example.demo.domain.promotion.valueobject.PromotionBenefits
import com.example.demo.domain.promotion.valueobject.PromotionConditions
import com.example.demo.domain.promotion.valueobject.PromotionPeriod
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
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
 * PromotionEngine 테스트
 * 프로모션 엔진 서비스의 비즈니스 로직을 테스트합니다.
 */
class PromotionEngineTest : FunSpec({
    
    val promotionRepository = mockk<PromotionRepository>()
    val promotionCalculator = mockk<PromotionCalculator>()
    val promotionRuleEngine = mockk<PromotionRuleEngine>()
    val promotionEngine = PromotionEngine(promotionRepository, promotionCalculator, promotionRuleEngine)
    
    beforeEach {
        clearMocks(promotionRepository, promotionCalculator, promotionRuleEngine)
    }
    
    val sampleProduct = Product(
        id = ProductId.of(1L),
        name = "테스트 상품",
        description = "테스트용 상품입니다",
        price = Price.of(Money(BigDecimal("10000"))),
        stock = Stock(100),
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = "https://example.com/image.jpg",
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val sampleCartItem = CartItem(
        product = sampleProduct,
        quantity = Quantity(2),
        unitPrice = Money(BigDecimal("10000"))
    )
    
    val sampleCart = Cart(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = listOf(sampleCartItem),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val sampleUser = User(
        id = UserId.of(1L),
        email = Email("test@example.com"),
        membershipLevel = MembershipLevel.REGULAR,
        isNewCustomer = false,
        createdAt = LocalDateTime.now()
    )
    
    val vipUser = User(
        id = UserId.of(2L),
        email = Email("vip@example.com"),
        membershipLevel = MembershipLevel.VIP,
        isNewCustomer = false,
        createdAt = LocalDateTime.now()
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
            minCartAmount = Money(BigDecimal("10000")),
            minQuantity = Quantity(1),
            targetUserLevel = MembershipLevel.REGULAR
        ),
        benefits = PromotionBenefits(
            discountPercentage = DiscountPercentage(BigDecimal("10")),
            discountAmount = null,
            maxDiscountAmount = Money(BigDecimal("5000"))
        ),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val vipPromotion = Promotion(
        id = PromotionId.of(2L),
        name = "VIP 프로모션",
        description = "VIP 전용 프로모션입니다",
        type = PromotionType.PERCENTAGE_DISCOUNT,
        priority = 2,
        isActive = true,
        period = PromotionPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
        conditions = PromotionConditions(
            targetCategory = null,
            minCartAmount = Money(BigDecimal("20000")),
            minQuantity = null,
            targetUserLevel = MembershipLevel.VIP
        ),
        benefits = PromotionBenefits(
            discountPercentage = DiscountPercentage(BigDecimal("15")),
            discountAmount = null,
            maxDiscountAmount = Money(BigDecimal("10000"))
        ),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    context("프로모션 적용 성공") {
        test("단일 프로모션이 적용된 장바구니를 처리할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("19000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("19000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
        
        test("여러 프로모션이 적용된 장바구니를 처리할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion, vipPromotion)
            val applicablePromotions = listOf(samplePromotion, vipPromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("3000"),
                finalAmount = BigDecimal("17000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, vipUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, vipUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, vipUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, vipUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("3000")
            result.finalAmount shouldBe BigDecimal("17000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, vipUser) }
            verify { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, vipUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, vipUser) }
        }
        
        test("적용 가능한 프로모션이 없는 장바구니를 처리할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = emptyList<Promotion>()
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = false)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("20000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
        
        test("활성 프로모션이 없는 경우를 처리할 수 있어야 한다") {
            // Given
            val activePromotions = emptyList<Promotion>()
            val applicablePromotions = emptyList<Promotion>()
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("20000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
    }
    
    context("특정 프로모션 적용") {
        test("특정 프로모션을 적용할 수 있어야 한다") {
            // Given
            val promotionId = 1L
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1500"),
                finalAmount = BigDecimal("18500"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findById(PromotionId.of(promotionId)) } returns samplePromotion
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, listOf(samplePromotion), sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("1500")
            result.finalAmount shouldBe BigDecimal("18500")
            
            verify { promotionRepository.findById(PromotionId.of(promotionId)) }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, listOf(samplePromotion), sampleUser) }
        }
        
        test("존재하지 않는 프로모션 적용 시 실패해야 한다") {
            // Given
            val promotionId = 999L
            
            every { promotionRepository.findById(PromotionId.of(promotionId)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionRepository.findById(PromotionId.of(promotionId)) }
        }
        
        test("적용 조건을 만족하지 않는 프로모션 적용 시 실패해야 한다") {
            // Given
            val promotionId = 1L
            
            every { promotionRepository.findById(PromotionId.of(promotionId)) } returns samplePromotion
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = false)
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "프로모션 적용 조건을 만족하지 않습니다: 테스트 프로모션"
            
            verify { promotionRepository.findById(PromotionId.of(promotionId)) }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
        }
        
        test("0 ID로 프로모션 적용 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = 0L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
        }
        
        test("음수 ID로 프로모션 적용 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = -1L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
        }
    }
    
    context("프로모션 필터링 테스트") {
        test("적용 가능한 프로모션만 필터링되어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion, vipPromotion)
            val applicablePromotions = listOf(samplePromotion) // vipPromotion은 적용 불가
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("19000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = false)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("19000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
        
        test("모든 프로모션이 적용 불가능한 경우를 처리할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion, vipPromotion)
            val applicablePromotions = emptyList<Promotion>()
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = false)
            every { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = false)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("20000")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionRuleEngine.evaluateRules(vipPromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
    }
    
    context("서비스 연동 테스트") {
        test("프로모션 리포지토리에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
        }
        
        test("프로모션 규칙 엔진에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
        }
        
        test("프로모션 계산기에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
    }
    
    context("경계값 테스트") {
        test("최소 할인 금액으로 프로모션을 적용할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1"),
                finalAmount = BigDecimal("19999"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.totalDiscount shouldBe BigDecimal("1")
            result.finalAmount shouldBe BigDecimal("19999")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
        
        test("최대 할인 금액으로 프로모션을 적용할 수 있어야 한다") {
            // Given
            val activePromotions = listOf(samplePromotion)
            val applicablePromotions = listOf(samplePromotion)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("20000"),
                finalAmount = BigDecimal("0"),
                appliedPromotions = emptyList()
            )
            
            every { promotionRepository.findActivePromotionsOrderByPriority() } returns activePromotions
            every { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) } returns 
                RuleEvaluationResult(isEligible = true)
            every { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) } returns promotionResult
            
            // When
            val result = promotionEngine.applyPromotions(sampleCart, sampleUser)
            
            // Then
            result.totalDiscount shouldBe BigDecimal("20000")
            result.finalAmount shouldBe BigDecimal("0")
            
            verify { promotionRepository.findActivePromotionsOrderByPriority() }
            verify { promotionRuleEngine.evaluateRules(samplePromotion, sampleCart, sampleUser) }
            verify { promotionCalculator.calculateDiscounts(sampleCart, applicablePromotions, sampleUser) }
        }
    }
})
