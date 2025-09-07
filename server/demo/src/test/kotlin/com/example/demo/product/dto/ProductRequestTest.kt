package com.example.demo.product.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal

class ProductRequestTest : DescribeSpec({
    
    describe("ProductRequest DTO") {
        
        context("기본 생성자로 생성할 때") {
            it("기본값이 올바르게 설정된다") {
                // Given
                val name = "테스트 제품"
                val price = BigDecimal("10000")
                val stockQuantity = 10

                // When
                val request = ProductRequest(
                    name = name,
                    price = price,
                    stockQuantity = stockQuantity
                )

                // Then
                request.name shouldBe name
                request.description.shouldBeNull()
                request.price shouldBe price
                request.stockQuantity shouldBe stockQuantity
                request.category.shouldBeNull()
                request.brand.shouldBeNull()
                request.imageUrl.shouldBeNull()
                request.isActive.shouldBeTrue()
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
                val isActive = false

                // When
                val request = ProductRequest(
                    name = name,
                    description = description,
                    price = price,
                    stockQuantity = stockQuantity,
                    category = category,
                    brand = brand,
                    imageUrl = imageUrl,
                    isActive = isActive
                )

                // Then
                request.name shouldBe name
                request.description shouldBe description
                request.price shouldBe price
                request.stockQuantity shouldBe stockQuantity
                request.category shouldBe category
                request.brand shouldBe brand
                request.imageUrl shouldBe imageUrl
                request.isActive shouldBe isActive
            }
        }

        context("copy 메서드 사용할 때") {
            it("지정된 필드만 변경되고 나머지는 유지된다") {
                // Given
                val originalRequest = ProductRequest(
                    name = "원본 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When
                val copiedRequest = originalRequest.copy(
                    name = "수정된 제품",
                    price = BigDecimal("20000"),
                    isActive = false
                )

                // Then
                copiedRequest.name shouldBe "수정된 제품"
                copiedRequest.price shouldBe BigDecimal("20000")
                copiedRequest.stockQuantity shouldBe 10 // 원본 값 유지
                copiedRequest.isActive shouldBe false
            }
        }

        context("equals와 hashCode") {
            it("동일한 필드를 가진 객체는 같다고 판단된다") {
                // Given
                val request1 = ProductRequest(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )
                val request2 = ProductRequest(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When & Then
                request1 shouldBe request2
                request1.hashCode() shouldBe request2.hashCode()
            }

            it("다른 필드를 가진 객체는 다르다고 판단된다") {
                // Given
                val request1 = ProductRequest(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )
                val request2 = ProductRequest(
                    name = "다른 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When & Then
                request1 shouldNotBe request2
                request1.hashCode() shouldNotBe request2.hashCode()
            }
        }

        context("toString") {
            it("제품 요청 정보가 포함된 문자열을 반환한다") {
                // Given
                val request = ProductRequest(
                    name = "테스트 제품",
                    price = BigDecimal("10000"),
                    stockQuantity = 10
                )

                // When
                val toString = request.toString()

                // Then
                toString shouldContain "테스트 제품"
                toString shouldContain "10000"
                toString shouldContain "10"
            }
        }
    }
})
