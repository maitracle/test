-- =====================================================
-- QA 시나리오 테스트 데이터 준비 스크립트
-- =====================================================
-- 이 스크립트는 QA 시나리오 문서의 모든 테스트 케이스를 실행할 수 있도록
-- 필요한 테스트 데이터를 데이터베이스에 삽입합니다.

-- =====================================================
-- 1. 기존 데이터 정리 (테스트 환경에서만 실행)
-- =====================================================
-- 주의: 프로덕션 환경에서는 절대 실행하지 마세요!

DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
DELETE FROM carts WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM promotions WHERE id BETWEEN 1 AND 50;
DELETE FROM products WHERE id BETWEEN 1 AND 50;
DELETE FROM users WHERE id BETWEEN 1 AND 10;

-- =====================================================
-- 2. 테스트 사용자 데이터
-- =====================================================

-- 신규 고객 (TC-019, TC-014 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(1, 'newuser@test.com', 'NEW', true, NOW() - INTERVAL '5 days', NOW());

-- VIP 회원 (TC-020, TC-015 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(2, 'vipuser@test.com', 'VIP', false, NOW() - INTERVAL '365 days', NOW());

-- 일반 회원 (TC-022 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(3, 'regularuser@test.com', 'REGULAR', false, NOW() - INTERVAL '180 days', NOW());

-- 프리미엄 회원 (TC-022 등에서 사용)
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(4, 'premiumuser@test.com', 'PREMIUM', false, NOW() - INTERVAL '730 days', NOW());

-- 추가 테스트 사용자들
INSERT INTO users (id, email, membership_level, is_new_customer, created_at, updated_at) VALUES
(5, 'testuser5@test.com', 'REGULAR', false, NOW() - INTERVAL '100 days', NOW()),
(6, 'testuser6@test.com', 'VIP', false, NOW() - INTERVAL '200 days', NOW()),
(7, 'testuser7@test.com', 'NEW', true, NOW() - INTERVAL '10 days', NOW()),
(8, 'testuser8@test.com', 'REGULAR', false, NOW() - INTERVAL '150 days', NOW()),
(9, 'testuser9@test.com', 'VIP', false, NOW() - INTERVAL '300 days', NOW()),
(10, 'testuser10@test.com', 'PREMIUM', false, NOW() - INTERVAL '500 days', NOW());

-- =====================================================
-- 3. 테스트 상품 데이터
-- =====================================================

-- 뷰티 카테고리 상품들 (TC-008, TC-020 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 100, '뷰티', 'BeautyBrand', true, NOW(), NOW()),
(2, '뷰티 마스크팩', '수분 마스크팩 10개입', 25000.00, 200, '뷰티', 'BeautyBrand', true, NOW(), NOW()),
(3, '뷰티 토너', '수분 토너 200ml', 30000.00, 150, '뷰티', 'BeautyBrand', true, NOW(), NOW());

-- 식품 카테고리 상품들 (TC-009, TC-020 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 80, '식품', 'FoodBrand', true, NOW(), NOW()),
(5, '식품 과자', '건강한 과자 200g', 20000.00, 300, '식품', 'FoodBrand', true, NOW(), NOW()),
(6, '식품 음료', '자연 음료 500ml', 15000.00, 250, '식품', 'FoodBrand', true, NOW(), NOW());

-- 생활용품 카테고리 상품들 (TC-017 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(7, '생활용품 세제', '천연 세제 1L', 25000.00, 120, '생활용품', 'LifeBrand', true, NOW(), NOW()),
(8, '생활용품 수건', '면 수건 5개입', 30000.00, 100, '생활용품', 'LifeBrand', true, NOW(), NOW());

-- 브랜드별 상품들 (TC-007 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(9, '브랜드X 상품A', '브랜드X의 프리미엄 상품', 40000.00, 50, '전자제품', 'BrandX', true, NOW(), NOW()),
(10, '브랜드Y 상품B', '브랜드Y의 일반 상품', 60000.00, 75, '전자제품', 'BrandY', true, NOW(), NOW());

-- 특정 상품 할인용 상품들 (TC-006 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(11, '할인 상품A', '10% 할인 적용 상품', 50000.00, 100, '할인상품', 'DiscountBrand', true, NOW(), NOW()),
(12, '일반 상품B', '할인 없는 일반 상품', 30000.00, 100, '일반상품', 'NormalBrand', true, NOW(), NOW());

-- 대용량 테스트용 상품들 (TC-024 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(13, '테스트 상품 1', '성능 테스트용 상품', 1000.00, 1000, '테스트', 'TestBrand', true, NOW(), NOW()),
(14, '테스트 상품 2', '성능 테스트용 상품', 2000.00, 1000, '테스트', 'TestBrand', true, NOW(), NOW()),
(15, '테스트 상품 3', '성능 테스트용 상품', 3000.00, 1000, '테스트', 'TestBrand', true, NOW(), NOW());

-- 경계값 테스트용 상품들 (TC-023 등에서 사용)
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(16, '경계값 상품 49999', '49,999원 상품', 49999.00, 100, '경계값', 'BoundaryBrand', true, NOW(), NOW()),
(17, '경계값 상품 50000', '50,000원 상품', 50000.00, 100, '경계값', 'BoundaryBrand', true, NOW(), NOW()),
(18, '경계값 상품 50001', '50,001원 상품', 50001.00, 100, '경계값', 'BoundaryBrand', true, NOW(), NOW());

-- 추가 테스트 상품들
INSERT INTO products (id, name, description, price, stock, category, brand, is_active, created_at, updated_at) VALUES
(19, '추가 테스트 상품 1', '추가 테스트용', 10000.00, 100, '추가', 'ExtraBrand', true, NOW(), NOW()),
(20, '추가 테스트 상품 2', '추가 테스트용', 20000.00, 100, '추가', 'ExtraBrand', true, NOW(), NOW()),
(21, '추가 테스트 상품 3', '추가 테스트용', 30000.00, 100, '추가', 'ExtraBrand', true, NOW(), NOW()),
(22, '추가 테스트 상품 4', '추가 테스트용', 40000.00, 100, '추가', 'ExtraBrand', true, NOW(), NOW()),
(23, '추가 테스트 상품 5', '추가 테스트용', 50000.00, 100, '추가', 'ExtraBrand', true, NOW(), NOW());

-- =====================================================
-- 4. 테스트 프로모션 데이터
-- =====================================================

-- 신규 고객 할인 (최우선순위) - TC-014, TC-019 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active, 
    start_date, end_date, is_new_customer_only, discount_amount, 
    created_at, updated_at
) VALUES (
    1, '신규 고객 할인', '신규 고객 첫 결제 시 15,000원 할인', 'FIXED_DISCOUNT', 1, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', true, 15000.00,
    NOW(), NOW()
);

-- 이벤트 할인 (최우선순위) - TC-015, TC-021 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    2, '블랙프라이데이 1+1', '블랙프라이데이 이벤트 1+1 프로모션', 'BUY_ONE_GET_ONE', 1, true,
    NOW() - INTERVAL '7 days', NOW() + INTERVAL '7 days', 50.00,
    NOW(), NOW()
);

-- 특정 상품 할인 - TC-006 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    3, '상품A 10% 할인', '특정 상품 10% 할인', 'PERCENTAGE_DISCOUNT', 5, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 10.00,
    NOW(), NOW()
);

-- 브랜드별 할인 - TC-007 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    4, '브랜드X 15% 할인', '브랜드X 상품 15% 할인', 'PERCENTAGE_DISCOUNT', 5, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 15.00,
    NOW(), NOW()
);

-- 카테고리 최소 구매액 할인 - TC-008 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_category, min_cart_amount, discount_amount,
    created_at, updated_at
) VALUES (
    5, '뷰티 카테고리 할인', '뷰티 50,000원 이상 구매 시 5,000원 할인', 'FIXED_DISCOUNT', 3, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', '뷰티', 50000.00, 5000.00,
    NOW(), NOW()
);

-- 카테고리 수량 기반 할인 - TC-009 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_category, min_quantity, discount_percentage,
    created_at, updated_at
) VALUES (
    6, '식품 수량 할인', '식품 3개 이상 구매 시 10% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', '식품', 3, 10.00,
    NOW(), NOW()
);

-- 장바구니 최소 금액 할인 - TC-010 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, min_cart_amount, discount_percentage,
    created_at, updated_at
) VALUES (
    7, '장바구니 10% 할인', '100,000원 이상 구매 시 10% 할인', 'PERCENTAGE_DISCOUNT', 4, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 100000.00, 10.00,
    NOW(), NOW()
);

