package com.example.demo.application.common.port

import com.example.demo.domain.promotion.model.Promotion
import com.example.demo.domain.promotion.model.PromotionId
import java.time.LocalDateTime

/**
 * 프로모션 데이터 접근을 위한 포트 인터페이스
 * 클린 아키텍처의 의존성 역전 원칙에 따라 애플리케이션 레이어에서 정의
 */
interface PromotionRepository {
    
    /**
     * 활성 프로모션 목록을 조회합니다.
     * @return 활성 프로모션 목록
     */
    fun findActivePromotions(): List<Promotion>
    
    /**
     * 특정 시점에 활성화된 프로모션 목록을 조회합니다.
     * @param dateTime 기준 시점
     * @return 해당 시점에 활성화된 프로모션 목록
     */
    fun findActivePromotionsAt(dateTime: LocalDateTime): List<Promotion>
    
    /**
     * 프로모션 ID로 프로모션을 조회합니다.
     * @param promotionId 프로모션 ID
     * @return 프로모션 정보, 없으면 null
     */
    fun findById(promotionId: PromotionId): Promotion?
    
    /**
     * 프로모션명으로 프로모션을 조회합니다.
     * @param name 프로모션명
     * @return 프로모션 정보, 없으면 null
     */
    fun findByName(name: String): Promotion?
    
    /**
     * 프로모션을 저장합니다.
     * @param promotion 저장할 프로모션
     * @return 저장된 프로모션
     */
    fun save(promotion: Promotion): Promotion
    
    /**
     * 프로모션을 삭제합니다.
     * @param promotionId 삭제할 프로모션 ID
     */
    fun delete(promotionId: PromotionId)
    
    /**
     * 모든 프로모션을 조회합니다.
     * @return 모든 프로모션 목록
     */
    fun findAll(): List<Promotion>
    
    /**
     * 우선순위로 정렬된 활성 프로모션 목록을 조회합니다.
     * @return 우선순위 순으로 정렬된 활성 프로모션 목록
     */
    fun findActivePromotionsOrderByPriority(): List<Promotion>
}
