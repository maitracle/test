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
import com.example.demo.domain.promotion.model.PromotionId
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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.assertions.throwables.shouldThrow
import java.math.BigDecimal
import java.time.LocalDateTime

class PromotionCalculatorTest : FunSpec({
    
    val calculator = PromotionCalculator()
    
    // 테스트 데이터 생성 헬퍼 함수들
    fun createTestUser(
        id: Long = 1L,
        email: String = "test@example.com",
        membershipLevel: MembershipLevel = MembershipLevel.REGULAR,
        isNewCustomer: Boolean = false
    ): User {
        return User.createExistingUser(
            id = UserId(id),
            email = Email(email),
            membershipLevel = membershipLevel,
            isNewCustomer = isNewCustomer,
            createdAt = LocalDateTime.now().minusDays(30)
        )
    }
    
    fun createTestProduct(
        id: Long = 1L,
        name: String = "Test Product",
        price: BigDecimal = BigDecimal("10000"),
        category: String? = null,
        brand: String? = null
    ): Product {
        return Product.createNew(
            id = ProductId.of(id),
            name = name,
            price = Price.of(price),
            stock = Stock.of(100),
            category = category,
            brand = brand
        )
    }
    
    fun createTestCart(
        userId: Long = 1L,
        items: List<CartItem>
    ): Cart {
        return Cart.createExisting(
            id = CartId.of(1L),
            userId = UserId(userId),
            items = items,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
    
    fun createTestCartItem(
        product: Product,
        quantity: Int = 1,
        unitPrice: BigDecimal? = null
    ): CartItem {
        return CartItem(
            product = product,
            quantity = Quantity.of(quantity),
            unitPrice = Money.of(unitPrice ?: product.price.amount.amount)
        )
    }
    
    fun createTestPromotion(
        id: Long = 1L,
        name: String = "Test Promotion",
        type: PromotionType = PromotionType.PERCENTAGE_DISCOUNT,
        priority: Int = 1,
        conditions: PromotionConditions = PromotionConditions(),
        benefits: PromotionBenefits,
        isActive: Boolean = true
    ): Promotion {
        val now = LocalDateTime.now()
        val period = PromotionPeriod(
            startDate = now.minusDays(1),
            endDate = now.plusDays(30)
        )
        
        return Promotion.create(
            name = name,
            description = "Test promotion description",
            type = type,
            priority = priority,
            period = period,
            conditions = conditions,
            benefits = benefits
        ).copy(id = PromotionId.of(id), isActive = isActive)
    }
    
    context("AppliedPromotion 데이터 클래스") {
        test("할인 금액이 있는 경우 hasDiscount는 true를 반환해야 한다") {
            // Given
            val appliedPromotion = AppliedPromotion(
                promotionId = 1L,
                promotionName = "Test Promotion",
                discountAmount = BigDecimal("1000")
            )
            
            // When & Then
            appliedPromotion.discountAmount shouldBe BigDecimal("1000")
        }
        
        test("프로모션 정보가 올바르게 저장되어야 한다") {
            // Given
            val appliedPromotion = AppliedPromotion(
                promotionId = 2L,
                promotionName = "VIP 할인",
                discountAmount = BigDecimal("5000")
            )
            
            // When & Then
            appliedPromotion.promotionId shouldBe 2L
            appliedPromotion.promotionName shouldBe "VIP 할인"
            appliedPromotion.discountAmount shouldBe BigDecimal("5000")
        }
    }
    
    context("PromotionResult 데이터 클래스") {
        test("할인이 적용된 경우 hasDiscount는 true를 반환해야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal("10000"),
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("9000"),
                appliedPromotions = listOf(
                    AppliedPromotion(1L, "Test", BigDecimal("1000"))
                )
            )
            
            // When & Then
            result.hasDiscount() shouldBe true
            result.getAppliedPromotionCount() shouldBe 1
            result.getDiscountRatio() shouldBe 0.1
        }
        
        test("할인이 없는 경우 hasDiscount는 false를 반환해야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal("10000"),
                totalDiscount = BigDecimal.ZERO,
                finalAmount = BigDecimal("10000"),
                appliedPromotions = emptyList()
            )
            
            // When & Then
            result.hasDiscount() shouldBe false
            result.getAppliedPromotionCount() shouldBe 0
            result.getDiscountRatio() shouldBe 0.0
        }
        
        test("할인율이 올바르게 계산되어야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal("20000"),
                totalDiscount = BigDecimal("5000"),
                finalAmount = BigDecimal("15000"),
                appliedPromotions = listOf(
                    AppliedPromotion(1L, "Test", BigDecimal("5000"))
                )
            )
            
            // When & Then
            result.getDiscountRatio() shouldBe 0.25
        }
        
        test("소계가 0인 경우 할인율은 0이어야 한다") {
            // Given
            val result = PromotionResult(
                subtotal = BigDecimal.ZERO,
                totalDiscount = BigDecimal("1000"),
                finalAmount = BigDecimal("-1000"),
                appliedPromotions = listOf(
                    AppliedPromotion(1L, "Test", BigDecimal("1000"))
                )
            )
            
            // When & Then
            result.getDiscountRatio() shouldBe 0.0
        }
    }
    
    context("calculateDiscounts 메서드") {
        test("단일 프로모션 할인을 올바르게 계산해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("10000")
            result.totalDiscount shouldBe BigDecimal("1000.00")
            result.finalAmount shouldBe BigDecimal("9000.00")
            result.appliedPromotions shouldHaveSize 1
            result.appliedPromotions[0].promotionId shouldBe promotion.id.value
            result.appliedPromotions[0].promotionName shouldBe promotion.name
            result.appliedPromotions[0].discountAmount shouldBe BigDecimal("1000.00")
        }
        
        test("여러 프로모션이 동시에 적용되어야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion1 = createTestPromotion(
                id = 1L,
                name = "10% 할인",
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            val promotion2 = createTestPromotion(
                id = 2L,
                name = "1000원 할인",
                type = PromotionType.FIXED_DISCOUNT,
                benefits = PromotionBenefits.fixedAmountDiscount(Money.of(BigDecimal("1000")))
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion1, promotion2), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("10000")
            result.totalDiscount shouldBe BigDecimal("2000.00") // 1000 + 1000
            result.finalAmount shouldBe BigDecimal("8000.00")
            result.appliedPromotions shouldHaveSize 2
        }
        
        test("조건을 만족하지 않는 프로모션은 적용되지 않아야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("5000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                conditions = PromotionConditions(
                    minCartAmount = Money.of(BigDecimal("10000"))
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("5000")
            result.totalDiscount shouldBe BigDecimal.ZERO
            result.finalAmount shouldBe BigDecimal("5000")
            result.appliedPromotions shouldBe emptyList()
        }
        
        test("비활성화된 프로모션은 적용되지 않아야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                isActive = false,
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("10000")
            result.totalDiscount shouldBe BigDecimal.ZERO
            result.finalAmount shouldBe BigDecimal("10000")
            result.appliedPromotions shouldBe emptyList()
        }
        
        test("빈 프로모션 목록으로 계산 시 할인이 없어야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            // When
            val result = calculator.calculateDiscounts(cart, emptyList(), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("10000")
            result.totalDiscount shouldBe BigDecimal.ZERO
            result.finalAmount shouldBe BigDecimal("10000")
            result.appliedPromotions shouldBe emptyList()
        }
    }
    
    context("calculateSinglePromotionDiscount 메서드") {
        test("단일 프로모션의 할인을 올바르게 계산해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(15),
                    null
                )
            )
            
            // When
            val discount = calculator.calculateSinglePromotionDiscount(cart, promotion, user)
            
            // Then
            discount.toBigDecimal() shouldBe BigDecimal("1500.00")
            discount.isApplied() shouldBe true
        }
        
        test("조건을 만족하지 않는 프로모션은 0원 할인을 반환해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("5000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                conditions = PromotionConditions(
                    minCartAmount = Money.of(BigDecimal("10000"))
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val discount = calculator.calculateSinglePromotionDiscount(cart, promotion, user)
            
            // Then
            discount.toBigDecimal() shouldBe BigDecimal.ZERO
            discount.isZero() shouldBe true
        }
    }
    
    context("comparePromotionDiscounts 메서드") {
        test("프로모션들을 할인 금액 순으로 정렬해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion1 = createTestPromotion(
                id = 1L,
                name = "5% 할인",
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(5),
                    null
                )
            )
            
            val promotion2 = createTestPromotion(
                id = 2L,
                name = "15% 할인",
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(15),
                    null
                )
            )
            
            val promotion3 = createTestPromotion(
                id = 3L,
                name = "10% 할인",
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val comparisons = calculator.comparePromotionDiscounts(
                cart, 
                listOf(promotion1, promotion2, promotion3), 
                user
            )
            
            // Then
            comparisons shouldHaveSize 3
            comparisons[0].first.id shouldBe promotion2.id // 15% 할인 (1500원)
            comparisons[1].first.id shouldBe promotion3.id // 10% 할인 (1000원)
            comparisons[2].first.id shouldBe promotion1.id // 5% 할인 (500원)
            
            comparisons[0].second.toBigDecimal() shouldBe BigDecimal("1500.00")
            comparisons[1].second.toBigDecimal() shouldBe BigDecimal("1000.00")
            comparisons[2].second.toBigDecimal() shouldBe BigDecimal("500.00")
        }
        
        test("적용되지 않는 프로모션도 포함되어야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("5000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val applicablePromotion = createTestPromotion(
                id = 1L,
                name = "적용 가능한 프로모션",
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            val inapplicablePromotion = createTestPromotion(
                id = 2L,
                name = "적용 불가능한 프로모션",
                conditions = PromotionConditions(
                    minCartAmount = Money.of(BigDecimal("10000"))
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(20),
                    null
                )
            )
            
            // When
            val comparisons = calculator.comparePromotionDiscounts(
                cart, 
                listOf(applicablePromotion, inapplicablePromotion), 
                user
            )
            
            // Then
            comparisons shouldHaveSize 2
            comparisons[0].first.id shouldBe applicablePromotion.id
            comparisons[0].second.toBigDecimal() shouldBe BigDecimal("500.00")
            comparisons[1].first.id shouldBe inapplicablePromotion.id
            comparisons[1].second.toBigDecimal() shouldBe BigDecimal.ZERO
        }
    }
    
    context("findOptimalPromotionCombination 메서드") {
        test("최적의 프로모션 조합을 찾아야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotions = listOf(
                createTestPromotion(
                    id = 1L,
                    name = "5% 할인",
                    priority = 3,
                    benefits = PromotionBenefits.percentageDiscount(
                        DiscountPercentage.of(5),
                        null
                    )
                ),
                createTestPromotion(
                    id = 2L,
                    name = "15% 할인",
                    priority = 1,
                    benefits = PromotionBenefits.percentageDiscount(
                        DiscountPercentage.of(15),
                        null
                    )
                ),
                createTestPromotion(
                    id = 3L,
                    name = "10% 할인",
                    priority = 2,
                    benefits = PromotionBenefits.percentageDiscount(
                        DiscountPercentage.of(10),
                        null
                    )
                )
            )
            
            // When
            val result = calculator.findOptimalPromotionCombination(cart, promotions, user, maxPromotions = 2)
            
            // Then
            result.appliedPromotions shouldHaveSize 2
            result.appliedPromotions[0].promotionId shouldBe 2L // 우선순위 1 (15% 할인)
            result.appliedPromotions[1].promotionId shouldBe 3L // 우선순위 2 (10% 할인)
            result.totalDiscount shouldBe BigDecimal("2500.00") // 1500 + 1000
        }
        
        test("최대 프로모션 개수를 제한해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotions = listOf(
                createTestPromotion(id = 1L, name = "프로모션1", benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(5), null)),
                createTestPromotion(id = 2L, name = "프로모션2", benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(10), null)),
                createTestPromotion(id = 3L, name = "프로모션3", benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(15), null)),
                createTestPromotion(id = 4L, name = "프로모션4", benefits = PromotionBenefits.percentageDiscount(DiscountPercentage.of(20), null))
            )
            
            // When
            val result = calculator.findOptimalPromotionCombination(cart, promotions, user, maxPromotions = 2)
            
            // Then
            result.appliedPromotions shouldHaveSize 2
        }
        
        test("적용 가능한 프로모션이 없는 경우 빈 결과를 반환해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("5000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotions = listOf(
                createTestPromotion(
                    conditions = PromotionConditions(
                        minCartAmount = Money.of(BigDecimal("10000"))
                    ),
                    benefits = PromotionBenefits.percentageDiscount(
                        DiscountPercentage.of(10),
                        null
                    )
                )
            )
            
            // When
            val result = calculator.findOptimalPromotionCombination(cart, promotions, user)
            
            // Then
            result.appliedPromotions shouldBe emptyList()
            result.totalDiscount shouldBe BigDecimal.ZERO
        }
    }
    
    context("previewPromotionApplication 메서드") {
        test("적용 가능한 프로모션의 미리보기를 제공해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(12),
                    null
                )
            )
            
            // When
            val (isApplicable, expectedDiscount) = calculator.previewPromotionApplication(cart, promotion, user)
            
            // Then
            isApplicable shouldBe true
            expectedDiscount.toBigDecimal() shouldBe BigDecimal("1200.00")
        }
        
        test("적용 불가능한 프로모션의 미리보기를 제공해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("5000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                conditions = PromotionConditions(
                    minCartAmount = Money.of(BigDecimal("10000"))
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(10),
                    null
                )
            )
            
            // When
            val (isApplicable, expectedDiscount) = calculator.previewPromotionApplication(cart, promotion, user)
            
            // Then
            isApplicable shouldBe false
            expectedDiscount.toBigDecimal() shouldBe BigDecimal.ZERO
        }
    }
    
    context("calculateFinalAmount 메서드") {
        test("최종 금액을 올바르게 계산해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(8),
                    null
                )
            )
            
            // When
            val finalAmount = calculator.calculateFinalAmount(cart, listOf(promotion), user)
            
            // Then
            finalAmount shouldBe BigDecimal("9200.00") // 10000 - 800
        }
        
        test("프로모션이 없는 경우 원래 금액을 반환해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            // When
            val finalAmount = calculator.calculateFinalAmount(cart, emptyList(), user)
            
            // Then
            finalAmount shouldBe BigDecimal("10000")
        }
    }
    
    context("calculateSavings 메서드") {
        test("절약 금액을 올바르게 계산해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(7),
                    null
                )
            )
            
            // When
            val savings = calculator.calculateSavings(cart, listOf(promotion), user)
            
            // Then
            savings shouldBe BigDecimal("700.00")
        }
        
        test("여러 프로모션의 총 절약 금액을 계산해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotions = listOf(
                createTestPromotion(
                    id = 1L,
                    benefits = PromotionBenefits.percentageDiscount(
                        DiscountPercentage.of(5),
                        null
                    )
                ),
                createTestPromotion(
                    id = 2L,
                    benefits = PromotionBenefits.fixedAmountDiscount(Money.of(BigDecimal("300")))
                )
            )
            
            // When
            val savings = calculator.calculateSavings(cart, promotions, user)
            
            // Then
            savings shouldBe BigDecimal("800.00") // 500 + 300
        }
        
        test("프로모션이 없는 경우 절약 금액은 0이어야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("10000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            // When
            val savings = calculator.calculateSavings(cart, emptyList(), user)
            
            // Then
            savings shouldBe BigDecimal.ZERO
        }
    }
    
    context("복합 시나리오 테스트") {
        test("VIP 회원에게 여러 조건이 있는 프로모션을 적용해야 한다") {
            // Given
            val product = createTestProduct(
                price = BigDecimal("50000"),
                category = "전자제품",
                brand = "삼성"
            )
            val cartItem = createTestCartItem(product, quantity = 2)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser(
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false
            )
            
            val promotion = createTestPromotion(
                conditions = PromotionConditions(
                    targetCategory = "전자제품",
                    minCartAmount = Money.of(BigDecimal("50000")),
                    minQuantity = Quantity.of(2),
                    targetUserLevel = MembershipLevel.VIP
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(15),
                    Money.of(BigDecimal("20000"))
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("100000") // 50000 * 2
            result.totalDiscount shouldBe BigDecimal("15000.00") // 100000 * 0.15 = 15000 (최대 할인 20000보다 작음)
            result.finalAmount shouldBe BigDecimal("85000.00")
            result.appliedPromotions shouldHaveSize 1
        }
        
        test("신규 고객 전용 프로모션을 적용해야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("30000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser(
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true
            )
            
            val promotion = createTestPromotion(
                conditions = PromotionConditions(
                    isNewCustomerOnly = true
                ),
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(20),
                    null
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("30000")
            result.totalDiscount shouldBe BigDecimal("6000.00") // 30000 * 0.20
            result.finalAmount shouldBe BigDecimal("24000.00")
            result.appliedPromotions shouldHaveSize 1
        }
        
        test("최대 할인 금액이 적용되어야 한다") {
            // Given
            val product = createTestProduct(price = BigDecimal("100000"))
            val cartItem = createTestCartItem(product, quantity = 1)
            val cart = createTestCart(items = listOf(cartItem))
            val user = createTestUser()
            
            val promotion = createTestPromotion(
                benefits = PromotionBenefits.percentageDiscount(
                    DiscountPercentage.of(20), // 20% 할인 = 20000원
                    Money.of(BigDecimal("15000")) // 최대 할인 15000원
                )
            )
            
            // When
            val result = calculator.calculateDiscounts(cart, listOf(promotion), user)
            
            // Then
            result.subtotal shouldBe BigDecimal("100000")
            result.totalDiscount shouldBe BigDecimal("15000") // 최대 할인 금액 적용
            result.finalAmount shouldBe BigDecimal("85000")
            result.appliedPromotions shouldHaveSize 1
        }
    }
})
