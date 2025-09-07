package com.example.demo.infrastructure.persistence.user

import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserRepositoryImplTest : FunSpec() {
    
    init {
        context("사용자 Repository 테스트") {
            test("ID로 사용자를 조회할 수 있어야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                val userEntity = UserEntity.createExisting(
                    id = 1L,
                    email = "test@example.com",
                    membershipLevel = "REGULAR",
                    isNewCustomer = false,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                val userId = UserId.of(1L)
                
                every { userJpaRepository.findById(1L) } returns java.util.Optional.of(userEntity)
                
                // When
                val foundUser = userRepository.findById(userId)
                
                // Then
                foundUser.shouldNotBeNull()
                foundUser!!.email.value shouldBe "test@example.com"
                foundUser.membershipLevel shouldBe MembershipLevel.REGULAR
                foundUser.isNewCustomer shouldBe false
                
                verify { userJpaRepository.findById(1L) }
            }
            
            test("존재하지 않는 ID로 조회하면 null을 반환해야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                val userId = UserId.of(999L)
                
                every { userJpaRepository.findById(999L) } returns java.util.Optional.empty()
                
                // When
                val foundUser = userRepository.findById(userId)
                
                // Then
                foundUser shouldBe null
                
                verify { userJpaRepository.findById(999L) }
            }
            
            test("이메일로 사용자를 조회할 수 있어야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                val userEntity = UserEntity.createExisting(
                    id = 1L,
                    email = "test@example.com",
                    membershipLevel = "VIP",
                    isNewCustomer = true,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                
                every { userJpaRepository.findByEmail("test@example.com") } returns userEntity
                
                // When
                val foundUser = userRepository.findByEmail(Email("test@example.com"))
                
                // Then
                foundUser.shouldNotBeNull()
                foundUser!!.email.value shouldBe "test@example.com"
                foundUser.membershipLevel shouldBe MembershipLevel.VIP
                foundUser.isNewCustomer shouldBe true
                
                verify { userJpaRepository.findByEmail("test@example.com") }
            }
            
            test("이메일 중복 여부를 확인할 수 있어야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                every { userJpaRepository.existsByEmail("test@example.com") } returns true
                every { userJpaRepository.existsByEmail("new@example.com") } returns false
                
                // When & Then
                userRepository.existsByEmail(Email("test@example.com")) shouldBe true
                userRepository.existsByEmail(Email("new@example.com")) shouldBe false
                
                verify { userJpaRepository.existsByEmail("test@example.com") }
                verify { userJpaRepository.existsByEmail("new@example.com") }
            }
            
            test("새로운 사용자를 저장할 수 있어야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                val user = User.createNewUser(
                    id = UserId.of(1L),
                    email = Email("newuser@example.com")
                )
                
                val savedUserEntity = UserEntity.createExisting(
                    id = 1L,
                    email = "newuser@example.com",
                    membershipLevel = "NEW",
                    isNewCustomer = true,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                
                every { userJpaRepository.save(any()) } returns savedUserEntity
                
                // When
                val savedUser = userRepository.save(user)
                
                // Then
                savedUser.shouldNotBeNull()
                savedUser!!.email.value shouldBe "newuser@example.com"
                savedUser.membershipLevel shouldBe MembershipLevel.NEW
                savedUser.isNewCustomer shouldBe true
                
                verify { userJpaRepository.save(any()) }
            }
            
            test("사용자를 삭제할 수 있어야 한다") {
                // Given
                val userJpaRepository = mockk<UserJpaRepository>()
                val userRepository = UserRepositoryImpl(userJpaRepository)
                
                val userId = UserId.of(1L)
                
                every { userJpaRepository.deleteById(1L) } returns Unit
                
                // When
                userRepository.delete(userId)
                
                // Then
                verify { userJpaRepository.deleteById(1L) }
            }
        }
    }
}