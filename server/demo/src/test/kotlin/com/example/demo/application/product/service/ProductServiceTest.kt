package com.example.demo.application.product.service

import com.example.demo.application.common.dto.request.CreateProductRequest
import com.example.demo.application.common.dto.response.ProductResponse
import com.example.demo.application.product.usecase.CreateProductUseCase
import com.example.demo.application.product.usecase.GetProductUseCase
import com.example.demo.application.product.usecase.SearchProductsUseCase
import com.example.demo.application.product.usecase.ProductSearchRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * ProductService 테스트
 * 상품 서비스의 비즈니스 로직을 테스트합니다.
 */
class ProductServiceTest : FunSpec({
    
    val createProductUseCase = mockk<CreateProductUseCase>()
    val getProductUseCase = mockk<GetProductUseCase>()
    val searchProductsUseCase = mockk<SearchProductsUseCase>()
    val productService = ProductService(createProductUseCase, getProductUseCase, searchProductsUseCase)
    
    beforeEach {
        clearMocks(createProductUseCase, getProductUseCase, searchProductsUseCase)
    }
    
    val sampleProductResponse = ProductResponse(
        id = 1L,
        name = "테스트 상품",
        description = "테스트용 상품입니다",
        price = BigDecimal("10000"),
        stock = 100,
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = "https://example.com/image.jpg",
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val sampleCreateRequest = CreateProductRequest(
        name = "테스트 상품",
        description = "테스트용 상품입니다",
        price = BigDecimal("10000"),
        stock = 100,
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = "https://example.com/image.jpg"
    )
    
    context("상품 생성") {
        test("상품을 생성할 수 있어야 한다") {
            // Given
            every { createProductUseCase.execute(sampleCreateRequest) } returns sampleProductResponse
            
            // When
            val result = productService.createProduct(sampleCreateRequest)
            
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
            
            verify { createProductUseCase.execute(sampleCreateRequest) }
        }
        
        test("최소 정보로 상품을 생성할 수 있어야 한다") {
            // Given
            val minimalRequest = CreateProductRequest(
                name = "최소 상품",
                price = BigDecimal("5000"),
                stock = 50
            )
            val minimalResponse = ProductResponse(
                id = 2L,
                name = "최소 상품",
                description = null,
                price = BigDecimal("5000"),
                stock = 50,
                category = null,
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            every { createProductUseCase.execute(minimalRequest) } returns minimalResponse
            
            // When
            val result = productService.createProduct(minimalRequest)
            
            // Then
            result.id shouldBe 2L
            result.name shouldBe "최소 상품"
            result.description shouldBe null
            result.price shouldBe BigDecimal("5000")
            result.stock shouldBe 50
            result.category shouldBe null
            result.brand shouldBe null
            result.imageUrl shouldBe null
            
            verify { createProductUseCase.execute(minimalRequest) }
        }
    }
    
    context("상품 조회") {
        test("상품 ID로 상품을 조회할 수 있어야 한다") {
            // Given
            val productId = 1L
            every { getProductUseCase.getProductById(productId) } returns sampleProductResponse
            
            // When
            val result = productService.getProductById(productId)
            
            // Then
            result.id shouldBe 1L
            result.name shouldBe "테스트 상품"
            
            verify { getProductUseCase.getProductById(productId) }
        }
        
        test("활성 상품을 조회할 수 있어야 한다") {
            // Given
            val activeProducts = listOf(sampleProductResponse)
            every { getProductUseCase.getActiveProducts() } returns activeProducts
            
            // When
            val result = productService.getActiveProducts()
            
            // Then
            result.size shouldBe 1
            result[0].isActive shouldBe true
            
            verify { getProductUseCase.getActiveProducts() }
        }
        
        test("모든 상품을 조회할 수 있어야 한다") {
            // Given
            val allProducts = listOf(sampleProductResponse)
            every { getProductUseCase.getAllProducts() } returns allProducts
            
            // When
            val result = productService.getAllProducts()
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { getProductUseCase.getAllProducts() }
        }
        
        test("카테고리로 상품을 조회할 수 있어야 한다") {
            // Given
            val category = "전자제품"
            val categoryProducts = listOf(sampleProductResponse)
            every { getProductUseCase.getProductsByCategory(category) } returns categoryProducts
            
            // When
            val result = productService.getProductsByCategory(category)
            
            // Then
            result.size shouldBe 1
            result[0].category shouldBe "전자제품"
            
            verify { getProductUseCase.getProductsByCategory(category) }
        }
        
        test("상품명으로 상품을 검색할 수 있어야 한다") {
            // Given
            val searchName = "테스트"
            val searchResults = listOf(sampleProductResponse)
            every { getProductUseCase.searchProductsByName(searchName) } returns searchResults
            
            // When
            val result = productService.searchProductsByName(searchName)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { getProductUseCase.searchProductsByName(searchName) }
        }
    }
    
    context("상품 검색") {
        test("상품을 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(
                name = "테스트",
                category = "전자제품",
                minPrice = BigDecimal("5000"),
                maxPrice = BigDecimal("20000"),
                activeOnly = true,
                sortBy = "price"
            )
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
        
        test("이름으로만 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(name = "테스트")
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
        
        test("카테고리로만 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(category = "전자제품")
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            result[0].category shouldBe "전자제품"
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
        
        test("가격 범위로 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(
                minPrice = BigDecimal("5000"),
                maxPrice = BigDecimal("20000")
            )
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            (result[0].price >= BigDecimal("5000")) shouldBe true
            (result[0].price <= BigDecimal("20000")) shouldBe true
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
        
        test("활성 상품만 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(activeOnly = true)
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            result[0].isActive shouldBe true
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
        
        test("정렬 옵션으로 검색할 수 있어야 한다") {
            // Given
            val searchRequest = ProductSearchRequest(sortBy = "price")
            val searchResults = listOf(sampleProductResponse)
            every { searchProductsUseCase.execute(searchRequest) } returns searchResults
            
            // When
            val result = productService.searchProducts(searchRequest)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { searchProductsUseCase.execute(searchRequest) }
        }
    }
    
    context("인기 상품 조회") {
        test("인기 상품을 조회할 수 있어야 한다") {
            // Given
            val limit = 5
            val popularProducts = listOf(sampleProductResponse)
            every { searchProductsUseCase.getPopularProducts(limit) } returns popularProducts
            
            // When
            val result = productService.getPopularProducts(limit)
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { searchProductsUseCase.getPopularProducts(limit) }
        }
        
        test("기본 limit으로 인기 상품을 조회할 수 있어야 한다") {
            // Given
            val popularProducts = listOf(sampleProductResponse)
            every { searchProductsUseCase.getPopularProducts(10) } returns popularProducts
            
            // When
            val result = productService.getPopularProducts()
            
            // Then
            result.size shouldBe 1
            result[0].name shouldBe "테스트 상품"
            
            verify { searchProductsUseCase.getPopularProducts(10) }
        }
    }
    
    context("서비스 조합 테스트") {
        test("상품 생성 후 조회할 수 있어야 한다") {
            // Given
            val createRequest = CreateProductRequest(
                name = "새 상품",
                price = BigDecimal("15000"),
                stock = 200
            )
            val createdProduct = ProductResponse(
                id = 3L,
                name = "새 상품",
                description = null,
                price = BigDecimal("15000"),
                stock = 200,
                category = null,
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { createProductUseCase.execute(createRequest) } returns createdProduct
            every { getProductUseCase.getProductById(3L) } returns createdProduct
            
            // When
            val createResult = productService.createProduct(createRequest)
            val getResult = productService.getProductById(3L)
            
            // Then
            createResult.id shouldBe 3L
            createResult.name shouldBe "새 상품"
            
            getResult.id shouldBe 3L
            getResult.name shouldBe "새 상품"
            
            verify { createProductUseCase.execute(createRequest) }
            verify { getProductUseCase.getProductById(3L) }
        }
        
        test("상품 생성 후 검색할 수 있어야 한다") {
            // Given
            val createRequest = CreateProductRequest(
                name = "검색 테스트 상품",
                price = BigDecimal("20000"),
                stock = 300,
                category = "테스트 카테고리"
            )
            val createdProduct = ProductResponse(
                id = 4L,
                name = "검색 테스트 상품",
                description = null,
                price = BigDecimal("20000"),
                stock = 300,
                category = "테스트 카테고리",
                brand = null,
                imageUrl = null,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            val searchRequest = ProductSearchRequest(category = "테스트 카테고리")
            
            every { createProductUseCase.execute(createRequest) } returns createdProduct
            every { searchProductsUseCase.execute(searchRequest) } returns listOf(createdProduct)
            
            // When
            val createResult = productService.createProduct(createRequest)
            val searchResult = productService.searchProducts(searchRequest)
            
            // Then
            createResult.id shouldBe 4L
            createResult.name shouldBe "검색 테스트 상품"
            
            searchResult.size shouldBe 1
            searchResult[0].name shouldBe "검색 테스트 상품"
            searchResult[0].category shouldBe "테스트 카테고리"
            
            verify { createProductUseCase.execute(createRequest) }
            verify { searchProductsUseCase.execute(searchRequest) }
        }
    }
    
    context("경계값 테스트") {
        test("빈 목록을 반환하는 경우를 처리할 수 있어야 한다") {
            // Given
            every { getProductUseCase.getAllProducts() } returns emptyList()
            every { getProductUseCase.getActiveProducts() } returns emptyList()
            every { getProductUseCase.getProductsByCategory("빈카테고리") } returns emptyList()
            every { getProductUseCase.searchProductsByName("존재하지않는상품") } returns emptyList()
            every { searchProductsUseCase.execute(any()) } returns emptyList()
            
            // When
            val allProducts = productService.getAllProducts()
            val activeProducts = productService.getActiveProducts()
            val categoryProducts = productService.getProductsByCategory("빈카테고리")
            val searchResults = productService.searchProductsByName("존재하지않는상품")
            val complexSearchResults = productService.searchProducts(ProductSearchRequest())
            
            // Then
            allProducts shouldBe emptyList()
            activeProducts shouldBe emptyList()
            categoryProducts shouldBe emptyList()
            searchResults shouldBe emptyList()
            complexSearchResults shouldBe emptyList()
            
            verify { getProductUseCase.getAllProducts() }
            verify { getProductUseCase.getActiveProducts() }
            verify { getProductUseCase.getProductsByCategory("빈카테고리") }
            verify { getProductUseCase.searchProductsByName("존재하지않는상품") }
            verify { searchProductsUseCase.execute(any()) }
        }
        
        test("대량의 상품을 처리할 수 있어야 한다") {
            // Given
            val largeProductList = (1..1000).map { i ->
                ProductResponse(
                    id = i.toLong(),
                    name = "상품 $i",
                    description = "상품 $i 설명",
                    price = BigDecimal("10000"),
                    stock = 100,
                    category = "카테고리 $i",
                    brand = "브랜드 $i",
                    imageUrl = "https://example.com/image$i.jpg",
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
            every { getProductUseCase.getAllProducts() } returns largeProductList
            
            // When
            val result = productService.getAllProducts()
            
            // Then
            result.size shouldBe 1000
            result[0].name shouldBe "상품 1"
            result[999].name shouldBe "상품 1000"
            
            verify { getProductUseCase.getAllProducts() }
        }
    }
})
