package com.example.demo.product.dto

import com.example.demo.product.Product
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductResponseTest : DescribeSpec({
    
    describe("ProductResponse DTO") {
        
        context("기본 생성자로 생성할 때") {
            it("모든 필드가 올바르게 설정된다") {
                // Given
                val id = 1L
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
                val response = ProductResponse(
                    id = id,
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
                response.id shouldBe id
                response.name shouldBe name
                response.description shouldBe description
                response.price shouldBe price
                response.stockQuantity shouldBe stockQuantity
                response.category shouldBe category
                response.brand shouldBe brand
                response.imageUrl shouldBe imageUrl
                response.isActive shouldBe isActive
                response.createdAt shouldBe createdAt
                response.updatedAt shouldBe updatedAt
            }
        }

        context("copy 메서드 사용할 때") {
            it("지정된 필드만 변경되고 나머지는 유지된다") {
                // Given
                val originalResponse = ProductResponse(
                    id = 1L,
                    name = "원본 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // When
                val copiedResponse = originalResponse.copy(
                    name = "수정된 제품",
                    price = BigDecimal("20000"),
                    isActive = false
                )

                // Then
                copiedResponse.name shouldBe "수정된 제품"
                copiedResponse.price shouldBe BigDecimal("20000")
                copiedResponse.stockQuantity shouldBe 10 // 원본 값 유지
                copiedResponse.isActive shouldBe false
                copiedResponse.createdAt shouldBe originalResponse.createdAt
                copiedResponse.updatedAt shouldBe originalResponse.updatedAt
            }
        }

        context("equals와 hashCode") {
            it("동일한 필드를 가진 객체는 같다고 판단된다") {
                // Given
                val response1 = ProductResponse(
                    id = 1L,
                    name = "테스트 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                val response2 = ProductResponse(
                    id = 1L,
                    name = "테스트 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = response1.createdAt,
                    updatedAt = response1.updatedAt
                )

                // When & Then
                response1 shouldBe response2
                response1.hashCode() shouldBe response2.hashCode()
            }

            it("다른 필드를 가진 객체는 다르다고 판단된다") {
                // Given
                val response1 = ProductResponse(
                    id = 1L,
                    name = "테스트 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                val response2 = ProductResponse(
                    id = 2L,
                    name = "다른 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // When & Then
                response1 shouldNotBe response2
                response1.hashCode() shouldNotBe response2.hashCode()
            }
        }

        context("toString") {
            it("제품 응답 정보가 포함된 문자열을 반환한다") {
                // Given
                val response = ProductResponse(
                    id = 1L,
                    name = "테스트 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // When
                val toString = response.toString()

                // Then
                toString shouldContain "테스트 제품"
                toString shouldContain "10000"
                toString shouldContain "10"
            }
        }

        context("from 정적 메서드") {
            it("Product 엔티티로부터 ProductResponse를 생성한다") {
                // Given
                val product = Product(
                    id = 1L,
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

                // When
                val response = ProductResponse.from(product)

                // Then
                response.shouldBeInstanceOf<ProductResponse>()
                response.id shouldBe product.id
                response.name shouldBe product.name
                response.description shouldBe product.description
                response.price shouldBe product.price
                response.stockQuantity shouldBe product.stockQuantity
                response.category shouldBe product.category
                response.brand shouldBe product.brand
                response.imageUrl shouldBe product.imageUrl
                response.isActive shouldBe product.isActive
                response.createdAt shouldBe product.createdAt
                response.updatedAt shouldBe product.updatedAt
            }

            it("null id를 가진 Product로부터 ProductResponse를 생성할 때 예외가 발생한다") {
                // Given
                val product = Product(
                    id = null,
                    name = "MacBook Pro",
                    price = BigDecimal("3500000"),
                    stockQuantity = 50,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // When & Then
                shouldThrow<NullPointerException> {
                    ProductResponse.from(product)
                }
            }

            it("모든 필드가 null인 Product로부터 ProductResponse를 생성한다") {
                // Given
                val product = Product(
                    id = 1L,
                    name = "테스트 제품",
                    description = null,
                    price = BigDecimal("10000"),
                    stockQuantity = 10,
                    category = null,
                    brand = null,
                    imageUrl = null,
                    isActive = true,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // When
                val response = ProductResponse.from(product)

                // Then
                response.id shouldBe 1L
                response.name shouldBe "테스트 제품"
                response.description.shouldBeNull()
                response.price shouldBe BigDecimal("10000")
                response.stockQuantity shouldBe 10
                response.category.shouldBeNull()
                response.brand.shouldBeNull()
                response.imageUrl.shouldBeNull()
                response.isActive shouldBe true
            }
        }
    }
})
