package com.example.demo.product

import com.example.demo.product.dto.ProductRequest
import com.example.demo.product.dto.ProductResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class ProductServiceTest : DescribeSpec({
    
    val mockProductRepository = mockk<ProductRepository>()
    val productService = ProductService(mockProductRepository)
    
    beforeEach {
        clearAllMocks()
    }
    
    describe("ProductService") {
        
        context("createProduct") {
            it("새로운 제품을 생성하고 ProductResponse를 반환한다") {
                // Given
                val request = ProductRequest(
                    name = "MacBook Pro",
                    description = "Apple의 최신 노트북",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    category = "노트북",
                    brand = "Apple",
                    imageUrl = "https://example.com/macbook.jpg",
                    isActive = true
                )
                
                val savedProduct = Product(
                    id = 1L,
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    stockQuantity = request.stockQuantity,
                    category = request.category,
                    brand = request.brand,
                    imageUrl = request.imageUrl,
                    isActive = request.isActive,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductRepository.save(any()) } returns savedProduct
                
                // When
                val result = productService.createProduct(request)
                
                // Then
                result.shouldBeInstanceOf<ProductResponse>()
                result.id shouldBe 1L
                result.name shouldBe request.name
                result.description shouldBe request.description
                result.price shouldBe request.price
                result.stockQuantity shouldBe request.stockQuantity
                result.category shouldBe request.category
                result.brand shouldBe request.brand
                result.imageUrl shouldBe request.imageUrl
                result.isActive shouldBe request.isActive
                
                verify { mockProductRepository.save(any()) }
            }
        }
        
        context("getProduct") {
            it("존재하는 제품 ID로 제품을 조회한다") {
                // Given
                val productId = 1L
                val product = Product(
                    id = productId,
                    name = "MacBook Pro",
                    description = "Apple의 최신 노트북",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    category = "노트북",
                    brand = "Apple",
                    imageUrl = "https://example.com/macbook.jpg",
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductRepository.findById(productId) } returns Optional.of(product)
                
                // When
                val result = productService.getProduct(productId)
                
                // Then
                result.shouldBeInstanceOf<ProductResponse>()
                result.id shouldBe productId
                result.name shouldBe product.name
                
                verify { mockProductRepository.findById(productId) }
            }
            
            it("존재하지 않는 제품 ID로 조회할 때 예외가 발생한다") {
                // Given
                val productId = 999L
                every { mockProductRepository.findById(productId) } returns Optional.empty()
                
                // When & Then
                shouldThrow<RuntimeException> {
                    productService.getProduct(productId)
                }.message shouldBe "제품을 찾을 수 없습니다. ID: $productId"
                
                verify { mockProductRepository.findById(productId) }
            }
        }
        
        context("getAllProducts") {
            it("활성화된 모든 제품을 조회한다") {
                // Given
                val products = listOf(
                    Product(
                        id = 1L,
                        name = "MacBook Pro",
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    ),
                    Product(
                        id = 2L,
                        name = "iPhone 15",
                        price = BigDecimal("1500000"),
                        stockQuantity = 100,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductRepository.findByIsActiveTrue() } returns products
                
                // When
                val result = productService.getAllProducts()
                
                // Then
                result.shouldBeInstanceOf<List<ProductResponse>>()
                result.size shouldBe 2
                result[0].name shouldBe "MacBook Pro"
                result[1].name shouldBe "iPhone 15"
                
                verify { mockProductRepository.findByIsActiveTrue() }
            }
        }
        
        context("getProductsByCategory") {
            it("특정 카테고리의 활성화된 제품들을 조회한다") {
                // Given
                val category = "노트북"
                val products = listOf(
                    Product(
                        id = 1L,
                        name = "MacBook Pro",
                        category = category,
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductRepository.findByCategoryAndIsActiveTrue(category) } returns products
                
                // When
                val result = productService.getProductsByCategory(category)
                
                // Then
                result.size shouldBe 1
                result[0].category shouldBe category
                
                verify { mockProductRepository.findByCategoryAndIsActiveTrue(category) }
            }
        }
        
        context("getProductsByBrand") {
            it("특정 브랜드의 활성화된 제품들을 조회한다") {
                // Given
                val brand = "Apple"
                val products = listOf(
                    Product(
                        id = 1L,
                        name = "MacBook Pro",
                        brand = brand,
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductRepository.findByBrandAndIsActiveTrue(brand) } returns products
                
                // When
                val result = productService.getProductsByBrand(brand)
                
                // Then
                result.size shouldBe 1
                result[0].brand shouldBe brand
                
                verify { mockProductRepository.findByBrandAndIsActiveTrue(brand) }
            }
        }
        
        context("searchProducts") {
            it("키워드로 제품을 검색한다") {
                // Given
                val keyword = "MacBook"
                val products = listOf(
                    Product(
                        id = 1L,
                        name = "MacBook Pro",
                        description = "Apple MacBook Pro",
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductRepository.searchByKeyword(keyword) } returns products
                
                // When
                val result = productService.searchProducts(keyword)
                
                // Then
                result.size shouldBe 1
                result[0].name shouldContain keyword
                
                verify { mockProductRepository.searchByKeyword(keyword) }
            }
        }
        
        context("getProductsByPriceRange") {
            it("가격 범위로 제품을 조회한다") {
                // Given
                val minPrice = BigDecimal("1000000")
                val maxPrice = BigDecimal("2000000")
                val products = listOf(
                    Product(
                        id = 1L,
                        name = "iPhone 15",
                        price = BigDecimal("1500000"),
                        stockQuantity = 100,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice) } returns products
                
                // When
                val result = productService.getProductsByPriceRange(minPrice, maxPrice)
                
                // Then
                result.size shouldBe 1
                result[0].price shouldBe BigDecimal("1500000")
                
                verify { mockProductRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice) }
            }
        }
        
        context("updateProduct") {
            it("존재하는 제품을 업데이트한다") {
                // Given
                val productId = 1L
                val existingProduct = Product(
                    id = productId,
                    name = "MacBook Pro",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                val request = ProductRequest(
                    name = "MacBook Pro Updated",
                    description = "Updated description",
                    price = BigDecimal("4000000"),
                    stockQuantity = 30,
                    category = "노트북",
                    brand = "Apple",
                    imageUrl = "https://example.com/macbook-updated.jpg",
                    isActive = true
                )
                
                val updatedProduct = existingProduct.copy(
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    stockQuantity = request.stockQuantity,
                    category = request.category,
                    brand = request.brand,
                    imageUrl = request.imageUrl,
                    isActive = request.isActive,
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductRepository.findById(productId) } returns Optional.of(existingProduct)
                every { mockProductRepository.save(any()) } returns updatedProduct
                
                // When
                val result = productService.updateProduct(productId, request)
                
                // Then
                result.shouldBeInstanceOf<ProductResponse>()
                result.name shouldBe request.name
                result.description shouldBe request.description
                result.price shouldBe request.price
                result.stockQuantity shouldBe request.stockQuantity
                
                verify { mockProductRepository.findById(productId) }
                verify { mockProductRepository.save(any()) }
            }
            
            it("존재하지 않는 제품을 업데이트할 때 예외가 발생한다") {
                // Given
                val productId = 999L
                val request = ProductRequest(
                    name = "Updated Product",
                    price = BigDecimal("1000000"),
                    stockQuantity = 10
                )
                
                every { mockProductRepository.findById(productId) } returns Optional.empty()
                
                // When & Then
                shouldThrow<RuntimeException> {
                    productService.updateProduct(productId, request)
                }.message shouldBe "제품을 찾을 수 없습니다. ID: $productId"
                
                verify { mockProductRepository.findById(productId) }
                verify(exactly = 0) { mockProductRepository.save(any()) }
            }
        }
        
        context("deleteProduct") {
            it("존재하는 제품을 삭제한다") {
                // Given
                val productId = 1L
                val product = Product(
                    id = productId,
                    name = "MacBook Pro",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductRepository.findById(productId) } returns Optional.of(product)
                every { mockProductRepository.delete(product) } just Runs
                
                // When
                productService.deleteProduct(productId)
                
                // Then
                verify { mockProductRepository.findById(productId) }
                verify { mockProductRepository.delete(product) }
            }
            
            it("존재하지 않는 제품을 삭제할 때 예외가 발생한다") {
                // Given
                val productId = 999L
                every { mockProductRepository.findById(productId) } returns Optional.empty()
                
                // When & Then
                shouldThrow<RuntimeException> {
                    productService.deleteProduct(productId)
                }.message shouldBe "제품을 찾을 수 없습니다. ID: $productId"
                
                verify { mockProductRepository.findById(productId) }
                verify(exactly = 0) { mockProductRepository.delete(any()) }
            }
        }
        
        context("deactivateProduct") {
            it("존재하는 제품을 비활성화한다") {
                // Given
                val productId = 1L
                val product = Product(
                    id = productId,
                    name = "MacBook Pro",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                val deactivatedProduct = product.copy(
                    isActive = false,
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductRepository.findById(productId) } returns Optional.of(product)
                every { mockProductRepository.save(any()) } returns deactivatedProduct
                
                // When
                val result = productService.deactivateProduct(productId)
                
                // Then
                result.shouldBeInstanceOf<ProductResponse>()
                result.isActive shouldBe false
                
                verify { mockProductRepository.findById(productId) }
                verify { mockProductRepository.save(any()) }
            }
            
            it("존재하지 않는 제품을 비활성화할 때 예외가 발생한다") {
                // Given
                val productId = 999L
                every { mockProductRepository.findById(productId) } returns Optional.empty()
                
                // When & Then
                shouldThrow<RuntimeException> {
                    productService.deactivateProduct(productId)
                }.message shouldBe "제품을 찾을 수 없습니다. ID: $productId"
                
                verify { mockProductRepository.findById(productId) }
                verify(exactly = 0) { mockProductRepository.save(any()) }
            }
        }
    }
})
