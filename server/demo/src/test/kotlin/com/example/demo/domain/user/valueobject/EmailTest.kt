package com.example.demo.domain.user.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class EmailTest : FunSpec({
    
    context("Email 생성") {
        
        test("정상적인 이메일로 Email을 생성할 수 있어야 한다") {
            // Given & When
            val email = Email("test@example.com")
            
            // Then
            email.value shouldBe "test@example.com"
        }
        
        test("빈 문자열로 Email 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Email("")
            }
            
            shouldThrow<IllegalArgumentException> {
                Email("   ")
            }
        }
        
        test("잘못된 형식의 이메일로 Email 생성 시 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Email("invalid-email")
            }
            
            shouldThrow<IllegalArgumentException> {
                Email("@example.com")
            }
            
            shouldThrow<IllegalArgumentException> {
                Email("test@")
            }
            
            shouldThrow<IllegalArgumentException> {
                Email("test@.com")
            }
        }
        
        test("companion object의 of 메서드로 Email을 생성할 수 있어야 한다") {
            // Given & When
            val email = Email.of("test@example.com")
            
            // Then
            email.value shouldBe "test@example.com"
        }
        
        test("safeOf 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val validEmail = Email.safeOf("test@example.com")
            val invalidEmail = Email.safeOf("invalid-email")
            val nullEmail = Email.safeOf(null)
            val blankEmail = Email.safeOf("")
            
            // Then
            validEmail shouldNotBe null
            validEmail?.value shouldBe "test@example.com"
            invalidEmail shouldBe null
            nullEmail shouldBe null
            blankEmail shouldBe null
        }
        
        test("isValid 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            Email.isValid("test@example.com") shouldBe true
            Email.isValid("user.name+tag@domain.co.uk") shouldBe true
            Email.isValid("invalid-email") shouldBe false
            Email.isValid("") shouldBe false
            Email.isValid("@example.com") shouldBe false
        }
    }
    
    context("Email 기능") {
        
        test("isFromDomain 메서드가 올바르게 작동해야 한다") {
            // Given
            val email = Email("test@example.com")
            
            // When & Then
            email.isFromDomain("example.com") shouldBe true
            email.isFromDomain("EXAMPLE.COM") shouldBe true
            email.isFromDomain("other.com") shouldBe false
        }
        
        test("getLocalPart 메서드가 올바르게 작동해야 한다") {
            // Given
            val email = Email("test.user@example.com")
            
            // When
            val localPart = email.getLocalPart()
            
            // Then
            localPart shouldBe "test.user"
        }
        
        test("getDomainPart 메서드가 올바르게 작동해야 한다") {
            // Given
            val email = Email("test@example.com")
            
            // When
            val domainPart = email.getDomainPart()
            
            // Then
            domainPart shouldBe "example.com"
        }
        
        test("toLowerCase 메서드가 올바르게 작동해야 한다") {
            // Given
            val email = Email("TEST@EXAMPLE.COM")
            
            // When
            val lowerEmail = email.toLowerCase()
            
            // Then
            lowerEmail.value shouldBe "test@example.com"
        }
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val email = Email("test@example.com")
            
            // When
            val stringValue = email.toString()
            
            // Then
            stringValue shouldBe "test@example.com"
        }
    }
    
    context("Email 비교") {
        
        test("이메일 비교가 올바르게 작동해야 한다") {
            // Given
            val email1 = Email("a@example.com")
            val email2 = Email("b@example.com")
            val email3 = Email("a@example.com")
            
            // When & Then
            (email1 < email2) shouldBe true
            (email2 > email1) shouldBe true
            (email1 == email3) shouldBe true
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given
            val email1 = Email("test@example.com")
            val email2 = Email("test@example.com")
            val email3 = Email("other@example.com")
            
            // When & Then
            email1.isEqualTo(email2) shouldBe true
            email1.isEqualTo(email3) shouldBe false
        }
    }
    
    context("Email 경계값 테스트") {
        
        test("최대 길이 이메일로 Email을 생성할 수 있어야 한다") {
            // Given
            val longEmail = "a".repeat(240) + "@example.com"
            
            // When
            val email = Email(longEmail)
            
            // Then
            email.value shouldBe longEmail
        }
        
        test("최대 길이를 초과하는 이메일로 Email 생성 시 예외가 발생해야 한다") {
            // Given
            val tooLongEmail = "a".repeat(255) + "@example.com"
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                Email(tooLongEmail)
            }
        }
        
        test("복잡한 이메일 형식이 올바르게 처리되어야 한다") {
            // Given & When
            val email1 = Email("user.name+tag@domain.co.uk")
            val email2 = Email("user123@sub.domain.com")
            val email3 = Email("test_email@example-domain.org")
            
            // Then
            email1.value shouldBe "user.name+tag@domain.co.uk"
            email2.value shouldBe "user123@sub.domain.com"
            email3.value shouldBe "test_email@example-domain.org"
        }
    }
})
