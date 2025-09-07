# 🛒 Demo E-commerce API 문서

프론트엔드 개발자를 위한 REST API 문서입니다.

## 📋 목차

- [기본 정보](#기본-정보)
- [인증](#인증)
- [공통 응답 형식](#공통-응답-형식)
- [에러 처리](#에러-처리)
- [API 엔드포인트](#api-엔드포인트)
  - [사용자 관리](#사용자-관리)
  - [상품 관리](#상품-관리)
  - [프로모션 관리](#프로모션-관리)
  - [장바구니 관리](#장바구니-관리)
- [데이터 모델](#데이터-모델)
- [예제 요청/응답](#예제-요청응답)

---

## 기본 정보

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **API 버전**: v1
- **데이터베이스**: H2 (메모리 데이터베이스)

---

## 인증

현재 버전에서는 인증이 구현되지 않았습니다. 향후 JWT 토큰 기반 인증이 추가될 예정입니다.

---

## 공통 응답 형식

### 성공 응답
```json
{
  "data": { ... },
  "status": 200,
  "message": "Success"
}
```

### 에러 응답
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다.",
    "details": ["이메일 형식이 올바르지 않습니다."]
  },
  "status": 400,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 에러 처리

### HTTP 상태 코드
- `200` - 성공
- `201` - 생성 성공
- `400` - 잘못된 요청
- `404` - 리소스를 찾을 수 없음
- `500` - 서버 내부 오류

### 에러 코드
- `VALIDATION_ERROR` - 입력값 검증 실패
- `NOT_FOUND` - 리소스를 찾을 수 없음
- `DUPLICATE_ERROR` - 중복 데이터
- `BUSINESS_ERROR` - 비즈니스 로직 오류

---

## API 엔드포인트

### 사용자 관리

#### 사용자 생성
```http
POST /api/users
```

**요청 본문:**
```json
{
  "email": "user@example.com",
  "membershipLevel": "REGULAR",
  "isNewCustomer": true
}
```

**응답:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "membershipLevel": "REGULAR",
  "isNewCustomer": true,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

#### 사용자 조회
```http
GET /api/users/{id}
```

**경로 변수:**
- `id` (Long): 사용자 ID

**응답:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "membershipLevel": "REGULAR",
  "isNewCustomer": true,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

### 상품 관리

#### 상품 생성
```http
POST /api/products
```

**요청 본문:**
```json
{
  "name": "스마트폰",
  "description": "최신 스마트폰",
  "price": 1000000,
  "stock": 100,
  "category": "전자제품",
  "brand": "삼성",
  "imageUrl": "https://example.com/image.jpg"
}
```

**응답:**
```json
{
  "id": 1,
  "name": "스마트폰",
  "description": "최신 스마트폰",
  "price": 1000000,
  "stock": 100,
  "category": "전자제품",
  "brand": "삼성",
  "imageUrl": "https://example.com/image.jpg",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### 상품 목록 조회
```http
GET /api/products
```

**응답:**
```json
[
  {
    "id": 1,
    "name": "스마트폰",
    "description": "최신 스마트폰",
    "price": 1000000,
    "stock": 100,
    "category": "전자제품",
    "brand": "삼성",
    "imageUrl": "https://example.com/image.jpg",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

#### 상품 상세 조회
```http
GET /api/products/{id}
```

**경로 변수:**
- `id` (Long): 상품 ID

**응답:** 상품 생성 응답과 동일

#### 상품 검색
```http
GET /api/products/search?category={category}&keyword={keyword}
```

**쿼리 파라미터:**
- `category` (String, 선택): 카테고리
- `keyword` (String, 선택): 검색 키워드

**응답:** 상품 목록 조회 응답과 동일

---

### 프로모션 관리

#### 프로모션 생성
```http
POST /api/promotions
```

**요청 본문:**
```json
{
  "name": "신규 회원 할인",
  "description": "신규 회원 10% 할인",
  "type": "PERCENTAGE_DISCOUNT",
  "priority": 1,
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-12-31T23:59:59Z",
  "targetCategory": "전자제품",
  "minCartAmount": 50000,
  "minQuantity": 1,
  "targetUserLevel": "NEW",
  "discountPercentage": 10.0,
  "discountAmount": null,
  "maxDiscountAmount": 100000
}
```

**응답:**
```json
{
  "promotionId": 1,
  "name": "신규 회원 할인",
  "description": "신규 회원 10% 할인",
  "type": "PERCENTAGE_DISCOUNT",
  "priority": 1,
  "isActive": true,
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-12-31T23:59:59Z",
  "targetCategory": "전자제품",
  "minCartAmount": 50000,
  "minQuantity": 1,
  "targetUserLevel": "NEW",
  "discountPercentage": 10.0,
  "discountAmount": null,
  "maxDiscountAmount": 100000,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### 프로모션 목록 조회
```http
GET /api/promotions?active={active}
```

**쿼리 파라미터:**
- `active` (Boolean, 선택): 활성 상태 필터

**응답:**
```json
[
  {
    "promotionId": 1,
    "name": "신규 회원 할인",
    "description": "신규 회원 10% 할인",
    "type": "PERCENTAGE_DISCOUNT",
    "priority": 1,
    "isActive": true,
    "startDate": "2024-01-01T00:00:00Z",
    "endDate": "2024-12-31T23:59:59Z",
    "targetCategory": "전자제품",
    "minCartAmount": 50000,
    "minQuantity": 1,
    "targetUserLevel": "NEW",
    "discountPercentage": 10.0,
    "discountAmount": null,
    "maxDiscountAmount": 100000,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

#### 프로모션 상세 조회
```http
GET /api/promotions/{id}
```

**경로 변수:**
- `id` (Long): 프로모션 ID

**응답:** 프로모션 생성 응답과 동일

#### 프로모션 수정
```http
PUT /api/promotions/{id}
```

**경로 변수:**
- `id` (Long): 프로모션 ID

**요청 본문:**
```json
{
  "name": "수정된 프로모션명",
  "description": "수정된 설명",
  "priority": 2
}
```

**응답:** 프로모션 생성 응답과 동일

#### 프로모션 삭제
```http
DELETE /api/promotions/{id}
```

**경로 변수:**
- `id` (Long): 프로모션 ID

**응답:** 204 No Content

#### 프로모션 활성화
```http
POST /api/promotions/{id}/activate
```

**경로 변수:**
- `id` (Long): 프로모션 ID

**응답:** 프로모션 생성 응답과 동일

#### 프로모션 비활성화
```http
POST /api/promotions/{id}/deactivate
```

**경로 변수:**
- `id` (Long): 프로모션 ID

**응답:** 프로모션 생성 응답과 동일

#### 프로모션 적용
```http
POST /api/promotions/apply
```

**요청 본문:**
```json
{
  "cartId": 1,
  "userId": 1
}
```

**응답:**
```json
{
  "appliedPromotions": [
    {
      "promotionId": 1,
      "promotionName": "신규 회원 할인",
      "discountAmount": 10000
    }
  ],
  "totalDiscount": 10000,
  "finalAmount": 90000
}
```

---

### 장바구니 관리

#### 장바구니 총액 계산 (프로모션 적용)
```http
POST /api/cart/calculate-with-promotions
```

**요청 본문:**
```json
{
  "userId": 1,
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

**응답:**
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "스마트폰",
      "unitPrice": 1000000,
      "quantity": 2,
      "totalPrice": 2000000
    },
    {
      "productId": 2,
      "productName": "케이스",
      "unitPrice": 50000,
      "quantity": 1,
      "totalPrice": 50000
    }
  ],
  "subtotal": 2050000,
  "totalDiscount": 100000,
  "shippingFee": 3000,
  "finalAmount": 1953000,
  "appliedPromotions": [
    {
      "promotionId": 1,
      "promotionName": "신규 회원 할인",
      "discountAmount": 100000
    }
  ]
}
```

#### 장바구니 검증
```http
POST /api/cart/validate
```

**요청 본문:**
```json
{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**응답:**
```json
{
  "isValid": true,
  "errors": [],
  "warnings": ["재고가 부족할 수 있습니다."]
}
```

---

## 데이터 모델

### 사용자 (User)
```typescript
interface User {
  id: number;
  email: string;
  membershipLevel: 'NEW' | 'REGULAR' | 'VIP' | 'PREMIUM';
  isNewCustomer: boolean;
  createdAt: string; // ISO 8601 형식
}
```

### 상품 (Product)
```typescript
interface Product {
  id: number;
  name: string;
  description?: string;
  price: number; // BigDecimal
  stock: number;
  category?: string;
  brand?: string;
  imageUrl?: string;
  isActive: boolean;
  createdAt: string; // ISO 8601 형식
  updatedAt: string; // ISO 8601 형식
}
```

### 프로모션 (Promotion)
```typescript
interface Promotion {
  promotionId: number;
  name: string;
  description?: string;
  type?: string;
  priority?: number;
  isActive?: boolean;
  startDate?: string; // ISO 8601 형식
  endDate?: string; // ISO 8601 형식
  targetCategory?: string;
  minCartAmount?: number; // BigDecimal
  minQuantity?: number;
  targetUserLevel?: string;
  discountPercentage?: number; // BigDecimal
  discountAmount?: number; // BigDecimal
  maxDiscountAmount?: number; // BigDecimal
  createdAt?: string; // ISO 8601 형식
  updatedAt?: string; // ISO 8601 형식
}
```

### 장바구니 아이템 (CartItem)
```typescript
interface CartItem {
  productId: number;
  quantity: number;
}

interface CartItemResponse {
  productId: number;
  productName: string;
  unitPrice: number; // BigDecimal
  quantity: number;
  totalPrice: number; // BigDecimal
}
```

### 장바구니 응답 (CartResponse)
```typescript
interface CartResponse {
  items: CartItemResponse[];
  subtotal: number; // BigDecimal
  totalDiscount: number; // BigDecimal
  shippingFee: number; // BigDecimal
  finalAmount: number; // BigDecimal
  appliedPromotions: AppliedPromotion[];
}

interface AppliedPromotion {
  promotionId: number;
  promotionName: string;
  discountAmount: number; // BigDecimal
}
```

---

## 예제 요청/응답

### 전체 쇼핑 플로우 예제

#### 1. 사용자 생성
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "membershipLevel": "NEW",
    "isNewCustomer": true
  }'
```

#### 2. 상품 목록 조회
```bash
curl -X GET http://localhost:8080/api/products
```

#### 3. 장바구니 계산
```bash
curl -X POST http://localhost:8080/api/cart/calculate-with-promotions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 1
      }
    ]
  }'
```

#### 4. 프로모션 목록 조회
```bash
curl -X GET http://localhost:8080/api/promotions?active=true
```

---

## 개발 환경 설정

### 서버 실행
```bash
./gradlew bootRun
```

### H2 데이터베이스 콘솔
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비어있음)

### 테스트 데이터
서버 시작 시 `data.sql` 파일을 통해 테스트 데이터가 자동으로 로드됩니다.

---

## 주의사항

1. **BigDecimal 처리**: 가격 관련 필드는 BigDecimal로 처리되며, JSON에서는 숫자로 직렬화됩니다.
2. **날짜 형식**: 모든 날짜는 ISO 8601 형식 (`YYYY-MM-DDTHH:mm:ssZ`)으로 처리됩니다.
3. **메모리 데이터베이스**: H2 메모리 데이터베이스를 사용하므로 서버 재시작 시 데이터가 초기화됩니다.
4. **CORS 설정**: 현재 모든 도메인에서의 요청을 허용하도록 설정되어 있습니다.

---

## 문의사항

API 관련 문의사항이나 버그 리포트는 개발팀에 연락해주세요.

**마지막 업데이트**: 2024-01-15
