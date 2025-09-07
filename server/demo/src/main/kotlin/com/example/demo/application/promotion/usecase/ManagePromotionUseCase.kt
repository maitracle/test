package com.example.demo.application.promotion.usecase

import com.example.demo.application.common.dto.response.PromotionResponse
import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.promotion.model.PromotionId
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 프로모션 관리 유스케이스
 * 프로모션의 조회, 수정, 삭제, 활성화/비활성화를 관리합니다.
 */
@Component
class ManagePromotionUseCase(
    private val promotionRepository: PromotionRepository
) {
    
    /**
     * 모든 프로모션을 조회합니다.
     * @return 프로모션 목록
     */
    fun getAllPromotions(): List<PromotionResponse> {
        return promotionRepository.findAll().map { promotion ->
            toPromotionResponse(promotion)
        }
    }
    
    /**
     * 활성 프로모션을 조회합니다.
     * @return 활성 프로모션 목록
     */
    fun getActivePromotions(): List<PromotionResponse> {
        return promotionRepository.findActivePromotionsOrderByPriority().map { promotion ->
            toPromotionResponse(promotion)
        }
    }
    
    /**
     * 프로모션 ID로 프로모션을 조회합니다.
     * @param promotionId 프로모션 ID
     * @return 프로모션 정보
     * @throws IllegalArgumentException 프로모션을 찾을 수 없는 경우
     */
    fun getPromotionById(promotionId: Long): PromotionResponse {
        val promotion = promotionRepository.findById(PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        return toPromotionResponse(promotion)
    }
    
    /**
     * 프로모션을 활성화합니다.
     * @param promotionId 프로모션 ID
     * @return 활성화된 프로모션 정보
     */
    fun activatePromotion(promotionId: Long): PromotionResponse {
        val promotion = promotionRepository.findById(PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        val activatedPromotion = promotion.activate()
        val savedPromotion = promotionRepository.save(activatedPromotion)
        
        return toPromotionResponse(savedPromotion)
    }
    
    /**
     * 프로모션을 비활성화합니다.
     * @param promotionId 프로모션 ID
     * @return 비활성화된 프로모션 정보
     */
    fun deactivatePromotion(promotionId: Long): PromotionResponse {
        val promotion = promotionRepository.findById(PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        val deactivatedPromotion = promotion.deactivate()
        val savedPromotion = promotionRepository.save(deactivatedPromotion)
        
        return toPromotionResponse(savedPromotion)
    }
    
    /**
     * 프로모션을 삭제합니다.
     * @param promotionId 프로모션 ID
     */
    fun deletePromotion(promotionId: Long) {
        val promotion = promotionRepository.findById(PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        promotionRepository.delete(PromotionId.of(promotionId))
    }
    
    /**
     * 프로모션을 수정합니다.
     * @param promotionId 프로모션 ID
     * @param name 프로모션명 (선택사항)
     * @param description 프로모션 설명 (선택사항)
     * @param priority 우선순위 (선택사항)
     * @return 수정된 프로모션 정보
     */
    fun updatePromotion(
        promotionId: Long,
        name: String? = null,
        description: String? = null,
        priority: Int? = null
    ): PromotionResponse {
        val promotion = promotionRepository.findById(PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        val updatedPromotion = promotion.copy(
            name = name ?: promotion.name,
            description = description ?: promotion.description,
            priority = priority ?: promotion.priority,
            updatedAt = LocalDateTime.now()
        )
        
        val savedPromotion = promotionRepository.save(updatedPromotion)
        
        return toPromotionResponse(savedPromotion)
    }
    
    /**
     * 도메인 모델을 응답 DTO로 변환합니다.
     * @param promotion 프로모션 도메인 모델
     * @return 프로모션 응답 DTO
     */
    private fun toPromotionResponse(promotion: com.example.demo.domain.promotion.model.Promotion): PromotionResponse {
        return PromotionResponse(
            id = promotion.id.value,
            name = promotion.name,
            description = promotion.description,
            type = promotion.type.name,
            priority = promotion.priority,
            isActive = promotion.isActive,
            startDate = promotion.period.startDate,
            endDate = promotion.period.endDate,
            targetCategory = promotion.conditions.targetCategory,
            minCartAmount = promotion.conditions.minCartAmount?.amount,
            minQuantity = promotion.conditions.minQuantity?.value,
            targetUserLevel = promotion.conditions.targetUserLevel?.name,
            discountPercentage = promotion.benefits.discountPercentage?.value,
            discountAmount = promotion.benefits.discountAmount?.amount,
            maxDiscountAmount = promotion.benefits.maxDiscountAmount?.amount,
            createdAt = promotion.createdAt,
            updatedAt = promotion.updatedAt
        )
    }
}
