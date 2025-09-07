package com.example.demo.domain.common.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class EntityIdTest : FunSpec({
    
    context("EntityId 생성") {
        
        test("정상적인 ID로 EntityId를 생성할 수 있어야 한다") {
            // Given & When
            val entityId = EntityId(123L)
            
            // Then
            entityId.value shouldBe 123L
        }
        
        test("0 이하의 ID로 EntityId 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                EntityId(0L)
            }
            
            shouldThrow<IllegalArgumentException> {
                EntityId(-1L)
            }
        }
        
        test("companion object의 of 메서드로 EntityId를 생성할 수 있어야 한다") {
            // Given & When
            val entityId1 = EntityId.of(123L)
            val entityId2 = EntityId.of(123)
            val entityId3 = EntityId.of("123")
            
            // Then
            entityId1.value shouldBe 123L
            entityId2.value shouldBe 123L
            entityId3.value shouldBe 123L
        }
        
        test("safeOf 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val validId = EntityId.safeOf(123L)
            val invalidId = EntityId.safeOf(0L)
            val negativeId = EntityId.safeOf(-1L)
            
            // Then
            validId shouldNotBe null
            validId?.value shouldBe 123L
            invalidId shouldBe null
            negativeId shouldBe null
        }
        
        test("safeOf 메서드가 String으로 올바르게 작동해야 한다") {
            // Given & When
            val validId = EntityId.safeOf("123")
            val invalidId = EntityId.safeOf("0")
            val nonNumericId = EntityId.safeOf("abc")
            
            // Then
            validId shouldNotBe null
            validId?.value shouldBe 123L
            invalidId shouldBe null
            nonNumericId shouldBe null
        }
    }
    
    context("EntityId 비교") {
        
        test("ID 비교가 올바르게 작동해야 한다") {
            // Given
            val entityId1 = EntityId(100L)
            val entityId2 = EntityId(50L)
            val entityId3 = EntityId(100L)
            
            // When & Then
            (entityId1 > entityId2) shouldBe true
            (entityId2 < entityId1) shouldBe true
            (entityId1 == entityId3) shouldBe true
        }
        
        test("isGreaterThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId1 = EntityId(100L)
            val entityId2 = EntityId(50L)
            
            // When & Then
            entityId1.isGreaterThan(entityId2) shouldBe true
            entityId2.isGreaterThan(entityId1) shouldBe false
        }
        
        test("isLessThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId1 = EntityId(50L)
            val entityId2 = EntityId(100L)
            
            // When & Then
            entityId1.isLessThan(entityId2) shouldBe true
            entityId2.isLessThan(entityId1) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId1 = EntityId(100L)
            val entityId2 = EntityId(100L)
            val entityId3 = EntityId(50L)
            
            // When & Then
            entityId1.isEqualTo(entityId2) shouldBe true
            entityId1.isEqualTo(entityId3) shouldBe false
        }
    }
    
    context("EntityId 변환") {
        
        test("toInt 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId = EntityId(123L)
            
            // When
            val intValue = entityId.toInt()
            
            // Then
            intValue shouldBe 123
        }
        
        test("toStringValue 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId = EntityId(123L)
            
            // When
            val stringValue = entityId.toStringValue()
            
            // Then
            stringValue shouldBe "123"
        }
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val entityId = EntityId(123L)
            
            // When
            val stringValue = entityId.toString()
            
            // Then
            stringValue shouldBe "123"
        }
    }
    
    context("EntityId 경계값 테스트") {
        
        test("최대 Long 값으로 EntityId를 생성할 수 있어야 한다") {
            // Given & When
            val entityId = EntityId(Long.MAX_VALUE)
            
            // Then
            entityId.value shouldBe Long.MAX_VALUE
        }
        
        test("최소 양수 값으로 EntityId를 생성할 수 있어야 한다") {
            // Given & When
            val entityId = EntityId(1L)
            
            // Then
            entityId.value shouldBe 1L
        }
    }
})
