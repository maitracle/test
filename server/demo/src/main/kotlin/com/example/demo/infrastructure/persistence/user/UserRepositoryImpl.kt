package com.example.demo.infrastructure.persistence.user

import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 Repository 구현체
 * 
 * UserRepository 포트의 구현체로, JPA를 사용하여 사용자 데이터를 관리합니다.
 * 도메인 모델과 엔티티 간의 변환을 담당합니다.
 */
@Component
@Transactional(readOnly = true)
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    
    override fun findById(userId: UserId): User? {
        return userJpaRepository.findById(userId.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.value)
            ?.toDomain()
    }
    
    @Transactional
    override fun save(user: User): User {
        val entity = user.toEntity()
        val savedEntity = userJpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    @Transactional
    override fun delete(userId: UserId) {
        userJpaRepository.deleteById(userId.value)
    }
    
    override fun findAll(): List<User> {
        return userJpaRepository.findAll()
            .map { it.toDomain() }
    }
    
    override fun findByMembershipLevel(membershipLevel: MembershipLevel): List<User> {
        return userJpaRepository.findAll()
            .filter { it.membershipLevel == membershipLevel.name }
            .map { it.toDomain() }
    }
    
    override fun existsByEmail(email: Email): Boolean {
        return userJpaRepository.existsByEmail(email.value)
    }
}

/**
 * UserEntity를 User 도메인 모델로 변환하는 확장 함수
 */
private fun UserEntity.toDomain(): User {
    return User.createExistingUser(
        id = UserId.of(id ?: throw IllegalStateException("User ID cannot be null")),
        email = Email(email),
        membershipLevel = MembershipLevel.valueOf(membershipLevel),
        isNewCustomer = isNewCustomer,
        createdAt = createdAt
    )
}

/**
 * User 도메인 모델을 UserEntity로 변환하는 확장 함수
 */
private fun User.toEntity(): UserEntity {
    return UserEntity.createExisting(
        id = id.value,
        email = email.value,
        membershipLevel = membershipLevel.name,
        isNewCustomer = isNewCustomer,
        createdAt = createdAt,
        updatedAt = java.time.LocalDateTime.now()
    )
}
