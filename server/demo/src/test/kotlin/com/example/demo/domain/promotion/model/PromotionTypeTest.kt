package com.example.demo.domain.promotion.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PromotionTypeTest : DescribeSpec({
    
    describe("PromotionType 열거형") {
        it("모든 프로모션 타입이 정의되어 있어야 한다") {
            // Given & When & Then
            PromotionType.values().size shouldBe 5
            
            PromotionType.PERCENTAGE_DISCOUNT.displayName shouldBe "퍼센트 할인"
            PromotionType.FIXED_DISCOUNT.displayName shouldBe "고정 금액 할인"
            PromotionType.BUY_ONE_GET_ONE.displayName shouldBe "1+1 프로모션"
            PromotionType.FREE_SHIPPING.displayName shouldBe "무료 배송"
            PromotionType.CASHBACK.displayName shouldBe "캐시백"
        }
        
        it("각 프로모션 타입의 설명이 있어야 한다") {
            // Given & When & Then
            PromotionType.PERCENTAGE_DISCOUNT.description shouldBe "주문 금액의 일정 비율을 할인하는 프로모션"
            PromotionType.FIXED_DISCOUNT.description shouldBe "고정된 금액을 할인하는 프로모션"
            PromotionType.BUY_ONE_GET_ONE.description shouldBe "하나를 구매하면 하나를 무료로 제공하는 프로모션"
            PromotionType.FREE_SHIPPING.description shouldBe "배송비를 무료로 제공하는 프로모션"
            PromotionType.CASHBACK.description shouldBe "구매 금액의 일정 비율을 캐시백으로 제공하는 프로모션"
        }
    }
    
    describe("할인 계산 지원 확인") {
        it("퍼센트 할인과 고정 금액 할인은 할인 계산을 지원해야 한다") {
            // Given & When & Then
            PromotionType.PERCENTAGE_DISCOUNT.supportsDiscountCalculation() shouldBe true
            PromotionType.FIXED_DISCOUNT.supportsDiscountCalculation() shouldBe true
        }
        
        it("1+1, 무료 배송, 캐시백은 할인 계산을 지원하지 않아야 한다") {
            // Given & When & Then
            PromotionType.BUY_ONE_GET_ONE.supportsDiscountCalculation() shouldBe false
            PromotionType.FREE_SHIPPING.supportsDiscountCalculation() shouldBe false
            PromotionType.CASHBACK.supportsDiscountCalculation() shouldBe false
        }
    }
    
    describe("수량 기반 프로모션 확인") {
        it("1+1 프로모션만 수량 기반이어야 한다") {
            // Given & When & Then
            PromotionType.BUY_ONE_GET_ONE.isQuantityBased() shouldBe true
            
            PromotionType.PERCENTAGE_DISCOUNT.isQuantityBased() shouldBe false
            PromotionType.FIXED_DISCOUNT.isQuantityBased() shouldBe false
            PromotionType.FREE_SHIPPING.isQuantityBased() shouldBe false
            PromotionType.CASHBACK.isQuantityBased() shouldBe false
        }
    }
    
    describe("배송 관련 프로모션 확인") {
        it("무료 배송만 배송 관련이어야 한다") {
            // Given & When & Then
            PromotionType.FREE_SHIPPING.isShippingRelated() shouldBe true
            
            PromotionType.PERCENTAGE_DISCOUNT.isShippingRelated() shouldBe false
            PromotionType.FIXED_DISCOUNT.isShippingRelated() shouldBe false
            PromotionType.BUY_ONE_GET_ONE.isShippingRelated() shouldBe false
            PromotionType.CASHBACK.isShippingRelated() shouldBe false
        }
    }
    
    describe("캐시백 프로모션 확인") {
        it("캐시백만 캐시백 프로모션이어야 한다") {
            // Given & When & Then
            PromotionType.CASHBACK.isCashback() shouldBe true
            
            PromotionType.PERCENTAGE_DISCOUNT.isCashback() shouldBe false
            PromotionType.FIXED_DISCOUNT.isCashback() shouldBe false
            PromotionType.BUY_ONE_GET_ONE.isCashback() shouldBe false
            PromotionType.FREE_SHIPPING.isCashback() shouldBe false
        }
    }
    
    describe("문자열 표현") {
        it("toString은 displayName을 반환해야 한다") {
            // Given & When & Then
            PromotionType.PERCENTAGE_DISCOUNT.toString() shouldBe "퍼센트 할인"
            PromotionType.FIXED_DISCOUNT.toString() shouldBe "고정 금액 할인"
            PromotionType.BUY_ONE_GET_ONE.toString() shouldBe "1+1 프로모션"
            PromotionType.FREE_SHIPPING.toString() shouldBe "무료 배송"
            PromotionType.CASHBACK.toString() shouldBe "캐시백"
        }
    }
})
