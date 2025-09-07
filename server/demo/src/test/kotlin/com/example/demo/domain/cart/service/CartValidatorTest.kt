package com.example.demo.domain.cart.service

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.user.model.UserId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import java.time.LocalDateTime

class CartValidatorTest : DescribeSpec({
    
    describe("CartValidator") {
        
        val validator = CartValidator()
        
        context("장바구니 검증") {
            
            it("유효한 장바구니는 검증을 통과한다") {
                // Given
                val cart = createValidCart()
                
                // When
                val result = validator.validateBasic(cart)
                
                // Then
                result.isSuccess().shouldBeTrue()
                result.errors.shouldBeEmpty()
            }
            
            it("빈 장바구니는 검증을 실패한다") {
                // Given
                val cart = createEmptyCart()
                
                // When
                val result = validator.validateBasic(cart)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("장바구니가 비어있습니다.")
            }
            
            it("비활성화된 상품이 있는 장바구니는 검증을 실패한다") {
                // Given
                val cart = createCartWithInactiveProduct()
                
                // When
                val result = validator.validateBasic(cart)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("비활성화된 상품이 있습니다: [비활성화된 상품]")
            }
            
            it("재고가 부족한 상품이 있는 장바구니는 검증을 실패한다") {
                // Given
                val cart = createCartWithInsufficientStock()
                
                // When
                val result = validator.validateBasic(cart)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("재고가 부족한 상품이 있습니다: [재고 부족 상품 (요청: 10, 재고: 5)]")
            }
        }
        
        context("최소 주문 금액 검증") {
            
            it("최소 주문 금액을 충족하는 장바구니는 검증을 통과한다") {
                // Given
                val cart = createCartWithAmount(Money.of(100000))
                val minOrderAmount = Money.of(50000)
                
                // When
                val result = validator.validateForOrder(cart, minOrderAmount)
                
                // Then
                result.isSuccess().shouldBeTrue()
            }
            
            it("최소 주문 금액을 충족하지 않는 장바구니는 검증을 실패한다") {
                // Given
                val cart = createCartWithAmount(Money.of(30000))
                val minOrderAmount = Money.of(50000)
                
                // When
                val result = validator.validateForOrder(cart, minOrderAmount)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("최소 주문 금액을 충족하지 않습니다. 현재: 30000, 최소: 50000")
            }
        }
        
        context("경고 메시지") {
            
            it("높은 주문 금액에 대해 경고를 표시한다") {
                // Given
                val cart = createCartWithAmount(Money.of(1500000))
                
                // When
                val result = validator.validateBasic(cart)
                
                // Then
                result.hasWarnings().shouldBeTrue()
                result.warnings.shouldContain("주문 금액이 높습니다. 결제 시 추가 확인이 필요할 수 있습니다.")
            }
        }
        
        context("장바구니 아이템 검증") {
            
            it("유효한 아이템은 검증을 통과한다") {
                // Given
                val item = createValidCartItem()
                
                // When
                val result = validator.validateItem(item)
                
                // Then
                result.isSuccess().shouldBeTrue()
                result.errors.shouldBeEmpty()
            }
            
            it("비활성화된 상품의 아이템은 검증을 실패한다") {
                // Given
                val item = createCartItemWithInactiveProduct()
                
                // When
                val result = validator.validateItem(item)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("상품이 비활성화되어 있습니다: 비활성화된 상품")
            }
            
            it("재고가 부족한 아이템은 검증을 실패한다") {
                // Given
                val item = createCartItemWithInsufficientStock()
                
                // When
                val result = validator.validateItem(item)
                
                // Then
                result.isFailure().shouldBeTrue()
                result.errors.shouldContain("재고가 부족합니다: 재고 부족 상품 (요청: 10, 재고: 5)")
            }
        }
    }
})

// 테스트용 헬퍼 함수들
fun createEmptyCart(): Cart {
    return Cart.createEmpty(CartId.of(1L), UserId.of(1L))
}

fun createValidCart(): Cart {
    val now = LocalDateTime.now()
    val item = createValidCartItem()
    return Cart.createExisting(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = listOf(item),
        createdAt = now,
        updatedAt = now
    )
}

fun createCartWithInactiveProduct(): Cart {
    val now = LocalDateTime.now()
    val product = createInactiveProduct()
    val item = CartItem.create(product, Quantity.of(2))
    return Cart.createExisting(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = listOf(item),
        createdAt = now,
        updatedAt = now
    )
}

fun createCartWithInsufficientStock(): Cart {
    val now = LocalDateTime.now()
    val product = createProductWithLowStock()
    val item = CartItem.create(product, Quantity.of(10)) // 재고보다 많은 수량
    return Cart.createExisting(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = listOf(item),
        createdAt = now,
        updatedAt = now
    )
}

fun createCartWithAmount(amount: Money): Cart {
    val now = LocalDateTime.now()
    val item = createCartItemWithPrice(amount.amount.toLong())
    return Cart.createExisting(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = listOf(item),
        createdAt = now,
        updatedAt = now
    )
}

fun createValidCartItem(): CartItem {
    val product = createValidProduct()
    return CartItem.create(product, Quantity.of(2))
}

fun createCartItemWithInactiveProduct(): CartItem {
    val product = createInactiveProduct()
    return CartItem.create(product, Quantity.of(2))
}

fun createCartItemWithInsufficientStock(): CartItem {
    val product = createProductWithLowStock()
    return CartItem.create(product, Quantity.of(10))
}

fun createCartItemWithPrice(price: Long): CartItem {
    val product = createProductWithPrice(price)
    return CartItem.create(product, Quantity.of(1))
}

fun createValidProduct(): Product {
    val now = LocalDateTime.now()
    return Product.createExisting(
        id = ProductId.of(1L),
        name = "유효한 상품",
        description = "테스트 상품",
        price = Price.of(Money.of(50000)),
        stock = Stock.of(100),
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = null,
        isActive = true,
        createdAt = now,
        updatedAt = now
    )
}

fun createInactiveProduct(): Product {
    val now = LocalDateTime.now()
    return Product.createExisting(
        id = ProductId.of(1L),
        name = "비활성화된 상품",
        description = "테스트 상품",
        price = Price.of(Money.of(50000)),
        stock = Stock.of(100),
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = null,
        isActive = false,
        createdAt = now,
        updatedAt = now
    )
}

fun createProductWithLowStock(): Product {
    val now = LocalDateTime.now()
    return Product.createExisting(
        id = ProductId.of(1L),
        name = "재고 부족 상품",
        description = "테스트 상품",
        price = Price.of(Money.of(50000)),
        stock = Stock.of(5),
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = null,
        isActive = true,
        createdAt = now,
        updatedAt = now
    )
}

fun createProductWithPrice(price: Long): Product {
    val now = LocalDateTime.now()
    return Product.createExisting(
        id = ProductId.of(1L),
        name = "가격 상품",
        description = "테스트 상품",
        price = Price.of(Money.of(price)),
        stock = Stock.of(100),
        category = "전자제품",
        brand = "테스트 브랜드",
        imageUrl = null,
        isActive = true,
        createdAt = now,
        updatedAt = now
    )
}