package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.common.valueobject.Money
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import java.math.BigDecimal

class DiscountPercentageTest : DescribeSpec({
    
    describe("DiscountPercentage 생성") {
        it("유효한 BigDecimal로 DiscountPercentage를 생성할 수 있어야 한다") {
            // Given
            val percentage = BigDecimal.valueOf(10)
            
            // When
            val discountPercentage = DiscountPercentage.of(percentage)
            
            // Then
            discountPercentage.value shouldBe percentage
        }
        
        it("Double로 DiscountPercentage를 생성할 수 있어야 한다") {
            // Given
            val percentage = 10.5
            
            // When
            val discountPercentage = DiscountPercentage.of(percentage)
            
            // Then
            discountPercentage.value shouldBe BigDecimal.valueOf(percentage)
        }
        
        it("Int로 DiscountPercentage를 생성할 수 있어야 한다") {
            // Given
            val percentage = 10
            
            // When
            val discountPercentage = DiscountPercentage.of(percentage)
            
            // Then
            discountPercentage.value shouldBe BigDecimal.valueOf(percentage.toLong())
        }
        
        it("0% DiscountPercentage를 생성할 수 있어야 한다") {
            // Given & When
            val discountPercentage = DiscountPercentage.zero()
            
            // Then
            discountPercentage.value shouldBe BigDecimal.ZERO
        }
        
        it("100% DiscountPercentage를 생성할 수 있어야 한다") {
            // Given & When
            val discountPercentage = DiscountPercentage.full()
            
            // Then
            discountPercentage.value shouldBe BigDecimal.valueOf(100)
        }
        
        it("범위를 벗어난 값으로 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                DiscountPercentage.of(BigDecimal.valueOf(-1))
            }
            
            shouldThrow<IllegalArgumentException> {
                DiscountPercentage.of(BigDecimal.valueOf(101))
            }
        }
    }
    
    describe("DiscountPercentage 안전 생성") {
        it("유효한 값으로 안전하게 생성할 수 있어야 한다") {
            // Given
            val percentage = BigDecimal.valueOf(10)
            
            // When
            val discountPercentage = DiscountPercentage.safeOf(percentage)
            
            // Then
            discountPercentage shouldNotBe null
            discountPercentage?.value shouldBe percentage
        }
        
        it("null 값으로 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val discountPercentage = DiscountPercentage.safeOf(null as BigDecimal?)
            
            // Then
            discountPercentage shouldBe null
        }
        
        it("범위를 벗어난 값으로 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val discountPercentage1 = DiscountPercentage.safeOf(BigDecimal.valueOf(-1))
            val discountPercentage2 = DiscountPercentage.safeOf(BigDecimal.valueOf(101))
            
            // Then
            discountPercentage1 shouldBe null
            discountPercentage2 shouldBe null
        }
        
        it("Double로 안전하게 생성할 수 있어야 한다") {
            // Given
            val percentage = 10.5
            
            // When
            val discountPercentage = DiscountPercentage.safeOf(percentage)
            
            // Then
            discountPercentage shouldNotBe null
            discountPercentage?.value shouldBe BigDecimal.valueOf(percentage)
        }
        
        it("범위를 벗어난 Double로 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val discountPercentage1 = DiscountPercentage.safeOf(-1.0)
            val discountPercentage2 = DiscountPercentage.safeOf(101.0)
            
            // Then
            discountPercentage1 shouldBe null
            discountPercentage2 shouldBe null
        }
    }
    
    describe("DiscountPercentage 연산") {
        it("Money에 할인 퍼센트를 적용할 수 있어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.of(10)
            val money = Money.of(10000L)
            
            // When
            val result = discountPercentage * money
            
            // Then
            result.amount shouldBe BigDecimal("1000.00")
        }
        
        it("0% 할인을 적용하면 0원이어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.zero()
            val money = Money.of(10000L)
            
            // When
            val result = discountPercentage * money
            
            // Then
            result.amount shouldBe BigDecimal("0.00")
        }
        
        it("100% 할인을 적용하면 전체 금액이어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.full()
            val money = Money.of(10000L)
            
            // When
            val result = discountPercentage * money
            
            // Then
            result.amount shouldBe BigDecimal("10000.00")
        }
        
        it("할인 퍼센트를 비교할 수 있어야 한다") {
            // Given
            val discountPercentage1 = DiscountPercentage.of(10)
            val discountPercentage2 = DiscountPercentage.of(20)
            
            // When & Then
            (discountPercentage1 < discountPercentage2) shouldBe true
            (discountPercentage1 > discountPercentage2) shouldBe false
            (discountPercentage1 == discountPercentage2) shouldBe false
        }
    }
    
    describe("DiscountPercentage 상태 확인") {
        it("할인이 적용되었는지 확인할 수 있어야 한다") {
            // Given
            val appliedPercentage = DiscountPercentage.of(10)
            val zeroPercentage = DiscountPercentage.zero()
            
            // When & Then
            appliedPercentage.isApplied() shouldBe true
            zeroPercentage.isApplied() shouldBe false
        }
        
        it("할인이 0%인지 확인할 수 있어야 한다") {
            // Given
            val appliedPercentage = DiscountPercentage.of(10)
            val zeroPercentage = DiscountPercentage.zero()
            
            // When & Then
            appliedPercentage.isZero() shouldBe false
            zeroPercentage.isZero() shouldBe true
        }
        
        it("Double로 변환할 수 있어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.of(10.5)
            
            // When
            val value = discountPercentage.toDouble()
            
            // Then
            value shouldBe 10.5
        }
        
        it("표시용 문자열을 생성할 수 있어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.of(10)
            
            // When
            val displayString = discountPercentage.toDisplayString()
            
            // Then
            displayString shouldBe "10%"
        }
    }
    
    describe("DiscountPercentage 문자열 표현") {
        it("toString은 퍼센트 형식이어야 한다") {
            // Given
            val discountPercentage = DiscountPercentage.of(10)
            
            // When
            val stringRepresentation = discountPercentage.toString()
            
            // Then
            stringRepresentation shouldBe "10%"
        }
    }
})
