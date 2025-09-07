package com.example.demo.infrastructure.web.cart

import com.example.demo.application.common.dto.request.CalculateCartRequest
import com.example.demo.application.common.dto.request.CartItemRequest
import com.example.demo.application.common.dto.request.ValidateCartRequest
import com.example.demo.application.common.dto.response.CalculateCartResponse
import com.example.demo.application.common.dto.response.ValidateCartResponse
import com.example.demo.infrastructure.web.cart.dto.CartRequest
import com.example.demo.infrastructure.web.cart.dto.CartResponse
import com.example.demo.infrastructure.web.cart.dto.CartItemWebResponse
import com.example.demo.infrastructure.web.cart.dto.AppliedPromotionWebResponse
import com.example.demo.infrastructure.web.cart.dto.ValidationResponse
import org.springframework.stereotype.Component

/**
 * 장바구니 관련 웹 DTO와 유스케이스 DTO 간의 변환을 담당하는 매퍼
 */
@Component
class CartWebMapper {
    
    /**
     * 웹 요청 DTO를 장바구니 계산 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 장바구니 계산 유스케이스 요청 DTO
     */
    fun toCalculateCartRequest(request: CartRequest): CalculateCartRequest {
        return CalculateCartRequest(
            userId = request.userId,
            items = request.items.map { item ->
                CartItemRequest(
                    productId = item.productId,
                    quantity = item.quantity
                )
            }
        )
    }
    
    /**
     * 웹 요청 DTO를 장바구니 검증 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 장바구니 검증 유스케이스 요청 DTO
     */
    fun toValidateCartRequest(request: CartRequest): ValidateCartRequest {
        return ValidateCartRequest(
            userId = request.userId,
            items = request.items.map { item ->
                CartItemRequest(
                    productId = item.productId,
                    quantity = item.quantity
                )
            }
        )
    }
    
    /**
     * 장바구니 계산 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 장바구니 계산 유스케이스 응답 DTO
     * @return 웹 응답 DTO
     */
    fun toCartResponse(response: CalculateCartResponse): CartResponse {
        return CartResponse(
            items = response.items.map { item ->
                CartItemWebResponse(
                    productId = item.productId,
                    productName = item.productName,
                    unitPrice = item.unitPrice,
                    quantity = item.quantity,
                    totalPrice = item.totalPrice
                )
            },
            subtotal = response.subtotal,
            totalDiscount = response.totalDiscount,
            shippingFee = java.math.BigDecimal.ZERO, // 기본 배송비 0원
            finalAmount = response.finalAmount,
            appliedPromotions = response.appliedPromotions.map { promotion ->
                AppliedPromotionWebResponse(
                    promotionId = promotion.promotionId,
                    promotionName = promotion.promotionName,
                    discountAmount = promotion.discountAmount
                )
            }
        )
    }
    
    /**
     * 장바구니 검증 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 장바구니 검증 유스케이스 응답 DTO
     * @return 웹 검증 응답 DTO
     */
    fun toValidationResponse(response: ValidateCartResponse): ValidationResponse {
        return ValidationResponse(
            isValid = response.isValid,
            errors = response.errors,
            warnings = response.warnings
        )
    }
}
