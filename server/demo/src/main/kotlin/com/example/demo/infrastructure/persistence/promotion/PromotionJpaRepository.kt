package com.example.demo.infrastructure.persistence.promotion

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 프로모션 JPA Repository 인터페이스
 * 
 * 프로모션 엔티티에 대한 데이터 접근을 위한 JPA Repository입니다.
 * Spring Data JPA의 기본 기능과 커스텀 쿼리를 제공합니다.
 */
@Repository
interface PromotionJpaRepository : JpaRepository<PromotionEntity, Long> {
    
    /**
     * 활성 프로모션 목록을 조회합니다.
     * @return 활성 프로모션 엔티티 목록
     */
    fun findByIsActiveTrue(): List<PromotionEntity>
    
    /**
     * 프로모션명으로 프로모션을 조회합니다.
     * @param name 프로모션명
     * @return 프로모션 엔티티, 없으면 null
     */
    fun findByName(name: String): PromotionEntity?
    
    /**
     * 프로모션 타입으로 프로모션들을 조회합니다.
     * @param type 프로모션 타입
     * @return 해당 타입의 프로모션 엔티티 목록
     */
    fun findByType(type: String): List<PromotionEntity>
    
    /**
     * 우선순위로 정렬된 활성 프로모션 목록을 조회합니다.
     * @return 우선순위 순으로 정렬된 활성 프로모션 엔티티 목록
     */
    fun findByIsActiveTrueOrderByPriorityAsc(): List<PromotionEntity>
    
    /**
     * 특정 시점에 활성화된 프로모션 목록을 조회합니다.
     * @param dateTime 기준 시점
     * @return 해당 시점에 활성화된 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.isActive = true AND p.startDate <= :dateTime AND p.endDate >= :dateTime")
    fun findActivePromotionsAt(@Param("dateTime") dateTime: LocalDateTime): List<PromotionEntity>
    
    /**
     * 특정 시점에 활성화된 프로모션을 우선순위로 정렬하여 조회합니다.
     * @param dateTime 기준 시점
     * @return 우선순위 순으로 정렬된 활성 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.isActive = true AND p.startDate <= :dateTime AND p.endDate >= :dateTime ORDER BY p.priority ASC")
    fun findActivePromotionsAtOrderByPriority(@Param("dateTime") dateTime: LocalDateTime): List<PromotionEntity>
    
    /**
     * 특정 카테고리 대상 프로모션을 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리 대상 프로모션 엔티티 목록
     */
    fun findByTargetCategory(category: String): List<PromotionEntity>
    
    /**
     * 특정 사용자 레벨 대상 프로모션을 조회합니다.
     * @param userLevel 사용자 레벨
     * @return 해당 사용자 레벨 대상 프로모션 엔티티 목록
     */
    fun findByTargetUserLevel(userLevel: String): List<PromotionEntity>
    
    /**
     * 신규 고객 전용 프로모션을 조회합니다.
     * @param isNewCustomerOnly 신규 고객 전용 여부
     * @return 신규 고객 전용 프로모션 엔티티 목록
     */
    fun findByIsNewCustomerOnly(isNewCustomerOnly: Boolean): List<PromotionEntity>
    
    /**
     * 특정 기간에 시작되는 프로모션을 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 시작되는 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.startDate BETWEEN :startDate AND :endDate")
    fun findByStartDateBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<PromotionEntity>
    
    /**
     * 특정 기간에 종료되는 프로모션을 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 종료되는 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.endDate BETWEEN :startDate AND :endDate")
    fun findByEndDateBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<PromotionEntity>
    
    /**
     * 특정 우선순위 이상의 프로모션을 조회합니다.
     * @param priority 우선순위
     * @return 해당 우선순위 이상의 프로모션 엔티티 목록
     */
    fun findByPriorityGreaterThanEqual(priority: Int): List<PromotionEntity>
    
    /**
     * 특정 우선순위 이하의 프로모션을 조회합니다.
     * @param priority 우선순위
     * @return 해당 우선순위 이하의 프로모션 엔티티 목록
     */
    fun findByPriorityLessThanEqual(priority: Int): List<PromotionEntity>
    
    /**
     * 할인 퍼센트가 있는 프로모션을 조회합니다.
     * @return 할인 퍼센트가 있는 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.discountPercentage IS NOT NULL")
    fun findByDiscountPercentageIsNotNull(): List<PromotionEntity>
    
    /**
     * 할인 금액이 있는 프로모션을 조회합니다.
     * @return 할인 금액이 있는 프로모션 엔티티 목록
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.discountAmount IS NOT NULL")
    fun findByDiscountAmountIsNotNull(): List<PromotionEntity>
    
    /**
     * 활성 프로모션 수를 조회합니다.
     * @return 활성 프로모션 수
     */
    @Query("SELECT COUNT(p) FROM PromotionEntity p WHERE p.isActive = true")
    fun countActivePromotions(): Long
    
    /**
     * 프로모션 타입별 프로모션 수를 조회합니다.
     * @return 프로모션 타입별 프로모션 수 목록
     */
    @Query("SELECT p.type, COUNT(p) FROM PromotionEntity p GROUP BY p.type")
    fun countByType(): List<Array<Any>>
    
    /**
     * 특정 시점에 활성화된 프로모션 수를 조회합니다.
     * @param dateTime 기준 시점
     * @return 해당 시점에 활성화된 프로모션 수
     */
    @Query("SELECT COUNT(p) FROM PromotionEntity p WHERE p.isActive = true AND p.startDate <= :dateTime AND p.endDate >= :dateTime")
    fun countActivePromotionsAt(@Param("dateTime") dateTime: LocalDateTime): Long
}
