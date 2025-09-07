package com.example.demo.application.user.service

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.user.usecase.CreateUserUseCase
import com.example.demo.application.user.usecase.GetUserUseCase
import com.example.demo.application.user.usecase.UserMembershipInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

/**
 * UserService 테스트
 * 사용자 서비스의 비즈니스 로직을 테스트합니다.
 */
class UserServiceTest : FunSpec({
    
    val createUserUseCase = mockk<CreateUserUseCase>()
    val getUserUseCase = mockk<GetUserUseCase>()
    val userService = UserService(createUserUseCase, getUserUseCase)
    
    val sampleUserResponse = UserResponse(
        id = 1L,
        email = "test@example.com",
        membershipLevel = "REGULAR",
        isNewCustomer = true,
        createdAt = LocalDateTime.now()
    )
    
    val sampleMembershipInfo = UserMembershipInfo(
        userId = 1L,
        email = "test@example.com",
        membershipLevel = "REGULAR",
        membershipPriority = 2,
        isNewCustomer = true,
        createdAt = LocalDateTime.now()
    )
    
    context("사용자 생성") {
        test("새로운 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "newuser@example.com",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            val expectedResponse = UserResponse(
                id = 2L,
                email = "newuser@example.com",
                membershipLevel = "REGULAR",
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            every { createUserUseCase.execute(request) } returns expectedResponse
            
            // When
            val result = userService.createUser(request)
            
            // Then
            result shouldBe expectedResponse
            verify { createUserUseCase.execute(request) }
        }
        
        test("VIP 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "vip@example.com",
                membershipLevel = "VIP",
                isNewCustomer = false
            )
            val expectedResponse = UserResponse(
                id = 3L,
                email = "vip@example.com",
                membershipLevel = "VIP",
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            every { createUserUseCase.execute(request) } returns expectedResponse
            
            // When
            val result = userService.createUser(request)
            
            // Then
            result shouldBe expectedResponse
            verify { createUserUseCase.execute(request) }
        }
    }
    
    context("사용자 조회") {
        test("사용자 ID로 사용자를 조회할 수 있어야 한다") {
            // Given
            val userId = 1L
            every { getUserUseCase.getUserById(userId) } returns sampleUserResponse
            
            // When
            val result = userService.getUserById(userId)
            
            // Then
            result shouldBe sampleUserResponse
            verify { getUserUseCase.getUserById(userId) }
        }
        
        test("이메일로 사용자를 조회할 수 있어야 한다") {
            // Given
            val email = "test@example.com"
            every { getUserUseCase.getUserByEmail(email) } returns sampleUserResponse
            
            // When
            val result = userService.getUserByEmail(email)
            
            // Then
            result shouldBe sampleUserResponse
            verify { getUserUseCase.getUserByEmail(email) }
        }
        
        test("모든 사용자를 조회할 수 있어야 한다") {
            // Given
            val userList = listOf(
                sampleUserResponse,
                UserResponse(
                    id = 2L,
                    email = "user2@example.com",
                    membershipLevel = "VIP",
                    isNewCustomer = false,
                    createdAt = LocalDateTime.now()
                )
            )
            every { getUserUseCase.getAllUsers() } returns userList
            
            // When
            val result = userService.getAllUsers()
            
            // Then
            result shouldBe userList
            result.size shouldBe 2
            verify { getUserUseCase.getAllUsers() }
        }
        
        test("특정 멤버십 레벨의 사용자들을 조회할 수 있어야 한다") {
            // Given
            val membershipLevel = "VIP"
            val vipUsers = listOf(
                UserResponse(
                    id = 1L,
                    email = "vip1@example.com",
                    membershipLevel = "VIP",
                    isNewCustomer = false,
                    createdAt = LocalDateTime.now()
                ),
                UserResponse(
                    id = 2L,
                    email = "vip2@example.com",
                    membershipLevel = "VIP",
                    isNewCustomer = false,
                    createdAt = LocalDateTime.now()
                )
            )
            every { getUserUseCase.getUsersByMembershipLevel(membershipLevel) } returns vipUsers
            
            // When
            val result = userService.getUsersByMembershipLevel(membershipLevel)
            
            // Then
            result shouldBe vipUsers
            result.size shouldBe 2
            result.all { it.membershipLevel == "VIP" } shouldBe true
            verify { getUserUseCase.getUsersByMembershipLevel(membershipLevel) }
        }
        
        test("사용자의 멤버십 정보를 조회할 수 있어야 한다") {
            // Given
            val userId = 1L
            every { getUserUseCase.getUserMembershipInfo(userId) } returns sampleMembershipInfo
            
            // When
            val result = userService.getUserMembershipInfo(userId)
            
            // Then
            result shouldBe sampleMembershipInfo
            result.userId shouldBe 1L
            result.membershipLevel shouldBe "REGULAR"
            result.membershipPriority shouldBe 2
            verify { getUserUseCase.getUserMembershipInfo(userId) }
        }
    }
    
    context("빈 결과 처리") {
        test("사용자가 없을 때 빈 목록을 반환해야 한다") {
            // Given
            every { getUserUseCase.getAllUsers() } returns emptyList()
            
            // When
            val result = userService.getAllUsers()
            
            // Then
            result shouldBe emptyList()
            verify { getUserUseCase.getAllUsers() }
        }
        
        test("특정 멤버십 레벨의 사용자가 없을 때 빈 목록을 반환해야 한다") {
            // Given
            val membershipLevel = "PREMIUM"
            every { getUserUseCase.getUsersByMembershipLevel(membershipLevel) } returns emptyList()
            
            // When
            val result = userService.getUsersByMembershipLevel(membershipLevel)
            
            // Then
            result shouldBe emptyList()
            verify { getUserUseCase.getUsersByMembershipLevel(membershipLevel) }
        }
    }
})
