package com.example.demo.domain.user.model

import com.example.demo.domain.user.valueobject.Email
import com.example.demo.domain.user.valueobject.MembershipLevel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDateTime

class UserTest : FunSpec({
    
    context("User 생성") {
        
        test("정상적인 사용자 정보로 User를 생성할 수 있어야 한다") {
            // Given
            val userId = UserId(1L)
            val email = Email("test@example.com")
            val membershipLevel = MembershipLevel.REGULAR
            val isNewCustomer = false
            val createdAt = LocalDateTime.now()
            
            // When
            val user = User(
                id = userId,
                email = email,
                membershipLevel = membershipLevel,
                isNewCustomer = isNewCustomer,
                createdAt = createdAt
            )
            
            // Then
            user.id shouldBe userId
            user.email shouldBe email
            user.membershipLevel shouldBe membershipLevel
            user.isNewCustomer shouldBe isNewCustomer
            user.createdAt shouldBe createdAt
        }
        
        test("companion object의 createNewUser 메서드로 신규 사용자를 생성할 수 있어야 한다") {
            // Given
            val userId = UserId(1L)
            val email = Email("newuser@example.com")
            
            // When
            val user = User.createNewUser(userId, email)
            
            // Then
            user.id shouldBe userId
            user.email shouldBe email
            user.membershipLevel shouldBe MembershipLevel.NEW
            user.isNewCustomer shouldBe true
            user.createdAt shouldNotBe null
        }
        
        test("companion object의 createExistingUser 메서드로 기존 사용자를 생성할 수 있어야 한다") {
            // Given
            val userId = UserId(1L)
            val email = Email("existing@example.com")
            val membershipLevel = MembershipLevel.VIP
            val isNewCustomer = false
            val createdAt = LocalDateTime.now().minusDays(30)
            
            // When
            val user = User.createExistingUser(userId, email, membershipLevel, isNewCustomer, createdAt)
            
            // Then
            user.id shouldBe userId
            user.email shouldBe email
            user.membershipLevel shouldBe membershipLevel
            user.isNewCustomer shouldBe isNewCustomer
            user.createdAt shouldBe createdAt
        }
    }
    
    context("프로모션 자격 검증") {
        
        test("isEligibleForPromotion 메서드가 올바르게 작동해야 한다") {
            // Given
            val vipUser = User(
                id = UserId(1L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            vipUser.isEligibleForPromotion(MembershipLevel.REGULAR) shouldBe true
            vipUser.isEligibleForPromotion(MembershipLevel.VIP) shouldBe true
            vipUser.isEligibleForPromotion(MembershipLevel.PREMIUM) shouldBe false
            vipUser.isEligibleForPromotion(null) shouldBe true
        }
        
        test("isEligibleForNewCustomerPromotion 메서드가 올바르게 작동해야 한다") {
            // Given
            val newCustomer = User(
                id = UserId(1L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            val regularCustomer = User(
                id = UserId(2L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            newCustomer.isEligibleForNewCustomerPromotion() shouldBe true
            regularCustomer.isEligibleForNewCustomerPromotion() shouldBe false
        }
        
        test("isEligibleForVipPromotion 메서드가 올바르게 작동해야 한다") {
            // Given
            val vipUser = User(
                id = UserId(1L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            val regularUser = User(
                id = UserId(2L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            vipUser.isEligibleForVipPromotion() shouldBe true
            regularUser.isEligibleForVipPromotion() shouldBe false
        }
        
        test("isEligibleForPremiumPromotion 메서드가 올바르게 작동해야 한다") {
            // Given
            val premiumUser = User(
                id = UserId(1L),
                email = Email("premium@example.com"),
                membershipLevel = MembershipLevel.PREMIUM,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            val vipUser = User(
                id = UserId(2L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            premiumUser.isEligibleForPremiumPromotion() shouldBe true
            vipUser.isEligibleForPremiumPromotion() shouldBe false
        }
    }
    
    context("회원 등급 관리") {
        
        test("upgradeMembership 메서드가 올바르게 작동해야 한다") {
            // Given
            val regularUser = User(
                id = UserId(1L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When
            val upgradedUser = regularUser.upgradeMembership(MembershipLevel.VIP)
            
            // Then
            upgradedUser.membershipLevel shouldBe MembershipLevel.VIP
            upgradedUser.id shouldBe regularUser.id
            upgradedUser.email shouldBe regularUser.email
            upgradedUser.isNewCustomer shouldBe regularUser.isNewCustomer
            upgradedUser.createdAt shouldBe regularUser.createdAt
        }
        
        test("upgradeMembership 메서드에서 낮은 등급으로 업그레이드 시 예외가 발생해야 한다") {
            // Given
            val vipUser = User(
                id = UserId(1L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                vipUser.upgradeMembership(MembershipLevel.REGULAR)
            }
        }
        
        test("updateNewCustomerStatus 메서드가 올바르게 작동해야 한다") {
            // Given
            val user = User(
                id = UserId(1L),
                email = Email("test@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            // When
            val updatedUser = user.updateNewCustomerStatus(false)
            
            // Then
            updatedUser.isNewCustomer shouldBe false
            updatedUser.id shouldBe user.id
            updatedUser.email shouldBe user.email
            updatedUser.membershipLevel shouldBe user.membershipLevel
            updatedUser.createdAt shouldBe user.createdAt
        }
        
        test("convertToRegularCustomer 메서드가 올바르게 작동해야 한다") {
            // Given
            val newCustomer = User(
                id = UserId(1L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            // When
            val regularCustomer = newCustomer.convertToRegularCustomer()
            
            // Then
            regularCustomer.isNewCustomer shouldBe false
            regularCustomer.id shouldBe newCustomer.id
            regularCustomer.email shouldBe newCustomer.email
            regularCustomer.membershipLevel shouldBe newCustomer.membershipLevel
            regularCustomer.createdAt shouldBe newCustomer.createdAt
        }
    }
    
    context("사용자 상태 확인") {
        
        test("isActive 메서드가 올바르게 작동해야 한다") {
            // Given
            val user = User(
                id = UserId(1L),
                email = Email("test@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            user.isActive() shouldBe true
        }
        
        test("isNewMember 메서드가 올바르게 작동해야 한다") {
            // Given
            val newUser = User(
                id = UserId(1L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            val regularUser = User(
                id = UserId(2L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            newUser.isNewMember() shouldBe true
            regularUser.isNewMember() shouldBe false
        }
        
        test("isRegularOrHigher 메서드가 올바르게 작동해야 한다") {
            // Given
            val newUser = User(
                id = UserId(1L),
                email = Email("new@example.com"),
                membershipLevel = MembershipLevel.NEW,
                isNewCustomer = true,
                createdAt = LocalDateTime.now()
            )
            
            val regularUser = User(
                id = UserId(2L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            newUser.isRegularOrHigher() shouldBe false
            regularUser.isRegularOrHigher() shouldBe true
        }
        
        test("isVipOrHigher 메서드가 올바르게 작동해야 한다") {
            // Given
            val regularUser = User(
                id = UserId(1L),
                email = Email("regular@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            val vipUser = User(
                id = UserId(2L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            regularUser.isVipOrHigher() shouldBe false
            vipUser.isVipOrHigher() shouldBe true
        }
        
        test("isPremium 메서드가 올바르게 작동해야 한다") {
            // Given
            val vipUser = User(
                id = UserId(1L),
                email = Email("vip@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            val premiumUser = User(
                id = UserId(2L),
                email = Email("premium@example.com"),
                membershipLevel = MembershipLevel.PREMIUM,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When & Then
            vipUser.isPremium() shouldBe false
            premiumUser.isPremium() shouldBe true
        }
    }
    
    context("가입 기간 계산") {
        
        test("getMembershipDurationInDays 메서드가 올바르게 작동해야 한다") {
            // Given
            val createdAt = LocalDateTime.now().minusDays(30)
            val user = User(
                id = UserId(1L),
                email = Email("test@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = createdAt
            )
            
            // When
            val duration = user.getMembershipDurationInDays()
            
            // Then
            duration shouldBe 30L
        }
        
        test("hasBeenMemberForAtLeast 메서드가 올바르게 작동해야 한다") {
            // Given
            val createdAt = LocalDateTime.now().minusDays(30)
            val user = User(
                id = UserId(1L),
                email = Email("test@example.com"),
                membershipLevel = MembershipLevel.REGULAR,
                isNewCustomer = false,
                createdAt = createdAt
            )
            
            // When & Then
            user.hasBeenMemberForAtLeast(20L) shouldBe true
            user.hasBeenMemberForAtLeast(30L) shouldBe true
            user.hasBeenMemberForAtLeast(40L) shouldBe false
        }
    }
    
    context("User 문자열 변환") {
        
        test("toString 메서드가 올바르게 작동해야 한다") {
            // Given
            val user = User(
                id = UserId(1L),
                email = Email("test@example.com"),
                membershipLevel = MembershipLevel.VIP,
                isNewCustomer = false,
                createdAt = LocalDateTime.now()
            )
            
            // When
            val stringValue = user.toString()
            
            // Then
            stringValue shouldBe "User(id=1, email=test@example.com, level=VIP 회원, isNew=false)"
        }
    }
})
