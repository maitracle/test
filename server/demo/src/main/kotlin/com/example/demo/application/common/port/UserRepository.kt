package com.example.demo.application.common.port

import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId
import com.example.demo.domain.user.valueobject.Email

/**
 * 사용자 데이터 접근을 위한 포트 인터페이스
 * 클린 아키텍처의 의존성 역전 원칙에 따라 애플리케이션 레이어에서 정의
 */
interface UserRepository {
    
    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 정보, 없으면 null
     */
    fun findById(userId: UserId): User?
    
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 이메일
     * @return 사용자 정보, 없으면 null
     */
    fun findByEmail(email: Email): User?
    
    /**
     * 사용자를 저장합니다.
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    fun save(user: User): User
    
    /**
     * 사용자를 삭제합니다.
     * @param userId 삭제할 사용자 ID
     */
    fun delete(userId: UserId)
    
    /**
     * 모든 사용자를 조회합니다.
     * @return 모든 사용자 목록
     */
    fun findAll(): List<User>
    
    /**
     * 특정 멤버십 레벨의 사용자들을 조회합니다.
     * @param membershipLevel 멤버십 레벨
     * @return 해당 레벨의 사용자 목록
     */
    fun findByMembershipLevel(membershipLevel: com.example.demo.domain.user.valueobject.MembershipLevel): List<User>
    
    /**
     * 이메일 중복 여부를 확인합니다.
     * @param email 확인할 이메일
     * @return 중복이면 true, 아니면 false
     */
    fun existsByEmail(email: Email): Boolean
}
