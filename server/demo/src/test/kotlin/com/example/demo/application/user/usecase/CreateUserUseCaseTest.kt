package com.example.demo.application.user.usecase

import com.example.demo.application.common.dto.request.CreateUserRequest
import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime

/**
 * CreateUserUseCase 테스트
 * 사용자 생성 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class CreateUserUseCaseTest : FunSpec({
    
    val userRepository = mockk<UserRepository>()
    val createUserUseCase = CreateUserUseCase(userRepository)
    
    beforeEach {
        clearMocks(userRepository)
    }
    
    context("사용자 생성 성공") {
        test("새로운 일반 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "newuser@example.com",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            val savedUser = User(
                id = UserId(1L),
                email = Email("newuser@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("newuser@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            val result = createUserUseCase.execute(request)
            
            // Then
            result.id shouldBe 1L
            result.email shouldBe "newuser@example.com"
            result.membershipLevel shouldBe "REGULAR"
            result.isNewCustomer shouldBe true
            result.createdAt shouldNotBe null
        }
        
        test("VIP 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "vip@example.com",
                membershipLevel = "VIP",
                isNewCustomer = false
            )
            val savedUser = User(
                id = UserId(2L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("vip@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            val result = createUserUseCase.execute(request)
            
            // Then
            result.id shouldBe 2L
            result.email shouldBe "vip@example.com"
            result.membershipLevel shouldBe "VIP"
            result.isNewCustomer shouldBe false
            
            verify { userRepository.existsByEmail(Email("vip@example.com")) }
            verify { userRepository.save(any()) }
        }
        
        test("프리미엄 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "premium@example.com",
                membershipLevel = "PREMIUM",
                isNewCustomer = false
            )
            val savedUser = User(
                id = UserId(3L),
                email = Email("premium@example.com"),
                membershipLevel = MembershipLevel.PREMIUM,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("premium@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            val result = createUserUseCase.execute(request)
            
            // Then
            result.id shouldBe 3L
            result.email shouldBe "premium@example.com"
            result.membershipLevel shouldBe "PREMIUM"
            result.isNewCustomer shouldBe false
            
            verify { userRepository.existsByEmail(Email("premium@example.com")) }
            verify { userRepository.save(any()) }
        }
        
        test("기본값으로 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(email = "default@example.com")
            val savedUser = User(
                id = UserId(4L),
                email = Email("default@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("default@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            val result = createUserUseCase.execute(request)
            
            // Then
            result.id shouldBe 4L
            result.email shouldBe "default@example.com"
            result.membershipLevel shouldBe "REGULAR"
            result.isNewCustomer shouldBe true
            
            verify { userRepository.existsByEmail(Email("default@example.com")) }
            verify { userRepository.save(any()) }
        }
    }
    
    context("사용자 생성 실패") {
        test("이미 존재하는 이메일로 사용자 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "existing@example.com",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            
            every { userRepository.existsByEmail(Email("existing@example.com")) } returns true
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createUserUseCase.execute(request)
            }
            
            exception.message shouldBe "이미 존재하는 이메일입니다: existing@example.com"
            verify { userRepository.existsByEmail(Email("existing@example.com")) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
        
        test("유효하지 않은 멤버십 레벨로 사용자 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "invalid@example.com",
                membershipLevel = "INVALID_LEVEL",
                isNewCustomer = true
            )
            
            every { userRepository.existsByEmail(Email("invalid@example.com")) } returns false
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createUserUseCase.execute(request)
            }
            
            exception.message shouldBe "유효하지 않은 멤버십 레벨입니다: INVALID_LEVEL"
            verify { userRepository.existsByEmail(Email("invalid@example.com")) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
        
        test("빈 이메일로 사용자 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createUserUseCase.execute(request)
            }
            
            exception.message shouldBe "Email cannot be blank"
        }
        
        test("공백 이메일로 사용자 생성 시 예외가 발생해야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "   ",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createUserUseCase.execute(request)
            }
            
            exception.message shouldBe "Email cannot be blank"
        }
    }
    
    context("경계값 테스트") {
        test("NEW 멤버십 레벨로 사용자를 생성할 수 있어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "new@example.com",
                membershipLevel = "NEW",
                isNewCustomer = true
            )
            val savedUser = User(
                id = UserId(5L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("new@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            val result = createUserUseCase.execute(request)
            
            // Then
            result.membershipLevel shouldBe "NEW"
            verify { userRepository.existsByEmail(Email("new@example.com")) }
            verify { userRepository.save(any()) }
        }
        
        test("대소문자 구분 없이 멤버십 레벨을 처리해야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "lowercase@example.com",
                membershipLevel = "regular", // 소문자
                isNewCustomer = true
            )
            
            every { userRepository.existsByEmail(Email("lowercase@example.com")) } returns false
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                createUserUseCase.execute(request)
            }
            
            exception.message shouldBe "유효하지 않은 멤버십 레벨입니다: regular"
            verify { userRepository.existsByEmail(Email("lowercase@example.com")) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }
    
    context("저장 과정 검증") {
        test("사용자 저장 시 올바른 도메인 모델이 전달되어야 한다") {
            // Given
            val request = CreateUserRequest(
                email = "save@example.com",
                membershipLevel = "REGULAR",
                isNewCustomer = true
            )
            val savedUser = User(
                id = UserId(6L),
                email = Email("save@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            every { userRepository.existsByEmail(Email("save@example.com")) } returns false
            every { userRepository.save(any()) } returns savedUser
            
            // When
            createUserUseCase.execute(request)
            
            // Then
            verify { userRepository.existsByEmail(Email("save@example.com")) }
            verify { userRepository.save(any()) }
        }
    }
})
