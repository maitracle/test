package com.example.demo.domain.promotion.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionType
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
import io.kotest.matchers.collections.shouldContain
import java.math.BigDecimal
import java.time.LocalDateTime

class PromotionRuleEngineTest : DescribeSpec({
    
    describe("PromotionRuleEngine 규칙 평가") {
        it("모든 규칙을 통과한 프로모션은 적용 가능해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val result = ruleEngine.evaluateRules(promotion, cart, user)
            
            // Then
            result.isEligible shouldBe true
            result.allRulesPassed() shouldBe true
            result.hasFailedRules() shouldBe false
            result.passedRules.size shouldBe 4
        }
        
        it("비활성화된 프로모션은 규칙 평가에서 실패해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion().deactivate()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val result = ruleEngine.evaluateRules(promotion, cart, user)
            
            // Then
            result.isEligible shouldBe false
            result.hasFailedRules() shouldBe true
            result.failedRules shouldContain "프로모션이 비활성화되어 있습니다"
        }
        
        it("기간이 유효하지 않은 프로모션은 규칙 평가에서 실패해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val expiredPeriod = PromotionPeriod.of(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
            )
            val promotion = createTestPromotion().copy(period = expiredPeriod)
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val result = ruleEngine.evaluateRules(promotion, cart, user)
            
            // Then
            result.isEligible shouldBe false
            result.hasFailedRules() shouldBe true
            result.failedRules.any { it.contains("프로모션 기간이 유효하지 않습니다") } shouldBe true
        }
        
        it("조건을 충족하지 않는 프로모션은 규칙 평가에서 실패해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val conditions = PromotionConditions.forMinAmount(Money.of(100000L))
            val promotion = createTestPromotion().copy(conditions = conditions)
            val cart = createTestCart() // 낮은 금액의 장바구니
            val user = createTestUser()
            
            // When
            val result = ruleEngine.evaluateRules(promotion, cart, user)
            
            // Then
            result.isEligible shouldBe false
            result.hasFailedRules() shouldBe true
            result.failedRules.any { it.contains("프로모션 조건을 충족하지 않습니다") } shouldBe true
        }
    }
    
    describe("PromotionRuleEngine 적용 가능성 확인") {
        it("적용 가능한 프로모션을 올바르게 식별해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val isEligible = ruleEngine.isEligibleForPromotion(promotion, cart, user)
            
            // Then
            isEligible shouldBe true
        }
        
        it("적용 불가능한 프로모션을 올바르게 식별해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion().deactivate()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val isEligible = ruleEngine.isEligibleForPromotion(promotion, cart, user)
            
            // Then
            isEligible shouldBe false
        }
    }
    
    describe("PromotionRuleEngine 필터링") {
        it("적용 가능한 프로모션들을 필터링할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotions = listOf(
                createTestPromotion(),
                createTestPromotion().deactivate(),
                createTestPromotion().copy(name = "또 다른 프로모션")
            )
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val eligiblePromotions = ruleEngine.filterEligiblePromotions(promotions, cart, user)
            
            // Then
            eligiblePromotions.size shouldBe 2
            eligiblePromotions.all { it.isActive } shouldBe true
        }
        
        it("모든 프로모션이 적용 불가능한 경우 빈 목록을 반환해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotions = listOf(
                createTestPromotion().deactivate(),
                createTestPromotion().deactivate()
            )
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val eligiblePromotions = ruleEngine.filterEligiblePromotions(promotions, cart, user)
            
            // Then
            eligiblePromotions.size shouldBe 0
        }
    }
    
    describe("PromotionRuleEngine 개별 조건 평가") {
        it("각 조건을 개별적으로 평가할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val results = ruleEngine.evaluateIndividualConditions(promotion, cart, user)
            
            // Then
            results["isActive"] shouldBe true
            results["isPeriodValid"] shouldBe true
            results["isCategorySatisfied"] shouldBe true
            results["isMinAmountSatisfied"] shouldBe true
            results["isMinQuantitySatisfied"] shouldBe true
            results["isUserLevelSatisfied"] shouldBe true
        }
        
        it("카테고리 조건이 설정된 경우 올바르게 평가해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val conditions = PromotionConditions.forCategory("테스트 카테고리")
            val promotion = createTestPromotion().copy(conditions = conditions)
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val results = ruleEngine.evaluateIndividualConditions(promotion, cart, user)
            
            // Then
            results["isCategorySatisfied"] shouldBe true
        }
        
        it("카테고리 조건을 충족하지 않는 경우 올바르게 평가해야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val conditions = PromotionConditions.forCategory("다른 카테고리")
            val promotion = createTestPromotion().copy(conditions = conditions)
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val results = ruleEngine.evaluateIndividualConditions(promotion, cart, user)
            
            // Then
            results["isCategorySatisfied"] shouldBe false
        }
    }
    
    describe("PromotionRuleEngine 충돌 감지") {
        it("충돌하는 프로모션을 감지할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion1 = createTestPromotion()
            val promotion2 = createTestPromotion().copy(name = "충돌 프로모션", priority = 1)
            
            // When
            val conflicts = ruleEngine.detectPromotionConflicts(listOf(promotion1, promotion2))
            
            // Then
            conflicts.size shouldBe 1
            conflicts[0].first shouldBe promotion1
            conflicts[0].second shouldBe promotion2
        }
        
        it("충돌하지 않는 프로모션은 감지하지 않아야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion1 = createTestPromotion()
            val differentPeriod = PromotionPeriod.of(
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(20)
            )
            val promotion2 = createTestPromotion().copy(
                name = "다른 프로모션",
                type = PromotionType.FIXED_DISCOUNT,
                priority = 2,
                period = differentPeriod
            )
            
            // When
            val conflicts = ruleEngine.detectPromotionConflicts(listOf(promotion1, promotion2))
            
            // Then
            conflicts.size shouldBe 0
        }
    }
    
    describe("PromotionRuleEngine 정렬") {
        it("우선순위로 정렬할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion1 = createTestPromotion().copy(priority = 3)
            val promotion2 = createTestPromotion().copy(priority = 1)
            val promotion3 = createTestPromotion().copy(priority = 2)
            
            // When
            val sorted = ruleEngine.sortByPriority(listOf(promotion1, promotion2, promotion3))
            
            // Then
            sorted[0].priority shouldBe 1
            sorted[1].priority shouldBe 2
            sorted[2].priority shouldBe 3
        }
        
        it("할인 금액으로 정렬할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion1 = createTestPromotion().copy(
                name = "낮은 할인",
                benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(5))
            )
            val promotion2 = createTestPromotion().copy(
                name = "높은 할인",
                benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(15))
            )
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val sorted = ruleEngine.sortByDiscountAmount(listOf(promotion1, promotion2), cart, user)
            
            // Then
            sorted.size shouldBe 2
            // 정렬이 올바르게 되었는지 확인 (높은 할인이 먼저)
            val firstDiscount = sorted[0].calculateDiscount(cart, user).amount.amount
            val secondDiscount = sorted[1].calculateDiscount(cart, user).amount.amount
            firstDiscount shouldBe BigDecimal("1500.00") // 15% 할인
            secondDiscount shouldBe BigDecimal("500.00") // 5% 할인
        }
    }
    
    describe("PromotionRuleEngine 효율성 평가") {
        it("프로모션의 효율성을 평가할 수 있어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val efficiency = ruleEngine.evaluatePromotionEfficiency(promotion, cart, user)
            
            // Then
            efficiency shouldBe 0.367 // 실제 계산된 효율성 (약 36.7%)
        }
        
        it("적용 불가능한 프로모션의 효율성은 0이어야 한다") {
            // Given
            val ruleEngine = PromotionRuleEngine()
            val promotion = createTestPromotion().deactivate()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val efficiency = ruleEngine.evaluatePromotionEfficiency(promotion, cart, user)
            
            // Then
            efficiency shouldBe 0.0
        }
    }
    
    describe("RuleEvaluationResult") {
        it("모든 규칙이 통과했는지 확인할 수 있어야 한다") {
            // Given
            val result = RuleEvaluationResult(
                isEligible = true,
                failedRules = emptyList(),
                passedRules = listOf("규칙1", "규칙2")
            )
            
            // When & Then
            result.allRulesPassed() shouldBe true
            result.hasFailedRules() shouldBe false
            result.getPassedRuleCount() shouldBe 2
            result.getFailedRuleCount() shouldBe 0
        }
        
        it("실패한 규칙이 있는지 확인할 수 있어야 한다") {
            // Given
            val result = RuleEvaluationResult(
                isEligible = false,
                failedRules = listOf("실패한 규칙"),
                passedRules = listOf("통과한 규칙")
            )
            
            // When & Then
            result.allRulesPassed() shouldBe false
            result.hasFailedRules() shouldBe true
            result.getPassedRuleCount() shouldBe 1
            result.getFailedRuleCount() shouldBe 1
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

private fun createTestPeriod(): PromotionPeriod {
    val now = LocalDateTime.now()
    return PromotionPeriod.of(now.minusDays(1), now.plusDays(7))
}
