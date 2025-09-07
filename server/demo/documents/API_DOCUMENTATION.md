# API 문서

이 문서는 프론트엔드 개발자가 백엔드 API와 연동하기 위한 상세한 API 명세서입니다.

## 기본 정보

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **API 버전**: v1

## 공통 응답 형식

### 성공 응답
```json
{
  "data": "응답 데이터",
  "status": 200,
  "message": "성공"
}
```

### 오류 응답
```json
{
  "error": "오류 메시지",
  "status": 400,
  "timestamp": "2024-01-01T00:00:00"
}
```

## API 엔드포인트

### 1. 사용자 관리 (Users)

#### 1.1 사용자 생성
- **URL**: `POST /api/users`
- **설명**: 새로운 사용자를 생성합니다.

**Request Body:**
```json
{
  "email": "user@example.com",
  "membershipLevel": "NEW",
  "isNewCustomer": true
}
```

**Request Fields:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | string | ✓ | 사용자 이메일 |
| membershipLevel | string | ✓ | 멤버십 레벨 (NEW, REGULAR, VIP, PREMIUM) |
| isNewCustomer | boolean | ✓ | 신규 고객 여부 |

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "membershipLevel": "NEW",
  "isNewCustomer": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

#### 1.2 사용자 조회
- **URL**: `GET /api/users/{id}`
- **설명**: 특정 사용자의 정보를 조회합니다.

**Path Parameters:**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| id | number | ✓ | 사용자 ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "membershipLevel": "NEW",
  "isNewCustomer": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

### 2. 상품 관리 (Products)

#### 2.1 상품 생성
- **URL**: `POST /api/products`
- **설명**: 새로운 상품을 생성합니다.

**Request Body:**
```json
{
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "전자제품",
  "brand": "브랜드명",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Request Fields:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | string | ✓ | 상품명 |
| description | string | | 상품 설명 |
| price | number | ✓ | 상품 가격 |
| stock | number | ✓ | 재고 수량 |
| category | string | | 상품 카테고리 |
| brand | string | | 브랜드명 |
| imageUrl | string | | 상품 이미지 URL |

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "전자제품",
  "brand": "브랜드명",
  "imageUrl": "https://example.com/image.jpg",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 2.2 상품 조회
- **URL**: `GET /api/products/{id}`
- **설명**: 특정 상품의 정보를 조회합니다.

**Path Parameters:**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| id | number | ✓ | 상품 ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "stock": 100,
  "category": "전자제품",
  "brand": "브랜드명",
  "imageUrl": "https://example.com/image.jpg",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 2.3 상품 목록 조회
- **URL**: `GET /api/products`
- **설명**: 모든 상품 목록을 조회합니다.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "상품명1",
    "description": "상품 설명1",
    "price": 10000.00,
    "stock": 100,
    "category": "전자제품",
    "brand": "브랜드명1",
    "imageUrl": "https://example.com/image1.jpg",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  {
    "id": 2,
    "name": "상품명2",
    "description": "상품 설명2",
    "price": 20000.00,
    "stock": 50,
    "category": "의류",
    "brand": "브랜드명2",
    "imageUrl": "https://example.com/image2.jpg",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

#### 2.4 상품 검색
- **URL**: `GET /api/products/search`
- **설명**: 카테고리나 키워드로 상품을 검색합니다.

**Query Parameters:**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| category | string | | 검색할 카테고리 |
| keyword | string | | 검색 키워드 |

**Example:**
```
GET /api/products/search?category=전자제품&keyword=스마트폰
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "스마트폰",
    "description": "최신 스마트폰",
    "price": 500000.00,
    "stock": 10,
    "category": "전자제품",
    "brand": "삼성",
    "imageUrl": "https://example.com/smartphone.jpg",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

### 3. 장바구니 관리 (Cart)

#### 3.1 장바구니 계산 (프로모션 적용)
- **URL**: `POST /api/cart/calculate-with-promotions`
- **설명**: 장바구니 총액을 계산하고 프로모션을 적용합니다.

**Request Body:**
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

**Request Fields:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | number | ✓ | 사용자 ID |
| items | array | ✓ | 장바구니 아이템 목록 |
| items[].productId | number | ✓ | 상품 ID |
| items[].quantity | number | ✓ | 수량 |

