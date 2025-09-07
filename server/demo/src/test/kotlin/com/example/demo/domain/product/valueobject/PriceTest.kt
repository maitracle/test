package com.example.demo.domain.product.valueobject

import com.example.demo.domain.common.valueobject.Money
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal

class PriceTest : FunSpec({
    
    context("Price 생성") {
        
        test("유효한 Money로 Price를 생성할 수 있어야 한다") {
            // Given
            val money = Money(BigDecimal("10000"))
            
            // When
            val price = Price(money)
            
            // Then
            price.amount shouldBe money
        }
        
        test("0원으로 Price를 생성할 수 있어야 한다") {
            // Given
            val money = Money(BigDecimal.ZERO)
            
            // When
            val price = Price(money)
            
            // Then
            price.amount shouldBe money
        }
        
        test("음수 Money로 Price를 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                val money = Money(BigDecimal("-1000"))
                Price(money)
            }
            exception.message shouldContain "Money amount cannot be negative"
        }
    }
    
    context("Price 연산") {
        
        test("Price 덧셈이 올바르게 계산되어야 한다") {
            // Given
            val price1 = Price(Money(BigDecimal("10000")))
            val price2 = Price(Money(BigDecimal("5000")))
            
            // When
            val result = price1 + price2
            
            // Then
            result.amount.amount shouldBe BigDecimal("15000")
        }
        
        test("Price 뺄셈이 올바르게 계산되어야 한다") {
            // Given
            val price1 = Price(Money(BigDecimal("10000")))
            val price2 = Price(Money(BigDecimal("3000")))
            
            // When
            val result = price1 - price2
            
            // Then
            result.amount.amount shouldBe BigDecimal("7000")
        }
        
        test("Price와 BigDecimal 곱셈이 올바르게 계산되어야 한다") {
            // Given
            val price = Price(Money(BigDecimal("10000")))
            val multiplier = BigDecimal("2.5")
            
            // When
            val result = price * multiplier
            
            // Then
            result.amount.amount shouldBe BigDecimal("25000.0")
        }
        
        test("Price와 정수 곱셈이 올바르게 계산되어야 한다") {
            // Given
            val price = Price(Money(BigDecimal("10000")))
            val multiplier = 3
            
            // When
            val result = price * multiplier
            
            // Then
            result.amount.amount shouldBe BigDecimal("30000")
        }
        
        test("Price 비교가 올바르게 작동해야 한다") {
            // Given
            val price1 = Price(Money(BigDecimal("10000")))
            val price2 = Price(Money(BigDecimal("5000")))
            val price3 = Price(Money(BigDecimal("10000")))
            
            // When & Then
            (price1 > price2) shouldBe true
            (price2 < price1) shouldBe true
            (price1 == price3) shouldBe true
        }
    }
    
    context("Price toString") {
        
        test("Price의 문자열 표현이 올바르게 반환되어야 한다") {
            // Given
            val price = Price(Money(BigDecimal("15000")))
            
            // When
            val stringRepresentation = price.toString()
            
            // Then
            stringRepresentation shouldBe "Price(15000)"
        }
    }
    
    context("Price companion object") {
        
        test("of(BigDecimal) 메서드로 Price를 생성할 수 있어야 한다") {
            // Given
            val amount = BigDecimal("10000")
            
            // When
            val price = Price.of(amount)
            
            // Then
            price.amount.amount shouldBe amount
        }
        
        test("of(Money) 메서드로 Price를 생성할 수 있어야 한다") {
            // Given
            val money = Money(BigDecimal("10000"))
            
            // When
            val price = Price.of(money)
            
            // Then
            price.amount shouldBe money
        }
        
        test("safeOf(BigDecimal) 메서드로 유효한 값의 Price를 생성할 수 있어야 한다") {
            // Given
            val amount = BigDecimal("10000")
            
            // When
            val price = Price.safeOf(amount)
            
            // Then
            price shouldNotBe null
            price!!.amount.amount shouldBe amount
        }
        
        test("safeOf(BigDecimal) 메서드로 null 값을 전달하면 null을 반환해야 한다") {
            // Given
            val amount: BigDecimal? = null
            
            // When
            val price = Price.safeOf(amount)
            
            // Then
            price shouldBe null
        }
        
        test("safeOf(Money) 메서드로 유효한 값의 Price를 생성할 수 있어야 한다") {
            // Given
            val money = Money(BigDecimal("10000"))
            
            // When
            val price = Price.safeOf(money)
            
            // Then
            price shouldNotBe null
            price!!.amount shouldBe money
        }
        
        test("safeOf(Money) 메서드로 null 값을 전달하면 null을 반환해야 한다") {
            // Given
            val money: Money? = null
            
            // When
            val price = Price.safeOf(money)
            
            // Then
            price shouldBe null
        }
        
        test("free 메서드로 무료 Price를 생성할 수 있어야 한다") {
            // When
            val price = Price.free()
            
            // Then
            price.amount.amount shouldBe BigDecimal.ZERO
        }
        
        test("음수 값으로 of 메서드를 호출하면 예외가 발생해야 한다") {
            // Given
            val amount = BigDecimal("-1000")
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                Price.of(amount)
            }
        }
        
        test("음수 Money로 of 메서드를 호출하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                val money = Money(BigDecimal("-1000"))
                Price.of(money)
            }
        }
    }
})
