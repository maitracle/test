# 🧪 QA 테스트 데이터 관리 가이드

---

## 📋 개요

이 문서는 장바구니 프로모션 시스템의 QA 테스트를 위한 데이터베이스 스크립트 사용법을 설명합니다.

## 📁 파일 구성

```
documents/
├── qa_scenarios.md              # QA 시나리오 문서
├── test_data_setup.sql          # 테스트 데이터 삽입 스크립트
├── test_data_cleanup.sql        # 테스트 데이터 정리 스크립트
├── test_data_validation.sql     # 테스트 데이터 검증 스크립트
└── TEST_DATA_README.md          # 이 파일
```

---

## 🚀 사용법

### 1. 테스트 환경 설정

#### 데이터베이스 연결 확인
```bash
# MySQL/MariaDB 연결 테스트
mysql -u [username] -p[password] -h [host] [database_name]

# PostgreSQL 연결 테스트
psql -U [username] -h [host] -d [database_name]
```

#### 테스트 데이터베이스 생성 (선택사항)
```sql
-- MySQL/MariaDB
CREATE DATABASE demo_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- PostgreSQL
CREATE DATABASE demo_test;
```

### 2. 테스트 데이터 준비

#### 전체 테스트 데이터 삽입
```bash
# MySQL/MariaDB
mysql -u [username] -p[password] -h [host] [database_name] < documents/test_data_setup.sql

# PostgreSQL
psql -U [username] -h [host] -d [database_name] -f documents/test_data_setup.sql
```

#### 데이터 삽입 확인
```bash
# MySQL/MariaDB
mysql -u [username] -p[password] -h [host] [database_name] < documents/test_data_validation.sql

# PostgreSQL
psql -U [username] -h [host] -d [database_name] -f documents/test_data_validation.sql
```

### 3. 테스트 실행

#### QA 시나리오 실행
1. `documents/qa_scenarios.md` 파일을 참조하여 테스트 케이스 실행
2. 각 테스트 케이스별로 준비된 데이터 활용
3. 테스트 결과 기록

#### 테스트 중 데이터 확인
```sql
-- 특정 사용자의 장바구니 확인
SELECT * FROM carts c
JOIN cart_items ci ON c.id = ci.cart_id
WHERE c.user_id = 1;

-- 활성화된 프로모션 확인
SELECT * FROM promotions 
WHERE is_active = true 
ORDER BY priority;
```

### 4. 테스트 완료 후 정리

#### 테스트 데이터 정리
```bash
# MySQL/MariaDB
mysql -u [username] -p[password] -h [host] [database_name] < documents/test_data_cleanup.sql

# PostgreSQL
psql -U [username] -h [host] -d [database_name] -f documents/test_data_cleanup.sql
```

---

## 📊 테스트 데이터 구성

### 사용자 데이터 (10명)
- **신규 고객**: 2명 (ID: 1, 7)
- **일반 회원**: 3명 (ID: 3, 5, 8)
- **VIP 회원**: 3명 (ID: 2, 6, 9)
- **프리미엄 회원**: 2명 (ID: 4, 10)

### 상품 데이터 (23개)
- **뷰티 카테고리**: 3개 상품 (ID: 1-3)
- **식품 카테고리**: 3개 상품 (ID: 4-6)
- **생활용품 카테고리**: 2개 상품 (ID: 7-8)
- **브랜드별 상품**: 2개 상품 (ID: 9-10)
- **할인 상품**: 2개 상품 (ID: 11-12)
- **성능 테스트용**: 3개 상품 (ID: 13-15)
- **경계값 테스트용**: 3개 상품 (ID: 16-18)
- **추가 테스트용**: 5개 상품 (ID: 19-23)

### 프로모션 데이터 (17개)
- **신규 고객 할인**: 15,000원 할인 (우선순위 1)
- **이벤트 할인**: 블랙프라이데이 1+1 (우선순위 1)
- **상품별 할인**: 특정 상품 10% 할인
- **브랜드별 할인**: 브랜드X 15% 할인
- **카테고리 할인**: 뷰티 50,000원 이상 5,000원 할인
- **수량 기반 할인**: 식품 3개 이상 10% 할인
- **장바구니 할인**: 100,000원 이상 10% 할인
- **무료 배송**: 50,000원 이상 무료 배송
- **회원 등급 할인**: VIP 5%, 프리미엄 7% 할인
- **결제 수단 할인**: X카드 10% 할인
- **시기별 할인**: 주말 5% 할인
- **세트 할인**: 식품+생활용품 20% 할인
- **캐시백**: 3% 캐시백
- **비활성화/만료/미래 프로모션**: 테스트용