**Response (200 OK):**
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "상품명1",
      "unitPrice": 10000.00,
      "quantity": 2,
      "totalPrice": 20000.00
    },
    {
      "productId": 2,
      "productName": "상품명2",
      "unitPrice": 15000.00,
      "quantity": 1,
      "totalPrice": 15000.00
    }
  ],
  "subtotal": 35000.00,
  "totalDiscount": 3500.00,
  "shippingFee": 3000.00,
  "finalAmount": 34500.00,
  "appliedPromotions": [
    {
      "promotionId": 1,
      "promotionName": "10% 할인 프로모션",
      "discountAmount": 3500.00
    }
  ]
}
```

#### 3.2 장바구니 검증
- **URL**: `POST /api/cart/validate`
- **설명**: 장바구니의 유효성을 검증합니다.

**Request Body:**
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

**Response (200 OK):**
```json
{
  "isValid": true,
  "errors": [],
  "warnings": [
    "재고가 부족할 수 있습니다."
  ]
}
```

**Response Fields:**
| 필드 | 타입 | 설명 |
|------|------|------|
| isValid | boolean | 장바구니 유효성 여부 |
| errors | array | 오류 메시지 목록 |
| warnings | array | 경고 메시지 목록 |

### 4. 프로모션 관리 (Promotions)

#### 4.1 프로모션 생성
- **URL**: `POST /api/promotions`
- **설명**: 새로운 프로모션을 생성합니다.

**Request Body:**
```json
{
  "name": "신규 회원 할인",
  "description": "신규 회원을 위한 특별 할인",
  "type": "PERCENTAGE",
  "priority": 1,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "targetCategory": "전자제품",
  "minCartAmount": 50000.00,
  "minQuantity": 1,
  "targetUserLevel": "NEW",
  "discountPercentage": 10.00,
  "discountAmount": null,
  "maxDiscountAmount": 10000.00
}
```

**Request Fields:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | string | ✓ | 프로모션명 |
| description | string | | 프로모션 설명 |
| type | string | ✓ | 프로모션 타입 (PERCENTAGE, FIXED_AMOUNT) |
| priority | number | ✓ | 우선순위 |
| startDate | string | ✓ | 시작일시 (ISO 8601) |
| endDate | string | ✓ | 종료일시 (ISO 8601) |
| targetCategory | string | | 대상 카테고리 |
| minCartAmount | number | | 최소 장바구니 금액 |
| minQuantity | number | | 최소 수량 |
| targetUserLevel | string | | 대상 사용자 레벨 |
| discountPercentage | number | | 할인율 (%) |
| discountAmount | number | | 고정 할인 금액 |
| maxDiscountAmount | number | | 최대 할인 금액 |

**Response (201 Created):**
```json
{
  "promotionId": 1,
  "name": "신규 회원 할인",
  "description": "신규 회원을 위한 특별 할인",
  "type": "PERCENTAGE",
  "priority": 1,
  "isActive": true,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "targetCategory": "전자제품",
  "minCartAmount": 50000.00,
  "minQuantity": 1,
  "targetUserLevel": "NEW",
  "discountPercentage": 10.00,
  "discountAmount": null,
  "maxDiscountAmount": 10000.00,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 4.2 프로모션 적용
- **URL**: `POST /api/promotions/apply`
- **설명**: 특정 장바구니에 프로모션을 적용합니다.

**Request Body:**
```json
{
  "cartId": 1,
  "userId": 1
}
```

**Request Fields:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| cartId | number | ✓ | 장바구니 ID |
| userId | number | ✓ | 사용자 ID |

**Response (200 OK):**
```json
{
  "promotionId": 1,
  "name": "신규 회원 할인",
  "description": "신규 회원을 위한 특별 할인",
  "type": "PERCENTAGE",
  "priority": 1,
  "isActive": true,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "targetCategory": "전자제품",
  "minCartAmount": 50000.00,
  "minQuantity": 1,
  "targetUserLevel": "NEW",
  "discountPercentage": 10.00,
  "discountAmount": null,
  "maxDiscountAmount": 10000.00,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## 데이터 타입 정의

### 멤버십 레벨 (MembershipLevel)
- `NEW`: 신규 회원
- `REGULAR`: 일반 회원
- `VIP`: VIP 회원
- `PREMIUM`: 프리미엄 회원

### 프로모션 타입 (PromotionType)
- `PERCENTAGE`: 퍼센트 할인
- `FIXED_AMOUNT`: 고정 금액 할인

## HTTP 상태 코드

| 코드 | 설명 |
|------|------|
| 200 | 성공 |
| 201 | 생성 성공 |
| 400 | 잘못된 요청 |
| 404 | 리소스를 찾을 수 없음 |
| 500 | 서버 오류 |

## 오류 처리

### 일반적인 오류 응답 형식
```json
{
  "error": "오류 메시지",
  "status": 400,
  "timestamp": "2024-01-01T00:00:00",
  "path": "/api/users",
  "details": "상세 오류 정보"
}
```

### 주요 오류 시나리오

1. **잘못된 요청 데이터 (400)**
   - 필수 필드 누락
   - 잘못된 데이터 타입
   - 유효하지 않은 값

2. **리소스 없음 (404)**
   - 존재하지 않는 사용자 ID
   - 존재하지 않는 상품 ID
   - 존재하지 않는 프로모션 ID

3. **서버 오류 (500)**
   - 데이터베이스 연결 오류
   - 예상치 못한 서버 오류

## 예제 요청/응답

### 사용자 생성 예제
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "membershipLevel": "NEW",
    "isNewCustomer": true
  }'
```

### 상품 검색 예제
```bash
curl -X GET "http://localhost:8080/api/products/search?category=전자제품&keyword=스마트폰" \
  -H "Content-Type: application/json"
```

### 장바구니 계산 예제
```bash
curl -X POST http://localhost:8080/api/cart/calculate-with-promotions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

## 주의사항

1. **날짜 형식**: 모든 날짜는 ISO 8601 형식 (`YYYY-MM-DDTHH:mm:ss`)을 사용합니다.
2. **금액 형식**: 모든 금액은 소수점 2자리까지 지원하는 숫자 형식입니다.
3. **페이징**: 현재 페이징 기능은 구현되지 않았습니다. 향후 추가 예정입니다.
4. **인증**: 현재 인증 기능은 구현되지 않았습니다. 향후 JWT 토큰 기반 인증을 추가할 예정입니다.

## 연락처

API 관련 문의사항이 있으시면 백엔드 개발팀에 연락해 주세요.