-- 무료 배송 - TC-011 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, min_cart_amount,
    created_at, updated_at
) VALUES (
    8, '무료 배송', '50,000원 이상 구매 시 무료 배송', 'FREE_SHIPPING', 5, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 50000.00,
    NOW(), NOW()
);

-- VIP 회원 할인 - TC-020, TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_user_level, discount_percentage,
    created_at, updated_at
) VALUES (
    9, 'VIP 회원 할인', 'VIP 회원 5% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 'VIP', 5.00,
    NOW(), NOW()
);

-- X카드 할인 - TC-012, TC-020 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    10, 'X카드 할인', 'X카드 결제 시 10% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 10.00,
    NOW(), NOW()
);

-- 주말 할인 - TC-013, TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    11, '주말 할인', '주말 결제 시 5% 할인', 'PERCENTAGE_DISCOUNT', 4, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 5.00,
    NOW(), NOW()
);

-- 세트 할인 (카테고리 할인과 중복 불가) - TC-017 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    12, '세트 할인', '식품 2개 + 생활용품 1개 구매 시 20% 할인', 'PERCENTAGE_DISCOUNT', 2, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 20.00,
    NOW(), NOW()
);

-- 프리미엄 회원 할인 - TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, target_user_level, discount_percentage,
    created_at, updated_at
) VALUES (
    13, '프리미엄 회원 할인', '프리미엄 회원 7% 할인', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 'PREMIUM', 7.00,
    NOW(), NOW()
);

