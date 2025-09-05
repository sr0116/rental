# 결제 흐름 연습(실제 테이블처럼 pk,fk 지정해서 테스트)
## 1. 전체 흐름 정리
- **Front-End**: Next.js (React)
  - PortOne SDK (`IMP.request_pay`) 호출
  - 결제 준비 → 결제 실행 → 완료 검증 → 환불 요청 플로우
- **Back-End**: Spring Boot (JPA + MariaDB)
  - `/prepare`: 결제 사전 등록
  - `/complete`: 결제 완료 검증 및 DB 업데이트
  - `/refund`: 환불 처리
- **DB**: MariaDB `tbl_payment` 테이블
  - status: PENDING → PAID → REFUNDED
  - > 환불 처리는 아직 포스트 맨에서만 적용시켜보고 이후 추가 테스트 예정

---

## 2. 결제 플로우
1. **prepare()**
   - 서버에서 `merchantUid` 생성 (`MCHT-타임스탬프(자동생성)`)
   - DB 상태 = `PENDING`
   - 생성된 `merchantUid`를 프론트에 반환

2. **프론트 PortOne 호출**
   - `IMP.init(merchantCode)` 호출
   - `IMP.request_pay` 실행 시 `merchant_uid` = 서버에서 반환한 값 그대로 사용
   - PortOne 결제 완료 시 응답에 `imp_uid`, `merchant_uid` 반환

3. **complete()**
   - 프론트에서 `imp_uid`, `merchant_uid` 서버로 전달
   - 서버는 DB에서 `merchantUid`로 결제 엔티티 조회
   - `impUid` 저장 + `paystatus = PAID` 업데이트

4. **refund()** 
   - `merchantUid` 혹은 `paymentId`로 결제 조회
   - 환불 처리 후 상태 = `REFUNDED`
   - PortOne API 연동 시 실제 취소 처리 필요

---

## 3. imp_Uid null 문제 및 merchantUid null 이슈
### 문제 1: `impUid`가 null \
- 원인: PortOne 응답(`imp_uid`)과 DTO 필드(`impUid`) 네이밍 불일치
- 해결: DTO에 `@JsonProperty("imp_uid")` 추가 or 프론트에서 camelCase로 변환

### 문제 2: `결제 내역을 찾을 수 없음`
- 원인: prepare()에서 생성한 `merchantUid`와 프론트에서 PortOne 호출 시 사용한 `## 1. 전체 아키텍처`가 다름
- 해결: **merchantUid는 반드시 서버에서 생성하고, 프론트는 반환받은 값을 그대로 사용**

---

## 4. 코드 구성
### Back-End(spring)
- `PaymentServiceImpl.prepare()` → `merchantUid` 생성 & 저장
- `PaymentServiceImpl.complete()` → PortOne 검증 후 DB 업데이트
- `PaymentRepository.findByMerchantUid()` → 조회 시 일치 여부 확인 필수

### Front-End (next.js)
- `fetch('/prepare')` → 서버에서 `merchantUid` 받기
- `IMP.request_pay()` → 받은 `merchantUid` 그대로 사용
- `fetch('/complete')` → `impUid`, `merchantUid` 서버로 전달

---

##  환불 API 연동 점검
- Next.js → POST /api/payments/refund 호출 구조 확인
  - 프론트에서 보내는 값: paymentId, amount, reason
  - impUid는 프론트가 아닌 백엔드(DB 조회) 에서 처리

- Spring Boot (백엔드) 흐름
  1. PaymentController.refund() → PaymentServiceImpl.refund()
  2. DB에서 paymentId 기준으로 결제 내역 조회 (impUid 포함)
  3. PortOneClient.cancelPayment() 호출 (PortOne REST API /payments/cancel)
  4. 환불 성공 시:
     - tbl_payment.paystatus → REFUNDED / PARTIAL_REFUNDED
     - tbl_refund 테이블에 기록 추가
---
##  문제 이슈  (결제 요청에서는 안 그랬는데 환불 때 이슈 발생)
- 환불 API 호출 시 imp_key, imp_secret 누락 오류 발생
- 원인: RestTemplate이 Map<String,String>을 form-urlencoded로 직렬화 → PortOne API가 JSON만 허용
- 결과: PortOne이 "imp_key, imp_secret 파라메터가 누락되었습니다." 에러 반환
---
## 해결 
- Map 대신 DTO 객체(TokenRequest)를 사용하여 JSON 직렬화 강제
- 최종 전송 JSON:
  {
    "imp_key": "5356...",
    "imp_secret": "mumg6..."
  }
- 이 방식으로 access_token 정상 발급됨

