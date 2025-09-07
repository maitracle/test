package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.user.model.UserId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import java.time.LocalDateTime

class CartTest : DescribeSpec({
    
    describe("Cart") {
        
        context("생성") {
            
            it("빈 장바구니를 생성할 수 있다") {
                // Given
                val cartId = CartId.of(1L)
                val userId = UserId.of(1L)
                
                // When
                val cart = Cart.createEmpty(cartId, userId)
                
                // Then
                cart.id shouldBe cartId
                cart.userId shouldBe userId
                cart.items.shouldBeEmpty()
                cart.isEmpty.shouldBeTrue()
                cart.isNotEmpty.shouldBeFalse()
                cart.itemCount shouldBe 0
            }
            
            it("기존 장바구니를 생성할 수 있다") {
                // Given
                val cartId = CartId.of(1L)
                val userId = UserId.of(1L)
                val now = LocalDateTime.now()
                val items = listOf(createTestCartItem())
                
                // When
                val cart = Cart.createExisting(cartId, userId, items, now, now)
                
                // Then
                cart.id shouldBe cartId
                cart.userId shouldBe userId
                cart.items shouldBe items
                cart.createdAt shouldBe now
                cart.updatedAt shouldBe now
            }
        }
        
        context("총 금액 및 수량 계산") {
            
            it("빈 장바구니의 총 금액은 0이다") {
                // Given
                val cart = createEmptyCart()
                
                // When
                val totalAmount = cart.totalAmount
                
                // Then
                totalAmount shouldBe Money.zero()
            }
            
            it("빈 장바구니의 총 수량은 0이다") {
                // Given
                val cart = createEmptyCart()
                
                // When
                val totalQuantity = cart.totalQuantity
                
                // Then
                totalQuantity shouldBe 0
            }
            
            it("아이템이 있는 장바구니의 총 금액이 올바르게 계산된다") {
                // Given
                val cartItem1 = createTestCartItem(price = 10000, quantity = 2)
                val cartItem2 = createTestCartItem(price = 20000, quantity = 1)
                val cart = createCartWithItems(listOf(cartItem1, cartItem2))
                
                // When
                val totalAmount = cart.totalAmount
                
                // Then
                totalAmount shouldBe Money.of(40000) // 20000 + 20000
            }
            
            it("아이템이 있는 장바구니의 총 수량이 올바르게 계산된다") {
                // Given
                val cartItem1 = createTestCartItem(quantity = 2)
                val cartItem2 = createTestCartItem(quantity = 3)
                val cart = createCartWithItems(listOf(cartItem1, cartItem2))
                
                // When
                val totalQuantity = cart.totalQuantity
                
                // Then
                totalQuantity shouldBe 5
            }
        }
        
        context("카테고리 및 브랜드 확인") {
            
            it("특정 카테고리의 상품이 있는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(category = "전자제품")
                val cart = createCartWithItems(listOf(cartItem))
                
                // When & Then
                cart.hasCategory("전자제품").shouldBeTrue()
                cart.hasCategory("의류").shouldBeFalse()
            }
            
            it("특정 브랜드의 상품이 있는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(brand = "삼성")
                val cart = createCartWithItems(listOf(cartItem))
                
                // When & Then
                cart.hasBrand("삼성").shouldBeTrue()
                cart.hasBrand("LG").shouldBeFalse()
            }
            
            it("특정 상품이 있는지 확인할 수 있다") {
                // Given
                val productId = ProductId.of(1L)
                val cartItem = createTestCartItem(productId = productId)
                val cart = createCartWithItems(listOf(cartItem))
                
                // When & Then
                cart.hasProduct(productId).shouldBeTrue()
                cart.hasProduct(ProductId.of(2L)).shouldBeFalse()
            }
        }
        
        context("아이템 추가") {
            
            it("새로운 상품을 장바구니에 추가할 수 있다") {
                // Given
                val cart = createEmptyCart()
                val product = createTestProduct()
                val quantity = Quantity.of(2)
                
                // When
                val updatedCart = cart.addItem(product, quantity)
                
                // Then
                updatedCart.items.shouldHaveSize(1)
                updatedCart.items[0].product shouldBe product
                updatedCart.items[0].quantity shouldBe quantity
                updatedCart.updatedAt shouldNotBe cart.updatedAt
            }
            
            it("이미 존재하는 상품을 추가하면 수량이 증가한다") {
                // Given
                val product = createTestProduct()
                val cartItem = createTestCartItem(product = product, quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val additionalQuantity = Quantity.of(3)
                
                // When
                val updatedCart = cart.addItem(product, additionalQuantity)
                
                // Then
                updatedCart.items.shouldHaveSize(1)
                updatedCart.items[0].quantity shouldBe Quantity.of(5) // 2 + 3
            }
            
            it("비활성화된 상품을 추가하면 예외가 발생한다") {
                // Given
                val cart = createEmptyCart()
                val inactiveProduct = createTestProduct(isActive = false)
                val quantity = Quantity.of(2)
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    cart.addItem(inactiveProduct, quantity)
                }
            }
            
            it("재고가 부족한 상품을 추가하면 예외가 발생한다") {
                // Given
                val cart = createEmptyCart()
                val product = createTestProduct(stockQuantity = 1)
                val quantity = Quantity.of(2)
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    cart.addItem(product, quantity)
                }
            }
        }
        
        context("아이템 제거") {
            
            it("장바구니에서 상품을 제거할 수 있다") {
                // Given
                val productId = ProductId.of(1L)
                val cartItem = createTestCartItem(productId = productId)
                val cart = createCartWithItems(listOf(cartItem))
                
                // When
                val updatedCart = cart.removeItem(productId)
                
                // Then
                updatedCart.items.shouldBeEmpty()
                updatedCart.updatedAt shouldNotBe cart.updatedAt
            }
            
            it("존재하지 않는 상품을 제거해도 예외가 발생하지 않는다") {
                // Given
                val cart = createEmptyCart()
                val nonExistentProductId = ProductId.of(999L)
                
                // When
                val updatedCart = cart.removeItem(nonExistentProductId)
                
                // Then
                updatedCart.items.shouldBeEmpty()
            }
        }
        
        context("수량 업데이트") {
            
            it("장바구니 아이템의 수량을 업데이트할 수 있다") {
                // Given
                val productId = ProductId.of(1L)
                val cartItem = createTestCartItem(productId = productId, quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val newQuantity = Quantity.of(5)
                
                // When
                val updatedCart = cart.updateItemQuantity(productId, newQuantity)
                
                // Then
                updatedCart.items[0].quantity shouldBe newQuantity
                updatedCart.updatedAt shouldNotBe cart.updatedAt
            }
            
            it("존재하지 않는 상품의 수량을 업데이트하면 예외가 발생한다") {
                // Given
                val cart = createEmptyCart()
                val nonExistentProductId = ProductId.of(999L)
                val newQuantity = Quantity.of(5)
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    cart.updateItemQuantity(nonExistentProductId, newQuantity)
                }
            }
            
            it("재고가 부족한 수량으로 업데이트하면 예외가 발생한다") {
                // Given
                val product = createTestProduct(stockQuantity = 3)
                val cartItem = createTestCartItem(product = product, quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val newQuantity = Quantity.of(5) // 재고보다 많은 수량
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    cart.updateItemQuantity(product.id, newQuantity)
                }
            }
        }
        
        context("장바구니 비우기") {
            
            it("장바구니를 비울 수 있다") {
                // Given
                val cartItem = createTestCartItem()
                val cart = createCartWithItems(listOf(cartItem))
                
                // When
                val clearedCart = cart.clear()
                
                // Then
                clearedCart.items.shouldBeEmpty()
                clearedCart.isEmpty.shouldBeTrue()
                clearedCart.updatedAt shouldNotBe cart.updatedAt
            }
        }
        
        context("유효성 검증") {
            
            it("유효한 장바구니는 isValid가 true를 반환한다") {
                // Given
                val cartItem = createTestCartItem()
                val cart = createCartWithItems(listOf(cartItem))
                
                // When
                val isValid = cart.isValid()
                
                // Then
                isValid.shouldBeTrue()
            }
            
            it("유효하지 않은 아이템이 있는 장바구니는 isValid가 false를 반환한다") {
                // Given
                val invalidCartItem = createTestCartItem(isValid = false)
                val cart = createCartWithItems(listOf(invalidCartItem))
                
                // When
                val isValid = cart.isValid()
                
                // Then
                isValid.shouldBeFalse()
            }
            
            it("유효하지 않은 아이템들을 반환할 수 있다") {
                // Given
                val validItem = createTestCartItem()
                val invalidItem = createTestCartItem(isValid = false)
                val cart = createCartWithItems(listOf(validItem, invalidItem))
                
                // When
                val invalidItems = cart.getInvalidItems()
                
                // Then
                invalidItems.shouldHaveSize(1)
                invalidItems.shouldContain(invalidItem)
            }
        }
        
        context("아이템 필터링") {
            
            it("카테고리별로 아이템을 필터링할 수 있다") {
                // Given
                val electronicsItem = createTestCartItem(category = "전자제품")
                val clothingItem = createTestCartItem(category = "의류")
                val cart = createCartWithItems(listOf(electronicsItem, clothingItem))
                
                // When
                val electronicsItems = cart.getItemsByCategory("전자제품")
                
                // Then
                electronicsItems.shouldHaveSize(1)
                electronicsItems.shouldContain(electronicsItem)
            }
            
            it("브랜드별로 아이템을 필터링할 수 있다") {
                // Given
                val samsungItem = createTestCartItem(brand = "삼성")
                val lgItem = createTestCartItem(brand = "LG")
                val cart = createCartWithItems(listOf(samsungItem, lgItem))
                
                // When
                val samsungItems = cart.getItemsByBrand("삼성")
                
                // Then
                samsungItems.shouldHaveSize(1)
                samsungItems.shouldContain(samsungItem)
            }
            
            it("상품 ID로 아이템을 찾을 수 있다") {
                // Given
                val productId = ProductId.of(1L)
                val cartItem = createTestCartItem(productId = productId)
                val cart = createCartWithItems(listOf(cartItem))
                
                // When
                val foundItem = cart.getItemByProductId(productId)
                
                // Then
                foundItem shouldBe cartItem
            }
            
            it("존재하지 않는 상품 ID로 찾으면 null을 반환한다") {
                // Given
                val cart = createEmptyCart()
                val nonExistentProductId = ProductId.of(999L)
                
                // When
                val foundItem = cart.getItemByProductId(nonExistentProductId)
                
                // Then
                foundItem.shouldBeNull()
            }
        }
        
        context("최소 금액 및 수량 확인") {
            
            it("최소 주문 금액을 충족하는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(price = 50000, quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val minAmount = Money.of(80000)
                
                // When
                val meetsMinimum = cart.meetsMinimumAmount(minAmount)
                
                // Then
                meetsMinimum.shouldBeTrue()
            }
            
            it("최소 주문 금액을 충족하지 않는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(price = 30000, quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val minAmount = Money.of(80000)
                
                // When
                val meetsMinimum = cart.meetsMinimumAmount(minAmount)
                
                // Then
                meetsMinimum.shouldBeFalse()
            }
            
            it("최소 주문 수량을 충족하는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(quantity = 5)
                val cart = createCartWithItems(listOf(cartItem))
                val minQuantity = Quantity.of(3)
                
                // When
                val meetsMinimum = cart.meetsMinimumQuantity(minQuantity)
                
                // Then
                meetsMinimum.shouldBeTrue()
            }
            
            it("최소 주문 수량을 충족하지 않는지 확인할 수 있다") {
                // Given
                val cartItem = createTestCartItem(quantity = 2)
                val cart = createCartWithItems(listOf(cartItem))
                val minQuantity = Quantity.of(5)
                
                // When
                val meetsMinimum = cart.meetsMinimumQuantity(minQuantity)
                
                // Then
                meetsMinimum.shouldBeFalse()
            }
        }
    }
})

