package com.example.demo.application.product.usecase

import com.example.demo.application.common.dto.request.CreateProductRequest
import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Stock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * CreateProductUseCase 테스트
 * 상품 생성 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class CreateProductUseCaseTest : FunSpec({
    
    val productRepository = mockk<ProductRepository>()
    val createProductUseCase = CreateProductUseCase(productRepository)
    
    beforeEach {
        clearMocks(productRepository)
    }
    
    context("상품 생성 성공") {
        test("새로운 상품을 생성할 수 있어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "테스트 상품",
                description = "테스트용 상품입니다",
                price = BigDecimal("10000"),
                stock = 100,
                category = "전자제품",
                brand = "테스트 브랜드",
                imageUrl = "https://example.com/image.jpg"
            )
            val savedProduct = Product(
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
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            val result = createProductUseCase.execute(request)
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 상품"
            result.description shouldBe "테스트용 상품입니다"
            result.price shouldBe BigDecimal("10000")
            result.stock shouldBe 100
            result.category shouldBe "전자제품"
            result.brand shouldBe "테스트 브랜드"
            result.imageUrl shouldBe "https://example.com/image.jpg"
            result.isActive shouldBe true
            result.createdAt shouldNotBe null
            result.updatedAt shouldNotBe null
            
            verify { productRepository.save(any()) }
        }
        
        test("최소 정보로 상품을 생성할 수 있어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "최소 상품",
                price = BigDecimal("5000"),
                stock = 50
            )
            val savedProduct = Product(
                id = ProductId.of(2L),
                name = "최소 상품",
                description = null,
                price = Price.of(Money(BigDecimal("5000"))),
                stock = Stock(50),
                category = null,
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            val result = createProductUseCase.execute(request)
            
            // Then
            result.id shouldBe 2L
            result.name shouldBe "최소 상품"
            result.description shouldBe null
            result.price shouldBe BigDecimal("5000")
            result.stock shouldBe 50
            result.category shouldBe null
            result.brand shouldBe null
            result.imageUrl shouldBe null
            result.isActive shouldBe true
            
            verify { productRepository.save(any()) }
        }
        
        test("고가 상품을 생성할 수 있어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "고가 상품",
                description = "비싼 상품입니다",
                price = BigDecimal("1000000"),
                stock = 1,
                category = "명품",
                brand = "럭셔리 브랜드"
            )
            val savedProduct = Product(
                id = ProductId.of(3L),
                name = "고가 상품",
                description = "비싼 상품입니다",
                price = Price.of(Money(BigDecimal("1000000"))),
                stock = Stock(1),
                category = "명품",
                brand = "럭셔리 브랜드",
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            val result = createProductUseCase.execute(request)
            
            // Then
            result.id shouldBe 3L
            result.name shouldBe "고가 상품"
            result.price shouldBe BigDecimal("1000000")
            result.stock shouldBe 1
            result.category shouldBe "명품"
            result.brand shouldBe "럭셔리 브랜드"
            
            verify { productRepository.save(any()) }
        }
    }
    
    context("상품 생성 실패") {
        test("빈 상품명으로 상품 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "",
                price = BigDecimal("10000"),
                stock = 100
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createProductUseCase.execute(request)
            }
            
            exception.message shouldBe "상품명은 필수입니다."
        }
        
        test("공백 상품명으로 상품 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "   ",
                price = BigDecimal("10000"),
                stock = 100
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createProductUseCase.execute(request)
            }
            
            exception.message shouldBe "상품명은 필수입니다."
        }
        
        test("음수 가격으로 상품 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "테스트 상품",
                price = BigDecimal("-1000"),
                stock = 100
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createProductUseCase.execute(request)
            }
            
            exception.message shouldBe "상품 가격은 0보다 커야 합니다."
        }
        
        test("음수 재고로 상품 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "테스트 상품",
                price = BigDecimal("10000"),
                stock = -10
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createProductUseCase.execute(request)
            }
            
            exception.message shouldBe "재고는 0 이상이어야 합니다."
        }
        
        test("0 가격으로 상품 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "무료 상품",
                price = BigDecimal("0"),
                stock = 100
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createProductUseCase.execute(request)
            }
            
            exception.message shouldBe "상품 가격은 0보다 커야 합니다."
        }
    }
    
    context("경계값 테스트") {
        test("최소 가격으로 상품을 생성할 수 있어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "최소 가격 상품",
                price = BigDecimal("1"),
                stock = 1
            )
            val savedProduct = Product(
                id = ProductId.of(4L),
                name = "최소 가격 상품",
                description = null,
                price = Price.of(Money(BigDecimal("1"))),
                stock = Stock(1),
                category = null,
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            val result = createProductUseCase.execute(request)
            
            // Then
            result.price shouldBe BigDecimal("1")
            result.stock shouldBe 1
            
            verify { productRepository.save(any()) }
        }
        
        test("최대 재고로 상품을 생성할 수 있어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "대량 재고 상품",
                price = BigDecimal("10000"),
                stock = Int.MAX_VALUE
            )
            val savedProduct = Product(
                id = ProductId.of(5L),
                name = "대량 재고 상품",
                description = null,
                price = Price.of(Money(BigDecimal("10000"))),
                stock = Stock(Int.MAX_VALUE),
                category = null,
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            val result = createProductUseCase.execute(request)
            
            // Then
            result.stock shouldBe Int.MAX_VALUE
            
            verify { productRepository.save(any()) }
        }
    }
    
    context("저장 과정 검증") {
        test("상품 저장 시 올바른 도메인 모델이 전달되어야 한다") {
            // Given
            val request = CreateProductRequest(
                name = "저장 테스트 상품",
                description = "저장 테스트용",
                price = BigDecimal("15000"),
                stock = 75,
                category = "테스트 카테고리",
                brand = "테스트 브랜드"
            )
            val savedProduct = Product(
                id = ProductId.of(6L),
                name = "저장 테스트 상품",
                description = "저장 테스트용",
                price = Price.of(Money(BigDecimal("15000"))),
                stock = Stock(75),
                category = "테스트 카테고리",
                brand = "테스트 브랜드",
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { productRepository.save(any()) } returns savedProduct
            
            // When
            createProductUseCase.execute(request)
            
            // Then
            verify { productRepository.save(any()) }
        }
    }
})
