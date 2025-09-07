package com.example.demo.domain.user.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class UserIdTest : FunSpec({
    
    context("UserId 생성") {
        
        test("정상적인 ID로 UserId를 생성할 수 있어야 한다") {
            // Given & When
            val userId = UserId(123L)
            
            // Then
            userId.value shouldBe 123L
        }
        
        test("0 이하의 ID로 UserId 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                UserId(0L)
            }
            
            shouldThrow<IllegalArgumentException> {
                UserId(-1L)
            }
        }
        
        test("companion object의 of 메서드로 UserId를 생성할 수 있어야 한다") {
            // Given & When
            val userId1 = UserId.of(123L)
            val userId2 = UserId.of(123)
            val userId3 = UserId.of("123")
            
            // Then
            userId1.value shouldBe 123L
            userId2.value shouldBe 123L
            userId3.value shouldBe 123L
        }
        
        test("safeOf 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val validId = UserId.safeOf(123L)
            val invalidId = UserId.safeOf(0L)
            val negativeId = UserId.safeOf(-1L)
            
            // Then
            validId shouldNotBe null
            validId?.value shouldBe 123L
            invalidId shouldBe null
            negativeId shouldBe null
        }
        
        test("safeOf 메서드가 String으로 올바르게 작동해야 한다") {
            // Given & When
            val validId = UserId.safeOf("123")
            val invalidId = UserId.safeOf("0")
            val nonNumericId = UserId.safeOf("abc")
            
            // Then
            validId shouldNotBe null
            validId?.value shouldBe 123L
            invalidId shouldBe null
            nonNumericId shouldBe null
        }
    }
    
    context("UserId 비교") {
        
        test("ID 비교가 올바르게 작동해야 한다") {
            // Given
            val userId1 = UserId(100L)
            val userId2 = UserId(50L)
            val userId3 = UserId(100L)
            
            // When & Then
            (userId1 > userId2) shouldBe true
            (userId2 < userId1) shouldBe true
            (userId1 == userId3) shouldBe true
        }
        
        test("isGreaterThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId1 = UserId(100L)
            val userId2 = UserId(50L)
            
            // When & Then
            userId1.isGreaterThan(userId2) shouldBe true
            userId2.isGreaterThan(userId1) shouldBe false
        }
        
        test("isLessThan 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId1 = UserId(50L)
            val userId2 = UserId(100L)
            
            // When & Then
            userId1.isLessThan(userId2) shouldBe true
            userId2.isLessThan(userId1) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId1 = UserId(100L)
            val userId2 = UserId(100L)
            val userId3 = UserId(50L)
            
            // When & Then
            userId1.isEqualTo(userId2) shouldBe true
            userId1.isEqualTo(userId3) shouldBe false
        }
    }
    
    context("UserId 변환") {
        
        test("toInt 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId = UserId(123L)
            
            // When
            val intValue = userId.toInt()
            
            // Then
            intValue shouldBe 123
        }
        
        test("toStringValue 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId = UserId(123L)
            
            // When
            val stringValue = userId.toStringValue()
            
            // Then
            stringValue shouldBe "123"
        }
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val userId = UserId(123L)
            
            // When
            val stringValue = userId.toString()
            
            // Then
            stringValue shouldBe "123"
        }
    }
    
    context("UserId 경계값 테스트") {
        
        test("최대 Long 값으로 UserId를 생성할 수 있어야 한다") {
            // Given & When
            val userId = UserId(Long.MAX_VALUE)
            
            // Then
            userId.value shouldBe Long.MAX_VALUE
        }
        
        test("최소 양수 값으로 UserId를 생성할 수 있어야 한다") {
            // Given & When
            val userId = UserId(1L)
            
            // Then
            userId.value shouldBe 1L
        }
    }
})
