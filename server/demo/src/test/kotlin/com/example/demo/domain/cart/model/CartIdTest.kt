package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.EntityId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf

class CartIdTest : DescribeSpec({
    
    describe("CartId") {
        
        context("생성") {
            
            it("유효한 값으로 CartId를 생성할 수 있다") {
                // Given
                val value = 1L
                
                // When
                val cartId = CartId.of(value)
                
                // Then
                cartId.value shouldBe value
                cartId.shouldBeInstanceOf<CartId>()
            }
            
            it("EntityId로 CartId를 생성할 수 있다") {
                // Given
                val entityId = EntityId(1L)
                
                // When
                val cartId = CartId.of(entityId)
                
                // Then
                cartId.value shouldBe 1L
                cartId.shouldBeInstanceOf<CartId>()
            }
            
            it("0 이하의 값으로 CartId를 생성하면 예외가 발생한다") {
                // Given
                val invalidValue = 0L
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    CartId.of(invalidValue)
                }
            }
            
            it("음수 값으로 CartId를 생성하면 예외가 발생한다") {
                // Given
                val negativeValue = -1L
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    CartId.of(negativeValue)
                }
            }
        }
        
        context("안전한 생성") {
            
            it("유효한 값으로 안전하게 CartId를 생성할 수 있다") {
                // Given
                val value = 1L
                
                // When
                val cartId = CartId.safeOf(value)
                
                // Then
                cartId.shouldNotBeNull()
                cartId!!.value shouldBe value
            }
            
            it("null 값으로 안전하게 생성하면 null을 반환한다") {
                // Given
                val nullValue: Long? = null
                
                // When
                val cartId = CartId.safeOf(nullValue)
                
                // Then
                cartId.shouldBeNull()
            }
            
            it("0 이하의 값으로 안전하게 생성하면 null을 반환한다") {
                // Given
                val invalidValue = 0L
                
                // When
                val cartId = CartId.safeOf(invalidValue)
                
                // Then
                cartId.shouldBeNull()
            }
        }
        
        context("비교") {
            
            it("같은 값을 가진 CartId는 같다") {
                // Given
                val cartId1 = CartId.of(1L)
                val cartId2 = CartId.of(1L)
                
                // When & Then
                cartId1 shouldBe cartId2
                cartId1.isEqualTo(cartId2) shouldBe true
            }
            
            it("다른 값을 가진 CartId는 다르다") {
                // Given
                val cartId1 = CartId.of(1L)
                val cartId2 = CartId.of(2L)
                
                // When & Then
                cartId1 shouldNotBe cartId2
                cartId1.isEqualTo(cartId2) shouldBe false
            }
            
            it("CartId의 크기를 비교할 수 있다") {
                // Given
                val cartId1 = CartId.of(1L)
                val cartId2 = CartId.of(2L)
                
                // When & Then
                cartId1.isLessThan(cartId2) shouldBe true
                cartId2.isGreaterThan(cartId1) shouldBe true
            }
        }
        
        context("변환") {
            
            it("CartId를 Int로 변환할 수 있다") {
                // Given
                val cartId = CartId.of(1L)
                
                // When
                val intValue = cartId.toInt()
                
                // Then
                intValue shouldBe 1
            }
            
            it("CartId를 String으로 변환할 수 있다") {
                // Given
                val cartId = CartId.of(1L)
                
                // When
                val stringValue = cartId.toStringValue()
                
                // Then
                stringValue shouldBe "1"
            }
            
            it("CartId의 toString은 의미있는 문자열을 반환한다") {
                // Given
                val cartId = CartId.of(1L)
                
                // When
                val stringRepresentation = cartId.toString()
                
                // Then
                stringRepresentation shouldBe "CartId(1)"
            }
        }
    }
})
