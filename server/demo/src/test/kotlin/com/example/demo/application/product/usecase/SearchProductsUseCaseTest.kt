package com.example.demo.application.product.usecase

import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Stock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * SearchProductsUseCase 테스트
 * 상품 검색 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class SearchProductsUseCaseTest : FunSpec({
    
    val productRepository = mockk<ProductRepository>()
    val searchProductsUseCase = SearchProductsUseCase(productRepository)
    
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
    
    val inactiveProduct = Product(
        id = ProductId.of(3L),
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
    
    val electronicsProduct = Product(
        id = ProductId.of(4L),
        name = "전자제품",
        description = "전자제품 카테고리 상품",
        price = Price.of(Money(BigDecimal("15000"))),
        stock = Stock(75),
        category = "전자제품",
        brand = "전자 브랜드",
        imageUrl = null,
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    context("이름으로 검색") {
        test("상품명으로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(name = "테스트")
            val searchResults = listOf(sampleProduct)
            every { productRepository.findByNameContaining("테스트") } returns searchResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { productRepository.findByNameContaining("테스트") }
        }
        
        test("부분 일치로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(name = "상품")
            val searchResults = listOf(sampleProduct, expensiveProduct)
            every { productRepository.findByNameContaining("상품") } returns searchResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 2
            result[0].name shouldBe "테스트 상품"
            result[1].name shouldBe "고가 상품"
            
            verify { productRepository.findByNameContaining("상품") }
        }
        
        test("존재하지 않는 상품명으로 검색 시 빈 목록을 반환해야 한다") {
            // Given
            val request = ProductSearchRequest(name = "존재하지않는상품")
            every { productRepository.findByNameContaining("존재하지않는상품") } returns emptyList()
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByNameContaining("존재하지않는상품") }
        }
    }
    
    context("카테고리로 검색") {
        test("카테고리로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(category = "전자제품")
            val searchResults = listOf(sampleProduct, electronicsProduct)
            every { productRepository.findByCategory("전자제품") } returns searchResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 2
            result[0].category shouldBe "전자제품"
            result[1].category shouldBe "전자제품"
            
            verify { productRepository.findByCategory("전자제품") }
        }
        
        test("존재하지 않는 카테고리로 검색 시 빈 목록을 반환해야 한다") {
            // Given
            val request = ProductSearchRequest(category = "존재하지않는카테고리")
            every { productRepository.findByCategory("존재하지않는카테고리") } returns emptyList()
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByCategory("존재하지않는카테고리") }
        }
    }
    
    context("이름과 카테고리로 검색") {
        test("이름과 카테고리 모두로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(name = "테스트", category = "전자제품")
            val categoryResults = listOf(sampleProduct, electronicsProduct)
            every { productRepository.findByCategory("전자제품") } returns categoryResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            result[0].category shouldBe "전자제품"
            
            verify { productRepository.findByCategory("전자제품") }
        }
        
        test("이름과 카테고리 모두 일치하지 않으면 빈 목록을 반환해야 한다") {
            // Given
            val request = ProductSearchRequest(name = "존재하지않는상품", category = "전자제품")
            val categoryResults = listOf(sampleProduct, electronicsProduct)
            every { productRepository.findByCategory("전자제품") } returns categoryResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findByCategory("전자제품") }
        }
    }
    
    context("브랜드로 검색") {
        test("브랜드로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(brand = "테스트 브랜드")
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 1
            result[0].brand shouldBe "테스트 브랜드"
            result[0].name shouldBe "테스트 상품"
            
            verify { productRepository.findAll() }
        }
        
        test("존재하지 않는 브랜드로 검색 시 빈 목록을 반환해야 한다") {
            // Given
            val request = ProductSearchRequest(brand = "존재하지않는브랜드")
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findAll() }
        }
    }
    
    context("가격 범위로 검색") {
        test("최소 가격으로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(minPrice = BigDecimal("20000"))
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "고가 상품"
            result[0].price shouldBe BigDecimal("50000")
            
            verify { productRepository.findAll() }
        }
        
        test("최대 가격으로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(maxPrice = BigDecimal("15000"))
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 3
            result.forEach { product ->
                (product.price <= BigDecimal("15000")) shouldBe true
            }
            
            verify { productRepository.findAll() }
        }
        
        test("가격 범위로 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(
                minPrice = BigDecimal("8000"),
                maxPrice = BigDecimal("20000")
            )
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 2
            result.forEach { product ->
                (product.price >= BigDecimal("8000")) shouldBe true
                (product.price <= BigDecimal("20000")) shouldBe true
            }
            
            verify { productRepository.findAll() }
        }
    }
    
    context("활성 상품만 검색") {
        test("활성 상품만 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(activeOnly = true)
            val activeProducts = listOf(sampleProduct, expensiveProduct, electronicsProduct)
            every { productRepository.findActiveProducts() } returns activeProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 3
            result.forEach { product ->
                product.isActive shouldBe true
            }
            
            verify { productRepository.findActiveProducts() }
        }
        
        test("활성 상품이 없을 때 빈 목록을 반환해야 한다") {
            // Given
            val request = ProductSearchRequest(activeOnly = true)
            every { productRepository.findActiveProducts() } returns emptyList()
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result shouldBe emptyList()
            verify { productRepository.findActiveProducts() }
        }
    }
    
    context("정렬") {
        test("이름으로 정렬할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(sortBy = "name")
            val allProducts = listOf(expensiveProduct, sampleProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 3
            result[0].name shouldBe "고가 상품"  // 가나다순 정렬 (ㄱ, ㄷ, ㅌ)
            result[1].name shouldBe "전자제품"
            result[2].name shouldBe "테스트 상품"
            
            verify { productRepository.findAll() }
        }
        
        test("가격으로 정렬할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(sortBy = "price")
            val allProducts = listOf(expensiveProduct, sampleProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 3
            result[0].price shouldBe BigDecimal("10000")
            result[1].price shouldBe BigDecimal("15000")
            result[2].price shouldBe BigDecimal("50000")
            
            verify { productRepository.findAll() }
        }
        
        test("생성일로 정렬할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(sortBy = "createdAt")
            val allProducts = listOf(expensiveProduct, sampleProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 3
            // 생성일 내림차순으로 정렬됨 (최신순)
            
            verify { productRepository.findAll() }
        }
    }
    
    context("인기 상품 조회") {
        test("인기 상품을 조회할 수 있어야 한다") {
            // Given
            val activeProducts = listOf(sampleProduct, expensiveProduct, electronicsProduct)
            every { productRepository.findActiveProducts() } returns activeProducts
            
            // When
            val result = searchProductsUseCase.getPopularProducts(limit = 2)
            
            // Then
            result.size shouldBe 2
            // 재고가 많은 순으로 정렬됨
            result[0].stock shouldBe 100
            result[1].stock shouldBe 75
            
            verify { productRepository.findActiveProducts() }
        }
        
        test("기본 limit으로 인기 상품을 조회할 수 있어야 한다") {
            // Given
            val activeProducts = listOf(sampleProduct, expensiveProduct, electronicsProduct)
            every { productRepository.findActiveProducts() } returns activeProducts
            
            // When
            val result = searchProductsUseCase.getPopularProducts()
            
            // Then
            result.size shouldBe 3 // 기본 limit은 10이지만 상품이 3개뿐
            verify { productRepository.findActiveProducts() }
        }
    }
    
    context("신상품 조회") {
        test("신상품을 조회할 수 있어야 한다") {
            // Given
            val activeProducts = listOf(sampleProduct, expensiveProduct, electronicsProduct)
            every { productRepository.findActiveProducts() } returns activeProducts
            
            // When
            val result = searchProductsUseCase.getNewProducts(limit = 2)
            
            // Then
            result.size shouldBe 2
            // 생성일 내림차순으로 정렬됨 (최신순)
            
            verify { productRepository.findActiveProducts() }
        }
    }
    
    context("재고 부족 상품 조회") {
        test("재고 부족 상품을 조회할 수 있어야 한다") {
            // Given
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.getLowStockProducts(threshold = 20)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "고가 상품"
            result[0].stock shouldBe 10
            
            verify { productRepository.findAll() }
        }
        
        test("기본 threshold로 재고 부족 상품을 조회할 수 있어야 한다") {
            // Given
            val allProducts = listOf(sampleProduct, expensiveProduct, inactiveProduct, electronicsProduct)
            every { productRepository.findAll() } returns allProducts
            
            // When
            val result = searchProductsUseCase.getLowStockProducts()
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "고가 상품"
            result[0].stock shouldBe 10
            
            verify { productRepository.findAll() }
        }
    }
    
    context("복합 검색") {
        test("여러 조건으로 복합 검색할 수 있어야 한다") {
            // Given
            val request = ProductSearchRequest(
                category = "전자제품",
                minPrice = BigDecimal("5000"),
                maxPrice = BigDecimal("20000"),
                activeOnly = true,
                sortBy = "price"
            )
            val categoryResults = listOf(sampleProduct, electronicsProduct)
            every { productRepository.findByCategory("전자제품") } returns categoryResults
            
            // When
            val result = searchProductsUseCase.execute(request)
            
            // Then
            result.size shouldBe 2
            result.forEach { product ->
                product.category shouldBe "전자제품"
                product.isActive shouldBe true
                (product.price >= BigDecimal("5000")) shouldBe true
                (product.price <= BigDecimal("20000")) shouldBe true
            }
            
            verify { productRepository.findByCategory("전자제품") }
        }
    }
})
