package com.example.demo.infrastructure.persistence.user

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자 JPA 엔티티
 * 
 * 사용자 도메인 모델을 데이터베이스에 매핑하기 위한 JPA 엔티티입니다.
 * 도메인 모델과 분리되어 데이터베이스 특화된 매핑을 제공합니다.
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_user_email", columnList = "email", unique = true),
        Index(name = "idx_user_membership_level", columnList = "membership_level"),
        Index(name = "idx_user_created_at", columnList = "created_at")
    ]
)
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 255, unique = true)
    val email: String,
    
    @Column(nullable = false, length = 20)
    val membershipLevel: String,
    
    @Column(nullable = false)
    val isNewCustomer: Boolean = false,
    
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * 엔티티 생성 시점에 updatedAt 설정
     */
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        // createdAt은 생성자에서 이미 설정되므로 여기서는 updatedAt만 설정
    }
    
    /**
     * 엔티티 업데이트 시점에 updatedAt 갱신
     */
    @PreUpdate
    fun preUpdate() {
        // updatedAt은 생성자에서 이미 설정되므로 여기서는 추가 작업 없음
    }
    
    /**
     * 엔티티가 유효한지 확인
     */
    fun isValid(): Boolean {
        return email.isNotBlank() && 
               membershipLevel.isNotBlank() &&
               createdAt != null &&
               updatedAt != null &&
               updatedAt.isAfter(createdAt) || updatedAt.isEqual(createdAt)
    }
    
    /**
     * 엔티티를 문자열로 변환
     */
    override fun toString(): String {
        return "UserEntity(id=$id, email=$email, membershipLevel=$membershipLevel, isNewCustomer=$isNewCustomer)"
    }
    
    companion object {
        /**
         * 새로운 사용자 엔티티 생성
         */
        fun create(
            email: String,
            membershipLevel: String,
            isNewCustomer: Boolean = false
        ): UserEntity {
            val now = LocalDateTime.now()
            return UserEntity(
                email = email,
                membershipLevel = membershipLevel,
                isNewCustomer = isNewCustomer,
                createdAt = now,
                updatedAt = now
            )
        }
        
        /**
         * 기존 사용자 엔티티 생성
         */
        fun createExisting(
            id: Long,
            email: String,
            membershipLevel: String,
            isNewCustomer: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): UserEntity {
            return UserEntity(
                id = id,
                email = email,
                membershipLevel = membershipLevel,
                isNewCustomer = isNewCustomer,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
