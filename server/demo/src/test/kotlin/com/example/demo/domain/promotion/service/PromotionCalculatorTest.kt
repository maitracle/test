package com.example.demo.domain.promotion.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionType
import com.example.demo.domain.promotion.valueobject.Discount
import com.example.demo.domain.promotion.valueobject.DiscountPercentage
import com.example.demo.domain.promotion.valueobject.PromotionBenefits
import com.example.demo.domain.promotion.valueobject.PromotionConditions
import com.example.demo.domain.promotion.valueobject.PromotionPeriod
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.LocalDateTime

class PromotionCalculatorTest : DescribeSpec({
    
    describe("PromotionCalculator 할인 계산") {
        it("단일 프로모션의 할인을 계산할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion()
            val user = createTestUser()
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.hasDiscount() shouldBe true
            result.totalDiscount shouldBe Money.of(1000L).amount // 10% 할인
            result.finalAmount shouldBe Money.of(9000L).amount
            result.appliedPromotions.size shouldBe 1
            result.appliedPromotions[0].promotionName shouldBe "테스트 프로모션"
        }
        
        it("여러 프로모션의 할인을 계산할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion1 = createTestPromotion()
            val promotion2 = createFixedDiscountPromotion()
            val user = createTestUser()
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion1, promotion2), user)
            
            // Then
            result.hasDiscount() shouldBe true
            result.totalDiscount shouldBe Money.of(1500L).amount // 1000 + 500
            result.finalAmount shouldBe Money.of(8500L).amount
            result.appliedPromotions.size shouldBe 2
        }
        
        it("적용 불가능한 프로모션은 할인에 포함되지 않아야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion().deactivate()
            val user = createTestUser()
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.hasDiscount() shouldBe false
            result.totalDiscount shouldBe BigDecimal.ZERO
            result.finalAmount shouldBe cart.totalAmount.amount
            result.appliedPromotions.size shouldBe 0
        }
        
        it("할인이 없는 경우 올바른 결과를 반환해야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val result = calculator.calculateDiscounts(cart, emptyList(), user)
            
            // Then
            result.hasDiscount() shouldBe false
            result.totalDiscount shouldBe BigDecimal.ZERO
            result.finalAmount shouldBe cart.totalAmount.amount
            result.appliedPromotions.size shouldBe 0
        }
    }
    
    describe("PromotionCalculator 단일 프로모션 계산") {
        it("단일 프로모션의 할인을 계산할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion()
            val user = createTestUser()
            
            // When
            val discount = calculator.calculateSinglePromotionDiscount(cart, promotion, user)
            
            // Then
            discount.isApplied() shouldBe true
            discount.amount.amount shouldBe Money.of(1000L).amount
        }
        
        it("적용 불가능한 프로모션의 할인은 0원이어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion().deactivate()
            val user = createTestUser()
            
            // When
            val discount = calculator.calculateSinglePromotionDiscount(cart, promotion, user)
            
            // Then
            discount.isZero() shouldBe true
        }
    }
    
    describe("PromotionCalculator 프로모션 비교") {
        it("여러 프로모션의 할인을 비교할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion1 = createTestPromotion() // 10% 할인 (1000원)
            val promotion2 = createFixedDiscountPromotion() // 500원 할인
            val user = createTestUser()
            
            // When
            val comparisons = calculator.comparePromotionDiscounts(cart, listOf(promotion1, promotion2), user)
            
            // Then
            comparisons.size shouldBe 2
            comparisons[0].first shouldBe promotion1 // 더 큰 할인이 먼저
            comparisons[0].second.amount.amount shouldBe Money.of(1000L).amount
            comparisons[1].first shouldBe promotion2
            comparisons[1].second.amount.amount shouldBe Money.of(500L).amount
        }
    }
    
    describe("PromotionCalculator 최적 조합 찾기") {
        it("최적의 프로모션 조합을 찾을 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotions = listOf(
                createTestPromotion(),
                createFixedDiscountPromotion(),
                createTestPromotion().copy(name = "또 다른 프로모션", priority = 2)
            )
            val user = createTestUser()
            
            // When
            val result = calculator.findOptimalPromotionCombination(cart, promotions, user, 2)
            
            // Then
            result.hasDiscount() shouldBe true
            result.appliedPromotions.size shouldBe 2
        }
        
        it("최대 프로모션 개수를 제한할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotions = listOf(
                createTestPromotion(),
                createFixedDiscountPromotion(),
                createTestPromotion().copy(name = "세 번째 프로모션", priority = 2)
            )
            val user = createTestUser()
            
            // When
            val result = calculator.findOptimalPromotionCombination(cart, promotions, user, 1)
            
            // Then
            result.appliedPromotions.size shouldBe 1
        }
    }
    
    describe("PromotionCalculator 미리보기") {
        it("프로모션 적용 가능성을 미리 확인할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion()
            val user = createTestUser()
            
            // When
            val (isApplicable, expectedDiscount) = calculator.previewPromotionApplication(cart, promotion, user)
            
            // Then
            isApplicable shouldBe true
            expectedDiscount.isApplied() shouldBe true
            expectedDiscount.amount.amount shouldBe Money.of(1000L).amount
        }
        
        it("적용 불가능한 프로모션의 미리보기를 확인할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion().deactivate()
            val user = createTestUser()
            
            // When
            val (isApplicable, expectedDiscount) = calculator.previewPromotionApplication(cart, promotion, user)
            
            // Then
            isApplicable shouldBe false
            expectedDiscount.isZero() shouldBe true
        }
    }
    
    describe("PromotionCalculator 최종 금액 계산") {
        it("최종 금액을 계산할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion()
            val user = createTestUser()
            
            // When
            val finalAmount = calculator.calculateFinalAmount(cart, listOf(promotion), user)
            
            // Then
            finalAmount shouldBe Money.of(9000L).amount
        }
        
        it("절약 금액을 계산할 수 있어야 한다") {
            // Given
            val calculator = PromotionCalculator()
            val cart = createTestCart()
            val promotion = createTestPromotion()
            val user = createTestUser()
            
            // When
            val savings = calculator.calculateSavings(cart, listOf(promotion), user)
            
            // Then
            savings shouldBe Money.of(1000L).amount
        }
    }
    
    describe("PromotionResult") {
        it("할인율을 계산할 수 있어야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal.valueOf(10000),
                totalDiscount = BigDecimal.valueOf(1000),
                finalAmount = BigDecimal.valueOf(9000),
                appliedPromotions = emptyList()
            )
            
            // When
            val discountRatio = result.getDiscountRatio()
            
            // Then
            discountRatio shouldBe 0.1
        }
        
        it("할인이 없는 경우 할인율은 0이어야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal.valueOf(10000),
                totalDiscount = BigDecimal.ZERO,
                finalAmount = BigDecimal.valueOf(10000),
                appliedPromotions = emptyList()
            )
            
            // When
            val discountRatio = result.getDiscountRatio()
            
            // Then
            discountRatio shouldBe 0.0
        }
        
        it("적용된 프로모션 개수를 반환할 수 있어야 한다") {
            // Given
            val appliedPromotions = listOf(
                AppliedPromotion(1L, "프로모션1", BigDecimal.valueOf(500)),
                AppliedPromotion(2L, "프로모션2", BigDecimal.valueOf(500))
            )
            val result = PromotionResult(
                subtotal = BigDecimal.valueOf(10000),
                totalDiscount = BigDecimal.valueOf(1000),
                finalAmount = BigDecimal.valueOf(9000),
                appliedPromotions = appliedPromotions
            )
            
            // When
            val count = result.getAppliedPromotionCount()
            
            // Then
            count shouldBe 2
        }
    }
})

