package com.example.demo.domain.user.valueobject

/**
 * 회원 등급을 나타내는 열거형
 * 
 * 사용자의 회원 등급을 정의하며,
 * 프로모션 적용 시 우선순위를 결정하는 데 사용됩니다.
 * 
 * @property priority 우선순위 (높을수록 높은 등급)
 * @property displayName 표시명
 * @property description 설명
 */
enum class MembershipLevel(
    val priority: Int,
    val displayName: String,
    val description: String
) {
    /**
     * 신규 회원
     */
    NEW(1, "신규 회원", "가입한 지 30일 이내의 신규 회원"),
    
    /**
     * 일반 회원
     */
    REGULAR(2, "일반 회원", "기본 회원 등급"),
    
    /**
     * VIP 회원
     */
    VIP(3, "VIP 회원", "높은 구매 실적을 가진 우수 회원"),
    
    /**
     * 프리미엄 회원
     */
    PREMIUM(4, "프리미엄 회원", "최고 등급의 특별 회원");
    
    /**
     * 우선순위 비교
     */
    fun comparePriority(other: MembershipLevel): Int = this.priority.compareTo(other.priority)
    
    /**
     * 다른 등급보다 높은지 확인
     */
    fun isHigherThan(other: MembershipLevel): Boolean = this.priority > other.priority
    
    /**
     * 다른 등급보다 낮은지 확인
     */
    fun isLowerThan(other: MembershipLevel): Boolean = this.priority < other.priority
    
    /**
     * 다른 등급과 같은지 확인
     */
    fun isEqualTo(other: MembershipLevel): Boolean = this == other
    
    /**
     * 다른 등급보다 높거나 같은지 확인
     */
    fun isHigherThanOrEqualTo(other: MembershipLevel): Boolean = this.priority >= other.priority
    
    /**
     * 다른 등급보다 낮거나 같은지 확인
     */
    fun isLowerThanOrEqualTo(other: MembershipLevel): Boolean = this.priority <= other.priority
    
    /**
     * 최소 등급 이상인지 확인
     */
    fun isAtLeast(minLevel: MembershipLevel): Boolean = this.priority >= minLevel.priority
    
    /**
     * 최대 등급 이하인지 확인
     */
    fun isAtMost(maxLevel: MembershipLevel): Boolean = this.priority <= maxLevel.priority
    
    /**
     * 신규 회원인지 확인
     */
    fun isNew(): Boolean = this == NEW
    
    /**
     * 일반 회원 이상인지 확인
     */
    fun isRegularOrHigher(): Boolean = this >= REGULAR
    
    /**
     * VIP 회원 이상인지 확인
     */
    fun isVipOrHigher(): Boolean = this >= VIP
    
    /**
     * 프리미엄 회원인지 확인
     */
    fun isPremium(): Boolean = this == PREMIUM
    
    /**
     * 다음 등급으로 업그레이드 가능한지 확인
     */
    fun canUpgrade(): Boolean = this != PREMIUM
    
    /**
     * 다음 등급 반환 (업그레이드 불가능한 경우 null)
     */
    fun getNextLevel(): MembershipLevel? = when (this) {
        NEW -> REGULAR
        REGULAR -> VIP
        VIP -> PREMIUM
        PREMIUM -> null
    }
    
    /**
     * 이전 등급 반환 (다운그레이드 불가능한 경우 null)
     */
    fun getPreviousLevel(): MembershipLevel? = when (this) {
        NEW -> null
        REGULAR -> NEW
        VIP -> REGULAR
        PREMIUM -> VIP
    }
    
    /**
     * 등급을 문자열로 변환
     */
    override fun toString(): String = displayName
    
    companion object {
        /**
         * 우선순위로부터 MembershipLevel 찾기
         */
        fun fromPriority(priority: Int): MembershipLevel? {
            return values().find { it.priority == priority }
        }
        
        /**
         * 표시명으로부터 MembershipLevel 찾기
         */
        fun fromDisplayName(displayName: String): MembershipLevel? {
            return values().find { it.displayName == displayName }
        }
        
        /**
         * 최소 등급 반환
         */
        fun getMinLevel(): MembershipLevel = NEW
        
        /**
         * 최대 등급 반환
         */
        fun getMaxLevel(): MembershipLevel = PREMIUM
        
        /**
         * 모든 등급 반환 (우선순위 순)
         */
        fun getAllLevels(): List<MembershipLevel> = values().sortedBy { it.priority }
        
        /**
         * 일반 회원 이상의 등급들 반환
         */
        fun getRegularOrHigherLevels(): List<MembershipLevel> = 
            values().filter { it.isRegularOrHigher() }.sortedBy { it.priority }
        
        /**
         * VIP 회원 이상의 등급들 반환
         */
        fun getVipOrHigherLevels(): List<MembershipLevel> = 
            values().filter { it.isVipOrHigher() }.sortedBy { it.priority }
    }
}
