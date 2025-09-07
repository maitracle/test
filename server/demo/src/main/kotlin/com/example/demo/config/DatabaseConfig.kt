package com.example.demo.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * 데이터베이스 설정
 * 
 * JPA 리포지토리와 트랜잭션 관리를 활성화합니다.
 * H2 데이터베이스와 JPA를 사용한 데이터 영속성을 설정합니다.
 */
@Configuration
@EnableJpaRepositories(basePackages = ["com.example.demo.infrastructure.persistence"])
@EnableTransactionManagement
class DatabaseConfig {
    
    // 추가적인 데이터베이스 설정이 필요한 경우 여기에 추가
    // 예: 커스텀 DataSource 설정, JPA Properties 설정 등
}
