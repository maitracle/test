package com.example.demo.domain.promotion.valueobject

import com.example.demo.domain.common.valueobject.Money
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import java.math.BigDecimal

class DiscountTest : DescribeSpec({
    
    describe("Discount 생성") {
        it("유효한 Money로 Discount를 생성할 수 있어야 한다") {
            // Given
            val money = Money.of(1000L)
            
            // When
            val discount = Discount.of(money)
            
            // Then
            discount.amount shouldBe money
        }
        
        it("BigDecimal로 Discount를 생성할 수 있어야 한다") {
            // Given
            val amount = BigDecimal.valueOf(1000)
            
            // When
            val discount = Discount.of(amount)
            
            // Then
            discount.amount.amount shouldBe amount
        }
        
        it("Long으로 Discount를 생성할 수 있어야 한다") {
            // Given
            val amount = 1000L
            
            // When
            val discount = Discount.of(amount)
            
            // Then
            discount.amount.amount shouldBe BigDecimal.valueOf(amount)
        }
        
        it("음수 금액으로 Discount를 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Discount.of(Money.of(-1000L))
            }
        }
        
        it("0원 Discount를 생성할 수 있어야 한다") {
            // Given & When
            val discount = Discount.zero()
            
            // Then
            discount.amount.amount shouldBe BigDecimal.ZERO
        }
    }
    
    describe("Discount 안전 생성") {
        it("유효한 값으로 안전하게 생성할 수 있어야 한다") {
            // Given
            val amount = BigDecimal.valueOf(1000)
            
            // When
            val discount = Discount.safeOf(amount)
            
            // Then
            discount.amount.amount shouldBe amount
        }
        
        it("null 값으로 안전하게 생성하면 0원 할인을 반환해야 한다") {
            // Given & When
            val discount = Discount.safeOf(null)
            
            // Then
            discount.amount.amount shouldBe BigDecimal.ZERO
        }
        
        it("음수 값으로 안전하게 생성하면 0원 할인을 반환해야 한다") {
            // Given & When
            val discount = Discount.safeOf(BigDecimal.valueOf(-1000))
            
            // Then
            discount.amount.amount shouldBe BigDecimal.ZERO
        }
    }
    
    describe("Discount 연산") {
        it("두 할인을 더할 수 있어야 한다") {
            // Given
            val discount1 = Discount.of(1000L)
            val discount2 = Discount.of(2000L)
            
            // When
            val result = discount1 + discount2
            
            // Then
            result.amount.amount shouldBe BigDecimal.valueOf(3000)
        }
        
        it("두 할인을 뺄 수 있어야 한다") {
            // Given
            val discount1 = Discount.of(3000L)
            val discount2 = Discount.of(1000L)
            
            // When
            val result = discount1 - discount2
            
            // Then
            result.amount.amount shouldBe BigDecimal.valueOf(2000)
        }
        
        it("할인을 비교할 수 있어야 한다") {
            // Given
            val discount1 = Discount.of(1000L)
            val discount2 = Discount.of(2000L)
            
            // When & Then
            (discount1 < discount2) shouldBe true
            (discount1 > discount2) shouldBe false
            (discount1 == discount2) shouldBe false
        }
    }
    
    describe("Discount 상태 확인") {
        it("할인이 적용되었는지 확인할 수 있어야 한다") {
            // Given
            val appliedDiscount = Discount.of(1000L)
            val zeroDiscount = Discount.zero()
            
            // When & Then
            appliedDiscount.isApplied() shouldBe true
            zeroDiscount.isApplied() shouldBe false
        }
        
        it("할인이 0원인지 확인할 수 있어야 한다") {
            // Given
            val appliedDiscount = Discount.of(1000L)
            val zeroDiscount = Discount.zero()
            
            // When & Then
            appliedDiscount.isZero() shouldBe false
            zeroDiscount.isZero() shouldBe true
        }
        
        it("BigDecimal로 변환할 수 있어야 한다") {
            // Given
            val discount = Discount.of(1000L)
            
            // When
            val amount = discount.toBigDecimal()
            
            // Then
            amount shouldBe BigDecimal.valueOf(1000)
        }
    }
    
    describe("Discount 문자열 표현") {
        it("toString은 적절한 형식이어야 한다") {
            // Given
            val discount = Discount.of(1000L)
            
            // When
            val stringRepresentation = discount.toString()
            
            // Then
            stringRepresentation shouldBe "Discount(1000)"
        }
    }
})
