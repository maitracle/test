-- =====================================================
-- QA 테스트 데이터 검증 스크립트
-- =====================================================
-- 이 스크립트는 테스트 데이터가 올바르게 삽입되었는지 검증합니다.

-- =====================================================
-- 1. 기본 데이터 개수 확인
-- =====================================================

SELECT '=== 기본 데이터 개수 확인 ===' as section;

SELECT 'Users' as table_name, COUNT(*) as count FROM users WHERE id BETWEEN 1 AND 10
UNION ALL
SELECT 'Products' as table_name, COUNT(*) as count FROM products WHERE id BETWEEN 1 AND 23
UNION ALL
SELECT 'Promotions' as table_name, COUNT(*) as count FROM promotions WHERE id BETWEEN 1 AND 17
UNION ALL
SELECT 'Carts' as table_name, COUNT(*) as count FROM carts WHERE id BETWEEN 1 AND 10
UNION ALL
SELECT 'Cart Items' as table_name, COUNT(*) as count FROM cart_items WHERE cart_id BETWEEN 1 AND 10;

-- =====================================================
-- 2. 사용자 데이터 검증
-- =====================================================

SELECT '=== 사용자 데이터 검증 ===' as section;

SELECT 
    id,
    email,
    membership_level,
    is_new_customer,
    created_at
FROM users 
WHERE id BETWEEN 1 AND 10
ORDER BY id;

-- 신규 고객 확인
SELECT '신규 고객' as user_type, COUNT(*) as count 
FROM users 
WHERE id BETWEEN 1 AND 10 AND is_new_customer = true;

-- 회원 등급별 분포
SELECT 
    membership_level,
    COUNT(*) as count
FROM users 
WHERE id BETWEEN 1 AND 10
GROUP BY membership_level
ORDER BY membership_level;

-- =====================================================
-- 3. 상품 데이터 검증
-- =====================================================

SELECT '=== 상품 데이터 검증 ===' as section;

-- 카테고리별 상품 분포
SELECT 
    category,
    COUNT(*) as count,
    MIN(price) as min_price,
    MAX(price) as max_price,
    AVG(price) as avg_price
FROM products 
WHERE id BETWEEN 1 AND 23
GROUP BY category
ORDER BY category;

-- 브랜드별 상품 분포
SELECT 
    brand,
    COUNT(*) as count,
    MIN(price) as min_price,
    MAX(price) as max_price
FROM products 
WHERE id BETWEEN 1 AND 23
GROUP BY brand
ORDER BY brand;

-- 가격대별 상품 분포
SELECT 
    CASE 
        WHEN price < 10000 THEN '1만원 미만'
        WHEN price < 30000 THEN '1-3만원'
        WHEN price < 50000 THEN '3-5만원'
        WHEN price < 100000 THEN '5-10만원'
        ELSE '10만원 이상'
    END as price_range,
    COUNT(*) as count
FROM products 
WHERE id BETWEEN 1 AND 23
GROUP BY 
    CASE 
        WHEN price < 10000 THEN '1만원 미만'
        WHEN price < 30000 THEN '1-3만원'
        WHEN price < 50000 THEN '3-5만원'
        WHEN price < 100000 THEN '5-10만원'
        ELSE '10만원 이상'
    END
ORDER BY min(price);

-- =====================================================
-- 4. 프로모션 데이터 검증
-- =====================================================

SELECT '=== 프로모션 데이터 검증 ===' as section;

-- 활성화된 프로모션 확인
SELECT 
    id,
    name,
    type,
    priority,
    is_active,
    start_date,
    end_date,
    CASE 
        WHEN NOW() BETWEEN start_date AND end_date THEN '진행중'
        WHEN NOW() < start_date THEN '시작전'
        WHEN NOW() > end_date THEN '종료됨'
    END as status
FROM promotions 
WHERE id BETWEEN 1 AND 17
ORDER BY priority, id;

-- 프로모션 타입별 분포
SELECT 
    type,
    COUNT(*) as count,
    AVG(priority) as avg_priority
FROM promotions 
WHERE id BETWEEN 1 AND 17
GROUP BY type
ORDER BY type;

-- 우선순위별 프로모션 분포
SELECT 
    priority,
    COUNT(*) as count,
    GROUP_CONCAT(name SEPARATOR ', ') as promotion_names
FROM promotions 
WHERE id BETWEEN 1 AND 17
GROUP BY priority
ORDER BY priority;

-- 신규 고객 전용 프로모션 확인
SELECT 
    id,
    name,
    is_new_customer_only,
    discount_amount,
    discount_percentage
FROM promotions 
WHERE id BETWEEN 1 AND 17 AND is_new_customer_only = true;

-- 카테고리별 프로모션 확인
SELECT 
    id,
    name,
    target_category,
    min_cart_amount,
    discount_amount,
    discount_percentage
FROM promotions 
WHERE id BETWEEN 1 AND 17 AND target_category IS NOT NULL;

-- =====================================================
-- 5. 장바구니 데이터 검증
-- =====================================================

SELECT '=== 장바구니 데이터 검증 ===' as section;

-- 사용자별 장바구니 현황
SELECT 
    u.id as user_id,
    u.email,
    u.membership_level,
    u.is_new_customer,
    c.id as cart_id,
    COUNT(ci.id) as item_count,
    COALESCE(SUM(ci.unit_price * ci.quantity), 0) as total_amount,
    COALESCE(AVG(ci.unit_price), 0) as avg_item_price
