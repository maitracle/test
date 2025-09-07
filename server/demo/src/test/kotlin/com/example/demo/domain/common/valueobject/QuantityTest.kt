package com.example.demo.domain.common.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class QuantityTest : FunSpec({
    
    context("Quantity 생성") {
        
        test("정상적인 수량으로 Quantity를 생성할 수 있어야 한다") {
            // Given & When
            val quantity = Quantity(5)
            
            // Then
            quantity.value shouldBe 5
        }
        
        test("1로 Quantity를 생성할 수 있어야 한다") {
            // Given & When
            val quantity = Quantity.one()
            
            // Then
            quantity.value shouldBe 1
        }
        
        test("0 이하의 수량으로 Quantity 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Quantity(0)
            }
            
            shouldThrow<IllegalArgumentException> {
                Quantity(-1)
            }
        }
        
        test("companion object의 of 메서드로 Quantity를 생성할 수 있어야 한다") {
            // Given & When
            val quantity1 = Quantity.of(5)
            val quantity2 = Quantity.of(5L)
            val quantity3 = Quantity.of("5")
            
            // Then
            quantity1.value shouldBe 5
            quantity2.value shouldBe 5
            quantity3.value shouldBe 5
        }
        
        test("safeOf 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val validQuantity = Quantity.safeOf(5)
            val invalidQuantity = Quantity.safeOf(0)
            val negativeQuantity = Quantity.safeOf(-1)
            
            // Then
            validQuantity shouldNotBe null
            validQuantity?.value shouldBe 5
            invalidQuantity shouldBe null
            negativeQuantity shouldBe null
        }
    }
    
    context("Quantity 연산") {
        
        test("수량 덧셈이 올바르게 계산되어야 한다") {
            // Given
            val quantity1 = Quantity(5)
            val quantity2 = Quantity(3)
            
            // When
            val result = quantity1 + quantity2
            
            // Then
            result.value shouldBe 8
        }
        
        test("수량 뺄셈이 올바르게 계산되어야 한다") {
            // Given
            val quantity1 = Quantity(10)
            val quantity2 = Quantity(3)
            
            // When
            val result = quantity1 - quantity2
            
            // Then
            result.value shouldBe 7
        }
        
        test("수량 곱셈이 올바르게 계산되어야 한다") {
            // Given
            val quantity = Quantity(5)
            
            // When
            val result = quantity * 3
            
            // Then
            result.value shouldBe 15
        }
        
        test("수량 비교가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(10)
            val quantity2 = Quantity(5)
            val quantity3 = Quantity(10)
            
            // When & Then
            (quantity1 > quantity2) shouldBe true
            (quantity2 < quantity1) shouldBe true
            (quantity1 == quantity3) shouldBe true
        }
    }
    
    context("Quantity 검증 메서드") {
        
        test("isGreaterThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(10)
            val quantity2 = Quantity(5)
            
            // When & Then
            quantity1.isGreaterThan(quantity2) shouldBe true
            quantity2.isGreaterThan(quantity1) shouldBe false
        }
        
        test("isLessThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(5)
            val quantity2 = Quantity(10)
            
            // When & Then
            quantity1.isLessThan(quantity2) shouldBe true
            quantity2.isLessThan(quantity1) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(10)
            val quantity2 = Quantity(10)
            val quantity3 = Quantity(5)
            
            // When & Then
            quantity1.isEqualTo(quantity2) shouldBe true
            quantity1.isEqualTo(quantity3) shouldBe false
        }
        
        test("isGreaterThanOrEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(10)
            val quantity2 = Quantity(10)
            val quantity3 = Quantity(5)
            
            // When & Then
            quantity1.isGreaterThanOrEqualTo(quantity2) shouldBe true
            quantity1.isGreaterThanOrEqualTo(quantity3) shouldBe true
            quantity3.isGreaterThanOrEqualTo(quantity1) shouldBe false
        }
        
        test("isLessThanOrEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity1 = Quantity(5)
            val quantity2 = Quantity(5)
            val quantity3 = Quantity(10)
            
            // When & Then
            quantity1.isLessThanOrEqualTo(quantity2) shouldBe true
            quantity1.isLessThanOrEqualTo(quantity3) shouldBe true
            quantity3.isLessThanOrEqualTo(quantity1) shouldBe false
        }
        
        test("isAtLeast 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity = Quantity(10)
            val minQuantity = Quantity(5)
            val maxQuantity = Quantity(15)
            
            // When & Then
            quantity.isAtLeast(minQuantity) shouldBe true
            quantity.isAtLeast(maxQuantity) shouldBe false
        }
        
        test("isAtMost 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity = Quantity(10)
            val minQuantity = Quantity(5)
            val maxQuantity = Quantity(15)
            
            // When & Then
            quantity.isAtMost(maxQuantity) shouldBe true
            quantity.isAtMost(minQuantity) shouldBe false
        }
    }
    
    context("Quantity 문자열 변환") {
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val quantity = Quantity(5)
            
            // When
            val stringValue = quantity.toString()
            
            // Then
            stringValue shouldBe "5"
        }
    }
})
