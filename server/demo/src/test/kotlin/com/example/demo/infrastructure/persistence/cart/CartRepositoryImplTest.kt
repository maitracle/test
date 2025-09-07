package com.example.demo.infrastructure.persistence.cart

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CartRepositoryImplTest : FunSpec() {
    
    init {
        context("장바구니 Repository 테스트") {
            test("사용자 ID로 장바구니를 조회할 수 있어야 한다") {
                // Given
                val cartJpaRepository = mockk<CartJpaRepository>()
                val userJpaRepository = mockk<com.example.demo.infrastructure.persistence.user.UserJpaRepository>()
                val cartRepository = CartRepositoryImpl(cartJpaRepository, userJpaRepository)
                
                val cartEntity = com.example.demo.infrastructure.persistence.cart.CartEntity.createExisting(
                    id = 1L,
                    userId = 1L,
                    items = emptyList(),
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                val userId = UserId.of(1L)
                
                every { cartJpaRepository.findByUserId(1L) } returns cartEntity
                
                // When
                val foundCart = cartRepository.findByUserId(userId)
                
                // Then
                foundCart.shouldNotBeNull()
                foundCart!!.userId.value shouldBe 1L
                foundCart.items.isEmpty() shouldBe true
                
                verify { cartJpaRepository.findByUserId(1L) }
            }
            
            test("존재하지 않는 사용자 ID로 조회하면 null을 반환해야 한다") {
                // Given
                val cartJpaRepository = mockk<CartJpaRepository>()
                val userJpaRepository = mockk<com.example.demo.infrastructure.persistence.user.UserJpaRepository>()
                val cartRepository = CartRepositoryImpl(cartJpaRepository, userJpaRepository)
                
                val userId = UserId.of(999L)
                
                every { cartJpaRepository.findByUserId(999L) } returns null
                
                // When
                val foundCart = cartRepository.findByUserId(userId)
                
                // Then
                foundCart shouldBe null
                
                verify { cartJpaRepository.findByUserId(999L) }
            }
            
            test("새로운 장바구니를 저장할 수 있어야 한다") {
                // Given
                val cartJpaRepository = mockk<CartJpaRepository>()
                val userJpaRepository = mockk<com.example.demo.infrastructure.persistence.user.UserJpaRepository>()
                val cartRepository = CartRepositoryImpl(cartJpaRepository, userJpaRepository)
                
                val user = User.createNewUser(
                    id = UserId.of(1L),
                    email = Email("newuser@example.com")
                )
                val cart = Cart.createEmpty(CartId.of(1L), user.id)
                
                val savedCartEntity = com.example.demo.infrastructure.persistence.cart.CartEntity.createExisting(
                    id = 1L,
                    userId = 1L,
                    items = emptyList(),
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                
                every { cartJpaRepository.save(any()) } returns savedCartEntity
                
                // When
                val savedCart = cartRepository.save(cart)
                
                // Then
                savedCart.shouldNotBeNull()
                savedCart!!.userId shouldBe user.id
                savedCart.items.isEmpty() shouldBe true
                
                verify { cartJpaRepository.save(any()) }
            }
            
            test("장바구니를 삭제할 수 있어야 한다") {
                // Given
                val cartJpaRepository = mockk<CartJpaRepository>()
                val userJpaRepository = mockk<com.example.demo.infrastructure.persistence.user.UserJpaRepository>()
                val cartRepository = CartRepositoryImpl(cartJpaRepository, userJpaRepository)
                
                val cartId = CartId.of(1L)
                
                every { cartJpaRepository.deleteById(1L) } returns Unit
                
                // When
                cartRepository.delete(cartId)
                
                // Then
                verify { cartJpaRepository.deleteById(1L) }
            }
        }
    }
}