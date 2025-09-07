package com.example.demo.infrastructure.persistence.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 사용자 JPA Repository 인터페이스
 * 
 * 사용자 엔티티에 대한 데이터 접근을 위한 JPA Repository입니다.
 * Spring Data JPA의 기본 기능을 제공합니다.
 */
@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 이메일
     * @return 사용자 엔티티, 없으면 null
     */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    fun findByEmail(@Param("email") email: String): UserEntity?
    
    /**
     * 이메일 중복 여부를 확인합니다.
     * @param email 확인할 이메일
     * @return 중복이면 true, 아니면 false
     */
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.email = :email")
    fun existsByEmail(@Param("email") email: String): Boolean
}