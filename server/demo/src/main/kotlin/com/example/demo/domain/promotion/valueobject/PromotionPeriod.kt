package com.example.demo.domain.promotion.valueobject

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 프로모션 기간 값 객체
 * 
 * 프로모션의 유효 기간을 나타냅니다.
 * 시작일과 종료일을 포함하며, 기간의 유효성을 검증합니다.
 */
data class PromotionPeriod(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
) {
    init {
        require(startDate.isBefore(endDate)) { 
            "Start date must be before end date: start=$startDate, end=$endDate" 
        }
    }
    
    /**
     * 현재 시간이 프로모션 기간 내에 있는지 확인합니다.
     * 
     * @return 프로모션 기간 유효 여부
     */
    fun isValid(): Boolean = LocalDateTime.now().let { now ->
        !now.isBefore(startDate) && !now.isAfter(endDate)
    }
    
    /**
     * 프로모션이 시작되었는지 확인합니다.
     * 
     * @return 프로모션 시작 여부
     */
    fun hasStarted(): Boolean = LocalDateTime.now().isAfter(startDate) || LocalDateTime.now().isEqual(startDate)
    
    /**
     * 프로모션이 종료되었는지 확인합니다.
     * 
     * @return 프로모션 종료 여부
     */
    fun hasEnded(): Boolean = LocalDateTime.now().isAfter(endDate)
    
    /**
     * 프로모션이 시작되기 전인지 확인합니다.
     * 
     * @return 프로모션 시작 전 여부
     */
    fun isBeforeStart(): Boolean = LocalDateTime.now().isBefore(startDate)
    
    /**
     * 프로모션 기간의 총 일수를 계산합니다.
     * 
     * @return 총 일수
     */
    fun getTotalDays(): Long = ChronoUnit.DAYS.between(startDate, endDate)
    
    /**
     * 프로모션 기간의 남은 일수를 계산합니다.
     * 
     * @return 남은 일수 (음수면 이미 종료됨)
     */
    fun getRemainingDays(): Long = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate)
    
    /**
     * 프로모션 기간의 진행률을 계산합니다.
     * 
     * @return 진행률 (0.0 ~ 1.0)
     */
    fun getProgressRatio(): Double {
        val totalDays = getTotalDays()
        if (totalDays <= 0) return 1.0
        
        val elapsedDays = ChronoUnit.DAYS.between(startDate, LocalDateTime.now())
        return (elapsedDays.toDouble() / totalDays.toDouble()).coerceIn(0.0, 1.0)
    }
    
    /**
     * 다른 프로모션 기간과 겹치는지 확인합니다.
     * 
     * @param other 다른 프로모션 기간
     * @return 기간 겹침 여부
     */
    fun overlapsWith(other: PromotionPeriod): Boolean {
        return !(this.endDate.isBefore(other.startDate) || this.startDate.isAfter(other.endDate))
    }
    
    /**
     * 프로모션 기간을 연장합니다.
     * 
     * @param days 연장할 일수
     * @return 연장된 프로모션 기간
     */
    fun extend(days: Long): PromotionPeriod {
        require(days > 0) { "Extension days must be positive: $days" }
        return copy(endDate = endDate.plusDays(days))
    }
    
    /**
     * 프로모션 기간을 단축합니다.
     * 
     * @param days 단축할 일수
     * @return 단축된 프로모션 기간
     */
    fun shorten(days: Long): PromotionPeriod {
        require(days > 0) { "Shortening days must be positive: $days" }
        val newEndDate = endDate.minusDays(days)
        require(newEndDate.isAfter(startDate)) { "Cannot shorten period beyond start date" }
        return copy(endDate = newEndDate)
    }
    
    override fun toString(): String = "PromotionPeriod($startDate ~ $endDate)"
    
    companion object {
        /**
         * 시작일과 종료일로부터 프로모션 기간을 생성합니다.
         * 
         * @param startDate 시작일
         * @param endDate 종료일
         * @return PromotionPeriod 인스턴스
         */
        fun of(startDate: LocalDateTime, endDate: LocalDateTime): PromotionPeriod = 
            PromotionPeriod(startDate, endDate)
        
        /**
         * 시작일과 기간(일수)으로부터 프로모션 기간을 생성합니다.
         * 
         * @param startDate 시작일
         * @param durationDays 기간(일수)
         * @return PromotionPeriod 인스턴스
         */
        fun fromStartDate(startDate: LocalDateTime, durationDays: Long): PromotionPeriod {
            require(durationDays > 0) { "Duration days must be positive: $durationDays" }
            return PromotionPeriod(startDate, startDate.plusDays(durationDays))
        }
        
        /**
         * 종료일과 기간(일수)으로부터 프로모션 기간을 생성합니다.
         * 
         * @param endDate 종료일
         * @param durationDays 기간(일수)
         * @return PromotionPeriod 인스턴스
         */
        fun fromEndDate(endDate: LocalDateTime, durationDays: Long): PromotionPeriod {
            require(durationDays > 0) { "Duration days must be positive: $durationDays" }
            return PromotionPeriod(endDate.minusDays(durationDays), endDate)
        }
        
        /**
         * 현재 시간부터 지정된 일수 동안의 프로모션 기간을 생성합니다.
         * 
         * @param durationDays 기간(일수)
         * @return PromotionPeriod 인스턴스
         */
        fun fromNow(durationDays: Long): PromotionPeriod {
            require(durationDays > 0) { "Duration days must be positive: $durationDays" }
            val now = LocalDateTime.now()
            return PromotionPeriod(now, now.plusDays(durationDays))
        }
    }
}
