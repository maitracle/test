package com.example.demo.infrastructure.persistence.cart

import com.example.demo.application.common.port.CartRepository
import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 장바구니 Repository 구현체
 * 
 * CartRepository 포트의 구현체로, JPA를 사용하여 장바구니 데이터를 관리합니다.
 * 도메인 모델과 엔티티 간의 변환을 담당합니다.
 */
@Component
@Transactional(readOnly = true)
class CartRepositoryImpl(
    private val cartJpaRepository: CartJpaRepository,
    private val userJpaRepository: com.example.demo.infrastructure.persistence.user.UserJpaRepository
) : CartRepository {
    
    override fun findUserById(userId: UserId): User? {
        return userJpaRepository.findById(userId.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    @Transactional
    override fun save(cart: Cart): Cart {
        val entity = cart.toEntity()
        val savedEntity = cartJpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    override fun findById(cartId: CartId): Cart? {
        return cartJpaRepository.findById(cartId.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findByUserId(userId: UserId): Cart? {
        return cartJpaRepository.findByUserId(userId.value)
            ?.toDomain()
    }
    
    @Transactional
    override fun delete(cartId: CartId) {
        cartJpaRepository.deleteById(cartId.value)
    }
}

/**
 * CartEntity를 Cart 도메인 모델로 변환하는 확장 함수
 */
private fun com.example.demo.infrastructure.persistence.cart.CartEntity.toDomain(): Cart {
    return Cart.createExisting(
        id = CartId.of(id ?: throw IllegalStateException("Cart ID cannot be null")),
        userId = UserId.of(userId),
        items = items.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Cart 도메인 모델을 CartEntity로 변환하는 확장 함수
 */
private fun Cart.toEntity(): com.example.demo.infrastructure.persistence.cart.CartEntity {
    return com.example.demo.infrastructure.persistence.cart.CartEntity.createExisting(
        id = id.value,
        userId = userId.value,
        items = items.map { it.toEntity() }.toMutableList(),
        createdAt = createdAt,
        updatedAt = java.time.LocalDateTime.now()
    )
}

/**
 * CartItemEntity를 CartItem 도메인 모델로 변환하는 확장 함수
 */
private fun CartItemEntity.toDomain(): com.example.demo.domain.cart.model.CartItem {
    // Product 정보를 별도로 조회해야 하므로, 여기서는 기본 정보만으로 CartItem 생성
    // 실제 구현에서는 ProductRepository를 통해 Product 정보를 조회해야 함
    val product = com.example.demo.domain.product.model.Product.createExisting(
        id = com.example.demo.domain.product.model.ProductId.of(productId),
        name = productName,
        description = productDescription,
        price = com.example.demo.domain.product.valueobject.Price.of(unitPrice),
        stock = com.example.demo.domain.common.valueobject.Stock.empty(), // 장바구니에서는 재고 정보가 필요하지 않음
        category = category,
        brand = brand,
        imageUrl = imageUrl,
        isActive = true,
        createdAt = createdAt,
        updatedAt = createdAt
    )
    
    return com.example.demo.domain.cart.model.CartItem(
        product = product,
        quantity = com.example.demo.domain.common.valueobject.Quantity.of(quantity),
        unitPrice = com.example.demo.domain.common.valueobject.Money.of(unitPrice)
    )
}

/**
 * CartItem 도메인 모델을 CartItemEntity로 변환하는 확장 함수
 */
private fun com.example.demo.domain.cart.model.CartItem.toEntity(): CartItemEntity {
    // CartItemEntity는 cart 파라미터가 필요하므로, 임시로 빈 CartEntity를 생성
    val tempCart = com.example.demo.infrastructure.persistence.cart.CartEntity.createEmpty(userId = 0L)
    
    return CartItemEntity.create(
        cart = tempCart,
        productId = product.id.value,
        productName = product.name,
        productDescription = product.description,
        unitPrice = unitPrice.amount,
        quantity = quantity.value,
        category = product.category,
        brand = product.brand,
        imageUrl = product.imageUrl
    )
}

/**
 * UserEntity를 User 도메인 모델로 변환하는 확장 함수 (CartRepository에서 사용)
 */
private fun com.example.demo.infrastructure.persistence.user.UserEntity.toDomain(): User {
    return User.createExistingUser(
        id = UserId.of(id ?: throw IllegalStateException("User ID cannot be null")),
        email = com.example.demo.domain.user.valueobject.Email(email),
        membershipLevel = com.example.demo.domain.user.valueobject.MembershipLevel.valueOf(membershipLevel),
        isNewCustomer = isNewCustomer,
        createdAt = createdAt
    )
}
