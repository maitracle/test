package com.example.demo.domain.user.model

import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import java.time.LocalDateTime

/**
 * 사용자를 나타내는 도메인 모델
 * 
 * 사용자의 기본 정보와 프로모션 자격을 관리하며,
 * 회원 등급 업그레이드 등의 비즈니스 로직을 포함합니다.
 * 
 * @property id 사용자 ID
 * @property email 이메일 주소
 * @property membershipLevel 회원 등급
 * @property isNewCustomer 신규 고객 여부
 * @property createdAt 생성일시
 */
data class User(
    val id: UserId,
    val email: Email,
    val membershipLevel: MembershipLevel,
    val isNewCustomer: Boolean,
    val createdAt: LocalDateTime
) {
    
    /**
     * 프로모션 자격 검증
     * 
     * @param requiredLevel 필요한 최소 회원 등급
     * @return 프로모션 적용 가능 여부
     */
    fun isEligibleForPromotion(requiredLevel: MembershipLevel?): Boolean {
        return requiredLevel?.let { targetLevel ->
            membershipLevel.isHigherThanOrEqualTo(targetLevel)
        } ?: true
    }
    
    /**
     * 신규 고객 전용 프로모션 자격 검증
     * 
     * @return 신규 고객 프로모션 적용 가능 여부
     */
    fun isEligibleForNewCustomerPromotion(): Boolean {
        return isNewCustomer
    }
    
    /**
     * VIP 이상 회원 프로모션 자격 검증
     * 
     * @return VIP 이상 프로모션 적용 가능 여부
     */
    fun isEligibleForVipPromotion(): Boolean {
        return membershipLevel.isVipOrHigher()
    }
    
    /**
     * 프리미엄 회원 프로모션 자격 검증
     * 
     * @return 프리미엄 프로모션 적용 가능 여부
     */
    fun isEligibleForPremiumPromotion(): Boolean {
        return membershipLevel.isPremium()
    }
    
    /**
     * 회원 등급 업그레이드
     * 
     * @param newLevel 새로운 회원 등급
     * @return 업그레이드된 사용자
     */
    fun upgradeMembership(newLevel: MembershipLevel): User {
        require(newLevel.isHigherThan(membershipLevel)) { 
            "New membership level must be higher than current level" 
        }
        return copy(membershipLevel = newLevel)
    }
    
    /**
     * 신규 고객 상태 변경
     * 
     * @param isNew 새로운 신규 고객 여부
     * @return 상태가 변경된 사용자
     */
    fun updateNewCustomerStatus(isNew: Boolean): User {
        return copy(isNewCustomer = isNew)
    }
    
    /**
     * 신규 고객에서 일반 고객으로 전환
     * 
     * @return 전환된 사용자
     */
    fun convertToRegularCustomer(): User {
        return copy(isNewCustomer = false)
    }
    
    /**
     * 사용자가 활성 상태인지 확인
     * 
     * @return 활성 상태 여부
     */
    fun isActive(): Boolean {
        return true // 현재는 모든 사용자가 활성 상태
    }
    
    /**
     * 사용자가 신규 회원인지 확인
     * 
     * @return 신규 회원 여부
     */
    fun isNewMember(): Boolean {
        return membershipLevel.isNew()
    }
    
    /**
     * 사용자가 일반 회원 이상인지 확인
     * 
     * @return 일반 회원 이상 여부
     */
    fun isRegularOrHigher(): Boolean {
        return membershipLevel.isRegularOrHigher()
    }
    
    /**
     * 사용자가 VIP 회원 이상인지 확인
     * 
     * @return VIP 회원 이상 여부
     */
    fun isVipOrHigher(): Boolean {
        return membershipLevel.isVipOrHigher()
    }
    
    /**
     * 사용자가 프리미엄 회원인지 확인
     * 
     * @return 프리미엄 회원 여부
     */
    fun isPremium(): Boolean {
        return membershipLevel.isPremium()
    }
    
    /**
     * 사용자의 가입 기간 계산 (일 단위)
     * 
     * @return 가입 기간 (일)
     */
    fun getMembershipDurationInDays(): Long {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays()
    }
    
    /**
     * 사용자의 가입 기간이 특정 일수 이상인지 확인
     * 
     * @param days 확인할 일수
     * @return 해당 일수 이상 가입 여부
     */
    fun hasBeenMemberForAtLeast(days: Long): Boolean {
        return getMembershipDurationInDays() >= days
    }
    
    /**
     * 사용자 정보를 문자열로 변환
     */
    override fun toString(): String {
        return "User(id=${id.value}, email=${email.value}, level=${membershipLevel.displayName}, isNew=$isNewCustomer)"
    }
    
    companion object {
        /**
         * 신규 사용자 생성
         * 
         * @param id 사용자 ID
         * @param email 이메일 주소
         * @return 신규 사용자
         */
        fun createNewUser(id: UserId, email: Email): User {
            return User(
                id = id,
                email = email,
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
        }
        
        /**
         * 기존 사용자 생성
         * 
         * @param id 사용자 ID
         * @param email 이메일 주소
         * @param membershipLevel 회원 등급
         * @param isNewCustomer 신규 고객 여부
         * @param createdAt 생성일시
         * @return 기존 사용자
         */
        fun createExistingUser(
            id: UserId,
            email: Email,
            membershipLevel: MembershipLevel,
            isNewCustomer: Boolean = false,
            createdAt: LocalDateTime
        ): User {
            return User(
                id = id,
                email = email,
                membershipLevel = membershipLevel,
                isNewCustomer = isNewCustomer,
                createdAt = createdAt
            )
        }
    }
}