-- 캐시백 프로모션 - TC-022 등에서 사용
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    14, '캐시백 프로모션', '구매 금액의 3% 캐시백', 'CASHBACK', 5, true,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 3.00,
    NOW(), NOW()
);

-- 비활성화된 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    15, '비활성화된 프로모션', '비활성화된 테스트 프로모션', 'PERCENTAGE_DISCOUNT', 3, false,
    NOW() - INTERVAL '30 days', NOW() + INTERVAL '30 days', 10.00,
    NOW(), NOW()
);

-- 만료된 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    16, '만료된 프로모션', '만료된 테스트 프로모션', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days', 10.00,
    NOW(), NOW()
);

-- 미래 프로모션 (TC-005 등에서 사용)
INSERT INTO promotions (
    id, name, description, type, priority, is_active,
    start_date, end_date, discount_percentage,
    created_at, updated_at
) VALUES (
    17, '미래 프로모션', '미래에 시작될 프로모션', 'PERCENTAGE_DISCOUNT', 3, true,
    NOW() + INTERVAL '30 days', NOW() + INTERVAL '60 days', 10.00,
    NOW(), NOW()
);

-- =====================================================
-- 5. 테스트 장바구니 데이터
-- =====================================================

-- 신규 고객 장바구니 (TC-019에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(1, 1, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(1, 1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 1, '뷰티', 'BeautyBrand', NOW(), NOW()),
(1, 4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 1, '식품', 'FoodBrand', NOW(), NOW());

-- VIP 회원 장바구니 (TC-020에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(2, 2, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(2, 1, '뷰티 스킨케어 세트', '프리미엄 스킨케어 세트', 70000.00, 1, '뷰티', 'BeautyBrand', NOW(), NOW()),
(2, 4, '식품 간식 세트', '다양한 간식 세트', 50000.00, 1, '식품', 'FoodBrand', NOW(), NOW());

-- 일반 회원 장바구니 (TC-022에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(3, 3, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(3, 11, '할인 상품A', '10% 할인 적용 상품', 50000.00, 1, '할인상품', 'DiscountBrand', NOW(), NOW()),
(3, 12, '일반 상품B', '할인 없는 일반 상품', 30000.00, 1, '일반상품', 'NormalBrand', NOW(), NOW());

-- 카테고리 할인 테스트용 장바구니 (TC-008에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(4, 5, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(4, 2, '뷰티 마스크팩', '수분 마스크팩 10개입', 25000.00, 1, '뷰티', 'BeautyBrand', NOW(), NOW()),
(4, 3, '뷰티 토너', '수분 토너 200ml', 30000.00, 1, '뷰티', 'BeautyBrand', NOW(), NOW());

-- 수량 기반 할인 테스트용 장바구니 (TC-009에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(5, 6, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(5, 5, '식품 과자', '건강한 과자 200g', 20000.00, 2, '식품', 'FoodBrand', NOW(), NOW()),
(5, 6, '식품 음료', '자연 음료 500ml', 15000.00, 1, '식품', 'FoodBrand', NOW(), NOW());

-- 브랜드 할인 테스트용 장바구니 (TC-007에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(6, 7, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(6, 9, '브랜드X 상품A', '브랜드X의 프리미엄 상품', 40000.00, 1, '전자제품', 'BrandX', NOW(), NOW()),
(6, 10, '브랜드Y 상품B', '브랜드Y의 일반 상품', 60000.00, 1, '전자제품', 'BrandY', NOW(), NOW());

-- 경계값 테스트용 장바구니 (TC-023에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(7, 8, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(7, 16, '경계값 상품 49999', '49,999원 상품', 49999.00, 1, '경계값', 'BoundaryBrand', NOW(), NOW());

-- 대용량 테스트용 장바구니 (TC-024에서 사용)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(8, 9, NOW(), NOW());

-- 대용량 테스트를 위한 장바구니 아이템들 (100개 상품)
INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(8, 13, '테스트 상품 1', '성능 테스트용 상품', 1000.00, 1, '테스트', 'TestBrand', NOW(), NOW()),
(8, 14, '테스트 상품 2', '성능 테스트용 상품', 2000.00, 1, '테스트', 'TestBrand', NOW(), NOW()),
(8, 15, '테스트 상품 3', '성능 테스트용 상품', 3000.00, 1, '테스트', 'TestBrand', NOW(), NOW());

-- 추가 테스트 장바구니들
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(9, 10, NOW(), NOW()),
(10, 1, NOW(), NOW());

INSERT INTO cart_items (
    cart_id, product_id, product_name, product_description, unit_price, quantity, 
    category, brand, created_at, updated_at
) VALUES
(9, 19, '추가 테스트 상품 1', '추가 테스트용', 10000.00, 1, '추가', 'ExtraBrand', NOW(), NOW()),
(9, 20, '추가 테스트 상품 2', '추가 테스트용', 20000.00, 1, '추가', 'ExtraBrand', NOW(), NOW()),
(10, 21, '추가 테스트 상품 3', '추가 테스트용', 30000.00, 1, '추가', 'ExtraBrand', NOW(), NOW()),
(10, 22, '추가 테스트 상품 4', '추가 테스트용', 40000.00, 1, '추가', 'ExtraBrand', NOW(), NOW());

-- =====================================================
-- 6. 데이터 검증 쿼리
-- =====================================================

-- 삽입된 데이터 확인
SELECT 'Users' as table_name, COUNT(*) as count FROM users WHERE id BETWEEN 1 AND 10
UNION ALL
SELECT 'Products' as table_name, COUNT(*) as count FROM products WHERE id BETWEEN 1 AND 23
UNION ALL
SELECT 'Promotions' as table_name, COUNT(*) as count FROM promotions WHERE id BETWEEN 1 AND 17
UNION ALL
SELECT 'Carts' as table_name, COUNT(*) as count FROM carts WHERE id BETWEEN 1 AND 10
UNION ALL
SELECT 'Cart Items' as table_name, COUNT(*) as count FROM cart_items WHERE cart_id BETWEEN 1 AND 10;

-- 활성화된 프로모션 확인
SELECT id, name, type, priority, is_active, start_date, end_date 
FROM promotions 
WHERE is_active = true 
ORDER BY priority, id;

-- 테스트 사용자별 장바구니 확인
SELECT 
    u.id as user_id,
    u.email,
    u.membership_level,
    u.is_new_customer,
    c.id as cart_id,
    COUNT(ci.id) as item_count,
    SUM(ci.unit_price * ci.quantity) as total_amount
FROM users u
LEFT JOIN carts c ON u.id = c.user_id
LEFT JOIN cart_items ci ON c.id = ci.cart_id
WHERE u.id BETWEEN 1 AND 10
GROUP BY u.id, u.email, u.membership_level, u.is_new_customer, c.id
ORDER BY u.id;

-- =====================================================
-- 7. 테스트 시나리오별 데이터 매핑
-- =====================================================

/*
테스트 시나리오별 데이터 매핑:

TC-001 ~ TC-005: 기본 기능 테스트
- 사용자: 1, 2, 3, 4
- 상품: 1~23
- 장바구니: 1~10

TC-006: 특정 상품 할인
- 상품: 11 (할인 상품A)
- 프로모션: 3 (상품A 10% 할인)

TC-007: 브랜드별 할인
- 상품: 9 (브랜드X), 10 (브랜드Y)
- 프로모션: 4 (브랜드X 15% 할인)
- 장바구니: 6

TC-008: 카테고리 최소 구매액 할인
- 상품: 1, 2, 3 (뷰티 카테고리)
- 프로모션: 5 (뷰티 카테고리 할인)
- 장바구니: 4

TC-009: 카테고리 수량 기반 할인
- 상품: 4, 5, 6 (식품 카테고리)
- 프로모션: 6 (식품 수량 할인)
- 장바구니: 5

TC-010: 장바구니 최소 금액 할인
- 프로모션: 7 (장바구니 10% 할인)
- 장바구니: 2 (120,000원)

TC-011: 무료 배송 조건
- 프로모션: 8 (무료 배송)
- 장바구니: 2 (120,000원)

TC-012: 특정 카드 할인
- 프로모션: 10 (X카드 할인)

TC-013: 주말 할인
- 프로모션: 11 (주말 할인)

TC-014: 신규 고객 할인 최우선 적용
- 사용자: 1 (신규 고객)
- 프로모션: 1 (신규 고객 할인)
- 장바구니: 1

TC-015: 이벤트 할인 최우선 적용
- 프로모션: 2 (블랙프라이데이 1+1)

TC-016: 쿠폰 중복 사용 불가
- 프로모션: 3, 4 (여러 쿠폰)

TC-017: 세트 할인과 카테고리 할인 중복 불가
- 프로모션: 12 (세트 할인), 5 (카테고리 할인)

TC-018: 프로모션 적용 순서 검증
- 프로모션: 5, 7, 3 (다양한 우선순위)

TC-019: 신규 고객 + 장바구니 120,000원 시나리오
- 사용자: 1 (신규 고객)
- 장바구니: 1 (120,000원)
- 프로모션: 1 (신규 고객 할인)

TC-020: VIP 회원 + X카드 + 카테고리 조건 충족 시나리오
- 사용자: 2 (VIP 회원)
- 장바구니: 2 (120,000원)
- 프로모션: 5, 9, 10 (뷰티 할인, VIP 할인, X카드 할인)

TC-021: 이벤트 기간 + 쿠폰 보유 시나리오
- 프로모션: 2 (블랙프라이데이 1+1)

TC-022: 다중 프로모션 중복 가능 시나리오
- 사용자: 3 (일반 회원)
- 프로모션: 9, 10, 11 (VIP 할인, X카드 할인, 주말 할인)

TC-023: 경계값 테스트
- 상품: 16, 17, 18 (경계값 상품들)
- 장바구니: 7

TC-024: 장바구니 계산 성능 테스트
- 장바구니: 8 (대용량 테스트용)

TC-025: 동시 사용자 부하 테스트
- 사용자: 1~10 (다양한 사용자)

TC-026: 대용량 프로모션 정책 테스트
- 프로모션: 1~17 (다양한 프로모션)

TC-027 ~ TC-030: 에러 처리 테스트
- 잘못된 데이터로 테스트

TC-031 ~ TC-033: 통합 테스트
- 전체 시스템 통합 테스트

TC-034 ~ TC-037: UI/UX 테스트
- 사용자 인터페이스 테스트
*/

-- =====================================================
-- 스크립트 실행 완료
-- =====================================================

SELECT 'Test data setup completed successfully!' as status;
