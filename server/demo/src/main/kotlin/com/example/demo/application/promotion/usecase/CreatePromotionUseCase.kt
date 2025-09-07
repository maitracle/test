package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.request.CreatePromotionRequest
import com.example.demo.application.common.dto.response.CreatePromotionResponse
import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import com.example.demo.domain.promotion.model.PromotionType
import com.example.demo.domain.promotion.valueobject.DiscountPercentage
import com.example.demo.domain.promotion.valueobject.PromotionBenefits
import com.example.demo.domain.promotion.valueobject.PromotionConditions
import com.example.demo.domain.promotion.valueobject.PromotionPeriod
import com.example.demo.domain.user.valueobject.MembershipLevel
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 프로모션 생성 유스케이스
 * 새로운 프로모션을 생성하고 저장합니다.
 */
@Component
class CreatePromotionUseCase(
    private val promotionRepository: PromotionRepository
) {
    
    /**
     * 프로모션 생성을 실행합니다.
     * @param request 프로모션 생성 요청
     * @return 프로모션 생성 결과
     */
    fun execute(request: CreatePromotionRequest): CreatePromotionResponse {
        // 1. 프로모션 도메인 모델 생성
        val promotion = Promotion(
            id = PromotionId.of(1L), // 임시 ID (실제로는 저장 시 생성됨)
            name = request.name,
            description = request.description,
            type = PromotionType.valueOf(request.type),
            priority = request.priority,
            isActive = true,
            period = PromotionPeriod(request.startDate, request.endDate),
            conditions = PromotionConditions(
                targetCategory = request.targetCategory,
                minCartAmount = request.minCartAmount?.let { Money(it) },
                minQuantity = request.minQuantity?.let { Quantity(it) },
                targetUserLevel = request.targetUserLevel?.let { MembershipLevel.valueOf(it) }
            ),
            benefits = PromotionBenefits(
                discountPercentage = request.discountPercentage?.let { DiscountPercentage(it) },
                discountAmount = request.discountAmount?.let { Money(it) },
                maxDiscountAmount = request.maxDiscountAmount?.let { Money(it) }
            ),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 2. 프로모션 저장
        val savedPromotion = promotionRepository.save(promotion)
        
        return CreatePromotionResponse(
            promotionId = savedPromotion.id.value,
            name = savedPromotion.name,
            isActive = savedPromotion.isActive
        )
    }
    
    /**
     * 프로모션 생성 전 유효성 검증을 수행합니다.
     * @param request 프로모션 생성 요청
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    fun validateCreateRequest(request: CreatePromotionRequest) {
        // 이름 중복 확인
        val existingPromotion = promotionRepository.findByName(request.name)
        if (existingPromotion != null) {
            throw IllegalArgumentException("이미 존재하는 프로모션 이름입니다: ${request.name}")
        }
        
        // 날짜 유효성 확인
        if (request.startDate.isAfter(request.endDate)) {
            throw IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.")
        }
        
        // 할인 조건 확인
        if (request.discountPercentage == null && request.discountAmount == null) {
            throw IllegalArgumentException("할인율 또는 할인금액 중 하나는 반드시 설정되어야 합니다.")
        }
        
        if (request.discountPercentage != null && request.discountAmount != null) {
            throw IllegalArgumentException("할인율과 할인금액은 동시에 설정할 수 없습니다.")
        }
        
        // 할인율 범위 확인
        request.discountPercentage?.let { percentage ->
            if (percentage < java.math.BigDecimal.ZERO || percentage > java.math.BigDecimal(100)) {
                throw IllegalArgumentException("할인율은 0% 이상 100% 이하여야 합니다.")
            }
        }
        
        // 할인금액 양수 확인
        request.discountAmount?.let { amount ->
            if (amount <= java.math.BigDecimal.ZERO) {
                throw IllegalArgumentException("할인금액은 0보다 커야 합니다.")
            }
        }
        
        // 우선순위 양수 확인
        if (request.priority <= 0) {
            throw IllegalArgumentException("우선순위는 1 이상이어야 합니다.")
        }
    }
}
