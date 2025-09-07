package com.example.demo.application.cart.service

import com.example.demo.application.common.dto.request.CalculateCartRequest
import com.example.demo.application.common.dto.request.ValidateCartRequest
import com.example.demo.application.common.dto.response.CalculateCartResponse
import com.example.demo.application.common.dto.response.ValidateCartResponse
import com.example.demo.application.cart.usecase.CalculateCartUseCase
import com.example.demo.application.cart.usecase.ValidateCartUseCase
import org.springframework.stereotype.Service

/**
 * 장바구니 서비스
 * 장바구니 관련 비즈니스 로직을 조합하고 제공합니다.
 */
@Service
class CartService(
    private val calculateCartUseCase: CalculateCartUseCase,
    private val validateCartUseCase: ValidateCartUseCase
) {
    
    /**
     * 장바구니 총액을 계산합니다.
     * @param request 장바구니 계산 요청
     * @return 장바구니 계산 결과
     */
    fun calculateCartTotal(request: CalculateCartRequest): CalculateCartResponse {
        return calculateCartUseCase.execute(request)
    }
    
    /**
     * 장바구니를 검증합니다.
     * @param request 장바구니 검증 요청
     * @return 장바구니 검증 결과
     */
    fun validateCart(request: ValidateCartRequest): ValidateCartResponse {
        return validateCartUseCase.execute(request)
    }
    
    /**
     * 장바구니를 검증하고 총액을 계산합니다.
     * @param request 장바구니 계산 요청
     * @return 장바구니 계산 결과 (검증 실패 시 예외 발생)
     */
    fun validateAndCalculateCart(request: CalculateCartRequest): CalculateCartResponse {
        // 먼저 검증
        val validateRequest = ValidateCartRequest(
            userId = request.userId,
            items = request.items
        )
        
        val validationResult = validateCartUseCase.execute(validateRequest)
        
        if (!validationResult.isValid) {
            throw IllegalArgumentException("장바구니 검증 실패: ${validationResult.errors.joinToString(", ")}")
        }
        
        // 검증 통과 시 계산
        return calculateCartUseCase.execute(request)
    }
}
