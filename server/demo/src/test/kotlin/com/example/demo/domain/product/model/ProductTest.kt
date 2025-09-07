package com.example.demo.domain.product.model

import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.valueobject.Price
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductTest : FunSpec({
    
    context("Product 생성") {
        
        test("새로운 Product를 생성할 수 있어야 한다") {
            // Given
            val id = ProductId.of(1L)
            val name = "테스트 상품"
            val price = Price.of(BigDecimal("10000"))
            val stock = Stock(10)
            val now = LocalDateTime.now()
            
            // When
            val product = Product.createNew(
                id = id,
                name = name,
                price = price,
                stock = stock
            )
            
            // Then
            product.id shouldBe id
            product.name shouldBe name
            product.description shouldBe null
            product.price shouldBe price
            product.stock shouldBe stock
            product.category shouldBe null
            product.brand shouldBe null
            product.imageUrl shouldBe null
            product.isActive shouldBe true
            product.createdAt shouldNotBe null
            product.updatedAt shouldNotBe null
        }
        
        test("모든 필드를 포함한 Product를 생성할 수 있어야 한다") {
            // Given
            val id = ProductId.of(1L)
            val name = "MacBook Pro"
            val description = "Apple의 최신 노트북"
            val price = Price.of(BigDecimal("3500000"))
            val stock = Stock(50)
            val category = "노트북"
            val brand = "Apple"
            val imageUrl = "https://example.com/macbook.jpg"
            val createdAt = LocalDateTime.now()
            val updatedAt = LocalDateTime.now()
            
            // When
            val product = Product.createExisting(
                id = id,
                name = name,
                description = description,
                price = price,
                stock = stock,
                category = category,
                brand = brand,
                imageUrl = imageUrl,
                isActive = true,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            
            // Then
            product.id shouldBe id
            product.name shouldBe name
            product.description shouldBe description
            product.price shouldBe price
            product.stock shouldBe stock
            product.category shouldBe category
            product.brand shouldBe brand
            product.imageUrl shouldBe imageUrl
            product.isActive shouldBe true
            product.createdAt shouldBe createdAt
            product.updatedAt shouldBe updatedAt
        }
        
        test("빈 상품명으로 Product를 생성하면 예외가 발생해야 한다") {
            // Given
            val id = ProductId.of(1L)
            val name = ""
            val price = Price.of(BigDecimal("10000"))
            val stock = Stock(10)
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                Product.createNew(id = id, name = name, price = price, stock = stock)
            }
            exception.message shouldContain "Product name cannot be blank"
        }
        
        test("너무 긴 상품명으로 Product를 생성하면 예외가 발생해야 한다") {
            // Given
            val id = ProductId.of(1L)
            val name = "a".repeat(101)
            val price = Price.of(BigDecimal("10000"))
            val stock = Stock(10)
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                Product.createNew(id = id, name = name, price = price, stock = stock)
            }
            exception.message shouldContain "Product name cannot exceed 100 characters"
        }
    }
    
    context("상품 가용성 검증") {
        
        test("활성화된 상품이 충분한 재고를 가지고 있으면 구매 가능해야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val quantity = Quantity(5)
            
            // When
            val isAvailable = product.isAvailable(quantity)
            
            // Then
            isAvailable shouldBe true
        }
        
        test("비활성화된 상품은 구매 불가능해야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            ).deactivate()
            val quantity = Quantity(5)
            
            // When
            val isAvailable = product.isAvailable(quantity)
            
            // Then
            isAvailable shouldBe false
        }
        
        test("재고가 부족한 상품은 구매 불가능해야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(3)
            )
            val quantity = Quantity(5)
            
            // When
            val isAvailable = product.isAvailable(quantity)
            
            // Then
            isAvailable shouldBe false
        }
        
        test("재고가 없는 상품은 구매 불가능해야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(0)
            )
            val quantity = Quantity(1)
            
            // When
            val isAvailable = product.isAvailable(quantity)
            
            // Then
            isAvailable shouldBe false
        }
    }
    
    context("재고 관리") {
        
        test("재고를 차감할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val quantity = Quantity(3)
            
            // When
            val updatedProduct = product.reduceStock(quantity)
            
            // Then
            updatedProduct.stock.quantity shouldBe 7
            updatedProduct.updatedAt shouldNotBe product.updatedAt
        }
        
        test("재고가 부족할 때 차감하면 예외가 발생해야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(3)
            )
            val quantity = Quantity(5)
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                product.reduceStock(quantity)
            }
            exception.message shouldContain "Insufficient stock"
        }
        
        test("재고를 증가시킬 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val quantity = Quantity(5)
            
            // When
            val updatedProduct = product.increaseStock(quantity)
            
            // Then
            updatedProduct.stock.quantity shouldBe 15
            updatedProduct.updatedAt shouldNotBe product.updatedAt
        }
    }
    
    context("상품 상태 관리") {
        
        test("상품을 활성화할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            ).deactivate()
            
            // When
            val activatedProduct = product.activate()
            
            // Then
            activatedProduct.isActive shouldBe true
            activatedProduct.updatedAt shouldNotBe product.updatedAt
        }
        
        test("상품을 비활성화할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            
            // When
            val deactivatedProduct = product.deactivate()
            
            // Then
            deactivatedProduct.isActive shouldBe false
            deactivatedProduct.updatedAt shouldNotBe product.updatedAt
        }
        
        test("상품이 활성화되어 있는지 확인할 수 있어야 한다") {
            // Given
            val activeProduct = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val inactiveProduct = activeProduct.deactivate()
            
            // When & Then
            activeProduct.isProductActive() shouldBe true
            inactiveProduct.isProductActive() shouldBe false
        }
        
        test("상품이 재고를 가지고 있는지 확인할 수 있어야 한다") {
            // Given
            val productWithStock = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val productWithoutStock = Product.createNew(
                id = ProductId.of(2L),
                name = "테스트 상품2",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(0)
            )
            
            // When & Then
            productWithStock.hasStock() shouldBe true
            productWithoutStock.hasStock() shouldBe false
        }
    }
    
    context("가격 계산") {
        
        test("상품의 총 가격을 계산할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            val quantity = Quantity(3)
            
            // When
            val totalPrice = product.calculateTotalPrice(quantity)
            
            // Then
            totalPrice.amount.amount shouldBe BigDecimal("30000")
        }
    }
    
    context("상품 정보 업데이트") {
        
        test("상품 정보를 업데이트할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "원본 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            
            // When
            val updatedProduct = product.updateInfo(
                name = "업데이트된 상품",
                description = "새로운 설명",
                price = Price.of(BigDecimal("15000")),
                category = "전자제품",
                brand = "테스트 브랜드"
            )
            
            // Then
            updatedProduct.name shouldBe "업데이트된 상품"
            updatedProduct.description shouldBe "새로운 설명"
            updatedProduct.price.amount.amount shouldBe BigDecimal("15000")
            updatedProduct.category shouldBe "전자제품"
            updatedProduct.brand shouldBe "테스트 브랜드"
            updatedProduct.updatedAt shouldNotBe product.updatedAt
        }
        
        test("일부 필드만 업데이트할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "원본 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            
            // When
            val updatedProduct = product.updateInfo(name = "새로운 이름")
            
            // Then
            updatedProduct.name shouldBe "새로운 이름"
            updatedProduct.price shouldBe product.price
            updatedProduct.stock shouldBe product.stock
            updatedProduct.updatedAt shouldNotBe product.updatedAt
        }
    }
    
    context("카테고리 및 브랜드 확인") {
        
        test("상품이 특정 카테고리에 속하는지 확인할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10),
                category = "전자제품"
            )
            
            // When & Then
            product.belongsToCategory("전자제품") shouldBe true
            product.belongsToCategory("의류") shouldBe false
            product.belongsToCategory("전자제품") shouldBe true // 대소문자 구분 없음
        }
        
        test("상품이 특정 브랜드에 속하는지 확인할 수 있어야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10),
                brand = "Apple"
            )
            
            // When & Then
            product.belongsToBrand("Apple") shouldBe true
            product.belongsToBrand("Samsung") shouldBe false
            product.belongsToBrand("apple") shouldBe true // 대소문자 구분 없음
        }
        
        test("카테고리가 없는 상품은 어떤 카테고리에도 속하지 않아야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            
            // When & Then
            product.belongsToCategory("전자제품") shouldBe false
        }
        
        test("브랜드가 없는 상품은 어떤 브랜드에도 속하지 않아야 한다") {
            // Given
            val product = Product.createNew(
                id = ProductId.of(1L),
                name = "테스트 상품",
                price = Price.of(BigDecimal("10000")),
                stock = Stock(10)
            )
            
            // When & Then
            product.belongsToBrand("Apple") shouldBe false
        }
    }
})
