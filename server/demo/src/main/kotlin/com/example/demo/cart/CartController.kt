package com.example.demo.cart

import com.example.demo.cart.dto.CartRequest
import com.example.demo.cart.dto.CartResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController(
    private val cartService: CartService
) {
    
    @PostMapping("/calculate")
    fun calculateCartTotal(@RequestBody request: CartRequest): ResponseEntity<CartResponse> {
        val cartResponse = cartService.calculateCartTotal(request)
        return ResponseEntity.ok(cartResponse)
    }
    
    @PostMapping("/validate")
    fun validateCartItems(@RequestBody request: CartRequest): ResponseEntity<Map<String, Any>> {
        val errors = cartService.validateCartItems(request)
        
        val response = if (errors.isEmpty()) {
            mapOf(
                "valid" to true,
                "message" to "장바구니가 유효합니다."
            )
        } else {
            mapOf(
                "valid" to false,
                "errors" to errors
            )
        }
        
        return ResponseEntity.ok(response)
    }
}
