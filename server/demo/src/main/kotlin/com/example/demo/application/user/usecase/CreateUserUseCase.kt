package com.example.demo.application.user.usecase

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 사용자 생성 유스케이스
 * 새로운 사용자를 생성하고 저장합니다.
 */
@Component
class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    
    /**
     * 사용자 생성을 실행합니다.
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 정보
     */
    fun execute(request: CreateUserRequest): UserResponse {
        // 1. 요청 유효성 검증
        validateCreateRequest(request)
        
        // 2. 사용자 도메인 모델 생성
        val user = User(
            id = UserId(0), // 새 사용자
            email = Email(request.email),
            membershipLevel = MembershipLevel.valueOf(request.membershipLevel),
            isNewCustomer = request.isNewCustomer,
            createdAt = LocalDateTime.now()
        )
        
        // 3. 사용자 저장
        val savedUser = userRepository.save(user)
        
        // 4. 응답 생성
        return toUserResponse(savedUser)
    }
    
    /**
     * 사용자 생성 요청 유효성 검증을 수행합니다.
     * @param request 사용자 생성 요청
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    private fun validateCreateRequest(request: CreateUserRequest) {
        // 이메일 중복 확인
        val email = Email(request.email)
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다: ${request.email}")
        }
        
        // 멤버십 레벨 유효성 확인
        try {
            MembershipLevel.valueOf(request.membershipLevel)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효하지 않은 멤버십 레벨입니다: ${request.membershipLevel}")
        }
    }
    
    /**
     * 도메인 모델을 응답 DTO로 변환합니다.
     * @param user 사용자 도메인 모델
     * @return 사용자 응답 DTO
     */
    private fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id.value,
            email = user.email.value,
            membershipLevel = user.membershipLevel.name,
            isNewCustomer = user.isNewCustomer,
            createdAt = user.createdAt
        )
    }
}
