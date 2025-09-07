package com.example.demo.cart

import com.example.demo.cart.dto.CartItemRequest
import com.example.demo.cart.dto.CartItemResponse
import com.example.demo.cart.dto.CartRequest
import com.example.demo.cart.dto.CartResponse
import com.example.demo.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class CartService(
    private val productRepository: ProductRepository
) {
    
    fun calculateCartTotal(request: CartRequest): CartResponse {
        val cartItems = mutableListOf<CartItemResponse>()
        var totalPrice = BigDecimal.ZERO
        var totalItems = 0
        
        for (itemRequest in request.items) {
            val product = productRepository.findById(itemRequest.productId)
                .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: ${itemRequest.productId}") }
            
            // 제품이 활성화되어 있는지 확인
            if (!product.isActive) {
                throw RuntimeException("비활성화된 제품입니다. ID: ${itemRequest.productId}")
            }
            
            // 재고 확인
            if (product.stockQuantity < itemRequest.quantity) {
                throw RuntimeException("재고가 부족합니다. 제품: ${product.name}, 요청 수량: ${itemRequest.quantity}, 재고: ${product.stockQuantity}")
            }
            
            val itemTotalPrice = product.price.multiply(BigDecimal(itemRequest.quantity))
            
            val cartItem = CartItemResponse(
                productId = product.id!!,
                productName = product.name,
                unitPrice = product.price,
                quantity = itemRequest.quantity,
                totalPrice = itemTotalPrice
            )
            
            cartItems.add(cartItem)
            totalPrice = totalPrice.add(itemTotalPrice)
            totalItems += itemRequest.quantity
        }
        
        return CartResponse(
            items = cartItems,
            totalPrice = totalPrice,
            totalItems = totalItems
        )
    }
    
    fun validateCartItems(request: CartRequest): List<String> {
        val errors = mutableListOf<String>()
        
        for (itemRequest in request.items) {
            try {
                val product = productRepository.findById(itemRequest.productId)
                    .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: ${itemRequest.productId}") }
                
                if (!product.isActive) {
                    errors.add("비활성화된 제품입니다: ${product.name} (ID: ${itemRequest.productId})")
                }
                
                if (product.stockQuantity < itemRequest.quantity) {
                    errors.add("재고 부족: ${product.name} - 요청 수량: ${itemRequest.quantity}, 재고: ${product.stockQuantity}")
                }
                
                if (itemRequest.quantity <= 0) {
                    errors.add("수량은 1개 이상이어야 합니다: ${product.name}")
                }
                
            } catch (e: RuntimeException) {
                errors.add(e.message ?: "알 수 없는 오류")
            }
        }
        
        return errors
    }
}
