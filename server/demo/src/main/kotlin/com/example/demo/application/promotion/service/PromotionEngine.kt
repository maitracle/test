package com.example.demo.application.promotion.service

import com.example.demo.application.common.port.PromotionRepository
import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.service.PromotionCalculator
import com.example.demo.domain.promotion.service.PromotionRuleEngine
import com.example.demo.domain.promotion.service.PromotionResult
import com.example.demo.domain.promotion.service.AppliedPromotion
import com.example.demo.domain.user.model.User
import org.springframework.stereotype.Service

/**
 * 프로모션 엔진 서비스
 * 장바구니에 프로모션을 적용하고 할인을 계산하는 핵심 서비스
 */
@Service
class PromotionEngine(
    private val promotionRepository: PromotionRepository
) {
    
    // 도메인 서비스들을 직접 인스턴스화 (클린 아키텍처 원칙 준수)
    private val promotionCalculator = PromotionCalculator()
    private val promotionRuleEngine = PromotionRuleEngine()
    
    /**
     * 장바구니와 사용자에 대해 활성 프로모션을 적용합니다.
     * @param cart 장바구니
     * @param user 사용자
     * @return 프로모션 적용 결과
     */
    fun applyPromotions(cart: Cart, user: User): PromotionResult {
        // 1. 활성 프로모션 조회
        val activePromotions = promotionRepository.findActivePromotionsOrderByPriority()
        
        // 2. 적용 가능한 프로모션 필터링
        val applicablePromotions = activePromotions.filter { promotion ->
            promotionRuleEngine.evaluateRules(promotion, cart, user).isEligible
        }
        
        // 3. 할인 계산
        return promotionCalculator.calculateDiscounts(cart, applicablePromotions, user)
    }
    
    /**
     * 특정 프로모션을 장바구니에 적용합니다.
     * @param cart 장바구니
     * @param user 사용자
     * @param promotionId 적용할 프로모션 ID
     * @return 프로모션 적용 결과
     */
    fun applySpecificPromotion(cart: Cart, user: User, promotionId: Long): PromotionResult {
        val promotion = promotionRepository.findById(com.example.demo.domain.promotion.model.PromotionId.of(promotionId))
            ?: throw IllegalArgumentException("프로모션을 찾을 수 없습니다: $promotionId")
        
        if (!promotionRuleEngine.evaluateRules(promotion, cart, user).isEligible) {
            throw IllegalArgumentException("프로모션 적용 조건을 만족하지 않습니다: ${promotion.name}")
        }
        
        return promotionCalculator.calculateDiscounts(cart, listOf(promotion), user)
    }
}

