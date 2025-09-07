package com.example.demo.application.user.usecase

import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import org.springframework.stereotype.Component

/**
 * 사용자 조회 유스케이스
 * 사용자 정보를 조회합니다.
 */
@Component
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    
    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 정보
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    fun getUserById(userId: Long): UserResponse {
        val user = userRepository.findById(UserId(userId))
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: $userId")
        
        return toUserResponse(user)
    }
    
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 이메일
     * @return 사용자 정보
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    fun getUserByEmail(email: String): UserResponse {
        val user = userRepository.findByEmail(Email(email))
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: $email")
        
        return toUserResponse(user)
    }
    
    /**
     * 모든 사용자를 조회합니다.
     * @return 모든 사용자 목록
     */
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { user ->
            toUserResponse(user)
        }
    }
    
    /**
     * 특정 멤버십 레벨의 사용자들을 조회합니다.
     * @param membershipLevel 멤버십 레벨
     * @return 해당 레벨의 사용자 목록
     */
    fun getUsersByMembershipLevel(membershipLevel: String): List<UserResponse> {
        val level = try {
            MembershipLevel.valueOf(membershipLevel)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효하지 않은 멤버십 레벨입니다: $membershipLevel")
        }
        
        return userRepository.findByMembershipLevel(level).map { user ->
            toUserResponse(user)
        }
    }
    
    /**
     * 사용자의 멤버십 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 멤버십 정보
     */
    fun getUserMembershipInfo(userId: Long): UserMembershipInfo {
        val user = userRepository.findById(UserId(userId))
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: $userId")
        
        return UserMembershipInfo(
            userId = user.id.value,
            email = user.email.value,
            membershipLevel = user.membershipLevel.name,
            membershipPriority = user.membershipLevel.priority,
            isNewCustomer = user.isNewCustomer,
            createdAt = user.createdAt
        )
    }
    
    /**
     * 도메인 모델을 응답 DTO로 변환합니다.
     * @param user 사용자 도메인 모델
     * @return 사용자 응답 DTO
     */
    private fun toUserResponse(user: com.example.demo.domain.user.model.User): UserResponse {
        return UserResponse(
            id = user.id.value,
            email = user.email.value,
            membershipLevel = user.membershipLevel.name,
            isNewCustomer = user.isNewCustomer,
            createdAt = user.createdAt
        )
    }
}

/**
 * 사용자 멤버십 정보
 */
data class UserMembershipInfo(
    val userId: Long,
    val email: String,
    val membershipLevel: String,
    val membershipPriority: Int,
    val isNewCustomer: Boolean,
    val createdAt: java.time.LocalDateTime
)
