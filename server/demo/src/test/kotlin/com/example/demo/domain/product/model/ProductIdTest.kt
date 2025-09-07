package com.example.demo.domain.product.model

import com.example.demo.domain.common.valueobject.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain

class ProductIdTest : FunSpec({
    
    context("ProductId 생성") {
        
        test("유효한 값으로 ProductId를 생성할 수 있어야 한다") {
            // Given
            val value = 1L
            
            // When
            val productId = ProductId.of(value)
            
            // Then
            productId.value shouldBe value
        }
        
        test("양수 값으로 ProductId를 생성할 수 있어야 한다") {
            // Given
            val value = 100L
            
            // When
            val productId = ProductId.of(value)
            
            // Then
            productId.value shouldBe value
        }
        
        test("0 값으로 ProductId를 생성하면 예외가 발생해야 한다") {
            // Given
            val value = 0L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                ProductId.of(value)
            }
            exception.message shouldContain "EntityId must be positive"
        }
        
        test("음수 값으로 ProductId를 생성하면 예외가 발생해야 한다") {
            // Given
            val value = -1L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                ProductId.of(value)
            }
            exception.message shouldContain "EntityId must be positive"
        }
    }
    
    context("ProductId 비교") {
        
        test("동일한 값을 가진 ProductId는 같아야 한다") {
            // Given
            val value = 1L
            val productId1 = ProductId.of(value)
            val productId2 = ProductId.of(value)
            
            // When & Then
            productId1 shouldBe productId2
            productId1.hashCode() shouldBe productId2.hashCode()
        }
        
        test("다른 값을 가진 ProductId는 달라야 한다") {
            // Given
            val productId1 = ProductId.of(1L)
            val productId2 = ProductId.of(2L)
            
            // When & Then
            productId1 shouldNotBe productId2
            productId1.hashCode() shouldNotBe productId2.hashCode()
        }
    }
    
    context("ProductId toString") {
        
        test("ProductId의 문자열 표현이 올바르게 반환되어야 한다") {
            // Given
            val value = 123L
            val productId = ProductId.of(value)
            
            // When
            val stringRepresentation = productId.toString()
            
            // Then
            stringRepresentation shouldBe "ProductId(123)"
        }
    }
    
    context("ProductId companion object") {
        
        test("of 메서드로 ProductId를 생성할 수 있어야 한다") {
            // Given
            val value = 1L
            
            // When
            val productId = ProductId.of(value)
            
            // Then
            productId.value shouldBe value
        }
        
        test("safeOf 메서드로 유효한 값의 ProductId를 생성할 수 있어야 한다") {
            // Given
            val value = 1L
            
            // When
            val productId = ProductId.safeOf(value)
            
            // Then
            productId shouldNotBe null
            productId!!.value shouldBe value
        }
        
        test("safeOf 메서드로 null 값을 전달하면 null을 반환해야 한다") {
            // Given
            val value: Long? = null
            
            // When
            val productId = ProductId.safeOf(value)
            
            // Then
            productId shouldBe null
        }
        
        test("safeOf 메서드로 유효하지 않은 값을 전달하면 예외가 발생해야 한다") {
            // Given
            val value = 0L
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                ProductId.safeOf(value)
            }
        }
        
        test("EntityId로부터 ProductId를 생성할 수 있어야 한다") {
            // Given
            val entityId = EntityId(1L)
            
            // When
            val productId = ProductId.of(entityId)
            
            // Then
            productId.value shouldBe 1L
        }
        
        test("EntityId의 safeOf 메서드로 ProductId를 생성할 수 있어야 한다") {
            // Given
            val entityId = EntityId(1L)
            
            // When
            val productId = ProductId.safeOf(entityId)
            
            // Then
            productId shouldNotBe null
            productId!!.value shouldBe 1L
        }
        
        test("null EntityId로 safeOf 메서드를 호출하면 null을 반환해야 한다") {
            // Given
            val entityId: EntityId? = null
            
            // When
            val productId = ProductId.safeOf(entityId)
            
            // Then
            productId shouldBe null
        }
    }
    
    context("ProductId EntityId 기능 위임") {
        
        test("ProductId 비교 기능이 올바르게 작동해야 한다") {
            // Given
            val productId1 = ProductId.of(1L)
            val productId2 = ProductId.of(2L)
            val productId3 = ProductId.of(1L)
            
            // When & Then
            productId1.isLessThan(productId2) shouldBe true
            productId2.isGreaterThan(productId1) shouldBe true
            productId1.isEqualTo(productId3) shouldBe true
        }
        
        test("ProductId 변환 기능이 올바르게 작동해야 한다") {
            // Given
            val productId = ProductId.of(123L)
            
            // When & Then
            productId.toInt() shouldBe 123
            productId.toStringValue() shouldBe "123"
        }
    }
})
