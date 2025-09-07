package com.example.demo.domain.promotion.model

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
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
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDateTime

class PromotionTest : DescribeSpec({
    
    describe("Promotion 생성") {
        it("유효한 정보로 Promotion을 생성할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When & Then
            promotion.name shouldBe "테스트 프로모션"
            promotion.type shouldBe PromotionType.PERCENTAGE_DISCOUNT
            promotion.priority shouldBe 1
            promotion.isActive shouldBe true
        }
        
        it("빈 이름으로 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Promotion.create(
                    name = "",
                    description = "설명",
                    type = PromotionType.PERCENTAGE_DISCOUNT,
                    priority = 1,
                    period = createTestPeriod(),
                    conditions = PromotionConditions.noConditions(),
                    benefits = createTestBenefits()
                )
            }
        }
        
        it("음수 우선순위로 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                Promotion.create(
                    name = "테스트 프로모션",
                    description = "설명",
                    type = PromotionType.PERCENTAGE_DISCOUNT,
                    priority = -1,
                    period = createTestPeriod(),
                    conditions = PromotionConditions.noConditions(),
                    benefits = createTestBenefits()
                )
            }
        }
    }
    
    describe("Promotion 적용 가능성 확인") {
        it("유효한 프로모션은 적용 가능해야 한다") {
            // Given
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val isApplicable = promotion.isApplicableTo(cart, user)
            
            // Then
            isApplicable shouldBe true
        }
        
        it("비활성화된 프로모션은 적용 불가능해야 한다") {
            // Given
            val promotion = createTestPromotion().deactivate()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val isApplicable = promotion.isApplicableTo(cart, user)
            
            // Then
            isApplicable shouldBe false
        }
        
        it("기간이 유효하지 않은 프로모션은 적용 불가능해야 한다") {
            // Given
            val expiredPeriod = PromotionPeriod.of(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
            )
            val promotion = createTestPromotion().copy(period = expiredPeriod)
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val isApplicable = promotion.isApplicableTo(cart, user)
            
            // Then
            isApplicable shouldBe false
        }
        
        it("조건을 충족하지 않는 프로모션은 적용 불가능해야 한다") {
            // Given
            val conditions = PromotionConditions.forMinAmount(Money.of(100000L)) // 높은 최소 금액
            val promotion = createTestPromotion().copy(conditions = conditions)
            val cart = createTestCart() // 낮은 금액의 장바구니
            val user = createTestUser()
            
            // When
            val isApplicable = promotion.isApplicableTo(cart, user)
            
            // Then
            isApplicable shouldBe false
        }
    }
    
    describe("Promotion 할인 계산") {
        it("적용 가능한 프로모션의 할인을 계산할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val discount = promotion.calculateDiscount(cart, user)
            
            // Then
            discount.amount.amount shouldBe Money.of(1000L).amount // 10% 할인 (10000 * 0.1 = 1000)
        }
        
        it("적용 불가능한 프로모션의 할인은 0원이어야 한다") {
            // Given
            val promotion = createTestPromotion().deactivate()
            val cart = createTestCart()
            val user = createTestUser()
            
            // When
            val discount = promotion.calculateDiscount(cart, user)
            
            // Then
            discount.isZero() shouldBe true
        }
    }
    
    describe("Promotion 상태 관리") {
        it("프로모션을 활성화할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion().deactivate()
            
            // When
            val activatedPromotion = promotion.activate()
            
            // Then
            activatedPromotion.isActive shouldBe true
            activatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("프로모션을 비활성화할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val deactivatedPromotion = promotion.deactivate()
            
            // Then
            deactivatedPromotion.isActive shouldBe false
            deactivatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("프로모션의 유효성을 확인할 수 있어야 한다") {
            // Given
            val validPromotion = createTestPromotion()
            val invalidPromotion = createTestPromotion().deactivate()
            
            // When & Then
            validPromotion.isValid() shouldBe true
            invalidPromotion.isValid() shouldBe false
        }
    }
    
    describe("Promotion 상태 확인") {
        it("프로모션 시작 여부를 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val startedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            val notStartedPeriod = PromotionPeriod.of(now.plusDays(1), now.plusDays(2))
            
            val startedPromotion = createTestPromotion().copy(period = startedPeriod)
            val notStartedPromotion = createTestPromotion().copy(period = notStartedPeriod)
            
            // When & Then
            startedPromotion.hasStarted() shouldBe true
            notStartedPromotion.hasStarted() shouldBe false
        }
        
        it("프로모션 종료 여부를 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val endedPeriod = PromotionPeriod.of(now.minusDays(2), now.minusDays(1))
            val notEndedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            val endedPromotion = createTestPromotion().copy(period = endedPeriod)
            val notEndedPromotion = createTestPromotion().copy(period = notEndedPeriod)
            
            // When & Then
            endedPromotion.hasEnded() shouldBe true
            notEndedPromotion.hasEnded() shouldBe false
        }
        
        it("프로모션 시작 전 여부를 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val beforeStartPeriod = PromotionPeriod.of(now.plusDays(1), now.plusDays(2))
            val startedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            val beforeStartPromotion = createTestPromotion().copy(period = beforeStartPeriod)
            val startedPromotion = createTestPromotion().copy(period = startedPeriod)
            
            // When & Then
            beforeStartPromotion.isBeforeStart() shouldBe true
            startedPromotion.isBeforeStart() shouldBe false
        }
    }
    
    describe("Promotion 수정") {
        it("우선순위를 변경할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val updatedPromotion = promotion.changePriority(5)
            
            // Then
            updatedPromotion.priority shouldBe 5
            updatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("음수 우선순위로 변경하면 예외가 발생해야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                promotion.changePriority(-1)
            }
        }
        
        it("설명을 업데이트할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val updatedPromotion = promotion.updateDescription("새로운 설명")
            
            // Then
            updatedPromotion.description shouldBe "새로운 설명"
            updatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("기간을 연장할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val extendedPromotion = promotion.extendPeriod(3L)
            
            // Then
            extendedPromotion.period.endDate shouldBe promotion.period.endDate.plusDays(3)
            extendedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("기간을 단축할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val shortenedPromotion = promotion.shortenPeriod(2L)
            
            // Then
            shortenedPromotion.period.endDate shouldBe promotion.period.endDate.minusDays(2)
            shortenedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("조건을 업데이트할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            val newConditions = PromotionConditions.forMinAmount(Money.of(50000L))
            
            // When
            val updatedPromotion = promotion.updateConditions(newConditions)
            
            // Then
            updatedPromotion.conditions shouldBe newConditions
            updatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
        
        it("혜택을 업데이트할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            val newBenefits = PromotionBenefits.fixedAmountDiscount(Money.of(5000L))
            
            // When
            val updatedPromotion = promotion.updateBenefits(newBenefits)
            
            // Then
            updatedPromotion.benefits shouldBe newBenefits
            updatedPromotion.updatedAt shouldNotBe promotion.updatedAt
        }
    }
    
    describe("Promotion 정보") {
        it("상태 설명을 반환할 수 있어야 한다") {
            // Given
            val activePromotion = createTestPromotion()
            val inactivePromotion = createTestPromotion().deactivate()
            
            // When & Then
            activePromotion.getStatusDescription() shouldBe "진행 중"
            inactivePromotion.getStatusDescription() shouldBe "비활성화됨"
        }
        
        it("요약 정보를 반환할 수 있어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val summary = promotion.getSummary()
            
            // Then
            summary shouldBe "테스트 프로모션 (퍼센트 할인) - 10% 할인"
        }
    }
    
    describe("Promotion 복사") {
        it("기존 프로모션을 복사하여 새로운 프로모션을 생성할 수 있어야 한다") {
            // Given
            val original = createTestPromotion()
            
            // When
            val copied = Promotion.copyFrom(original, "복사된 프로모션", "복사 설명")
            
            // Then
            copied.name shouldBe "복사된 프로모션"
            copied.description shouldBe "복사 설명"
            copied.type shouldBe original.type
            copied.priority shouldBe original.priority
            copied.createdAt shouldNotBe original.createdAt
        }
    }
    
    describe("Promotion 문자열 표현") {
        it("toString은 적절한 형식이어야 한다") {
            // Given
            val promotion = createTestPromotion()
            
            // When
            val stringRepresentation = promotion.toString()
            
            // Then
            stringRepresentation shouldContain "Promotion"
            stringRepresentation shouldContain "테스트 프로모션"
            stringRepresentation shouldContain "PERCENTAGE_DISCOUNT"
        }
    }
})

// 테스트용 헬퍼 함수들
private fun createTestPromotion(): Promotion {
    return Promotion.create(
        name = "테스트 프로모션",
        description = "테스트용 프로모션입니다",
        type = PromotionType.PERCENTAGE_DISCOUNT,
        priority = 1,
        period = createTestPeriod(),
        conditions = PromotionConditions.noConditions(),
        benefits = createTestBenefits()
    )
}

private fun createTestPeriod(): PromotionPeriod {
    val now = LocalDateTime.now()
    return PromotionPeriod.of(now.minusDays(1), now.plusDays(7))
}

private fun createTestBenefits(): PromotionBenefits {
    return PromotionBenefits.percentageDiscount(DiscountPercentage.of(10))
}

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
