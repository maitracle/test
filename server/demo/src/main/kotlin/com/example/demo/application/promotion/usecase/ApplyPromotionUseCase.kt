package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.request.ApplyPromotionRequest
import com.example.demo.application.common.dto.response.ApplyPromotionResponse
import com.example.demo.application.promotion.service.PromotionEngine
import org.springframework.stereotype.Component

/**
 * 프로모션 적용 유스케이스
 * 장바구니에 프로모션을 적용하고 할인을 계산합니다.
 */
@Component
class ApplyPromotionUseCase(
    private val promotionEngine: PromotionEngine
) {
    
    /**
     * 프로모션 적용을 실행합니다.
     * @param request 프로모션 적용 요청
     * @return 프로모션 적용 결과
     */
    fun execute(request: ApplyPromotionRequest): ApplyPromotionResponse {
        // 프로모션 엔진을 통해 프로모션 적용
        val result = promotionEngine.applyPromotions(request.cart, request.user)
        
        return ApplyPromotionResponse(
            appliedPromotions = result.appliedPromotions.map { appliedPromotion ->
                com.example.demo.application.common.dto.response.AppliedPromotionResponse(
                    promotionId = appliedPromotion.promotionId,
                    promotionName = appliedPromotion.promotionName,
                    discountAmount = appliedPromotion.discountAmount
                )
            },
            totalDiscount = result.totalDiscount,
            finalAmount = result.finalAmount
        )
    }
    
    /**
     * 특정 프로모션을 적용합니다.
     * @param cart 장바구니
     * @param user 사용자
     * @param promotionId 적용할 프로모션 ID
     * @return 프로모션 적용 결과
     */
    fun executeSpecificPromotion(cart: com.example.demo.domain.cart.model.Cart, user: com.example.demo.domain.user.model.User, promotionId: Long): ApplyPromotionResponse {
        val result = promotionEngine.applySpecificPromotion(cart, user, promotionId)
        
        return ApplyPromotionResponse(
            appliedPromotions = result.appliedPromotions.map { appliedPromotion ->
                com.example.demo.application.common.dto.response.AppliedPromotionResponse(
                    promotionId = appliedPromotion.promotionId,
                    promotionName = appliedPromotion.promotionName,
                    discountAmount = appliedPromotion.discountAmount
                )
            },
            totalDiscount = result.totalDiscount,
            finalAmount = result.finalAmount
        )
    }
}
