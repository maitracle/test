package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.request.ApplyPromotionRequest
import com.example.demo.application.common.dto.response.ApplyPromotionResponse
import com.example.demo.application.common.dto.response.AppliedPromotionResponse
import com.example.demo.application.promotion.service.PromotionEngine
import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.promotion.service.PromotionResult
import com.example.demo.domain.promotion.service.AppliedPromotion
import com.example.demo.domain.promotion.model.PromotionId
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
 * ApplyPromotionUseCase 테스트
 * 프로모션 적용 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class ApplyPromotionUseCaseTest : FunSpec({
    
    val promotionEngine = mockk<PromotionEngine>()
    val applyPromotionUseCase = ApplyPromotionUseCase(promotionEngine)
    
    beforeEach {
        clearMocks(promotionEngine)
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
    
    context("프로모션 적용 성공") {
        test("단일 프로모션이 적용된 장바구니를 처리할 수 있어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = sampleUser)
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "테스트 프로모션",
                discountAmount = BigDecimal("1000")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("19000"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { promotionEngine.applyPromotions(sampleCart, sampleUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionId shouldBe 1L
            result.appliedPromotions[0].promotionName shouldBe "테스트 프로모션"
            result.appliedPromotions[0].discountAmount shouldBe BigDecimal("1000")
            
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("19000")
            
            verify { promotionEngine.applyPromotions(sampleCart, sampleUser) }
        }
        
        test("여러 프로모션이 적용된 장바구니를 처리할 수 있어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = vipUser)
            
            val appliedPromotions = listOf(
                AppliedPromotion(
                    promotionId = 1L,
                    promotionName = "VIP 할인",
                    discountAmount = BigDecimal("2000")
                ),
                AppliedPromotion(
                    promotionId = 2L,
                    promotionName = "대량 구매 할인",
                    discountAmount = BigDecimal("1000")
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("3000"),
                finalAmount = BigDecimal("17000"),
                appliedPromotions = appliedPromotions
            )
            
            every { promotionEngine.applyPromotions(sampleCart, vipUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.appliedPromotions.size shouldBe 2
            result.appliedPromotions[0].promotionName shouldBe "VIP 할인"
            result.appliedPromotions[1].promotionName shouldBe "대량 구매 할인"
            
            result.totalDiscount shouldBe BigDecimal("3000")
            result.finalAmount shouldBe BigDecimal("17000")
            
            verify { promotionEngine.applyPromotions(sampleCart, vipUser) }
        }
        
        test("프로모션이 적용되지 않은 장바구니를 처리할 수 있어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = sampleUser)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionEngine.applyPromotions(sampleCart, sampleUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.appliedPromotions shouldBe emptyList()
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("20000")
            
            verify { promotionEngine.applyPromotions(sampleCart, sampleUser) }
        }
    }
    
    context("특정 프로모션 적용") {
        test("특정 프로모션을 적용할 수 있어야 한다") {
            // Given
            val promotionId = 1L
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "특별 할인",
                discountAmount = BigDecimal("1500")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1500"),
                finalAmount = BigDecimal("18500"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            
            // Then
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionId shouldBe 1L
            result.appliedPromotions[0].promotionName shouldBe "특별 할인"
            result.appliedPromotions[0].discountAmount shouldBe BigDecimal("1500")
            
            result.totalDiscount shouldBe BigDecimal("1500")
            result.finalAmount shouldBe BigDecimal("18500")
            
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
        
        test("존재하지 않는 프로모션 적용 시 예외가 발생해야 한다") {
            // Given
            val promotionId = 999L
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } throws 
                IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "프로모션을 찾을 수 없습니다: 999"
            
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
        
        test("적용 조건을 만족하지 않는 프로모션 적용 시 예외가 발생해야 한다") {
            // Given
            val promotionId = 1L
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } throws 
                IllegalArgumentException("프로모션 적용 조건을 만족하지 않습니다: 테스트 프로모션")
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "프로모션 적용 조건을 만족하지 않습니다: 테스트 프로모션"
            
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
    }
    
    context("경계값 테스트") {
        test("최소 할인 금액으로 프로모션을 적용할 수 있어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = sampleUser)
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "최소 할인",
                discountAmount = BigDecimal("1")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("1"),
                finalAmount = BigDecimal("19999"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { promotionEngine.applyPromotions(sampleCart, sampleUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.totalDiscount shouldBe BigDecimal("1")
            result.finalAmount shouldBe BigDecimal("19999")
            
            verify { promotionEngine.applyPromotions(sampleCart, sampleUser) }
        }
        
        test("최대 할인 금액으로 프로모션을 적용할 수 있어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = sampleUser)
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "최대 할인",
                discountAmount = BigDecimal("20000")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("20000"),
                finalAmount = BigDecimal("0"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { promotionEngine.applyPromotions(sampleCart, sampleUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.totalDiscount shouldBe BigDecimal("20000")
            result.finalAmount shouldBe BigDecimal("0")
            
            verify { promotionEngine.applyPromotions(sampleCart, sampleUser) }
        }
        
        test("0 ID로 프로모션 적용 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = 0L
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } throws 
                IllegalArgumentException("EntityId must be positive: 0")
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
            
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
        
        test("음수 ID로 프로모션 적용 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val promotionId = -1L
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } throws 
                IllegalArgumentException("EntityId must be positive: -1")
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
            
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
    }
    
    context("프로모션 엔진 연동 테스트") {
        test("프로모션 엔진에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = vipUser)
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionEngine.applyPromotions(sampleCart, vipUser) } returns promotionResult
            
            // When
            applyPromotionUseCase.execute(request)
            
            // Then
            verify { promotionEngine.applyPromotions(sampleCart, vipUser) }
        }
        
        test("특정 프로모션 적용 시 프로모션 엔진에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val promotionId = 1L
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) } returns promotionResult
            
            // When
            applyPromotionUseCase.executeSpecificPromotion(sampleCart, sampleUser, promotionId)
            
            // Then
            verify { promotionEngine.applySpecificPromotion(sampleCart, sampleUser, promotionId) }
        }
    }
    
    context("응답 변환 테스트") {
        test("도메인 객체를 응답 DTO로 올바르게 변환해야 한다") {
            // Given
            val request = ApplyPromotionRequest(cart = sampleCart, user = sampleUser)
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "변환 테스트",
                discountAmount = BigDecimal("500")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("500"),
                finalAmount = BigDecimal("19500"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { promotionEngine.applyPromotions(sampleCart, sampleUser) } returns promotionResult
            
            // When
            val result = applyPromotionUseCase.execute(request)
            
            // Then
            result.appliedPromotions[0].promotionId shouldBe appliedPromotion.promotionId
            result.appliedPromotions[0].promotionName shouldBe appliedPromotion.promotionName
            result.appliedPromotions[0].discountAmount shouldBe appliedPromotion.discountAmount
            
            result.totalDiscount shouldBe promotionResult.totalDiscount
            result.finalAmount shouldBe promotionResult.finalAmount
            
            verify { promotionEngine.applyPromotions(sampleCart, sampleUser) }
        }
    }
})
