package com.example.demo.application.cart.usecase

import com.example.demo.application.common.dto.request.CalculateCartRequest
import com.example.demo.application.common.dto.request.CartItemRequest
import com.example.demo.application.common.dto.response.CalculateCartResponse
import com.example.demo.application.common.dto.response.CartItemResponse
import com.example.demo.application.common.dto.response.AppliedPromotionResponse
import com.example.demo.application.common.port.CartRepository
import com.example.demo.application.common.port.ProductRepository
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
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import com.example.demo.domain.promotion.service.PromotionResult
import com.example.demo.domain.promotion.service.AppliedPromotion
import com.example.demo.domain.promotion.model.PromotionId
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
 * CalculateCartUseCase 테스트
 * 장바구니 계산 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class CalculateCartUseCaseTest : FunSpec({
    
    val cartRepository = mockk<CartRepository>()
    val productRepository = mockk<ProductRepository>()
    val promotionEngine = mockk<PromotionEngine>()
    val calculateCartUseCase = CalculateCartUseCase(cartRepository, productRepository, promotionEngine)
    
    beforeEach {
        clearMocks(cartRepository, productRepository, promotionEngine)
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
    
    val expensiveProduct = Product(
        id = ProductId.of(2L),
        name = "고가 상품",
        description = "비싼 상품입니다",
        price = Price.of(Money(BigDecimal("50000"))),
        stock = Stock(10),
        category = "명품",
        brand = "럭셔리 브랜드",
        imageUrl = "https://example.com/expensive.jpg",
        isActive = true,
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
    
    context("장바구니 계산 성공") {
        test("단일 아이템으로 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2)
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(1L)) } returns sampleUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items.size shouldBe 1
            result.items[0].productId shouldBe 1L
            result.items[0].productName shouldBe "테스트 상품"
            result.items[0].unitPrice shouldBe BigDecimal("10000")
            result.items[0].quantity shouldBe 2
            result.items[0].totalPrice shouldBe BigDecimal("20000")
            
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("20000")
            result.appliedPromotions shouldBe emptyList()
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { cartRepository.findUserById(UserId.of(1L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
        
        test("여러 아이템으로 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2),
                    CartItemRequest(productId = 2L, quantity = 1)
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("70000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("70000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns expensiveProduct
            every { cartRepository.findUserById(UserId.of(1L)) } returns sampleUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items.size shouldBe 2
            result.items[0].productId shouldBe 1L
            result.items[0].totalPrice shouldBe BigDecimal("20000")
            result.items[1].productId shouldBe 2L
            result.items[1].totalPrice shouldBe BigDecimal("50000")
            
            result.subtotal shouldBe BigDecimal("70000")
            result.finalAmount shouldBe BigDecimal("70000")
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
            verify { cartRepository.findUserById(UserId.of(1L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
        
        test("프로모션이 적용된 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 2L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 1)
                )
            )
            
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "VIP 할인",
                discountAmount = BigDecimal("1000")
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("10000"),
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("9000"),
                appliedPromotions = listOf(appliedPromotion)
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(2L)) } returns vipUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items.size shouldBe 1
            result.subtotal shouldBe BigDecimal("10000")
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("9000")
            
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionId shouldBe 1L
            result.appliedPromotions[0].promotionName shouldBe "VIP 할인"
            result.appliedPromotions[0].discountAmount shouldBe BigDecimal("1000")
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { cartRepository.findUserById(UserId.of(2L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
        
        test("여러 프로모션이 적용된 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 2L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2),
                    CartItemRequest(productId = 2L, quantity = 1)
                )
            )
            
            val appliedPromotions = listOf(
                AppliedPromotion(
                    promotionId = 1L,
                    promotionName = "VIP 할인",
                    discountAmount = BigDecimal("2000")
                ),
                AppliedPromotion(
                    promotionId = 2L,
                    promotionName = "대량 구매 할인",
                    discountAmount = BigDecimal("5000")
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("70000"),
                totalDiscount = BigDecimal("7000"),
                finalAmount = BigDecimal("63000"),
                appliedPromotions = appliedPromotions
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns expensiveProduct
            every { cartRepository.findUserById(UserId.of(2L)) } returns vipUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.subtotal shouldBe BigDecimal("70000")
            result.totalDiscount shouldBe BigDecimal("7000")
            result.finalAmount shouldBe BigDecimal("63000")
            
            result.appliedPromotions.size shouldBe 2
            result.appliedPromotions[0].promotionName shouldBe "VIP 할인"
            result.appliedPromotions[1].promotionName shouldBe "대량 구매 할인"
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
            verify { cartRepository.findUserById(UserId.of(2L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
    }
    
    context("장바구니 계산 실패") {
        test("존재하지 않는 상품이 포함된 장바구니 계산 시 실패해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 999L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "상품을 찾을 수 없습니다: 999"
            
            verify { productRepository.findById(ProductId.of(999L)) }
        }
        
        test("존재하지 않는 사용자로 장바구니 계산 시 실패해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 999L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(999L)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "사용자를 찾을 수 없습니다: 999"
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { cartRepository.findUserById(UserId.of(999L)) }
        }
        
        test("음수 수량이 포함된 장바구니 계산 시 실패해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = -1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "Quantity must be positive: -1"
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("0 수량이 포함된 장바구니 계산 시 실패해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 0)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "Quantity must be positive: 0"
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("비활성 상품이 포함된 장바구니 계산 시 실패해야 한다") {
            // Given
            val inactiveProduct = Product(
                id = ProductId.of(3L),
                name = "비활성 상품",
                description = "비활성화된 상품입니다",
                price = Price.of(Money(BigDecimal("3000"))),
                stock = Stock(50),
                category = "기타",
                brand = "기타 브랜드",
                imageUrl = null,
                isActive = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 3L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(3L)) } returns inactiveProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "상품 재고가 부족합니다: 비활성 상품 (요청: 1, 재고: 50)"
            
            verify { productRepository.findById(ProductId.of(3L)) }
        }
        
        test("재고 부족 상품이 포함된 장바구니 계산 시 실패해야 한다") {
            // Given
            val lowStockProduct = Product(
                id = ProductId.of(4L),
                name = "재고 부족 상품",
                description = "재고가 적은 상품입니다",
                price = Price.of(Money(BigDecimal("5000"))),
                stock = Stock(5),
                category = "기타",
                brand = "기타 브랜드",
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 4L, quantity = 10) // 재고 5개인데 10개 주문
                )
            )
            
            every { productRepository.findById(ProductId.of(4L)) } returns lowStockProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "상품 재고가 부족합니다: 재고 부족 상품 (요청: 10, 재고: 5)"
            
            verify { productRepository.findById(ProductId.of(4L)) }
        }
    }
    
    context("경계값 테스트") {
        test("최소 수량으로 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 1)
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("10000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("10000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(1L)) } returns sampleUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items[0].quantity shouldBe 1
            result.finalAmount shouldBe BigDecimal("10000")
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { cartRepository.findUserById(UserId.of(1L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
        
        test("최대 수량으로 장바구니를 계산할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 100) // 재고와 동일
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("1000000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("1000000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(1L)) } returns sampleUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items[0].quantity shouldBe 100
            result.finalAmount shouldBe BigDecimal("1000000")
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { cartRepository.findUserById(UserId.of(1L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
        
        test("0 ID로 상품 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 0L, quantity = 1)
                )
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
        }
        
        test("음수 ID로 상품 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = -1L, quantity = 1)
                )
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                calculateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
        }
    }
    
    context("대용량 장바구니 테스트") {
        test("여러 아이템이 포함된 장바구니를 계산할 수 있어야 한다") {
            // Given
            val items = (1..5).map { i ->
                CartItemRequest(productId = i.toLong(), quantity = 1)
            }
            val request = CalculateCartRequest(
                userId = 1L,
                items = items
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("50000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("50000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(3L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(4L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(5L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(1L)) } returns sampleUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            val result = calculateCartUseCase.execute(request)
            
            // Then
            result.items.size shouldBe 5
            result.finalAmount shouldBe BigDecimal("50000")
            
            // 5번의 상품 조회가 발생했는지 확인
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
            verify { productRepository.findById(ProductId.of(3L)) }
            verify { productRepository.findById(ProductId.of(4L)) }
            verify { productRepository.findById(ProductId.of(5L)) }
            verify { cartRepository.findUserById(UserId.of(1L)) }
            verify { promotionEngine.applyPromotions(any(), any()) }
        }
    }
    
    context("프로모션 엔진 연동 테스트") {
        test("프로모션 엔진에 올바른 파라미터가 전달되어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 2L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2)
                )
            )
            
            val promotionResult = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("20000"),
                appliedPromotions = emptyList()
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { cartRepository.findUserById(UserId.of(2L)) } returns vipUser
            every { promotionEngine.applyPromotions(any(), any()) } returns promotionResult
            
            // When
            calculateCartUseCase.execute(request)
            
            // Then
            verify { promotionEngine.applyPromotions(any(), vipUser) }
        }
    }
})