FROM users u
LEFT JOIN carts c ON u.id = c.user_id
LEFT JOIN cart_items ci ON c.id = ci.cart_id
WHERE u.id BETWEEN 1 AND 10
GROUP BY u.id, u.email, u.membership_level, u.is_new_customer, c.id
ORDER BY u.id;

-- 장바구니별 상품 분포
SELECT 
    c.id as cart_id,
    c.user_id,
    ci.category,
    COUNT(*) as item_count,
    SUM(ci.quantity) as total_quantity,
    SUM(ci.unit_price * ci.quantity) as category_total
FROM carts c
JOIN cart_items ci ON c.id = ci.cart_id
WHERE c.id BETWEEN 1 AND 10
GROUP BY c.id, c.user_id, ci.category
ORDER BY c.id, ci.category;

-- =====================================================
-- 6. 테스트 시나리오별 데이터 준비 상태 확인
-- =====================================================

SELECT '=== 테스트 시나리오별 데이터 준비 상태 ===' as section;

-- TC-019: 신규 고객 + 장바구니 120,000원 시나리오
SELECT 
    'TC-019' as test_case,
    '신규 고객 + 장바구니 120,000원' as scenario,
    CASE 
        WHEN EXISTS(SELECT 1 FROM users WHERE id = 1 AND is_new_customer = true) 
        AND EXISTS(SELECT 1 FROM carts WHERE id = 1) 
        AND EXISTS(SELECT 1 FROM promotions WHERE id = 1 AND is_active = true)
        THEN '준비완료'
        ELSE '준비필요'
    END as status;

-- TC-020: VIP 회원 + X카드 + 카테고리 조건 충족 시나리오
SELECT 
    'TC-020' as test_case,
    'VIP 회원 + X카드 + 카테고리 조건 충족' as scenario,
    CASE 
        WHEN EXISTS(SELECT 1 FROM users WHERE id = 2 AND membership_level = 'VIP') 
        AND EXISTS(SELECT 1 FROM carts WHERE id = 2) 
        AND EXISTS(SELECT 1 FROM promotions WHERE id = 5 AND target_category = '뷰티')
        AND EXISTS(SELECT 1 FROM promotions WHERE id = 9 AND target_user_level = 'VIP')
        AND EXISTS(SELECT 1 FROM promotions WHERE id = 10)
        THEN '준비완료'
        ELSE '준비필요'
    END as status;

-- TC-021: 이벤트 기간 + 쿠폰 보유 시나리오
SELECT 
    'TC-021' as test_case,
    '이벤트 기간 + 쿠폰 보유' as scenario,
    CASE 
        WHEN EXISTS(SELECT 1 FROM promotions WHERE id = 2 AND type = 'BUY_ONE_GET_ONE')
        THEN '준비완료'
        ELSE '준비필요'
    END as status;

-- =====================================================
-- 7. 데이터 무결성 검증
-- =====================================================

SELECT '=== 데이터 무결성 검증 ===' as section;

-- 외래키 무결성 확인
SELECT 
    'Cart Items -> Carts' as relationship,
    COUNT(*) as orphaned_items
FROM cart_items ci
LEFT JOIN carts c ON ci.cart_id = c.id
WHERE c.id IS NULL;

-- 장바구니 아이템의 상품 정보 일관성 확인
SELECT 
    'Cart Items Product Info' as check_type,
    COUNT(*) as inconsistent_items
FROM cart_items ci
WHERE ci.product_id IS NULL OR ci.product_name IS NULL OR ci.unit_price IS NULL;

-- 프로모션 기간 유효성 확인
SELECT 
    'Promotion Period Validity' as check_type,
    COUNT(*) as invalid_promotions
FROM promotions 
WHERE start_date >= end_date;

-- =====================================================
-- 8. 성능 테스트용 데이터 확인
-- =====================================================

SELECT '=== 성능 테스트용 데이터 확인 ===' as section;

-- 대용량 테스트용 장바구니 확인
SELECT 
    c.id as cart_id,
    c.user_id,
    COUNT(ci.id) as item_count,
    SUM(ci.unit_price * ci.quantity) as total_amount
FROM carts c
JOIN cart_items ci ON c.id = ci.cart_id
WHERE c.id = 8  -- 대용량 테스트용 장바구니
GROUP BY c.id, c.user_id;

-- 경계값 테스트용 상품 확인
SELECT 
    id,
    name,
    price,
    category
FROM products 
WHERE id IN (16, 17, 18)  -- 경계값 테스트용 상품들
ORDER BY price;

-- =====================================================
-- 9. 요약 정보
-- =====================================================

SELECT '=== 요약 정보 ===' as section;

SELECT 
    'Total Test Users' as metric,
    COUNT(*) as value
FROM users 
WHERE id BETWEEN 1 AND 10

UNION ALL

SELECT 
    'Total Test Products' as metric,
    COUNT(*) as value
FROM products 
WHERE id BETWEEN 1 AND 23

UNION ALL

SELECT 
    'Active Promotions' as metric,
    COUNT(*) as value
FROM promotions 
WHERE id BETWEEN 1 AND 17 AND is_active = true

UNION ALL

SELECT 
    'Test Carts' as metric,
    COUNT(*) as value
FROM carts 
WHERE id BETWEEN 1 AND 10

UNION ALL

SELECT 
    'Total Cart Items' as metric,
    COUNT(*) as value
FROM cart_items 
WHERE cart_id BETWEEN 1 AND 10;

-- =====================================================
-- 검증 완료
-- =====================================================

SELECT 'Data validation completed successfully!' as status;
