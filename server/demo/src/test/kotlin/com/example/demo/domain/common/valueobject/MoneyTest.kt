package com.example.demo.domain.common.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import java.math.BigDecimal

class MoneyTest : FunSpec({
    
    context("Money 생성") {
        
        test("정상적인 금액으로 Money를 생성할 수 있어야 한다") {
            // Given & When
            val money = Money(BigDecimal("1000.50"))
            
            // Then
            money.amount shouldBe BigDecimal("1000.50")
        }
        
        test("0원으로 Money를 생성할 수 있어야 한다") {
            // Given & When
            val money = Money.zero()
            
            // Then
            money.amount shouldBe BigDecimal.ZERO
        }
        
        test("음수 금액으로 Money 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Money(BigDecimal("-100"))
            }
        }
        
        test("companion object의 of 메서드로 Money를 생성할 수 있어야 한다") {
            // Given & When
            val money1 = Money.of(BigDecimal("100"))
            val money2 = Money.of(100L)
            val money3 = Money.of(100.0)
            val money4 = Money.of("100")
            
            // Then
            money1.amount shouldBe BigDecimal("100")
            money2.amount shouldBe BigDecimal("100")
            money3.amount shouldBe BigDecimal("100.0")
            money4.amount shouldBe BigDecimal("100")
        }
    }
    
    context("Money 연산") {
        
        test("금액 덧셈이 올바르게 계산되어야 한다") {
            // Given
            val money1 = Money(BigDecimal("1000"))
            val money2 = Money(BigDecimal("500"))
            
            // When
            val result = money1 + money2
            
            // Then
            result.amount shouldBe BigDecimal("1500")
        }
        
        test("금액 뺄셈이 올바르게 계산되어야 한다") {
            // Given
            val money1 = Money(BigDecimal("1000"))
            val money2 = Money(BigDecimal("300"))
            
            // When
            val result = money1 - money2
            
            // Then
            result.amount shouldBe BigDecimal("700")
        }
        
        test("금액 곱셈이 올바르게 계산되어야 한다") {
            // Given
            val money = Money(BigDecimal("1000"))
            
            // When
            val result1 = money * BigDecimal("1.5")
            val result2 = money * 2
            
            // Then
            result1.amount shouldBe BigDecimal("1500.0")
            result2.amount shouldBe BigDecimal("2000")
        }
        
        test("금액 비교가 올바르게 작동해야 한다") {
            // Given
            val money1 = Money(BigDecimal("1000"))
            val money2 = Money(BigDecimal("500"))
            val money3 = Money(BigDecimal("1000"))
            
            // When & Then
            (money1 > money2) shouldBe true
            (money2 < money1) shouldBe true
            (money1 == money3) shouldBe true
        }
    }
    
    context("Money 검증 메서드") {
        
        test("isZero 메서드가 올바르게 작동해야 한다") {
            // Given
            val zeroMoney = Money.zero()
            val nonZeroMoney = Money(BigDecimal("100"))
            
            // When & Then
            zeroMoney.isZero() shouldBe true
            nonZeroMoney.isZero() shouldBe false
        }
        
        test("isPositive 메서드가 올바르게 작동해야 한다") {
            // Given
            val zeroMoney = Money.zero()
            val positiveMoney = Money(BigDecimal("100"))
            
            // When & Then
            zeroMoney.isPositive() shouldBe false
            positiveMoney.isPositive() shouldBe true
        }
        
        test("isGreaterThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val money1 = Money(BigDecimal("1000"))
            val money2 = Money(BigDecimal("500"))
            
            // When & Then
            money1.isGreaterThan(money2) shouldBe true
            money2.isGreaterThan(money1) shouldBe false
        }
        
        test("isLessThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val money1 = Money(BigDecimal("500"))
            val money2 = Money(BigDecimal("1000"))
            
            // When & Then
            money1.isLessThan(money2) shouldBe true
            money2.isLessThan(money1) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val money1 = Money(BigDecimal("1000"))
            val money2 = Money(BigDecimal("1000"))
            val money3 = Money(BigDecimal("500"))
            
            // When & Then
            money1.isEqualTo(money2) shouldBe true
            money1.isEqualTo(money3) shouldBe false
        }
    }
    
    context("Money 반올림") {
        
        test("roundTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val money = Money(BigDecimal("1000.567"))
            
            // When
            val rounded = money.roundTo(2)
            
            // Then
            rounded.amount shouldBe BigDecimal("1000.57")
        }
    }
    
    context("Money 문자열 변환") {
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val money = Money(BigDecimal("1000.50"))
            
            // When
            val stringValue = money.toString()
            
            // Then
            stringValue shouldBe "1000.50"
        }
    }
})
