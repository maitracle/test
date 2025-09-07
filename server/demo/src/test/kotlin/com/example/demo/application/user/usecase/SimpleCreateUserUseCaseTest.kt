package com.example.demo.application.user.usecase

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

/**
 * 간단한 CreateUserUseCase 테스트
 * MockK 문제를 해결하기 위한 간단한 테스트
 */
class SimpleCreateUserUseCaseTest : FunSpec({
    
    val userRepository = mockk<UserRepository>()
    val createUserUseCase = CreateUserUseCase(userRepository)
    
    test("새로운 사용자를 생성할 수 있어야 한다") {
        // Given
        val request = CreateUserRequest(
            email = "test@example.com",
            membershipLevel = "REGULAR",
            isNewCustomer = true
        )
        val savedUser = User(
            id = UserId(1L),
            email = Email("test@example.com"),
            membershipLevel = MembershipLevel.REGULAR,
            isNewCustomer = true,
            createdAt = LocalDateTime.now()
        )
        
        every { userRepository.existsByEmail(Email("test@example.com")) } returns false
        every { userRepository.save(any()) } returns savedUser
        
        // When
        val result = createUserUseCase.execute(request)
        
        // Then
        result.id shouldBe 1L
        result.email shouldBe "test@example.com"
        result.membershipLevel shouldBe "REGULAR"
        result.isNewCustomer shouldBe true
    }
})
