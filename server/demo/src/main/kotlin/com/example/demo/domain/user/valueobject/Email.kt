package com.example.demo.domain.user.valueobject

/**
 * 이메일을 나타내는 값 객체
 * 
 * 이메일 주소의 형식을 검증하고,
 * 이메일 관련 기능을 제공합니다.
 * 
 * @property value 이메일 주소
 */
@JvmInline
value class Email(val value: String) {
    
    init {
        require(value.isNotBlank()) { 
            "Email cannot be blank" 
        }
        require(value.matches(EMAIL_REGEX)) { 
            "Invalid email format: $value" 
        }
        require(value.length <= MAX_EMAIL_LENGTH) {
            "Email length cannot exceed $MAX_EMAIL_LENGTH characters: $value"
        }
    }
    
    /**
     * 이메일 비교
     */
    operator fun compareTo(other: Email): Int = value.compareTo(other.value)
    
    /**
     * 이메일이 다른 이메일과 같은지 확인
     */
    fun isEqualTo(other: Email): Boolean = this == other
    
    /**
     * 이메일이 특정 도메인인지 확인
     */
    fun isFromDomain(domain: String): Boolean {
        return value.endsWith("@$domain", ignoreCase = true)
    }
    
    /**
     * 이메일의 로컬 부분(@ 앞부분) 반환
     */
    fun getLocalPart(): String {
        return value.substringBefore("@")
    }
    
    /**
     * 이메일의 도메인 부분(@ 뒷부분) 반환
     */
    fun getDomainPart(): String {
        return value.substringAfter("@")
    }
    
    /**
     * 이메일을 소문자로 변환
     */
    fun toLowerCase(): Email = Email(value.lowercase())
    
    /**
     * 이메일을 문자열로 변환
     */
    override fun toString(): String = value
    
    companion object {
        /**
         * 이메일 정규식 패턴
         */
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        /**
         * 최대 이메일 길이
         */
        private const val MAX_EMAIL_LENGTH = 254
        
        /**
         * String으로부터 Email 생성
         */
        fun of(value: String): Email = Email(value)
        
        /**
         * 안전한 Email 생성 (유효하지 않을 경우 null 반환)
         */
        fun safeOf(value: String?): Email? = if (value != null && value.isNotBlank() && value.matches(EMAIL_REGEX) && value.length <= MAX_EMAIL_LENGTH) {
            Email(value)
        } else null
        
        /**
         * 이메일 형식 검증
         */
        fun isValid(email: String): Boolean {
            return email.isNotBlank() && 
                   email.matches(EMAIL_REGEX) && 
                   email.length <= MAX_EMAIL_LENGTH
        }
    }
}
