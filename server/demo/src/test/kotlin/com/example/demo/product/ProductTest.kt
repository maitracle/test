package com.example.demo.product

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductTest : DescribeSpec({
    
    describe("Product 엔티티") {
        
        context("기본 생성자로 생성할 때") {
            it("기본값이 올바르게 설정된다") {
                // Given
                val name = "테스트 제품"
                val price = BigDecimal("10000")
                val stockQuantity = 10

                // When
                val product = Product(
                    name = name,
                    price = price,
                    stockQuantity = stockQuantity
                )

                // Then
                product.id.shouldBeNull()
                product.name shouldBe name
                product.description.shouldBeNull()
                product.price shouldBe price
                product.stockQuantity shouldBe stockQuantity
                product.category.shouldBeNull()
                product.brand.shouldBeNull()
                product.imageUrl.shouldBeNull()
                product.isActive.shouldBeTrue()
                product.createdAt.shouldNotBeNull()
                product.updatedAt.shouldNotBeNull()
            }
        }

        context("모든 필드를 포함하여 생성할 때") {
            it("모든 필드가 올바르게 설정된다") {
                // Given
                val name = "MacBook Pro"
                val description = "Apple의 최신 노트북"
                val price = BigDecimal("3500000")
                val stockQuantity = 50
                val category = "노트북"
                val brand = "Apple"
                val imageUrl = "https://example.com/macbook.jpg"
                val isActive = true
                val createdAt = LocalDateTime.now()
                val updatedAt = LocalDateTime.now()

                // When
                val product = Product(
                    name = name,
                    description = description,
                    price = price,
                    stockQuantity = stockQuantity,
                    category = category,
                    brand = brand,
                    imageUrl = imageUrl,
                    isActive = isActive,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )

                // Then
                product.name shouldBe name
                product.description shouldBe description
                product.price shouldBe price
                product.stockQuantity shouldBe stockQuantity
                product.category shouldBe category
                product.brand shouldBe brand
                product.imageUrl shouldBe imageUrl
                product.isActive shouldBe isActive
                product.createdAt shouldBe createdAt
                product.updatedAt shouldBe updatedAt
            }
        }

        context("copy 메서드 사용할 때") {
            it("지정된 필드만 변경되고 나머지는 유지된다") {
                // Given
                val originalProduct = Product(
                    name = "원본 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When
                val copiedProduct = originalProduct.copy(
                    name = "수정된 제품",
                    price = BigDecimal("20000"),
                    isActive = false
                )

                // Then
                copiedProduct.name shouldBe "수정된 제품"
                copiedProduct.price shouldBe BigDecimal("20000")
                copiedProduct.stockQuantity shouldBe 10 // 원본 값 유지
                copiedProduct.isActive.shouldBeFalse()
                copiedProduct.createdAt shouldBe originalProduct.createdAt
                copiedProduct.updatedAt shouldBe originalProduct.updatedAt
            }
        }

        context("equals와 hashCode") {
            it("동일한 필드를 가진 객체는 같다고 판단된다") {
                // Given
                val now = LocalDateTime.now()
                val product1 = Product(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    createdAt = now,
                    updatedAt = now
                )
                val product2 = Product(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    createdAt = now,
                    updatedAt = now
                )

                // When & Then
                product1 shouldBe product2
                product1.hashCode() shouldBe product2.hashCode()
            }

            it("다른 필드를 가진 객체는 다르다고 판단된다") {
                // Given
                val product1 = Product(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )
                val product2 = Product(
                    name = "다른 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When & Then
                product1 shouldNotBe product2
                product1.hashCode() shouldNotBe product2.hashCode()
            }
        }

        context("toString") {
            it("제품 정보가 포함된 문자열을 반환한다") {
                // Given
                val product = Product(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When
                val toString = product.toString()

                // Then
                toString shouldContain "테스트 제품"
                toString shouldContain "10000"
                toString shouldContain "10"
            }
        }
    }
})
