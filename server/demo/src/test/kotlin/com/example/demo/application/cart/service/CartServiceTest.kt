package com.example.demo.application.cart.service

import com.example.demo.application.common.dto.request.CalculateCartRequest
import com.example.demo.application.common.dto.request.ValidateCartRequest
import com.example.demo.application.common.dto.request.CartItemRequest
import com.example.demo.application.common.dto.response.CalculateCartResponse
import com.example.demo.application.common.dto.response.ValidateCartResponse
import com.example.demo.application.common.dto.response.CartItemResponse
import com.example.demo.application.common.dto.response.AppliedPromotionResponse
import com.example.demo.application.cart.usecase.CalculateCartUseCase
import com.example.demo.application.cart.usecase.ValidateCartUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal

/**
 * CartService 테스트
 * 장바구니 서비스의 비즈니스 로직을 테스트합니다.
 */
class CartServiceTest : FunSpec({
    
    val calculateCartUseCase = mockk<CalculateCartUseCase>()
    val validateCartUseCase = mockk<ValidateCartUseCase>()
    val cartService = CartService(calculateCartUseCase, validateCartUseCase)
    
    beforeEach {
        clearMocks(calculateCartUseCase, validateCartUseCase)
    }
    
    val sampleCartItemRequest = CartItemRequest(productId = 1L, quantity = 2)
    val sampleCalculateRequest = CalculateCartRequest(
        userId = 1L,
        items = listOf(sampleCartItemRequest)
    )
    
    val sampleValidateRequest = ValidateCartRequest(
        userId = 1L,
        items = listOf(sampleCartItemRequest)
    )
    
    val sampleCartItemResponse = CartItemResponse(
        productId = 1L,
        productName = "테스트 상품",
        unitPrice = BigDecimal("10000"),
        quantity = 2,
        totalPrice = BigDecimal("20000")
    )
    
    val sampleCalculateResponse = CalculateCartResponse(
        items = listOf(sampleCartItemResponse),
        subtotal = BigDecimal("20000"),
        totalDiscount = BigDecimal("1000"),
        finalAmount = BigDecimal("19000"),
        appliedPromotions = listOf(
            AppliedPromotionResponse(
                promotionId = 1L,
                promotionName = "테스트 프로모션",
                discountAmount = BigDecimal("1000")
            )
        )
    )
    
    val sampleValidateResponse = ValidateCartResponse(
        isValid = true,
        errors = emptyList(),
        warnings = emptyList()
    )
    
    context("장바구니 총액 계산") {
        test("장바구니 총액을 계산할 수 있어야 한다") {
            // Given
            every { calculateCartUseCase.execute(sampleCalculateRequest) } returns sampleCalculateResponse
            
            // When
            val result = cartService.calculateCartTotal(sampleCalculateRequest)
            
            // Then
            result.items.size shouldBe 1
            result.items[0].productId shouldBe 1L
            result.items[0].productName shouldBe "테스트 상품"
            result.items[0].unitPrice shouldBe BigDecimal("10000")
            result.items[0].quantity shouldBe 2
            result.items[0].totalPrice shouldBe BigDecimal("20000")
            
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("19000")
            
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionId shouldBe 1L
            result.appliedPromotions[0].promotionName shouldBe "테스트 프로모션"
            result.appliedPromotions[0].discountAmount shouldBe BigDecimal("1000")
            
            verify { calculateCartUseCase.execute(sampleCalculateRequest) }
        }
        
        test("여러 아이템이 포함된 장바구니 총액을 계산할 수 있어야 한다") {
            // Given
            val multipleItemsRequest = CalculateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2),
                    CartItemRequest(productId = 2L, quantity = 1)
                )
            )
            
            val multipleItemsResponse = CalculateCartResponse(
                items = listOf(
                    CartItemResponse(
                        productId = 1L,
                        productName = "상품1",
                        unitPrice = BigDecimal("10000"),
                        quantity = 2,
                        totalPrice = BigDecimal("20000")
                    ),
                    CartItemResponse(
                        productId = 2L,
                        productName = "상품2",
                        unitPrice = BigDecimal("15000"),
                        quantity = 1,
                        totalPrice = BigDecimal("15000")
                    )
                ),
                subtotal = BigDecimal("35000"),
                totalDiscount = BigDecimal("2000"),
                finalAmount = BigDecimal("33000"),
                appliedPromotions = emptyList()
            )
            
            every { calculateCartUseCase.execute(multipleItemsRequest) } returns multipleItemsResponse
            
            // When
            val result = cartService.calculateCartTotal(multipleItemsRequest)
            
            // Then
            result.items.size shouldBe 2
            result.subtotal shouldBe BigDecimal("35000")
            result.totalDiscount shouldBe BigDecimal("2000")
            result.finalAmount shouldBe BigDecimal("33000")
            
            verify { calculateCartUseCase.execute(multipleItemsRequest) }
        }
        
        test("프로모션이 적용된 장바구니 총액을 계산할 수 있어야 한다") {
            // Given
            val promotionResponse = CalculateCartResponse(
                items = listOf(sampleCartItemResponse),
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("5000"),
                finalAmount = BigDecimal("15000"),
                appliedPromotions = listOf(
                    AppliedPromotionResponse(
                        promotionId = 1L,
                        promotionName = "VIP 할인",
                        discountAmount = BigDecimal("3000")
                    ),
                    AppliedPromotionResponse(
                        promotionId = 2L,
                        promotionName = "대량 구매 할인",
                        discountAmount = BigDecimal("2000")
                    )
                )
            )
            
            every { calculateCartUseCase.execute(sampleCalculateRequest) } returns promotionResponse
            
            // When
            val result = cartService.calculateCartTotal(sampleCalculateRequest)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("5000")
            result.finalAmount shouldBe BigDecimal("15000")
            
            result.appliedPromotions.size shouldBe 2
            result.appliedPromotions[0].promotionName shouldBe "VIP 할인"
            result.appliedPromotions[1].promotionName shouldBe "대량 구매 할인"
            
            verify { calculateCartUseCase.execute(sampleCalculateRequest) }
        }
    }
    
    context("장바구니 검증") {
        test("장바구니를 검증할 수 있어야 한다") {
            // Given
            every { validateCartUseCase.execute(sampleValidateRequest) } returns sampleValidateResponse
            
            // When
            val result = cartService.validateCart(sampleValidateRequest)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            verify { validateCartUseCase.execute(sampleValidateRequest) }
        }
        
        test("유효하지 않은 장바구니를 검증할 수 있어야 한다") {
            // Given
            val invalidResponse = ValidateCartResponse(
                isValid = false,
                errors = listOf("상품을 찾을 수 없습니다: 999", "재고가 부족합니다: 테스트 상품"),
                warnings = listOf("재고가 부족할 수 있습니다: 경고 상품")
            )
            
            every { validateCartUseCase.execute(sampleValidateRequest) } returns invalidResponse
            
            // When
            val result = cartService.validateCart(sampleValidateRequest)
            
            // Then
            result.isValid shouldBe false
            result.errors.size shouldBe 2
            result.errors[0] shouldBe "상품을 찾을 수 없습니다: 999"
            result.errors[1] shouldBe "재고가 부족합니다: 테스트 상품"
            
            result.warnings.size shouldBe 1
            result.warnings[0] shouldBe "재고가 부족할 수 있습니다: 경고 상품"
            
            verify { validateCartUseCase.execute(sampleValidateRequest) }
        }
        
        test("경고가 있는 장바구니를 검증할 수 있어야 한다") {
            // Given
            val warningResponse = ValidateCartResponse(
                isValid = true,
                errors = emptyList(),
                warnings = listOf(
                    "재고가 부족할 수 있습니다: 상품1",
                    "재고가 부족할 수 있습니다: 상품2"
                )
            )
            
            every { validateCartUseCase.execute(sampleValidateRequest) } returns warningResponse
            
            // When
            val result = cartService.validateCart(sampleValidateRequest)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings.size shouldBe 2
            result.warnings[0] shouldBe "재고가 부족할 수 있습니다: 상품1"
            result.warnings[1] shouldBe "재고가 부족할 수 있습니다: 상품2"
            
            verify { validateCartUseCase.execute(sampleValidateRequest) }
        }
    }
    
    context("장바구니 검증 및 계산") {
        test("검증 통과 후 장바구니를 계산할 수 있어야 한다") {
            // Given
            every { validateCartUseCase.execute(any()) } returns sampleValidateResponse
            every { calculateCartUseCase.execute(sampleCalculateRequest) } returns sampleCalculateResponse
            
            // When
            val result = cartService.validateAndCalculateCart(sampleCalculateRequest)
            
            // Then
            result.items.size shouldBe 1
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("1000")
            result.finalAmount shouldBe BigDecimal("19000")
            
            verify { validateCartUseCase.execute(any()) }
            verify { calculateCartUseCase.execute(sampleCalculateRequest) }
        }
        
        test("검증 실패 시 장바구니 계산이 실패해야 한다") {
            // Given
            val invalidResponse = ValidateCartResponse(
                isValid = false,
                errors = listOf("상품을 찾을 수 없습니다: 999"),
                warnings = emptyList()
            )
            
            every { validateCartUseCase.execute(any()) } returns invalidResponse
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                cartService.validateAndCalculateCart(sampleCalculateRequest)
            }
            
            exception.message shouldBe "장바구니 검증 실패: 상품을 찾을 수 없습니다: 999"
            
            verify { validateCartUseCase.execute(any()) }
            verify(exactly = 0) { calculateCartUseCase.execute(any()) }
        }
        
        test("여러 검증 오류가 있을 때 모든 오류를 표시해야 한다") {
            // Given
            val multipleErrorsResponse = ValidateCartResponse(
                isValid = false,
                errors = listOf(
                    "상품을 찾을 수 없습니다: 999",
                    "재고가 부족합니다: 테스트 상품",
                    "비활성 상품입니다: 비활성 상품"
                ),
                warnings = emptyList()
            )
            
            every { validateCartUseCase.execute(any()) } returns multipleErrorsResponse
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                cartService.validateAndCalculateCart(sampleCalculateRequest)
            }
            
            exception.message shouldBe "장바구니 검증 실패: 상품을 찾을 수 없습니다: 999, 재고가 부족합니다: 테스트 상품, 비활성 상품입니다: 비활성 상품"
            
            verify { validateCartUseCase.execute(any()) }
            verify(exactly = 0) { calculateCartUseCase.execute(any()) }
        }
        
        test("검증 통과 후 프로모션이 적용된 장바구니를 계산할 수 있어야 한다") {
            // Given
            val promotionResponse = CalculateCartResponse(
                items = listOf(sampleCartItemResponse),
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("3000"),
                finalAmount = BigDecimal("17000"),
                appliedPromotions = listOf(
                    AppliedPromotionResponse(
                        promotionId = 1L,
                        promotionName = "특별 할인",
                        discountAmount = BigDecimal("3000")
                    )
                )
            )
            
            every { validateCartUseCase.execute(any()) } returns sampleValidateResponse
            every { calculateCartUseCase.execute(sampleCalculateRequest) } returns promotionResponse
            
            // When
            val result = cartService.validateAndCalculateCart(sampleCalculateRequest)
            
            // Then
            result.subtotal shouldBe BigDecimal("20000")
            result.totalDiscount shouldBe BigDecimal("3000")
            result.finalAmount shouldBe BigDecimal("17000")
            
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionName shouldBe "특별 할인"
            
            verify { validateCartUseCase.execute(any()) }
            verify { calculateCartUseCase.execute(sampleCalculateRequest) }
        }
    }
    
    context("서비스 조합 테스트") {
        test("장바구니 검증 후 총액 계산을 순차적으로 수행할 수 있어야 한다") {
            // Given
            val validateRequest = ValidateCartRequest(
                userId = 1L,
                items = listOf(CartItemRequest(productId = 1L, quantity = 1))
            )
            
            val calculateRequest = CalculateCartRequest(
                userId = 1L,
                items = listOf(CartItemRequest(productId = 1L, quantity = 1))
            )
            
            every { validateCartUseCase.execute(validateRequest) } returns sampleValidateResponse
            every { calculateCartUseCase.execute(calculateRequest) } returns sampleCalculateResponse
            
            // When
            val validateResult = cartService.validateCart(validateRequest)
            val calculateResult = cartService.calculateCartTotal(calculateRequest)
            
            // Then
            validateResult.isValid shouldBe true
            calculateResult.finalAmount shouldBe BigDecimal("19000")
            
            verify { validateCartUseCase.execute(validateRequest) }
            verify { calculateCartUseCase.execute(calculateRequest) }
        }
        
        test("장바구니 검증 및 계산을 통합적으로 수행할 수 있어야 한다") {
            // Given
            val request = CalculateCartRequest(
                userId = 1L,
                items = listOf(CartItemRequest(productId = 1L, quantity = 1))
            )
            
            every { validateCartUseCase.execute(any()) } returns sampleValidateResponse
            every { calculateCartUseCase.execute(request) } returns sampleCalculateResponse
            
            // When
            val result = cartService.validateAndCalculateCart(request)
            
            // Then
            result.finalAmount shouldBe BigDecimal("19000")
            
            verify { validateCartUseCase.execute(any()) }
            verify { calculateCartUseCase.execute(request) }
        }
    }
    
    context("경계값 테스트") {
        test("빈 장바구니를 처리할 수 있어야 한다") {
            // Given
            val emptyRequest = CalculateCartRequest(
                userId = 1L,
                items = emptyList()
            )
            
            val emptyResponse = CalculateCartResponse(
                items = emptyList(),
                subtotal = BigDecimal("0"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("0"),
                appliedPromotions = emptyList()
            )
            
            every { calculateCartUseCase.execute(emptyRequest) } returns emptyResponse
            
            // When
            val result = cartService.calculateCartTotal(emptyRequest)
            
            // Then
            result.items shouldBe emptyList()
            result.subtotal shouldBe BigDecimal("0")
            result.totalDiscount shouldBe BigDecimal("0")
            result.finalAmount shouldBe BigDecimal("0")
            result.appliedPromotions shouldBe emptyList()
            
            verify { calculateCartUseCase.execute(emptyRequest) }
        }
        
        test("빈 장바구니를 검증할 수 있어야 한다") {
            // Given
            val emptyValidateRequest = ValidateCartRequest(
                userId = 1L,
                items = emptyList()
            )
            
            val emptyValidateResponse = ValidateCartResponse(
                isValid = true,
                errors = emptyList(),
                warnings = emptyList()
            )
            
            every { validateCartUseCase.execute(emptyValidateRequest) } returns emptyValidateResponse
            
            // When
            val result = cartService.validateCart(emptyValidateRequest)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            verify { validateCartUseCase.execute(emptyValidateRequest) }
        }
        
        test("빈 장바구니를 검증하고 계산할 수 있어야 한다") {
            // Given
            val emptyRequest = CalculateCartRequest(
                userId = 1L,
                items = emptyList()
            )
            
            val emptyValidateResponse = ValidateCartResponse(
                isValid = true,
                errors = emptyList(),
                warnings = emptyList()
            )
            
            val emptyCalculateResponse = CalculateCartResponse(
                items = emptyList(),
                subtotal = BigDecimal("0"),
                totalDiscount = BigDecimal("0"),
                finalAmount = BigDecimal("0"),
                appliedPromotions = emptyList()
            )
            
            every { validateCartUseCase.execute(any()) } returns emptyValidateResponse
            every { calculateCartUseCase.execute(emptyRequest) } returns emptyCalculateResponse
            
            // When
            val result = cartService.validateAndCalculateCart(emptyRequest)
            
            // Then
            result.items shouldBe emptyList()
            result.finalAmount shouldBe BigDecimal("0")
            
            verify { validateCartUseCase.execute(any()) }
            verify { calculateCartUseCase.execute(emptyRequest) }
        }
    }
    
    context("대용량 장바구니 테스트") {
        test("많은 아이템이 포함된 장바구니를 처리할 수 있어야 한다") {
            // Given
            val items = (1..100).map { i ->
                CartItemRequest(productId = i.toLong(), quantity = 1)
            }
            val largeRequest = CalculateCartRequest(
                userId = 1L,
                items = items
            )
            
            val largeResponse = CalculateCartResponse(
                items = items.map { item ->
                    CartItemResponse(
                        productId = item.productId,
                        productName = "상품 ${item.productId}",
                        unitPrice = BigDecimal("1000"),
                        quantity = item.quantity,
                        totalPrice = BigDecimal("1000")
                    )
                },
                subtotal = BigDecimal("100000"),
                totalDiscount = BigDecimal("5000"),
                finalAmount = BigDecimal("95000"),
                appliedPromotions = emptyList()
            )
            
            every { calculateCartUseCase.execute(largeRequest) } returns largeResponse
            
            // When
            val result = cartService.calculateCartTotal(largeRequest)
            
            // Then
            result.items.size shouldBe 100
            result.subtotal shouldBe BigDecimal("100000")
            result.finalAmount shouldBe BigDecimal("95000")
            
            verify { calculateCartUseCase.execute(largeRequest) }
        }
    }
})