// 테스트용 헬퍼 함수들
private fun createTestCart(): Cart {
    val product = createTestProduct()
    return Cart.createEmpty(CartId.of(1L), UserId.of(1L)).addItem(product, Quantity.of(1))
}

private fun createTestProduct(): Product {
    return Product.createNew(
        id = ProductId.of(1L),
        name = "테스트 상품",
        description = "테스트용 상품입니다",
        price = Price.of(Money.of(10000L)),
        stock = Stock.of(100),
        category = "테스트 카테고리",
        brand = "테스트 브랜드"
    )
}

private fun createTestUser(): User {
    return User.createExistingUser(
        id = UserId.of(1L),
        email = Email("test@example.com"),
        membershipLevel = MembershipLevel.REGULAR,
        isNewCustomer = false,
        createdAt = LocalDateTime.now()
    )
}

private fun createTestPromotion(): Promotion {
    return Promotion.create(
        name = "테스트 프로모션",
        description = "테스트용 프로모션입니다",
        type = PromotionType.PERCENTAGE_DISCOUNT,
        priority = 1,
        period = createTestPeriod(),
        conditions = PromotionConditions.noConditions(),
        benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(10))
    )
}

private fun createFixedDiscountPromotion(): Promotion {
    return Promotion.create(
        name = "고정 할인 프로모션",
        description = "고정 금액 할인 프로모션입니다",
        type = PromotionType.FIXED_DISCOUNT,
        priority = 2,
        period = createTestPeriod(),
        conditions = PromotionConditions.noConditions(),
        benefits = PromotionBenefits.fixedAmountDiscount(Money.of(500L))
    )
}

private fun createTestPeriod(): PromotionPeriod {
    val now = LocalDateTime.now()
    return PromotionPeriod.of(now.minusDays(1), now.plusDays(7))
}
