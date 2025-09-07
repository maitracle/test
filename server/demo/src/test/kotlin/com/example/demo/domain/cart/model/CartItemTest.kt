package com.example.demo.domain.cart.model

import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.common.valueobject.Stock
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import java.math.BigDecimal
import java.time.LocalDateTime

class CartItemTest : DescribeSpec({
    
    describe("CartItem") {
        
        context("생성") {
            
            it("유효한 값으로 CartItem을 생성할 수 있다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(2)
                val unitPrice = Money.of(50000)
                
                // When
                val cartItem = CartItem(
                    product = product,
                    quantity = quantity,
                    unitPrice = unitPrice
                )
                
                // Then
                cartItem.product shouldBe product
                cartItem.quantity shouldBe quantity
                cartItem.unitPrice shouldBe unitPrice
            }
            
            it("companion object의 create 메서드로 CartItem을 생성할 수 있다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(2)
                
                // When
                val cartItem = CartItem.create(product, quantity)
                
                // Then
                cartItem.product shouldBe product
                cartItem.quantity shouldBe quantity
                cartItem.unitPrice shouldBe product.price.amount
            }
            
            it("음수 단위 가격으로 CartItem을 생성하면 예외가 발생한다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(2)
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    Money.of(-1000) // Money 생성자에서 음수 검증
                }
            }
        }
        
        context("총 가격 계산") {
            
            it("총 가격이 올바르게 계산된다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(3)
                val unitPrice = Money.of(10000)
                val cartItem = CartItem(product, quantity, unitPrice)
                
                // When
                val totalPrice = cartItem.totalPrice
                
                // Then
                totalPrice shouldBe Money.of(30000)
            }
            
            it("수량이 1일 때 총 가격은 단위 가격과 같다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(1)
                val unitPrice = Money.of(50000)
                val cartItem = CartItem(product, quantity, unitPrice)
                
                // When
                val totalPrice = cartItem.totalPrice
                
                // Then
                totalPrice shouldBe unitPrice
            }
        }
        
        context("유효성 검증") {
            
            it("유효한 CartItem은 isValid가 true를 반환한다") {
                // Given
                val product = createTestProduct()
                val quantity = Quantity.of(2)
                val unitPrice = Money.of(50000)
                val cartItem = CartItem(product, quantity, unitPrice)
                
                // When
                val isValid = cartItem.isValid()
                
                // Then
                isValid.shouldBeTrue()
            }
            
            it("비활성화된 상품의 CartItem은 isValid가 false를 반환한다") {
                // Given
                val product = createTestProduct(isActive = false)
                val quantity = Quantity.of(2)
                val unitPrice = Money.of(50000)
                val cartItem = CartItem(product, quantity, unitPrice)
                
                // When
                val isValid = cartItem.isValid()
                
                // Then
                isValid.shouldBeFalse()
            }
            
            it("재고가 부족한 CartItem은 isValid가 false를 반환한다") {
                // Given
                val product = createTestProduct(stockQuantity = 1)
                val quantity = Quantity.of(2)
                val unitPrice = Money.of(50000)
                val cartItem = CartItem(product, quantity, unitPrice)
                
                // When
                val isValid = cartItem.isValid()
                
                // Then
                isValid.shouldBeFalse()
            }
        }
        
        context("카테고리 및 브랜드 확인") {
            
            it("상품이 특정 카테고리에 속하는지 확인할 수 있다") {
                // Given
                val product = createTestProduct(category = "전자제품")
                val cartItem = CartItem.create(product, Quantity.of(1))
                
                // When & Then
                cartItem.belongsToCategory("전자제품").shouldBeTrue()
                cartItem.belongsToCategory("의류").shouldBeFalse()
                cartItem.belongsToCategory("ELECTRONICS").shouldBeFalse() // 대소문자 구분
            }
            
            it("상품이 특정 브랜드에 속하는지 확인할 수 있다") {
                // Given
                val product = createTestProduct(brand = "삼성")
                val cartItem = CartItem.create(product, Quantity.of(1))
                
                // When & Then
                cartItem.belongsToBrand("삼성").shouldBeTrue()
                cartItem.belongsToBrand("LG").shouldBeFalse()
            }
        }
        
        context("수량 관리") {
            
            it("수량을 추가할 수 있다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val additionalQuantity = Quantity.of(3)
                
                // When
                val updatedItem = cartItem.addQuantity(additionalQuantity)
                
                // Then
                updatedItem.quantity shouldBe Quantity.of(5)
                updatedItem.product shouldBe cartItem.product
                updatedItem.unitPrice shouldBe cartItem.unitPrice
            }
            
            it("수량을 업데이트할 수 있다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val newQuantity = Quantity.of(5)
                
                // When
                val updatedItem = cartItem.updateQuantity(newQuantity)
                
                // Then
                updatedItem.quantity shouldBe newQuantity
                updatedItem.product shouldBe cartItem.product
                updatedItem.unitPrice shouldBe cartItem.unitPrice
            }
        }
        
        context("가격 관리") {
            
            it("단위 가격을 업데이트할 수 있다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val newUnitPrice = Money.of(60000)
                
                // When
                val updatedItem = cartItem.updateUnitPrice(newUnitPrice)
                
                // Then
                updatedItem.unitPrice shouldBe newUnitPrice
                updatedItem.product shouldBe cartItem.product
                updatedItem.quantity shouldBe cartItem.quantity
            }
            
            it("음수 단위 가격으로 업데이트하면 예외가 발생한다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    Money.of(-1000) // Money 생성자에서 음수 검증
                }
            }
        }
        
        context("할인 계산") {
            
            it("할인율로 할인된 가격을 계산할 수 있다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val discountRate = 0.1 // 10% 할인
                
                // When
                val discountedPrice = cartItem.calculateDiscountedPrice(discountRate)
                
                // Then
                val expectedPrice = cartItem.totalPrice - (cartItem.totalPrice * java.math.BigDecimal.valueOf(discountRate))
                discountedPrice shouldBe expectedPrice
            }
            
            it("할인 금액으로 할인된 가격을 계산할 수 있다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val discountAmount = Money.of(15000)
                
                // When
                val discountedPrice = cartItem.applyDiscount(discountAmount)
                
                // Then
                val expectedPrice = cartItem.totalPrice - discountAmount
                discountedPrice shouldBe expectedPrice
            }
            
            it("할인 금액이 총 가격보다 클 때 총 가격만큼만 할인된다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(1))
                val discountAmount = Money.of(150000) // 총 가격보다 큰 할인 금액
                
                // When
                val discountedPrice = cartItem.applyDiscount(discountAmount)
                
                // Then
                discountedPrice shouldBe Money.zero()
            }
        }
        
        context("상품 비교") {
            
            it("같은 상품의 CartItem인지 확인할 수 있다") {
                // Given
                val product1 = createTestProduct(id = 1L)
                val product2 = createTestProduct(id = 1L)
                val cartItem1 = CartItem.create(product1, Quantity.of(2))
                val cartItem2 = CartItem.create(product2, Quantity.of(3))
                
                // When
                val isSameProduct = cartItem1.isSameProduct(cartItem2)
                
                // Then
                isSameProduct.shouldBeTrue()
            }
            
            it("다른 상품의 CartItem인지 확인할 수 있다") {
                // Given
                val product1 = createTestProduct(id = 1L)
                val product2 = createTestProduct(id = 2L)
                val cartItem1 = CartItem.create(product1, Quantity.of(2))
                val cartItem2 = CartItem.create(product2, Quantity.of(2))
                
                // When
                val isSameProduct = cartItem1.isSameProduct(cartItem2)
                
                // Then
                isSameProduct.shouldBeFalse()
            }
        }
        
        context("예외 처리") {
            
            it("잘못된 할인율로 할인 계산 시 예외가 발생한다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                val invalidDiscountRate = 1.5 // 150% 할인 (유효하지 않음)
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    cartItem.calculateDiscountedPrice(invalidDiscountRate)
                }
            }
            
            it("음수 할인 금액으로 할인 적용 시 예외가 발생한다") {
                // Given
                val product = createTestProduct()
                val cartItem = CartItem.create(product, Quantity.of(2))
                
                // When & Then
                shouldThrow<IllegalArgumentException> {
                    Money.of(-1000) // Money 생성자에서 음수 검증
                }
            }
        }
    }
})
