package com.example.demo.domain.promotion.valueobject

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDateTime

class PromotionPeriodTest : DescribeSpec({
    
    describe("PromotionPeriod 생성") {
        it("유효한 시작일과 종료일로 생성할 수 있어야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate.plusDays(7)
            
            // When
            val period = PromotionPeriod.of(startDate, endDate)
            
            // Then
            period.startDate shouldBe startDate
            period.endDate shouldBe endDate
        }
        
        it("시작일이 종료일보다 늦으면 예외가 발생해야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate.minusDays(1)
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                PromotionPeriod.of(startDate, endDate)
            }
        }
        
        it("시작일과 종료일이 같으면 예외가 발생해야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                PromotionPeriod.of(startDate, endDate)
            }
        }
    }
    
    describe("PromotionPeriod 팩토리 메서드") {
        it("시작일과 기간으로 생성할 수 있어야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val durationDays = 7L
            
            // When
            val period = PromotionPeriod.fromStartDate(startDate, durationDays)
            
            // Then
            period.startDate shouldBe startDate
            period.endDate shouldBe startDate.plusDays(durationDays)
        }
        
        it("종료일과 기간으로 생성할 수 있어야 한다") {
            // Given
            val endDate = LocalDateTime.now().plusDays(7)
            val durationDays = 7L
            
            // When
            val period = PromotionPeriod.fromEndDate(endDate, durationDays)
            
            // Then
            period.startDate shouldBe endDate.minusDays(durationDays)
            period.endDate shouldBe endDate
        }
        
        it("현재 시간부터 기간으로 생성할 수 있어야 한다") {
            // Given
            val durationDays = 7L
            
            // When
            val period = PromotionPeriod.fromNow(durationDays)
            
            // Then
            val now = LocalDateTime.now()
            period.startDate.isBefore(now.plusMinutes(1)) shouldBe true
            period.endDate shouldBe period.startDate.plusDays(durationDays)
        }
        
        it("음수 기간으로 생성하면 예외가 발생해야 한다") {
            // Given & When & Then
            shouldThrow<IllegalArgumentException> {
                PromotionPeriod.fromStartDate(LocalDateTime.now(), -1L)
            }
            
            shouldThrow<IllegalArgumentException> {
                PromotionPeriod.fromEndDate(LocalDateTime.now(), -1L)
            }
            
            shouldThrow<IllegalArgumentException> {
                PromotionPeriod.fromNow(-1L)
            }
        }
    }
    
    describe("PromotionPeriod 유효성 확인") {
        it("현재 시간이 기간 내에 있으면 유효해야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            // When
            val isValid = period.isValid()
            
            // Then
            isValid shouldBe true
        }
        
        it("현재 시간이 기간 전이면 유효하지 않아야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period = PromotionPeriod.of(now.plusDays(1), now.plusDays(2))
            
            // When
            val isValid = period.isValid()
            
            // Then
            isValid shouldBe false
        }
        
        it("현재 시간이 기간 후면 유효하지 않아야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period = PromotionPeriod.of(now.minusDays(2), now.minusDays(1))
            
            // When
            val isValid = period.isValid()
            
            // Then
            isValid shouldBe false
        }
    }
    
    describe("PromotionPeriod 상태 확인") {
        it("시작되었는지 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val startedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            val notStartedPeriod = PromotionPeriod.of(now.plusDays(1), now.plusDays(2))
            
            // When & Then
            startedPeriod.hasStarted() shouldBe true
            notStartedPeriod.hasStarted() shouldBe false
        }
        
        it("종료되었는지 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val endedPeriod = PromotionPeriod.of(now.minusDays(2), now.minusDays(1))
            val notEndedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            // When & Then
            endedPeriod.hasEnded() shouldBe true
            notEndedPeriod.hasEnded() shouldBe false
        }
        
        it("시작 전인지 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val beforeStartPeriod = PromotionPeriod.of(now.plusDays(1), now.plusDays(2))
            val startedPeriod = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            // When & Then
            beforeStartPeriod.isBeforeStart() shouldBe true
            startedPeriod.isBeforeStart() shouldBe false
        }
    }
    
    describe("PromotionPeriod 계산") {
        it("총 일수를 계산할 수 있어야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate.plusDays(7)
            val period = PromotionPeriod.of(startDate, endDate)
            
            // When
            val totalDays = period.getTotalDays()
            
            // Then
            totalDays shouldBe 7L
        }
        
        it("남은 일수를 계산할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period = PromotionPeriod.of(now.minusDays(1), now.plusDays(6))
            
            // When
            val remainingDays = period.getRemainingDays()
            
            // Then
            // 현재 시간이 시작일과 종료일 사이에 있으므로 남은 일수는 5일 또는 6일일 수 있음
            remainingDays shouldBe 5L
        }
        
        it("진행률을 계산할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period = PromotionPeriod.of(now.minusDays(1), now.plusDays(1))
            
            // When
            val progressRatio = period.getProgressRatio()
            
            // Then
            progressRatio shouldBe 0.5
        }
    }
    
    describe("PromotionPeriod 겹침 확인") {
        it("겹치는 기간을 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period1 = PromotionPeriod.of(now, now.plusDays(7))
            val period2 = PromotionPeriod.of(now.plusDays(3), now.plusDays(10))
            
            // When
            val overlaps = period1.overlapsWith(period2)
            
            // Then
            overlaps shouldBe true
        }
        
        it("겹치지 않는 기간을 확인할 수 있어야 한다") {
            // Given
            val now = LocalDateTime.now()
            val period1 = PromotionPeriod.of(now, now.plusDays(3))
            val period2 = PromotionPeriod.of(now.plusDays(5), now.plusDays(8))
            
            // When
            val overlaps = period1.overlapsWith(period2)
            
            // Then
            overlaps shouldBe false
        }
    }
    
    describe("PromotionPeriod 수정") {
        it("기간을 연장할 수 있어야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate.plusDays(7)
            val period = PromotionPeriod.of(startDate, endDate)
            
            // When
            val extendedPeriod = period.extend(3L)
            
            // Then
            extendedPeriod.endDate shouldBe endDate.plusDays(3)
        }
        
        it("기간을 단축할 수 있어야 한다") {
            // Given
            val startDate = LocalDateTime.now()
            val endDate = startDate.plusDays(7)
            val period = PromotionPeriod.of(startDate, endDate)
            
            // When
            val shortenedPeriod = period.shorten(2L)
            
            // Then
            shortenedPeriod.endDate shouldBe endDate.minusDays(2)
        }
        
        it("음수로 연장하면 예외가 발생해야 한다") {
            // Given
            val period = PromotionPeriod.fromNow(7L)
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                period.extend(-1L)
            }
        }
        
        it("음수로 단축하면 예외가 발생해야 한다") {
            // Given
            val period = PromotionPeriod.fromNow(7L)
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                period.shorten(-1L)
            }
        }
        
        it("시작일보다 짧게 단축하면 예외가 발생해야 한다") {
            // Given
            val period = PromotionPeriod.fromNow(7L)
            
            // When & Then
            shouldThrow<IllegalArgumentException> {
                period.shorten(8L)
            }
        }
    }
    
    describe("PromotionPeriod 문자열 표현") {
        it("toString은 적절한 형식이어야 한다") {
            // Given
            val startDate = LocalDateTime.of(2024, 1, 1, 0, 0)
            val endDate = LocalDateTime.of(2024, 1, 8, 0, 0)
            val period = PromotionPeriod.of(startDate, endDate)
            
            // When
            val stringRepresentation = period.toString()
            
            // Then
            stringRepresentation shouldBe "PromotionPeriod(2024-01-01T00:00 ~ 2024-01-08T00:00)"
        }
    }
})
