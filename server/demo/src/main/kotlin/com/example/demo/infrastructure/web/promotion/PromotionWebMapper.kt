package com.example.demo.infrastructure.web.promotion

import com.example.demo.application.common.dto.request.ApplyPromotionRequest
import com.example.demo.application.common.dto.request.CreatePromotionRequest
import com.example.demo.application.common.dto.response.ApplyPromotionResponse
import com.example.demo.application.common.dto.response.CreatePromotionResponse
import com.example.demo.application.common.dto.response.PromotionResponse
import com.example.demo.infrastructure.web.promotion.dto.CreatePromotionWebRequest
import com.example.demo.infrastructure.web.promotion.dto.PromotionWebResponse
import com.example.demo.infrastructure.web.promotion.dto.ApplyPromotionWebRequest
import org.springframework.stereotype.Component

/**
 * 프로모션 관련 웹 DTO와 유스케이스 DTO 간의 변환을 담당하는 매퍼
 */
@Component
class PromotionWebMapper {
    
    /**
     * 웹 요청 DTO를 프로모션 생성 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 프로모션 생성 유스케이스 요청 DTO
     */
    fun toCreatePromotionRequest(request: CreatePromotionWebRequest): CreatePromotionRequest {
        return CreatePromotionRequest(
            name = request.name,
            description = request.description,
            type = request.type,
            priority = request.priority,
            startDate = request.startDate,
            endDate = request.endDate,
            targetCategory = request.targetCategory,
            minCartAmount = request.minCartAmount,
            minQuantity = request.minQuantity,
            targetUserLevel = request.targetUserLevel,
            discountPercentage = request.discountPercentage,
            discountAmount = request.discountAmount,
            maxDiscountAmount = request.maxDiscountAmount
        )
    }
    
    /**
     * 웹 요청 DTO를 프로모션 적용 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 프로모션 적용 유스케이스 요청 DTO
     * @throws IllegalArgumentException Cart나 User를 찾을 수 없는 경우
     */
    fun toApplyPromotionRequest(request: ApplyPromotionWebRequest): ApplyPromotionRequest {
        // 실제로는 CartRepository와 UserRepository를 주입받아서 
        // Cart와 User 도메인 모델을 조회해야 하지만, 
        // 여기서는 간단하게 처리하기 위해 예외를 발생시킴
        throw IllegalArgumentException("프로모션 적용 기능은 아직 구현되지 않았습니다. Cart와 User 도메인 모델 조회가 필요합니다.")
    }
    
    /**
     * 프로모션 생성 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 프로모션 생성 유스케이스 응답 DTO
     * @return 프로모션 웹 응답 DTO
     */
    fun toPromotionWebResponse(response: CreatePromotionResponse): PromotionWebResponse {
        return PromotionWebResponse(
            promotionId = response.promotionId,
            name = response.name,
            isActive = response.isActive
        )
    }
    
    /**
     * 프로모션 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 프로모션 유스케이스 응답 DTO
     * @return 프로모션 웹 응답 DTO
     */
    fun toPromotionWebResponse(response: PromotionResponse): PromotionWebResponse {
        return PromotionWebResponse(
            promotionId = response.id,
            name = response.name,
            description = response.description,
            type = response.type,
            priority = response.priority,
            isActive = response.isActive,
            startDate = response.startDate,
            endDate = response.endDate,
            targetCategory = response.targetCategory,
            minCartAmount = response.minCartAmount,
            minQuantity = response.minQuantity,
            targetUserLevel = response.targetUserLevel,
            discountPercentage = response.discountPercentage,
            discountAmount = response.discountAmount,
            maxDiscountAmount = response.maxDiscountAmount,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }
    
    /**
     * 프로모션 적용 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 프로모션 적용 유스케이스 응답 DTO
     * @return 프로모션 적용 웹 응답 DTO
     */
    fun toApplyPromotionWebResponse(response: ApplyPromotionResponse): Map<String, Any> {
        return mapOf(
            "appliedPromotions" to response.appliedPromotions,
            "totalDiscount" to response.totalDiscount,
            "finalAmount" to response.finalAmount
        )
    }
}
