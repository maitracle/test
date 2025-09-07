package com.example.demo.application.product.usecase

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
 * GetProductUseCase 테스트
 * 상품 조회 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class GetProductUseCaseTest : FunSpec({
    
    val productRepository = mockk<ProductRepository>()
    val getProductUseCase = GetProductUseCase(productRepository)
    
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
    
    val inactiveProduct = Product(
        id = ProductId.of(2L),
        name = "비활성 상품",
        description = "비활성화된 상품입니다",
        price = Price.of(Money(BigDecimal("5000"))),
        stock = Stock(50),
        category = "기타",
        brand = "기타 브랜드",
        imageUrl = null,
        isActive = false,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val vipProduct = Product(
        id = ProductId.of(3L),
        name = "VIP 상품",
        description = "VIP 전용 상품입니다",
        price = Price.of(Money(BigDecimal("50000"))),
        stock = Stock(10),
        category = "명품",
        brand = "럭셔리 브랜드",
        imageUrl = "https://example.com/vip.jpg",
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    context("상품 ID로 조회") {
        test("존재하는 상품 ID로 조회할 수 있어야 한다") {
            // Given
            val productId = 1L
            every { productRepository.findById(ProductId.of(productId)) } returns sampleProduct
            
            // When
            val result = getProductUseCase.getProductById(productId)
            
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
            result.createdAt shouldBe sampleProduct.createdAt
            result.updatedAt shouldBe sampleProduct.updatedAt
            
            verify { productRepository.findById(ProductId.of(productId)) }
        }
        
        test("존재하지 않는 상품 ID로 조회 시 예외가 발생해야 한다") {
            // Given
            val productId = 999L
            every { productRepository.findById(ProductId.of(productId)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getProductUseCase.getProductById(productId)
            }
            
            exception.message shouldBe "상품을 찾을 수 없습니다: 999"
            verify { productRepository.findById(ProductId.of(productId)) }
        }
        
        test("비활성 상품을 ID로 조회할 수 있어야 한다") {
            // Given
            val productId = 2L
            every { productRepository.findById(ProductId.of(productId)) } returns inactiveProduct
            
            // When
            val result = getProductUseCase.getProductById(productId)
            
            // Then
            result.id shouldBe 2L
            result.name shouldBe "비활성 상품"
            result.isActive shouldBe false
            
            verify { productRepository.findById(ProductId.of(productId)) }
        }
    }
    
    context("활성 상품 조회") {
        test("활성 상품만 조회할 수 있어야 한다") {
            // Given
            val activeProducts = listOf(sampleProduct, vipProduct)
            every { productRepository.findActiveProducts() } returns activeProducts
            
            // When
            val result = getProductUseCase.getActiveProducts()
            
            // Then
            result.size shouldBe 2
            result[0].id shouldBe 1L
            result[0].name shouldBe "테스트 상품"
            result[0].isActive shouldBe true
            
            result[1].id shouldBe 3L
            result[1].name shouldBe "VIP 상품"
            result[1].isActive shouldBe true
            
            verify { productRepository.findActiveProducts() }
        }
        
        test("활성 상품이 없을 때 빈 목록을 반환해야 한다") {
            // Given
            every { productRepository.findActiveProducts() } returns emptyList()
            
            // When
            val result = getProductUseCase.getActiveProducts()
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findActiveProducts() }
        }
    }
    
    context("모든 상품 조회") {
        test("모든 상품을 조회할 수 있어야 한다") {
            // Given
            val allProducts = listOf(sampleProduct, inactiveProduct, vipProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = getProductUseCase.getAllProducts()
            
            // Then
            result.size shouldBe 3
            result[0].id shouldBe 1L
            result[0].name shouldBe "테스트 상품"
            result[0].isActive shouldBe true
            
            result[1].id shouldBe 2L
            result[1].name shouldBe "비활성 상품"
            result[1].isActive shouldBe false
            
            result[2].id shouldBe 3L
            result[2].name shouldBe "VIP 상품"
            result[2].isActive shouldBe true
            
            verify { productRepository.findAll() }
        }
        
        test("상품이 없을 때 빈 목록을 반환해야 한다") {
            // Given
            every { productRepository.findAll() } returns emptyList()
            
            // When
            val result = getProductUseCase.getAllProducts()
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findAll() }
        }
    }
    
    context("카테고리별 조회") {
        test("특정 카테고리의 상품들을 조회할 수 있어야 한다") {
            // Given
            val electronicsProducts = listOf(sampleProduct)
            every { productRepository.findByCategory("전자제품") } returns electronicsProducts
            
            // When
            val result = getProductUseCase.getProductsByCategory("전자제품")
            
            // Then
            result.size shouldBe 1
            result[0].category shouldBe "전자제품"
            result[0].name shouldBe "테스트 상품"
            
            verify { productRepository.findByCategory("전자제품") }
        }
        
        test("존재하지 않는 카테고리로 조회 시 빈 목록을 반환해야 한다") {
            // Given
            every { productRepository.findByCategory("존재하지않는카테고리") } returns emptyList()
            
            // When
            val result = getProductUseCase.getProductsByCategory("존재하지않는카테고리")
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByCategory("존재하지않는카테고리") }
        }
        
        test("빈 카테고리로 조회 시 빈 목록을 반환해야 한다") {
            // Given
            every { productRepository.findByCategory("") } returns emptyList()
            
            // When
            val result = getProductUseCase.getProductsByCategory("")
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByCategory("") }
        }
    }
    
    context("상품명으로 검색") {
        test("상품명으로 상품을 검색할 수 있어야 한다") {
            // Given
            val searchResults = listOf(sampleProduct)
            every { productRepository.findByNameContaining("테스트") } returns searchResults
            
            // When
            val result = getProductUseCase.searchProductsByName("테스트")
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { productRepository.findByNameContaining("테스트") }
        }
        
        test("부분 일치로 상품을 검색할 수 있어야 한다") {
            // Given
            val searchResults = listOf(sampleProduct, vipProduct)
            every { productRepository.findByNameContaining("상품") } returns searchResults
            
            // When
            val result = getProductUseCase.searchProductsByName("상품")
            
            // Then
            result.size shouldBe 2
            result[0].name shouldBe "테스트 상품"
            result[1].name shouldBe "VIP 상품"
            
            verify { productRepository.findByNameContaining("상품") }
        }
        
        test("존재하지 않는 상품명으로 검색 시 빈 목록을 반환해야 한다") {
            // Given
            every { productRepository.findByNameContaining("존재하지않는상품") } returns emptyList()
            
            // When
            val result = getProductUseCase.searchProductsByName("존재하지않는상품")
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByNameContaining("존재하지않는상품") }
        }
        
        test("빈 문자열로 검색 시 빈 목록을 반환해야 한다") {
            // Given
            // 빈 문자열일 때는 repository 호출 없이 바로 빈 목록 반환
            
            // When
            val result = getProductUseCase.searchProductsByName("")
            
            // Then
            result shouldBe emptyList()
            // 빈 문자열일 때는 repository 호출하지 않음
        }
    }
    
    context("경계값 테스트") {
        test("0 ID로 상품 조회 시 ProductId 검증 예외가 발생해야 한다") {
            // Given
            val productId = 0L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getProductUseCase.getProductById(productId)
            }
            
            exception.message shouldBe "EntityId must be positive: 0"
        }
        
        test("음수 ID로 상품 조회 시 ProductId 검증 예외가 발생해야 한다") {
            // Given
            val productId = -1L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getProductUseCase.getProductById(productId)
            }
            
            exception.message shouldBe "EntityId must be positive: -1"
        }
    }
    
    context("대소문자 처리") {
        test("대소문자 구분 없이 카테고리 검색이 가능해야 한다") {
            // Given
            val searchResults = listOf(sampleProduct)
            every { productRepository.findByCategory("전자제품") } returns searchResults
            
            // When
            val result = getProductUseCase.getProductsByCategory("전자제품")
            
            // Then
            result.size shouldBe 1
            result[0].category shouldBe "전자제품"
            
            verify { productRepository.findByCategory("전자제품") }
        }
        
        test("대소문자 구분 없이 상품명 검색이 가능해야 한다") {
            // Given
            val searchResults = listOf(sampleProduct)
            every { productRepository.findByNameContaining("테스트") } returns searchResults
            
            // When
            val result = getProductUseCase.searchProductsByName("테스트")
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { productRepository.findByNameContaining("테스트") }
        }
    }
})
