package com.example.demo.domain.user.valueobject

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MembershipLevelTest : FunSpec({
    
    context("MembershipLevel 기본 기능") {
        
        test("모든 회원 등급이 올바르게 정의되어야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.priority shouldBe 1
            MembershipLevel.NEW.displayName shouldBe "신규 회원"
            MembershipLevel.NEW.description shouldBe "가입한 지 30일 이내의 신규 회원"
            
            MembershipLevel.REGULAR.priority shouldBe 2
            MembershipLevel.REGULAR.displayName shouldBe "일반 회원"
            MembershipLevel.REGULAR.description shouldBe "기본 회원 등급"
            
            MembershipLevel.VIP.priority shouldBe 3
            MembershipLevel.VIP.displayName shouldBe "VIP 회원"
            MembershipLevel.VIP.description shouldBe "높은 구매 실적을 가진 우수 회원"
            
            MembershipLevel.PREMIUM.priority shouldBe 4
            MembershipLevel.PREMIUM.displayName shouldBe "프리미엄 회원"
            MembershipLevel.PREMIUM.description shouldBe "최고 등급의 특별 회원"
        }
        
        test("toString 메서드가 표시명을 반환해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.toString() shouldBe "신규 회원"
            MembershipLevel.REGULAR.toString() shouldBe "일반 회원"
            MembershipLevel.VIP.toString() shouldBe "VIP 회원"
            MembershipLevel.PREMIUM.toString() shouldBe "프리미엄 회원"
        }
    }
    
    context("MembershipLevel 비교") {
        
        test("우선순위 비교가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.isLowerThan(MembershipLevel.REGULAR) shouldBe true
            MembershipLevel.REGULAR.isLowerThan(MembershipLevel.VIP) shouldBe true
            MembershipLevel.VIP.isLowerThan(MembershipLevel.PREMIUM) shouldBe true
            MembershipLevel.NEW.isEqualTo(MembershipLevel.NEW) shouldBe true
        }
        
        test("isHigherThan 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.VIP.isHigherThan(MembershipLevel.REGULAR) shouldBe true
            MembershipLevel.REGULAR.isHigherThan(MembershipLevel.VIP) shouldBe false
            MembershipLevel.REGULAR.isHigherThan(MembershipLevel.REGULAR) shouldBe false
        }
        
        test("isLowerThan 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.REGULAR.isLowerThan(MembershipLevel.VIP) shouldBe true
            MembershipLevel.VIP.isLowerThan(MembershipLevel.REGULAR) shouldBe false
            MembershipLevel.REGULAR.isLowerThan(MembershipLevel.REGULAR) shouldBe false
        }
        
        test("isEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.VIP.isEqualTo(MembershipLevel.VIP) shouldBe true
            MembershipLevel.VIP.isEqualTo(MembershipLevel.REGULAR) shouldBe false
        }
        
        test("isHigherThanOrEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.VIP.isHigherThanOrEqualTo(MembershipLevel.REGULAR) shouldBe true
            MembershipLevel.VIP.isHigherThanOrEqualTo(MembershipLevel.VIP) shouldBe true
            MembershipLevel.REGULAR.isHigherThanOrEqualTo(MembershipLevel.VIP) shouldBe false
        }
        
        test("isLowerThanOrEqualTo 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.REGULAR.isLowerThanOrEqualTo(MembershipLevel.VIP) shouldBe true
            MembershipLevel.REGULAR.isLowerThanOrEqualTo(MembershipLevel.REGULAR) shouldBe true
            MembershipLevel.VIP.isLowerThanOrEqualTo(MembershipLevel.REGULAR) shouldBe false
        }
    }
    
    context("MembershipLevel 검증 메서드") {
        
        test("isNew 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.isNew() shouldBe true
            MembershipLevel.REGULAR.isNew() shouldBe false
            MembershipLevel.VIP.isNew() shouldBe false
            MembershipLevel.PREMIUM.isNew() shouldBe false
        }
        
        test("isRegularOrHigher 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.isRegularOrHigher() shouldBe false
            MembershipLevel.REGULAR.isRegularOrHigher() shouldBe true
            MembershipLevel.VIP.isRegularOrHigher() shouldBe true
            MembershipLevel.PREMIUM.isRegularOrHigher() shouldBe true
        }
        
        test("isVipOrHigher 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.isVipOrHigher() shouldBe false
            MembershipLevel.REGULAR.isVipOrHigher() shouldBe false
            MembershipLevel.VIP.isVipOrHigher() shouldBe true
            MembershipLevel.PREMIUM.isVipOrHigher() shouldBe true
        }
        
        test("isPremium 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.isPremium() shouldBe false
            MembershipLevel.REGULAR.isPremium() shouldBe false
            MembershipLevel.VIP.isPremium() shouldBe false
            MembershipLevel.PREMIUM.isPremium() shouldBe true
        }
        
        test("canUpgrade 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.canUpgrade() shouldBe true
            MembershipLevel.REGULAR.canUpgrade() shouldBe true
            MembershipLevel.VIP.canUpgrade() shouldBe true
            MembershipLevel.PREMIUM.canUpgrade() shouldBe false
        }
    }
    
    context("MembershipLevel 업그레이드/다운그레이드") {
        
        test("getNextLevel 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.getNextLevel() shouldBe MembershipLevel.REGULAR
            MembershipLevel.REGULAR.getNextLevel() shouldBe MembershipLevel.VIP
            MembershipLevel.VIP.getNextLevel() shouldBe MembershipLevel.PREMIUM
            MembershipLevel.PREMIUM.getNextLevel() shouldBe null
        }
        
        test("getPreviousLevel 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.NEW.getPreviousLevel() shouldBe null
            MembershipLevel.REGULAR.getPreviousLevel() shouldBe MembershipLevel.NEW
            MembershipLevel.VIP.getPreviousLevel() shouldBe MembershipLevel.REGULAR
            MembershipLevel.PREMIUM.getPreviousLevel() shouldBe MembershipLevel.VIP
        }
    }
    
    context("MembershipLevel companion object") {
        
        test("fromPriority 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.fromPriority(1) shouldBe MembershipLevel.NEW
            MembershipLevel.fromPriority(2) shouldBe MembershipLevel.REGULAR
            MembershipLevel.fromPriority(3) shouldBe MembershipLevel.VIP
            MembershipLevel.fromPriority(4) shouldBe MembershipLevel.PREMIUM
            MembershipLevel.fromPriority(5) shouldBe null
            MembershipLevel.fromPriority(0) shouldBe null
        }
        
        test("fromDisplayName 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.fromDisplayName("신규 회원") shouldBe MembershipLevel.NEW
            MembershipLevel.fromDisplayName("일반 회원") shouldBe MembershipLevel.REGULAR
            MembershipLevel.fromDisplayName("VIP 회원") shouldBe MembershipLevel.VIP
            MembershipLevel.fromDisplayName("프리미엄 회원") shouldBe MembershipLevel.PREMIUM
            MembershipLevel.fromDisplayName("존재하지 않는 등급") shouldBe null
        }
        
        test("getMinLevel 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.getMinLevel() shouldBe MembershipLevel.NEW
        }
        
        test("getMaxLevel 메서드가 올바르게 작동해야 한다") {
            // Given & When & Then
            MembershipLevel.getMaxLevel() shouldBe MembershipLevel.PREMIUM
        }
        
        test("getAllLevels 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val allLevels = MembershipLevel.getAllLevels()
            
            // Then
            allLevels shouldBe listOf(
                MembershipLevel.NEW,
                MembershipLevel.REGULAR,
                MembershipLevel.VIP,
                MembershipLevel.PREMIUM
            )
        }
        
        test("getRegularOrHigherLevels 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val regularOrHigher = MembershipLevel.getRegularOrHigherLevels()
            
            // Then
            regularOrHigher shouldBe listOf(
                MembershipLevel.REGULAR,
                MembershipLevel.VIP,
                MembershipLevel.PREMIUM
            )
        }
        
        test("getVipOrHigherLevels 메서드가 올바르게 작동해야 한다") {
            // Given & When
            val vipOrHigher = MembershipLevel.getVipOrHigherLevels()
            
            // Then
            vipOrHigher shouldBe listOf(
                MembershipLevel.VIP,
                MembershipLevel.PREMIUM
            )
        }
    }
})
