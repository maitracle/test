-- =====================================================
-- QA 테스트 데이터 정리 스크립트
-- =====================================================
-- 이 스크립트는 테스트 완료 후 테스트 데이터를 정리합니다.
-- 주의: 프로덕션 환경에서는 절대 실행하지 마세요!

-- =====================================================
-- 1. 테스트 데이터 삭제 (외래키 순서 고려)
-- =====================================================

-- 장바구니 아이템 삭제
DELETE FROM cart_items WHERE cart_id IN (
    SELECT id FROM carts WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
);

-- 장바구니 삭제
DELETE FROM carts WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

-- 프로모션 삭제
DELETE FROM promotions WHERE id BETWEEN 1 AND 50;

-- 상품 삭제
DELETE FROM products WHERE id BETWEEN 1 AND 50;

-- 사용자 삭제
DELETE FROM users WHERE id BETWEEN 1 AND 10;

-- =====================================================
-- 2. 삭제 확인
-- =====================================================

SELECT 'Cleanup completed!' as status;

-- 삭제된 데이터 확인
SELECT 'Users' as table_name, COUNT(*) as remaining_count FROM users WHERE id BETWEEN 1 AND 10
UNION ALL
SELECT 'Products' as table_name, COUNT(*) as remaining_count FROM products WHERE id BETWEEN 1 AND 50
UNION ALL
SELECT 'Promotions' as table_name, COUNT(*) as remaining_count FROM promotions WHERE id BETWEEN 1 AND 50
UNION ALL
SELECT 'Carts' as table_name, COUNT(*) as remaining_count FROM carts WHERE user_id BETWEEN 1 AND 10
UNION ALL
SELECT 'Cart Items' as table_name, COUNT(*) as remaining_count FROM cart_items WHERE cart_id BETWEEN 1 AND 10;
