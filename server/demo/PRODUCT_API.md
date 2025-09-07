# Product API 사용법

## 개요
커머스 IT 회사에서 사용할 수 있는 제품 관리 API입니다.

## API 엔드포인트

### 1. 제품 생성
```
POST /api/products
Content-Type: application/json

{
  "name": "제품명",
  "description": "제품 설명",
  "price": 100000,
  "stockQuantity": 50,
  "category": "카테고리",
  "brand": "브랜드",
  "imageUrl": "이미지 URL",
  "isActive": true
}
```

### 2. 제품 조회
```
GET /api/products/{id}
```

### 3. 전체 제품 목록 조회
```
GET /api/products
```

### 4. 카테고리별 제품 조회
```
GET /api/products/category/{category}
```

### 5. 브랜드별 제품 조회
```
GET /api/products/brand/{brand}
```

### 6. 키워드 검색
```
GET /api/products/search?keyword=검색어
```

### 7. 가격 범위별 제품 조회
```
GET /api/products/price-range?minPrice=100000&maxPrice=500000
```

### 8. 제품 수정
```
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "수정된 제품명",
  "description": "수정된 제품 설명",
  "price": 120000,
  "stockQuantity": 60,
  "category": "수정된 카테고리",
  "brand": "수정된 브랜드",
  "imageUrl": "수정된 이미지 URL",
  "isActive": true
}
```

### 9. 제품 삭제
```
DELETE /api/products/{id}
```

### 10. 제품 비활성화
```
PATCH /api/products/{id}/deactivate
```

## 샘플 데이터
애플리케이션 시작 시 다음과 같은 샘플 제품들이 자동으로 생성됩니다:
- MacBook Pro 16인치 (Apple)
- iPhone 15 Pro (Apple)
- Samsung Galaxy S24 Ultra (Samsung)
- LG OLED TV 65인치 (LG)
- Sony WH-1000XM5 (Sony)

## 데이터베이스
- H2 인메모리 데이터베이스 사용
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (비어있음)

## Cart API

### 1. 장바구니 총 가격 계산
```
POST /api/cart/calculate
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

### 2. 장바구니 유효성 검사
```
POST /api/cart/validate
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

## 실행 방법
1. 프로젝트를 빌드합니다: `./gradlew build`
2. 애플리케이션을 실행합니다: `./gradlew bootRun`
3. API 테스트: 
   - 제품 API: http://localhost:8080/api/products
   - 장바구니 API: http://localhost:8080/api/cart/calculate 