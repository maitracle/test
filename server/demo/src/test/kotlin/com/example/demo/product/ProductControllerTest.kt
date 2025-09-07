package com.example.demo.product

import com.example.demo.product.dto.ProductRequest
import com.example.demo.product.dto.ProductResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductControllerTest : DescribeSpec({
    
    val mockProductService = mockk<ProductService>()
    val productController = ProductController(mockProductService)
    val mockMvc = MockMvcBuilders.standaloneSetup(productController).build()
    val objectMapper = ObjectMapper()
    
    beforeEach {
        clearAllMocks()
    }
    
    describe("ProductController") {
        
        context("POST /api/products") {
            it("새로운 제품을 생성한다") {
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
                
                val response = ProductResponse(
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
                
                every { mockProductService.createProduct(request) } returns response
                
                // When & Then
                mockMvc.perform(
                    post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("MacBook Pro"))
                    .andExpect(jsonPath("$.price").value(3500000))
                    .andExpect(jsonPath("$.stockQuantity").value(50))
                    .andExpect(jsonPath("$.category").value("노트북"))
                    .andExpect(jsonPath("$.brand").value("Apple"))
                    .andExpect(jsonPath("$.isActive").value(true))
                
                verify { mockProductService.createProduct(request) }
            }
        }
        
        context("GET /api/products/{id}") {
            it("특정 제품을 조회한다") {
                // Given
                val productId = 1L
                val response = ProductResponse(
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
                
                every { mockProductService.getProduct(productId) } returns response
                
                // When & Then
                mockMvc.perform(get("/api/products/$productId"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(productId))
                    .andExpect(jsonPath("$.name").value("MacBook Pro"))
                    .andExpect(jsonPath("$.price").value(3500000))
                
                verify { mockProductService.getProduct(productId) }
            }
        }
        
        context("GET /api/products") {
            it("모든 제품을 조회한다") {
                // Given
                val responses = listOf(
                    ProductResponse(
                        id = 1L,
                        name = "MacBook Pro",
                        description = null,
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        category = null,
                        brand = null,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    ),
                    ProductResponse(
                        id = 2L,
                        name = "iPhone 15",
                        description = null,
                        price = BigDecimal("1500000"),
                        stockQuantity = 100,
                        category = null,
                        brand = null,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductService.getAllProducts() } returns responses
                
                // When & Then
                mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[0].name").value("MacBook Pro"))
                    .andExpect(jsonPath("$[1].name").value("iPhone 15"))
                
                verify { mockProductService.getAllProducts() }
            }
        }
        
        context("GET /api/products/category/{category}") {
            it("특정 카테고리의 제품들을 조회한다") {
                // Given
                val category = "노트북"
                val responses = listOf(
                    ProductResponse(
                        id = 1L,
                        name = "MacBook Pro",
                        description = null,
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        category = category,
                        brand = null,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductService.getProductsByCategory(category) } returns responses
                
                // When & Then
                mockMvc.perform(get("/api/products/category/$category"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[0].category").value(category))
                
                verify { mockProductService.getProductsByCategory(category) }
            }
        }
        
        context("GET /api/products/brand/{brand}") {
            it("특정 브랜드의 제품들을 조회한다") {
                // Given
                val brand = "Apple"
                val responses = listOf(
                    ProductResponse(
                        id = 1L,
                        name = "MacBook Pro",
                        description = null,
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        category = null,
                        brand = brand,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductService.getProductsByBrand(brand) } returns responses
                
                // When & Then
                mockMvc.perform(get("/api/products/brand/$brand"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[0].brand").value(brand))
                
                verify { mockProductService.getProductsByBrand(brand) }
            }
        }
        
        context("GET /api/products/search") {
            it("키워드로 제품을 검색한다") {
                // Given
                val keyword = "MacBook"
                val responses = listOf(
                    ProductResponse(
                        id = 1L,
                        name = "MacBook Pro",
                        description = "Apple MacBook Pro",
                        price = BigDecimal("3500000"),
                        stockQuantity = 50,
                        category = null,
                        brand = null,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductService.searchProducts(keyword) } returns responses
                
                // When & Then
                mockMvc.perform(get("/api/products/search").param("keyword", keyword))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[0].name").value("MacBook Pro"))
                
                verify { mockProductService.searchProducts(keyword) }
            }
        }
        
        context("GET /api/products/price-range") {
            it("가격 범위로 제품을 조회한다") {
                // Given
                val minPrice = BigDecimal("1000000")
                val maxPrice = BigDecimal("2000000")
                val responses = listOf(
                    ProductResponse(
                        id = 1L,
                        name = "iPhone 15",
                        description = null,
                        price = BigDecimal("1500000"),
                        stockQuantity = 100,
                        category = null,
                        brand = null,
                        imageUrl = null,
                        isActive = true,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                
                every { mockProductService.getProductsByPriceRange(minPrice, maxPrice) } returns responses
                
                // When & Then
                mockMvc.perform(
                    get("/api/products/price-range")
                        .param("minPrice", "1000000")
                        .param("maxPrice", "2000000")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[0].price").value(1500000))
                
                verify { mockProductService.getProductsByPriceRange(minPrice, maxPrice) }
            }
        }
        
        context("PUT /api/products/{id}") {
            it("제품을 업데이트한다") {
                // Given
                val productId = 1L
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
                
                val response = ProductResponse(
                    id = productId,
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
                
                every { mockProductService.updateProduct(productId, request) } returns response
                
                // When & Then
                mockMvc.perform(
                    put("/api/products/$productId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(productId))
                    .andExpect(jsonPath("$.name").value("MacBook Pro Updated"))
                    .andExpect(jsonPath("$.price").value(4000000))
                
                verify { mockProductService.updateProduct(productId, request) }
            }
        }
        
        context("DELETE /api/products/{id}") {
            it("제품을 삭제한다") {
                // Given
                val productId = 1L
                every { mockProductService.deleteProduct(productId) } just Runs
                
                // When & Then
                mockMvc.perform(delete("/api/products/$productId"))
                    .andExpect(status().isNoContent)
                
                verify { mockProductService.deleteProduct(productId) }
            }
        }
        
        context("PATCH /api/products/{id}/deactivate") {
            it("제품을 비활성화한다") {
                // Given
                val productId = 1L
                val response = ProductResponse(
                    id = productId,
                    name = "MacBook Pro",
                    description = null,
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = false,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                
                every { mockProductService.deactivateProduct(productId) } returns response
                
                // When & Then
                mockMvc.perform(patch("/api/products/$productId/deactivate"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(productId))
                    .andExpect(jsonPath("$.isActive").value(false))
                
                verify { mockProductService.deactivateProduct(productId) }
            }
        }
    }
})
