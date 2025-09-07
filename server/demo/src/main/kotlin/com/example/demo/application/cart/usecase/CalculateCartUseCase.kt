package com.example.demo.application.cart.usecase

import com.example.demo.application.common.dto.request.CalculateCartRequest
import com.example.demo.application.common.dto.response.CalculateCartResponse
import com.example.demo.application.common.dto.response.CartItemResponse
import com.example.demo.application.common.port.CartRepository
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.application.promotion.service.PromotionEngine
import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.cart.model.CartItem
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.user.model.UserId
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 장바구니 계산 유스케이스
 * 장바구니 아이템들의 총액을 계산하고 프로모션을 적용합니다.
 */
@Component
class CalculateCartUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val promotionEngine: PromotionEngine
) {
    
    /**
     * 장바구니 계산을 실행합니다.
     * @param request 장바구니 계산 요청
     * @return 장바구니 계산 결과
     */
    fun execute(request: CalculateCartRequest): CalculateCartResponse {
        // 1. 장바구니 아이템 검증 및 생성
        val cartItems = validateAndBuildCartItems(request.items)
        
        // 2. 장바구니 생성
        val cart = Cart(
            id = CartId.of(1L), // 임시 ID (실제로는 저장 시 생성됨)
            userId = UserId.of(request.userId),
            items = cartItems,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 3. 사용자 정보 조회
        val user = cartRepository.findUserById(UserId.of(request.userId))
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: ${request.userId}")
        
        // 4. 프로모션 적용
        val promotionResult = promotionEngine.applyPromotions(cart, user)
        
        // 5. 응답 생성
        return CalculateCartResponse(
            items = cartItems.map { item ->
                CartItemResponse(
                    productId = item.product.id.value,
                    productName = item.product.name,
                    unitPrice = item.unitPrice.amount,
                    quantity = item.quantity.value,
                    totalPrice = item.totalPrice.amount
                )
            },
            subtotal = cart.totalAmount.amount,
            totalDiscount = promotionResult.totalDiscount,
            finalAmount = promotionResult.finalAmount,
            appliedPromotions = promotionResult.appliedPromotions.map { appliedPromotion ->
                com.example.demo.application.common.dto.response.AppliedPromotionResponse(
                    promotionId = appliedPromotion.promotionId,
                    promotionName = appliedPromotion.promotionName,
                    discountAmount = appliedPromotion.discountAmount
                )
            }
        )
    }
    
    /**
     * 장바구니 아이템들을 검증하고 생성합니다.
     * @param itemRequests 장바구니 아이템 요청 목록
     * @return 검증된 장바구니 아이템 목록
     */
    private fun validateAndBuildCartItems(itemRequests: List<com.example.demo.application.common.dto.request.CartItemRequest>): List<CartItem> {
        return itemRequests.map { itemRequest ->
            val product = productRepository.findById(ProductId.of(itemRequest.productId))
                ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: ${itemRequest.productId}")
            
            val quantity = Quantity(itemRequest.quantity)
            
            if (!product.isAvailable(quantity)) {
                throw IllegalArgumentException("상품 재고가 부족합니다: ${product.name} (요청: ${quantity.value}, 재고: ${product.stock.quantity})")
            }
            
            CartItem(
                product = product,
                quantity = quantity,
                unitPrice = product.price.amount
            )
        }
    }
}
