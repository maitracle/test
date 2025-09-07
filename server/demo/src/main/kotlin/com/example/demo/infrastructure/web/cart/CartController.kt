package com.example.demo.infrastructure.web.cart

import com.example.demo.application.cart.usecase.CalculateCartUseCase
import com.example.demo.application.cart.usecase.ValidateCartUseCase
import com.example.demo.infrastructure.web.cart.dto.CartRequest
import com.example.demo.infrastructure.web.cart.dto.CartResponse
import com.example.demo.infrastructure.web.cart.dto.ValidationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 장바구니 관련 REST API 컨트롤러
 * 장바구니 계산, 검증 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/cart")
class CartController(
    private val calculateCartUseCase: CalculateCartUseCase,
    private val validateCartUseCase: ValidateCartUseCase,
    private val cartWebMapper: CartWebMapper
) {
    
    /**
     * 장바구니 총액 계산 (프로모션 적용)
     * POST /api/cart/calculate-with-promotions
     */
    @PostMapping("/calculate-with-promotions")
    fun calculateCartWithPromotions(@RequestBody request: CartRequest): ResponseEntity<CartResponse> {
        val useCaseRequest = cartWebMapper.toCalculateCartRequest(request)
        val useCaseResponse = calculateCartUseCase.execute(useCaseRequest)
        val response = cartWebMapper.toCartResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 장바구니 검증
     * POST /api/cart/validate
     */
    @PostMapping("/validate")
    fun validateCart(@RequestBody request: CartRequest): ResponseEntity<ValidationResponse> {
        val useCaseRequest = cartWebMapper.toValidateCartRequest(request)
        val useCaseResponse = validateCartUseCase.execute(useCaseRequest)
        val response = cartWebMapper.toValidationResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
}
