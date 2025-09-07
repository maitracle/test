package com.example.demo.domain.promotion.model

import com.example.demo.domain.common.valueobject.EntityId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class PromotionIdTest : DescribeSpec({
    
    describe("PromotionId 생성") {
        it("유효한 Long 값으로 PromotionId를 생성할 수 있어야 한다") {
            // Given
            val value = 1L
            
            // When
            val promotionId = PromotionId.of(value)
            
            // Then
            promotionId.value shouldBe value
        }
        
        it("EntityId로부터 PromotionId를 생성할 수 있어야 한다") {
            // Given
            val entityId = EntityId(1L)
            
            // When
            val promotionId = PromotionId.of(entityId)
            
            // Then
            promotionId.value shouldBe entityId.value
        }
        
        it("양수가 아닌 값으로 PromotionId를 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                PromotionId.of(0L)
            }
            
            shouldThrow<IllegalArgumentException> {
                PromotionId.of(-1L)
            }
        }
    }
    
    describe("PromotionId 안전 생성") {
        it("유효한 값으로 안전하게 생성할 수 있어야 한다") {
            // Given
            val value = 1L
            
            // When
            val promotionId = PromotionId.safeOf(value)
            
            // Then
            promotionId shouldNotBe null
            promotionId?.value shouldBe value
        }
        
        it("null 값으로 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val promotionId = PromotionId.safeOf(null as Long?)
            
            // Then
            promotionId shouldBe null
        }
        
        it("양수가 아닌 값으로 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val promotionId1 = PromotionId.safeOf(0L)
            val promotionId2 = PromotionId.safeOf(-1L)
            
            // Then
            promotionId1 shouldBe null
            promotionId2 shouldBe null
        }
        
        it("EntityId로부터 안전하게 생성할 수 있어야 한다") {
            // Given
            val entityId = EntityId(1L)
            
            // When
            val promotionId = PromotionId.safeOf(entityId)
            
            // Then
            promotionId shouldNotBe null
            promotionId?.value shouldBe entityId.value
        }
        
        it("null EntityId로부터 안전하게 생성하면 null을 반환해야 한다") {
            // Given & When
            val promotionId = PromotionId.safeOf(null as EntityId?)
            
            // Then
            promotionId shouldBe null
        }
    }
    
    describe("PromotionId 비교") {
        it("같은 값을 가진 PromotionId는 같아야 한다") {
            // Given
            val promotionId1 = PromotionId.of(1L)
            val promotionId2 = PromotionId.of(1L)
            
            // When & Then
            promotionId1 shouldBe promotionId2
            promotionId1.hashCode() shouldBe promotionId2.hashCode()
        }
        
        it("다른 값을 가진 PromotionId는 달라야 한다") {
            // Given
            val promotionId1 = PromotionId.of(1L)
            val promotionId2 = PromotionId.of(2L)
            
            // When & Then
            promotionId1 shouldNotBe promotionId2
        }
    }
    
    describe("PromotionId 문자열 표현") {
        it("toString은 적절한 형식이어야 한다") {
            // Given
            val promotionId = PromotionId.of(1L)
            
            // When
            val stringRepresentation = promotionId.toString()
            
            // Then
            stringRepresentation shouldBe "PromotionId(1)"
        }
    }
})
