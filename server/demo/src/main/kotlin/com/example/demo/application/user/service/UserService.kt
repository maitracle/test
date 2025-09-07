package com.example.demo.application.user.service

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.user.usecase.CreateUserUseCase
import com.example.demo.application.user.usecase.GetUserUseCase
import com.example.demo.application.user.usecase.UserMembershipInfo
import org.springframework.stereotype.Service

/**
 * 사용자 서비스
 * 사용자 관련 비즈니스 로직을 조합하고 제공합니다.
 */
@Service
class UserService(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase
) {
    
    /**
     * 사용자를 생성합니다.
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 정보
     */
    fun createUser(request: CreateUserRequest): UserResponse {
        return createUserUseCase.execute(request)
    }
    
    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    fun getUserById(userId: Long): UserResponse {
        return getUserUseCase.getUserById(userId)
    }
    
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 이메일
     * @return 사용자 정보
     */
    fun getUserByEmail(email: String): UserResponse {
        return getUserUseCase.getUserByEmail(email)
    }
    
    /**
     * 모든 사용자를 조회합니다.
     * @return 모든 사용자 목록
     */
    fun getAllUsers(): List<UserResponse> {
        return getUserUseCase.getAllUsers()
    }
    
    /**
     * 특정 멤버십 레벨의 사용자들을 조회합니다.
     * @param membershipLevel 멤버십 레벨
     * @return 해당 레벨의 사용자 목록
     */
    fun getUsersByMembershipLevel(membershipLevel: String): List<UserResponse> {
        return getUserUseCase.getUsersByMembershipLevel(membershipLevel)
    }
    
    /**
     * 사용자의 멤버십 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 멤버십 정보
     */
    fun getUserMembershipInfo(userId: Long): UserMembershipInfo {
        return getUserUseCase.getUserMembershipInfo(userId)
    }
}