// 테스트용 헬퍼 함수들
fun createEmptyCart(): Cart {
    return Cart.createEmpty(CartId.of(1L), UserId.of(1L))
}

fun createCartWithItems(items: List<CartItem>): Cart {
    val now = LocalDateTime.now()
    return Cart.createExisting(
        id = CartId.of(1L),
        userId = UserId.of(1L),
        items = items,
        createdAt = now,
        updatedAt = now
    )
}

fun createTestProduct(
    id: Long = 1L,
    name: String = "테스트 상품",
    price: Long = 50000,
    stockQuantity: Int = 100,
    category: String? = "전자제품",
    brand: String? = "테스트 브랜드",
    isActive: Boolean = true
): Product {
    val now = LocalDateTime.now()
    return Product.createExisting(
        id = ProductId.of(id),
        name = name,
        description = "테스트 상품 설명",
        price = Price.of(Money.of(price)),
        stock = Stock.of(stockQuantity),
        category = category,
        brand = brand,
        imageUrl = null,
        isActive = isActive,
        createdAt = now,
        updatedAt = now
    )
}

fun createTestCartItem(
    productId: ProductId = ProductId.of(1L),
    product: Product? = null,
    price: Long = 50000,
    quantity: Int = 2,
    category: String? = "전자제품",
    brand: String? = "테스트 브랜드",
    isValid: Boolean = true
): CartItem {
    val testProduct = product ?: createTestProduct(
        id = productId.value,
        price = price,
        category = category,
        brand = brand,
        isActive = isValid
    )
    
    return CartItem.create(testProduct, Quantity.of(quantity))
}
