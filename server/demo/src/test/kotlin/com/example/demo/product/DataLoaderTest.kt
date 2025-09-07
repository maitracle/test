package com.example.demo.product

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldContain
import io.mockk.*
import java.math.BigDecimal

class DataLoaderTest : DescribeSpec({
    
    val mockProductRepository = mockk<ProductRepository>()
    val dataLoader = DataLoader(mockProductRepository)
    
    beforeEach {
        clearAllMocks()
    }
    
    describe("DataLoader") {
        
        context("run 메서드") {
            it("기존 데이터가 없을 때 샘플 데이터를 생성한다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                verify { mockProductRepository.count() }
                verify { mockProductRepository.saveAll(any<List<Product>>()) }
            }
            
            it("기존 데이터가 있을 때는 샘플 데이터를 생성하지 않는다") {
                // Given
                every { mockProductRepository.count() } returns 5L
                
                // When
                dataLoader.run()
                
                // Then
                verify { mockProductRepository.count() }
                verify(exactly = 0) { mockProductRepository.saveAll(any<List<Product>>()) }
            }
            
            it("생성되는 샘플 데이터가 올바른 구조를 가진다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                verify { mockProductRepository.saveAll(any<List<Product>>()) }
                
                // 샘플 데이터 검증을 위해 capture된 데이터 확인
                val capturedProducts = slot<List<Product>>()
                verify { mockProductRepository.saveAll(capture(capturedProducts)) }
                
                val products = capturedProducts.captured
                products.shouldHaveSize(5)
                
                // MacBook Pro 검증
                val macbook = products.find { it.name == "MacBook Pro 16인치" }
                macbook.shouldNotBe(null)
                macbook?.let {
                    it.name shouldBe "MacBook Pro 16인치"
                    it.description shouldBe "Apple M3 Pro 칩을 탑재한 최신 MacBook Pro"
                    it.price shouldBe BigDecimal("3500000")
                    it.stockQuantity shouldBe 50
                    it.category shouldBe "노트북"
                    it.brand shouldBe "Apple"
                    it.imageUrl shouldBe "https://example.com/macbook-pro.jpg"
                    it.isActive shouldBe true
                }
                
                // iPhone 15 Pro 검증
                val iphone = products.find { it.name == "iPhone 15 Pro" }
                iphone.shouldNotBe(null)
                iphone?.let {
                    it.name shouldBe "iPhone 15 Pro"
                    it.description shouldBe "A17 Pro 칩과 48MP 카메라를 탑재한 프리미엄 스마트폰"
                    it.price shouldBe BigDecimal("1500000")
                    it.stockQuantity shouldBe 100
                    it.category shouldBe "스마트폰"
                    it.brand shouldBe "Apple"
                    it.imageUrl shouldBe "https://example.com/iphone15-pro.jpg"
                    it.isActive shouldBe true
                }
                
                // Samsung Galaxy S24 Ultra 검증
                val galaxy = products.find { it.name == "Samsung Galaxy S24 Ultra" }
                galaxy.shouldNotBe(null)
                galaxy?.let {
                    it.name shouldBe "Samsung Galaxy S24 Ultra"
                    it.description shouldBe "S Pen과 200MP 카메라를 탑재한 안드로이드 플래그십"
                    it.price shouldBe BigDecimal("1800000")
                    it.stockQuantity shouldBe 75
                    it.category shouldBe "스마트폰"
                    it.brand shouldBe "Samsung"
                    it.imageUrl shouldBe "https://example.com/galaxy-s24-ultra.jpg"
                    it.isActive shouldBe true
                }
                
                // LG OLED TV 검증
                val tv = products.find { it.name == "LG OLED TV 65인치" }
                tv.shouldNotBe(null)
                tv?.let {
                    it.name shouldBe "LG OLED TV 65인치"
                    it.description shouldBe "4K 해상도와 완벽한 블랙을 제공하는 OLED TV"
                    it.price shouldBe BigDecimal("2800000")
                    it.stockQuantity shouldBe 30
                    it.category shouldBe "TV"
                    it.brand shouldBe "LG"
                    it.imageUrl shouldBe "https://example.com/lg-oled-tv.jpg"
                    it.isActive shouldBe true
                }
                
                // Sony WH-1000XM5 검증
                val headphone = products.find { it.name == "Sony WH-1000XM5" }
                headphone.shouldNotBe(null)
                headphone?.let {
                    it.name shouldBe "Sony WH-1000XM5"
                    it.description shouldBe "업계 최고 수준의 노이즈 캔슬링 헤드폰"
                    it.price shouldBe BigDecimal("450000")
                    it.stockQuantity shouldBe 200
                    it.category shouldBe "오디오"
                    it.brand shouldBe "Sony"
                    it.imageUrl shouldBe "https://example.com/sony-wh1000xm5.jpg"
                    it.isActive shouldBe true
                }
            }
            
            it("모든 샘플 제품이 활성화 상태로 생성된다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                val capturedProducts = slot<List<Product>>()
                verify { mockProductRepository.saveAll(capture(capturedProducts)) }
                
                val products = capturedProducts.captured
                products.forEach { product ->
                    product.isActive shouldBe true
                }
            }
            
            it("모든 샘플 제품이 고유한 이름을 가진다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                val capturedProducts = slot<List<Product>>()
                verify { mockProductRepository.saveAll(capture(capturedProducts)) }
                
                val products = capturedProducts.captured
                val productNames = products.map { it.name }
                productNames.shouldHaveSize(5)
                
                // 중복 이름이 없는지 확인
                productNames.distinct().shouldHaveSize(5)
            }
            
            it("모든 샘플 제품이 올바른 카테고리를 가진다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                val capturedProducts = slot<List<Product>>()
                verify { mockProductRepository.saveAll(capture(capturedProducts)) }
                
                val products = capturedProducts.captured
                val categories = products.mapNotNull { it.category }
                
                categories.shouldContain("노트북")
                categories.shouldContain("스마트폰")
                categories.shouldContain("TV")
                categories.shouldContain("오디오")
            }
            
            it("모든 샘플 제품이 올바른 브랜드를 가진다") {
                // Given
                every { mockProductRepository.count() } returns 0L
                every { mockProductRepository.saveAll(any<List<Product>>()) } returns listOf()
                
                // When
                dataLoader.run()
                
                // Then
                val capturedProducts = slot<List<Product>>()
                verify { mockProductRepository.saveAll(capture(capturedProducts)) }
                
                val products = capturedProducts.captured
                val brands = products.mapNotNull { it.brand }
                
                brands.shouldContain("Apple")
                brands.shouldContain("Samsung")
                brands.shouldContain("LG")
                brands.shouldContain("Sony")
            }
        }
    }
})
