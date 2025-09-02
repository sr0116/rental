# 배송지/배송 API 및 Next.js 연동 정리

## 1. DB 테이블 추가 (필요한 최소 정보만..null 허용함)
- **tbl_member**: 회원 정보 (mno, email, name 등)
- **tbl_product**: 상품 정보 (pno, name, price 등)
- **tbl_address**: 배송지 정보 (addrno, mno, name, tel, zipcode, addr, addr_detail, isDefault, memo, regdate)
- **tbl_delivery**: 배송 요청 (dno, order_id, subscribe_id, mno, addrno, pno, tracking_no, carrier_no, status, memo, shipped_at, regdate)
- **tbl_return**: 회수 요청 (rid, subscribe_id, mno, addrno, pno, status, reason, proof_url, damage_memo, regdate, voiddate)
---
## 2. Spring Boot 
### AddressService
- create(AddressRequest): 배송지 등록
- list(mno): 회원별 배송지 목록 조회
- setDefault(mno, addrno): 특정 배송지를 기본 배송지로 설정

### DeliveryService
- create(DeliveryCreateRequest): 배송 요청 생성
- updateStatus(dno, DeliveryStatusUpdateRequest): 배송 상태 변경
- list(mno): 회원별 배송 목록 조회

### 상태 전이 규칙 적용
->  동일 상태로의 전이나 건너뛰기 전이 모두 허용 x

- READY -> PREPARING -> SHIPPING -> DELIVERED
- 동일 상태 전이는 에러 처리(기본 버튼으로 기본 배송지 설정할 때 동일한 주소지 누르면 에러 처리)
---
## 3. REST API 엔드포인트
-> REST API는 자원을 URI로 표현, HTTP 메서드(POST, GET, PATCH 등)로 동작을 구분

-> 엔드 포인트 : "자원에 접근하는 URL + HTTP 메서드" 조합 (클라이언트가 서버 기능에 접근하기 위해 호출하는 최종 URL 경로라고 보면 됨)

### AddressController
- POST /api/addresses : 배송지 등록
- GET /api/addresses?mno={id} : 배송지 목록 조회
- PATCH /api/addresses/{addrno}/default?mno={id} : 기본 배송지 지정

### DeliveryController
- POST /api/deliveries : 배송 생성
- GET /api/deliveries?mno={id} : 배송 목록 조회
- PATCH /api/deliveries/{dno}/status : 배송 상태 변경
---
## 4. Postman 테스트 
1. 배송지 등록 → POST /api/addresses
2. 배송지 목록 조회 → GET /api/addresses
3. 기본 배송지 지정 → PATCH /api/addresses/{addrno}/default
4. 배송 생성 → POST /api/deliveries
5. 배송 목록 조회 → GET /api/deliveries
6. 배송 상태 변경 → PATCH /api/deliveries/{dno}/status
---
## 5. Next.js 연동
### 배송지 관리 페이지
- 카카오 주소 API를 사용해 우편번호와 도로명 주소 검색
- 상세주소, 수령인, 연락처 입력 후 스프링 API 호출해 DB 저장
- 기본 배송지 설정 

### 알아두면 좋은것
- `Script` 컴포넌트로 카카오 API 로드(스프링에서 불러오는 것이 아니라 next에서 호출해서 사용)

