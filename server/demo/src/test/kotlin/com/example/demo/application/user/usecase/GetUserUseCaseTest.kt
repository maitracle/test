package com.example.demo.application.user.usecase

import com.example.demo.application.common.dto.response.UserResponse
import com.example.demo.application.common.port.UserRepository
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

/**
 * GetUserUseCase 테스트
 * 사용자 조회 유스케이스의 비즈니스 로직을 테스트합니다.
 */
class GetUserUseCaseTest : FunSpec({
    
    val userRepository = mockk<UserRepository>()
    val getUserUseCase = GetUserUseCase(userRepository)
    
    beforeEach {
        clearMocks(userRepository)
    }
    
    val sampleUser = User(
        id = UserId(1L),
        email = Email("test@example.com"),
        membershipLevel = MembershipLevel.REGULAR,
        isNewCustomer = true,
        createdAt = LocalDateTime.now()
    )
    
    val vipUser = User(
        id = UserId(2L),
        email = Email("vip@example.com"),
        membershipLevel = MembershipLevel.VIP,
        isNewCustomer = false,
        createdAt = LocalDateTime.now()
    )
    
    val premiumUser = User(
        id = UserId(3L),
        email = Email("premium@example.com"),
        membershipLevel = MembershipLevel.PREMIUM,
        isNewCustomer = false,
        createdAt = LocalDateTime.now()
    )
    
    context("사용자 ID로 조회") {
        test("존재하는 사용자 ID로 조회할 수 있어야 한다") {
            // Given
            val userId = 1L
            every { userRepository.findById(UserId(userId)) } returns sampleUser
            
            // When
            val result = getUserUseCase.getUserById(userId)
            
            // Then
            result.id shouldBe 1L
            result.email shouldBe "test@example.com"
            result.membershipLevel shouldBe "REGULAR"
            result.isNewCustomer shouldBe true
            result.createdAt shouldBe sampleUser.createdAt
            
            verify { userRepository.findById(UserId(userId)) }
        }
        
        test("존재하지 않는 사용자 ID로 조회 시 예외가 발생해야 한다") {
            // Given
            val userId = 999L
            every { userRepository.findById(UserId(userId)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserById(userId)
            }
            
            exception.message shouldBe "사용자를 찾을 수 없습니다: 999"
            verify { userRepository.findById(UserId(userId)) }
        }
        
        test("VIP 사용자를 ID로 조회할 수 있어야 한다") {
            // Given
            val userId = 2L
            every { userRepository.findById(UserId(userId)) } returns vipUser
            
            // When
            val result = getUserUseCase.getUserById(userId)
            
            // Then
            result.id shouldBe 2L
            result.email shouldBe "vip@example.com"
            result.membershipLevel shouldBe "VIP"
            result.isNewCustomer shouldBe false
            
            verify { userRepository.findById(UserId(userId)) }
        }
    }
    
    context("이메일로 조회") {
        test("존재하는 이메일로 조회할 수 있어야 한다") {
            // Given
            val email = "test@example.com"
            every { userRepository.findByEmail(Email(email)) } returns sampleUser
            
            // When
            val result = getUserUseCase.getUserByEmail(email)
            
            // Then
            result.id shouldBe 1L
            result.email shouldBe "test@example.com"
            result.membershipLevel shouldBe "REGULAR"
            result.isNewCustomer shouldBe true
            
            verify { userRepository.findByEmail(Email(email)) }
        }
        
        test("존재하지 않는 이메일로 조회 시 예외가 발생해야 한다") {
            // Given
            val email = "nonexistent@example.com"
            every { userRepository.findByEmail(Email(email)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserByEmail(email)
            }
            
            exception.message shouldBe "사용자를 찾을 수 없습니다: nonexistent@example.com"
            verify { userRepository.findByEmail(Email(email)) }
        }
        
        test("프리미엄 사용자를 이메일로 조회할 수 있어야 한다") {
            // Given
            val email = "premium@example.com"
            every { userRepository.findByEmail(Email(email)) } returns premiumUser
            
            // When
            val result = getUserUseCase.getUserByEmail(email)
            
            // Then
            result.id shouldBe 3L
            result.email shouldBe "premium@example.com"
            result.membershipLevel shouldBe "PREMIUM"
            result.isNewCustomer shouldBe false
            
            verify { userRepository.findByEmail(Email(email)) }
        }
    }
    
    context("모든 사용자 조회") {
        test("모든 사용자를 조회할 수 있어야 한다") {
            // Given
            val allUsers = listOf(sampleUser, vipUser, premiumUser)
            every { userRepository.findAll() } returns allUsers
            
            // When
            val result = getUserUseCase.getAllUsers()
            
            // Then
            result.size shouldBe 3
            result[0].id shouldBe 1L
            result[0].email shouldBe "test@example.com"
            result[0].membershipLevel shouldBe "REGULAR"
            
            result[1].id shouldBe 2L
            result[1].email shouldBe "vip@example.com"
            result[1].membershipLevel shouldBe "VIP"
            
            result[2].id shouldBe 3L
            result[2].email shouldBe "premium@example.com"
            result[2].membershipLevel shouldBe "PREMIUM"
            
            verify { userRepository.findAll() }
        }
        
        test("사용자가 없을 때 빈 목록을 반환해야 한다") {
            // Given
            every { userRepository.findAll() } returns emptyList()
            
            // When
            val result = getUserUseCase.getAllUsers()
            
            // Then
            result shouldBe emptyList()
            verify { userRepository.findAll() }
        }
    }
    
    context("멤버십 레벨별 조회") {
        test("REGULAR 멤버십 레벨의 사용자들을 조회할 수 있어야 한다") {
            // Given
            val regularUsers = listOf(sampleUser)
            every { userRepository.findByMembershipLevel(MembershipLevel.REGULAR) } returns regularUsers
            
            // When
            val result = getUserUseCase.getUsersByMembershipLevel("REGULAR")
            
            // Then
            result.size shouldBe 1
            result[0].membershipLevel shouldBe "REGULAR"
            result[0].email shouldBe "test@example.com"
            
            verify { userRepository.findByMembershipLevel(MembershipLevel.REGULAR) }
        }
        
        test("VIP 멤버십 레벨의 사용자들을 조회할 수 있어야 한다") {
            // Given
            val vipUsers = listOf(vipUser)
            every { userRepository.findByMembershipLevel(MembershipLevel.VIP) } returns vipUsers
            
            // When
            val result = getUserUseCase.getUsersByMembershipLevel("VIP")
            
            // Then
            result.size shouldBe 1
            result[0].membershipLevel shouldBe "VIP"
            result[0].email shouldBe "vip@example.com"
            
            verify { userRepository.findByMembershipLevel(MembershipLevel.VIP) }
        }
        
        test("PREMIUM 멤버십 레벨의 사용자들을 조회할 수 있어야 한다") {
            // Given
            val premiumUsers = listOf(premiumUser)
            every { userRepository.findByMembershipLevel(MembershipLevel.PREMIUM) } returns premiumUsers
            
            // When
            val result = getUserUseCase.getUsersByMembershipLevel("PREMIUM")
            
            // Then
            result.size shouldBe 1
            result[0].membershipLevel shouldBe "PREMIUM"
            result[0].email shouldBe "premium@example.com"
            
            verify { userRepository.findByMembershipLevel(MembershipLevel.PREMIUM) }
        }
        
        test("NEW 멤버십 레벨의 사용자들을 조회할 수 있어야 한다") {
            // Given
            val newUser = User(
                id = UserId(4L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            val newUsers = listOf(newUser)
            every { userRepository.findByMembershipLevel(MembershipLevel.NEW) } returns newUsers
            
            // When
            val result = getUserUseCase.getUsersByMembershipLevel("NEW")
            
            // Then
            result.size shouldBe 1
            result[0].membershipLevel shouldBe "NEW"
            result[0].email shouldBe "new@example.com"
            
            verify { userRepository.findByMembershipLevel(MembershipLevel.NEW) }
        }
        
        test("유효하지 않은 멤버십 레벨로 조회 시 예외가 발생해야 한다") {
            // Given
            val invalidLevel = "INVALID_LEVEL"
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUsersByMembershipLevel(invalidLevel)
            }
            
            exception.message shouldBe "유효하지 않은 멤버십 레벨입니다: INVALID_LEVEL"
        }
        
        test("특정 멤버십 레벨의 사용자가 없을 때 빈 목록을 반환해야 한다") {
            // Given
            every { userRepository.findByMembershipLevel(MembershipLevel.PREMIUM) } returns emptyList()
            
            // When
            val result = getUserUseCase.getUsersByMembershipLevel("PREMIUM")
            
            // Then
            result shouldBe emptyList()
            verify { userRepository.findByMembershipLevel(MembershipLevel.PREMIUM) }
        }
    }
    
    context("멤버십 정보 조회") {
        test("사용자의 멤버십 정보를 조회할 수 있어야 한다") {
            // Given
            val userId = 1L
            every { userRepository.findById(UserId(userId)) } returns sampleUser
            
            // When
            val result = getUserUseCase.getUserMembershipInfo(userId)
            
            // Then
            result.userId shouldBe 1L
            result.email shouldBe "test@example.com"
            result.membershipLevel shouldBe "REGULAR"
            result.membershipPriority shouldBe 2
            result.isNewCustomer shouldBe true
            result.createdAt shouldBe sampleUser.createdAt
            
            verify { userRepository.findById(UserId(userId)) }
        }
        
        test("VIP 사용자의 멤버십 정보를 조회할 수 있어야 한다") {
            // Given
            val userId = 2L
            every { userRepository.findById(UserId(userId)) } returns vipUser
            
            // When
            val result = getUserUseCase.getUserMembershipInfo(userId)
            
            // Then
            result.userId shouldBe 2L
            result.email shouldBe "vip@example.com"
            result.membershipLevel shouldBe "VIP"
            result.membershipPriority shouldBe 3
            result.isNewCustomer shouldBe false
            
            verify { userRepository.findById(UserId(userId)) }
        }
        
        test("프리미엄 사용자의 멤버십 정보를 조회할 수 있어야 한다") {
            // Given
            val userId = 3L
            every { userRepository.findById(UserId(userId)) } returns premiumUser
            
            // When
            val result = getUserUseCase.getUserMembershipInfo(userId)
            
            // Then
            result.userId shouldBe 3L
            result.email shouldBe "premium@example.com"
            result.membershipLevel shouldBe "PREMIUM"
            result.membershipPriority shouldBe 4
            result.isNewCustomer shouldBe false
            
            verify { userRepository.findById(UserId(userId)) }
        }
        
        test("존재하지 않는 사용자의 멤버십 정보 조회 시 예외가 발생해야 한다") {
            // Given
            val userId = 999L
            every { userRepository.findById(UserId(userId)) } returns null
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserMembershipInfo(userId)
            }
            
            exception.message shouldBe "사용자를 찾을 수 없습니다: 999"
            verify { userRepository.findById(UserId(userId)) }
        }
    }
    
    context("경계값 테스트") {
        test("0 ID로 사용자 조회 시 UserId 검증 예외가 발생해야 한다") {
            // Given
            val userId = 0L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserById(userId)
            }
            
            exception.message shouldBe "UserId must be positive: 0"
        }
        
        test("음수 ID로 사용자 조회 시 UserId 검증 예외가 발생해야 한다") {
            // Given
            val userId = -1L
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserById(userId)
            }
            
            exception.message shouldBe "UserId must be positive: -1"
        }
        
        test("빈 이메일로 사용자 조회 시 예외가 발생해야 한다") {
            // Given
            val email = ""
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUserByEmail(email)
            }
            
            exception.message shouldBe "Email cannot be blank"
        }
    }
    
    context("대소문자 처리") {
        test("소문자 멤버십 레벨로 조회 시 예외가 발생해야 한다") {
            // Given
            val invalidLevel = "regular"
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUsersByMembershipLevel(invalidLevel)
            }
            
            exception.message shouldBe "유효하지 않은 멤버십 레벨입니다: regular"
        }
        
        test("혼합 대소문자 멤버십 레벨로 조회 시 예외가 발생해야 한다") {
            // Given
            val invalidLevel = "Vip"
            
            // When & Then
            val exception = shouldThrow<IllegalArgumentException> {
                getUserUseCase.getUsersByMembershipLevel(invalidLevel)
            }
            
            exception.message shouldBe "유효하지 않은 멤버십 레벨입니다: Vip"
        }
    }
})
