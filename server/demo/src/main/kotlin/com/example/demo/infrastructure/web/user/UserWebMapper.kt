package com.example.demo.infrastructure.web.user

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.infrastructure.web.user.dto.CreateUserWebRequest
import com.example.demo.infrastructure.web.user.dto.UserWebResponse
import org.springframework.stereotype.Component

/**
 * 사용자 관련 웹 DTO와 유스케이스 DTO 간의 변환을 담당하는 매퍼
 */
@Component
class UserWebMapper {
    
    /**
     * 웹 요청 DTO를 사용자 생성 유스케이스 요청 DTO로 변환합니다.
     * @param request 웹 요청 DTO
     * @return 사용자 생성 유스케이스 요청 DTO
     */
    fun toCreateUserRequest(request: CreateUserWebRequest): CreateUserRequest {
        return CreateUserRequest(
            email = request.email,
            membershipLevel = request.membershipLevel,
            isNewCustomer = request.isNewCustomer
        )
    }
    
    /**
     * 사용자 유스케이스 응답 DTO를 웹 응답 DTO로 변환합니다.
     * @param response 사용자 유스케이스 응답 DTO
     * @return 사용자 웹 응답 DTO
     */
    fun toUserWebResponse(response: UserResponse): UserWebResponse {
        return UserWebResponse(
            id = response.id,
            email = response.email,
            membershipLevel = response.membershipLevel,
            isNewCustomer = response.isNewCustomer,
            createdAt = response.createdAt
        )
    }
}
