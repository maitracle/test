-- =====================================================
-- H2 데이터베이스 자동 초기화 데이터
-- =====================================================
-- 이 파일은 Spring Boot 애플리케이션 시작 시 자동으로 실행됩니다.
-- H2 인메모리 데이터베이스에 테스트 데이터를 삽입합니다.

-- =====================================================
-- 1. 테스트 사용자 데이터
-- =====================================================

-- 신규 고객 (TC-019, TC-014 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(1, 'newuser@test.com', 'NEW', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- VIP 회원 (TC-020, TC-015 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(2, 'vipuser@test.com', 'VIP', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 일반 회원 (TC-022 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(3, 'regularuser@test.com', 'REGULAR', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 프리미엄 회원 (TC-022 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(4, 'premiumuser@test.com', 'PREMIUM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 추가 테스트 사용자들
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(5, 'testuser5@test.com', 'REGULAR', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'testuser6@test.com', 'VIP', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'testuser7@test.com', 'NEW', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'testuser8@test.com', 'REGULAR', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'testuser9@test.com', 'VIP', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'testuser10@test.com', 'PREMIUM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 2. 테스트 상품 데이터
-- =====================================================

-- 뷰티 카테고리 상품들 (TC-008, TC-020 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 100, '뷰티', 'BeautyBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '뷰티 마스크팩', '수분 마스크팩 10개입', 25000.00, 200, '뷰티', 'BeautyBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '뷰티 토너', '수분 토너 200ml', 30000.00, 150, '뷰티', 'BeautyBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 식품 카테고리 상품들 (TC-009, TC-020 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 80, '식품', 'FoodBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '식품 과자', '건강한 과자 200g', 20000.00, 300, '식품', 'FoodBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '식품 음료', '자연 음료 500ml', 15000.00, 250, '식품', 'FoodBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 생활용품 카테고리 상품들 (TC-017 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(7, '생활용품 세제', '천연 세제 1L', 25000.00, 120, '생활용품', 'LifeBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '생활용품 수건', '면 수건 5개입', 30000.00, 100, '생활용품', 'LifeBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 브랜드별 상품들 (TC-007 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(9, '브랜드X 상품A', '브랜드X의 프리미엄 상품', 40000.00, 50, '전자제품', 'BrandX', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, '브랜드Y 상품B', '브랜드Y의 일반 상품', 60000.00, 75, '전자제품', 'BrandY', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 특정 상품 할인용 상품들 (TC-006 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(11, '할인 상품A', '10% 할인 적용 상품', 50000.00, 100, '할인상품', 'DiscountBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, '일반 상품B', '할인 없는 일반 상품', 30000.00, 100, '일반상품', 'NormalBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 대용량 테스트용 상품들 (TC-024 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(13, '테스트 상품 1', '성능 테스트용 상품', 1000.00, 1000, '테스트', 'TestBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, '테스트 상품 2', '성능 테스트용 상품', 2000.00, 1000, '테스트', 'TestBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, '테스트 상품 3', '성능 테스트용 상품', 3000.00, 1000, '테스트', 'TestBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 경계값 테스트용 상품들 (TC-023 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(16, '경계값 상품 49999', '49,999원 상품', 49999.00, 100, '경계값', 'BoundaryBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, '경계값 상품 50000', '50,000원 상품', 50000.00, 100, '경계값', 'BoundaryBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, '경계값 상품 50001', '50,001원 상품', 50001.00, 100, '경계값', 'BoundaryBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 추가 테스트 상품들
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(19, '추가 테스트 상품 1', '추가 테스트용', 10000.00, 100, '추가', 'ExtraBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, '추가 테스트 상품 2', '추가 테스트용', 20000.00, 100, '추가', 'ExtraBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, '추가 테스트 상품 3', '추가 테스트용', 30000.00, 100, '추가', 'ExtraBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, '추가 테스트 상품 4', '추가 테스트용', 40000.00, 100, '추가', 'ExtraBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, '추가 테스트 상품 5', '추가 테스트용', 50000.00, 100, '추가', 'ExtraBrand', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 3. 테스트 프로모션 데이터
-- =====================================================

-- 신규 고객 할인 (최우선순위) - TC-014, TC-019 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active, 
    start_date, end_date, is_new_customer_only, discount_amount, 
    created_at, updated_at
) VALUES (
    1, '신규 고객 할인', '신규 고객 첫 결제 시 15,000원 할인', 'FIXED_DISCOUNT', 1, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, true, 15000.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 이벤트 할인 (최우선순위) - TC-015, TC-021 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    2, '블랙프라이데이 1+1', '블랙프라이데이 이벤트 1+1 프로모션', 'BUY_ONE_GET_ONE', 1, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7' DAY, 50.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 특정 상품 할인 - TC-006 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    3, '상품A 10% 할인', '특정 상품 10% 할인', 'PERCENTAGE_DISCOUNT', 5, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 브랜드별 할인 - TC-007 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    4, '브랜드X 15% 할인', '브랜드X 상품 15% 할인', 'PERCENTAGE_DISCOUNT', 5, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 15.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 카테고리 최소 구매액 할인 - TC-008 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_category, min_cart_amount, discount_amount,
    created_at, updated_at
) VALUES (
    5, '뷰티 카테고리 할인', '뷰티 50,000원 이상 구매 시 5,000원 할인', 'FIXED_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, '뷰티', 50000.00, 5000.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 카테고리 수량 기반 할인 - TC-009 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_category, min_quantity, discount_percentage,
    created_at, updated_at
) VALUES (
    6, '식품 수량 할인', '식품 3개 이상 구매 시 10% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, '식품', 3, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 장바구니 최소 금액 할인 - TC-010 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, min_cart_amount, discount_percentage,
    created_at, updated_at
) VALUES (
    7, '장바구니 10% 할인', '100,000원 이상 구매 시 10% 할인', 'PERCENTAGE_DISCOUNT', 4, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 100000.00, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 무료 배송 - TC-011 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, min_cart_amount,
    created_at, updated_at
) VALUES (
    8, '무료 배송', '50,000원 이상 구매 시 무료 배송', 'FREE_SHIPPING', 5, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 50000.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- VIP 회원 할인 - TC-020, TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_user_level, discount_percentage,
    created_at, updated_at
) VALUES (
    9, 'VIP 회원 할인', 'VIP 회원 5% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'VIP', 5.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- X카드 할인 - TC-012, TC-020 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    10, 'X카드 할인', 'X카드 결제 시 10% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 주말 할인 - TC-013, TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    11, '주말 할인', '주말 결제 시 5% 할인', 'PERCENTAGE_DISCOUNT', 4, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 5.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 세트 할인 (카테고리 할인과 중복 불가) - TC-017 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    12, '세트 할인', '식품 2개 + 생활용품 1개 구매 시 20% 할인', 'PERCENTAGE_DISCOUNT', 2, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 20.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 프리미엄 회원 할인 - TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_user_level, discount_percentage,
    created_at, updated_at
) VALUES (
    13, '프리미엄 회원 할인', '프리미엄 회원 7% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'PREMIUM', 7.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 캐시백 프로모션 - TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    14, '캐시백 프로모션', '구매 금액의 3% 캐시백', 'CASHBACK', 5, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 3.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 비활성화된 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    15, '비활성화된 프로모션', '비활성화된 테스트 프로모션', 'PERCENTAGE_DISCOUNT', 3, false,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 만료된 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    16, '만료된 프로모션', '만료된 테스트 프로모션', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP - INTERVAL '60' DAY, CURRENT_TIMESTAMP - INTERVAL '30' DAY, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 미래 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    17, '미래 프로모션', '미래에 시작될 프로모션', 'PERCENTAGE_DISCOUNT', 3, true,
    CURRENT_TIMESTAMP + INTERVAL '30' DAY, CURRENT_TIMESTAMP + INTERVAL '60' DAY, 10.00,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- =====================================================
-- 4. 테스트 장바구니 데이터
-- =====================================================

-- 신규 고객 장바구니 (TC-019에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(1, 1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 1, '뷰티', 'BeautyBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 1, '식품', 'FoodBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- VIP 회원 장바구니 (TC-020에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(2, 1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 1, '뷰티', 'BeautyBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 1, '식품', 'FoodBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 일반 회원 장바구니 (TC-022에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(3, 11, '할인 상품A', '10% 할인 적용 상품', 50000.00, 1, '할인상품', 'DiscountBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 12, '일반 상품B', '할인 없는 일반 상품', 30000.00, 1, '일반상품', 'NormalBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 카테고리 할인 테스트용 장바구니 (TC-008에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(4, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(4, 2, '뷰티 마스크팩', '수분 마스크팩 10개입', 25000.00, 1, '뷰티', 'BeautyBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 3, '뷰티 토너', '수분 토너 200ml', 30000.00, 1, '뷰티', 'BeautyBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 수량 기반 할인 테스트용 장바구니 (TC-009에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(5, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(5, 5, '식품 과자', '건강한 과자 200g', 20000.00, 2, '식품', 'FoodBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 6, '식품 음료', '자연 음료 500ml', 15000.00, 1, '식품', 'FoodBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 브랜드 할인 테스트용 장바구니 (TC-007에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(6, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(6, 9, '브랜드X 상품A', '브랜드X의 프리미엄 상품', 40000.00, 1, '전자제품', 'BrandX', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 10, '브랜드Y 상품B', '브랜드Y의 일반 상품', 60000.00, 1, '전자제품', 'BrandY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 경계값 테스트용 장바구니 (TC-023에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(7, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(7, 16, '경계값 상품 49999', '49,999원 상품', 49999.00, 1, '경계값', 'BoundaryBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 대용량 테스트용 장바구니 (TC-024에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(8, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 대용량 테스트를 위한 장바구니 아이템들
INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(8, 13, '테스트 상품 1', '성능 테스트용 상품', 1000.00, 1, '테스트', 'TestBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 14, '테스트 상품 2', '성능 테스트용 상품', 2000.00, 1, '테스트', 'TestBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 15, '테스트 상품 3', '성능 테스트용 상품', 3000.00, 1, '테스트', 'TestBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 추가 테스트 장바구니들
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(9, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(9, 19, '추가 테스트 상품 1', '추가 테스트용', 10000.00, 1, '추가', 'ExtraBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 20, '추가 테스트 상품 2', '추가 테스트용', 20000.00, 1, '추가', 'ExtraBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 21, '추가 테스트 상품 3', '추가 테스트용', 30000.00, 1, '추가', 'ExtraBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 22, '추가 테스트 상품 4', '추가 테스트용', 40000.00, 1, '추가', 'ExtraBrand', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 데이터 삽입 완료
-- =====================================================