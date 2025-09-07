package com.example.demo.domain.common.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class StockTest : FunSpec({
    
    context("Stock 생성") {
        
        test("정상적인 재고로 Stock을 생성할 수 있어야 한다") {
            // Given & When
            val stock = Stock(100)
            
            // Then
            stock.quantity shouldBe 100
        }
        
        test("0 재고로 Stock을 생성할 수 있어야 한다") {
            // Given & When
            val stock = Stock.empty()
            
            // Then
            stock.quantity shouldBe 0
        }
        
        test("음수 재고로 Stock 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Stock(-1)
            }
        }
        
        test("companion object의 of 메서드로 Stock을 생성할 수 있어야 한다") {
            // Given & When
            val stock1 = Stock.of(100)
            val stock2 = Stock.of(100L)
            val stock3 = Stock.of("100")
            
            // Then
            stock1.quantity shouldBe 100
            stock2.quantity shouldBe 100
            stock3.quantity shouldBe 100
        }
        
        test("safeOf 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val validStock = Stock.safeOf(100)
            val negativeStock = Stock.safeOf(-10)
            
            // Then
            validStock.quantity shouldBe 100
            negativeStock.quantity shouldBe 0
        }
    }
    
    context("Stock 재고 확인") {
        
        test("hasEnough 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            val requiredQuantity = Quantity(50)
            val insufficientQuantity = Quantity(150)
            
            // When & Then
            stock.hasEnough(requiredQuantity) shouldBe true
            stock.hasEnough(insufficientQuantity) shouldBe false
        }
        
        test("isEmpty 메서드가 올바르게 작동해야 한다") {
            // Given
            val emptyStock = Stock.empty()
            val nonEmptyStock = Stock(100)
            
            // When & Then
            emptyStock.isEmpty() shouldBe true
            nonEmptyStock.isEmpty() shouldBe false
        }
        
        test("isNotEmpty 메서드가 올바르게 작동해야 한다") {
            // Given
            val emptyStock = Stock.empty()
            val nonEmptyStock = Stock(100)
            
            // When & Then
            emptyStock.isNotEmpty() shouldBe false
            nonEmptyStock.isNotEmpty() shouldBe true
        }
    }
    
    context("Stock 재고 관리") {
        
        test("재고 차감이 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            val reduceQuantity = Quantity(30)
            
            // When
            val result = stock.reduce(reduceQuantity)
            
            // Then
            result.quantity shouldBe 70
        }
        
        test("재고가 부족할 때 차감 시 예외가 발생해야 한다") {
            // Given
            val stock = Stock(50)
            val reduceQuantity = Quantity(100)
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                stock.reduce(reduceQuantity)
            }
        }
        
        test("재고 증가가 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            val increaseQuantity = Quantity(50)
            
            // When
            val result = stock.increase(increaseQuantity)
            
            // Then
            result.quantity shouldBe 150
        }
        
        test("재고 설정이 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            
            // When
            val result = stock.set(200)
            
            // Then
            result.quantity shouldBe 200
        }
    }
    
    context("Stock 비교") {
        
        test("재고 비교가 올바르게 작동해야 한다") {
            // Given
            val stock1 = Stock(100)
            val stock2 = Stock(50)
            val stock3 = Stock(100)
            
            // When & Then
            (stock1 > stock2) shouldBe true
            (stock2 < stock1) shouldBe true
            (stock1 == stock3) shouldBe true
        }
        
        test("isGreaterThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock1 = Stock(100)
            val stock2 = Stock(50)
            
            // When & Then
            stock1.isGreaterThan(stock2) shouldBe true
            stock2.isGreaterThan(stock1) shouldBe false
        }
        
        test("isLessThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock1 = Stock(50)
            val stock2 = Stock(100)
            
            // When & Then
            stock1.isLessThan(stock2) shouldBe true
            stock2.isLessThan(stock1) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock1 = Stock(100)
            val stock2 = Stock(100)
            val stock3 = Stock(50)
            
            // When & Then
            stock1.isEqualTo(stock2) shouldBe true
            stock1.isEqualTo(stock3) shouldBe false
        }
        
        test("isAtLeast 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            val minStock = Stock(50)
            val maxStock = Stock(150)
            
            // When & Then
            stock.isAtLeast(minStock) shouldBe true
            stock.isAtLeast(maxStock) shouldBe false
        }
        
        test("isAtMost 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            val minStock = Stock(50)
            val maxStock = Stock(150)
            
            // When & Then
            stock.isAtMost(maxStock) shouldBe true
            stock.isAtMost(minStock) shouldBe false
        }
    }
    
    context("Stock 문자열 변환") {
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val stock = Stock(100)
            
            // When
            val stringValue = stock.toString()
            
            // Then
            stringValue shouldBe "100"
        }
    }
})
