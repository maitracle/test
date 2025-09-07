package com.example.demo.application.cart.usecase

import com.example.demo.application.common.dto.request.ValidateCartRequest
import com.example.demo.application.common.dto.request.CartItemRequest
import com.example.demo.application.common.dto.response.ValidateCartResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Stock
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
 * ValidateCartUseCase 테스트
 * 장바구니 검증 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class ValidateCartUseCaseTest : FunSpec({
    
    val productRepository = mockk<ProductRepository>()
    val validateCartUseCase = ValidateCartUseCase(productRepository)
    
    beforeEach {
        clearMocks(productRepository)
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
    
    val lowStockProduct = Product(
        id = ProductId.of(2L),
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
    
    context("장바구니 검증 성공") {
        test("유효한 장바구니 아이템들을 검증할 수 있어야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 2),
                    CartItemRequest(productId = 2L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns lowStockProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings.size shouldBe 1
            result.warnings[0] shouldBe "상품 재고가 부족합니다: 재고 부족 상품 (재고: 5개)"
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
        }
        
        test("단일 아이템으로 장바구니를 검증할 수 있어야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("빈 장바구니를 검증할 수 있어야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = emptyList()
            )
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings.size shouldBe 1
            result.warnings[0] shouldBe "장바구니가 비어있습니다."
        }
    }
    
    context("장바구니 검증 실패") {
        test("존재하지 않는 상품이 포함된 장바구니 검증 시 실패해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 999L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(999L)) } returns null
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe false
            result.errors.size shouldBe 1
            result.errors[0] shouldBe "상품을 찾을 수 없습니다: 999"
            
            verify { productRepository.findById(ProductId.of(999L)) }
        }
        
        test("비활성 상품이 포함된 장바구니 검증 시 실패해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 3L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(3L)) } returns inactiveProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe false
            result.errors.size shouldBe 2
            result.errors.contains("비활성화된 상품입니다: 비활성 상품") shouldBe true
            result.errors.contains("상품 재고가 부족합니다: 비활성 상품 (요청: 1, 재고: 50)") shouldBe true
            
            verify { productRepository.findById(ProductId.of(3L)) }
        }
        
        test("재고 부족 상품이 포함된 장바구니 검증 시 실패해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 2L, quantity = 10) // 재고 5개인데 10개 주문
                )
            )
            
            every { productRepository.findById(ProductId.of(2L)) } returns lowStockProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe false
            result.errors.size shouldBe 1
            result.errors[0] shouldBe "상품 재고가 부족합니다: 재고 부족 상품 (요청: 10, 재고: 5)"
            
            verify { productRepository.findById(ProductId.of(2L)) }
        }
        
        test("음수 수량이 포함된 장바구니 검증 시 실패해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = -1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                validateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "Quantity must be positive: -1"
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("0 수량이 포함된 장바구니 검증 시 실패해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 0)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                validateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "Quantity must be positive: 0"
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("여러 오류가 있는 장바구니 검증 시 모든 오류를 반환해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 999L, quantity = 1), // 존재하지 않는 상품
                    CartItemRequest(productId = 3L, quantity = 1),    // 비활성 상품
                    CartItemRequest(productId = 2L, quantity = 10)    // 재고 부족
                )
            )
            
            every { productRepository.findById(ProductId.of(999L)) } returns null
            every { productRepository.findById(ProductId.of(3L)) } returns inactiveProduct
            every { productRepository.findById(ProductId.of(2L)) } returns lowStockProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe false
            result.errors.size shouldBe 4 // 존재하지 않는 상품, 비활성 상품, 재고 부족, 수량 오류
            result.errors.contains("상품을 찾을 수 없습니다: 999") shouldBe true
            result.errors.contains("비활성화된 상품입니다: 비활성 상품") shouldBe true
            result.errors.contains("상품 재고가 부족합니다: 재고 부족 상품 (요청: 10, 재고: 5)") shouldBe true
            
            verify { productRepository.findById(ProductId.of(999L)) }
            verify { productRepository.findById(ProductId.of(3L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
        }
    }
    
    context("장바구니 검증 경고") {
        test("재고가 적은 상품에 대해 경고를 표시해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 2L, quantity = 3) // 재고 5개 중 3개 주문 (60%)
                )
            )
            
            every { productRepository.findById(ProductId.of(2L)) } returns lowStockProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings.size shouldBe 1
            result.warnings[0] shouldBe "상품 재고가 부족합니다: 재고 부족 상품 (재고: 5개)"
            
            verify { productRepository.findById(ProductId.of(2L)) }
        }
        
        test("여러 경고가 있는 장바구니 검증 시 모든 경고를 반환해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 80), // 재고 100개 중 80개 주문 (80%)
                    CartItemRequest(productId = 2L, quantity = 3)   // 재고 5개 중 3개 주문 (60%)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns lowStockProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings.size shouldBe 1 // 재고 5개인 상품만 경고 (10개 미만)
            result.warnings.contains("상품 재고가 부족합니다: 재고 부족 상품 (재고: 5개)") shouldBe true
            
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
        }
    }
    
    context("경계값 테스트") {
        test("최소 수량으로 장바구니를 검증할 수 있어야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 1)
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("최대 수량으로 장바구니를 검증할 수 있어야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 1L, quantity = 100) // 재고와 동일
                )
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            verify { productRepository.findById(ProductId.of(1L)) }
        }
        
        test("0 ID로 상품 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = 0L, quantity = 1)
                )
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                validateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
        }
        
        test("음수 ID로 상품 조회 시 EntityId 검증 예외가 발생해야 한다") {
            // Given
            val request = ValidateCartRequest(
                userId = 1L,
                items = listOf(
                    CartItemRequest(productId = -1L, quantity = 1)
                )
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                validateCartUseCase.execute(request)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
        }
    }
    
    context("대용량 장바구니 테스트") {
        test("여러 아이템이 포함된 장바구니를 검증할 수 있어야 한다") {
            // Given
            val items = (1..5).map { i ->
                CartItemRequest(productId = i.toLong(), quantity = 1)
            }
            val request = ValidateCartRequest(
                userId = 1L,
                items = items
            )
            
            every { productRepository.findById(ProductId.of(1L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(2L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(3L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(4L)) } returns sampleProduct
            every { productRepository.findById(ProductId.of(5L)) } returns sampleProduct
            
            // When
            val result = validateCartUseCase.execute(request)
            
            // Then
            result.isValid shouldBe true
            result.errors shouldBe emptyList()
            result.warnings shouldBe emptyList()
            
            // 5번의 상품 조회가 발생했는지 확인
            verify { productRepository.findById(ProductId.of(1L)) }
            verify { productRepository.findById(ProductId.of(2L)) }
            verify { productRepository.findById(ProductId.of(3L)) }
            verify { productRepository.findById(ProductId.of(4L)) }
            verify { productRepository.findById(ProductId.of(5L)) }
        }
    }
})