### 장바구니 데이터 (10개)
- **신규 고객 장바구니**: 120,000원 (TC-019용)
- **VIP 회원 장바구니**: 120,000원 (TC-020용)
- **일반 회원 장바구니**: 80,000원 (TC-022용)
- **카테고리 할인 테스트용**: 55,000원 (TC-008용)
- **수량 기반 할인 테스트용**: 55,000원 (TC-009용)
- **브랜드 할인 테스트용**: 100,000원 (TC-007용)
- **경계값 테스트용**: 49,999원 (TC-023용)
- **성능 테스트용**: 대용량 (TC-024용)
- **추가 테스트용**: 2개 장바구니

---

## 🎯 테스트 시나리오별 데이터 매핑

### 기본 기능 테스트 (TC-001 ~ TC-005)
- **사용자**: 1, 2, 3, 4
- **상품**: 1~23
- **장바구니**: 1~10

### 프로모션 정책 테스트 (TC-006 ~ TC-013)
- **TC-006**: 상품 11 + 프로모션 3
- **TC-007**: 상품 9,10 + 프로모션 4 + 장바구니 6
- **TC-008**: 상품 1,2,3 + 프로모션 5 + 장바구니 4
- **TC-009**: 상품 4,5,6 + 프로모션 6 + 장바구니 5
- **TC-010**: 프로모션 7 + 장바구니 2
- **TC-011**: 프로모션 8 + 장바구니 2
- **TC-012**: 프로모션 10
- **TC-013**: 프로모션 11

### 할인 적용 규칙 테스트 (TC-014 ~ TC-018)
- **TC-014**: 사용자 1 + 프로모션 1 + 장바구니 1
- **TC-015**: 프로모션 2
- **TC-016**: 프로모션 3, 4
- **TC-017**: 프로모션 12, 5
- **TC-018**: 프로모션 5, 7, 3

### 사용자 시나리오 테스트 (TC-019 ~ TC-023)
- **TC-019**: 사용자 1 + 장바구니 1 + 프로모션 1
- **TC-020**: 사용자 2 + 장바구니 2 + 프로모션 5,9,10
- **TC-021**: 프로모션 2
- **TC-022**: 사용자 3 + 프로모션 9,10,11
- **TC-023**: 상품 16,17,18 + 장바구니 7

### 성능 테스트 (TC-024 ~ TC-026)
- **TC-024**: 장바구니 8 (대용량)
- **TC-025**: 사용자 1~10
- **TC-026**: 프로모션 1~17

---

## ⚠️ 주의사항

### 보안
- **절대 프로덕션 환경에서 실행하지 마세요!**
- 테스트 환경에서만 사용하세요
- 실행 전 데이터베이스 백업을 권장합니다

### 데이터 무결성
- 외래키 제약조건을 고려한 삭제 순서 준수
- 테스트 완료 후 반드시 정리 스크립트 실행
- 동시 실행 시 데이터 충돌 가능성 있음

### 성능
- 대용량 테스트 시 데이터베이스 성능 모니터링
- 인덱스가 올바르게 생성되었는지 확인
- 메모리 사용량 모니터링

---

## 🔧 문제 해결

### 일반적인 문제

#### 1. 외래키 제약조건 오류
```sql
-- 해결방법: 올바른 순서로 삭제
DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id IN (1,2,3,4,5,6,7,8,9,10));
DELETE FROM carts WHERE user_id IN (1,2,3,4,5,6,7,8,9,10);
DELETE FROM promotions WHERE id BETWEEN 1 AND 50;
DELETE FROM products WHERE id BETWEEN 1 AND 50;
DELETE FROM users WHERE id BETWEEN 1 AND 10);
```

#### 2. 중복 키 오류
```sql
-- 해결방법: 기존 데이터 확인 후 삭제
SELECT * FROM users WHERE id BETWEEN 1 AND 10;
DELETE FROM users WHERE id BETWEEN 1 AND 10;
```

#### 3. 데이터 타입 오류
```sql
-- 해결방법: 테이블 스키마 확인
DESCRIBE users;
DESCRIBE products;
DESCRIBE promotions;
DESCRIBE carts;
DESCRIBE cart_items;
```

### 로그 확인
```sql
-- MySQL/MariaDB
SHOW PROCESSLIST;
SHOW ENGINE INNODB STATUS;

-- PostgreSQL
SELECT * FROM pg_stat_activity;
```

---

## 📞 지원

테스트 데이터 관련 문제가 발생하면:

1. **로그 확인**: 데이터베이스 로그 및 애플리케이션 로그
2. **스키마 확인**: 테이블 구조 및 제약조건 확인
3. **데이터 검증**: `test_data_validation.sql` 실행
4. **정리 후 재실행**: `test_data_cleanup.sql` → `test_data_setup.sql`

---

## 📝 변경 이력

- **v1.0** (2024-01-XX): 초기 버전
  - 기본 테스트 데이터 구성
  - QA 시나리오별 데이터 매핑
  - 검증 및 정리 스크립트

---

이 가이드를 통해 효과적인 QA 테스트를 진행하시기 바랍니다! 🚀
