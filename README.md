##  결제 연습 흐름

### 1. Spring Boot 백엔드
- MariaDB 연결 (`rental` DB) -> np.imchobo.com
- `RentalPayment` 엔티티 및 DTO, Repository, Mapper, Service, Controller 구현
- PortOne API 연동
  - `/prepare` : 결제 사전등록
  - `/complete` : 결제 완료 검증
  - `/refund` : 환불 처리
- MapStruct 적용해봄 (Entity ↔ DTO 자동 변환)
- application.yml 환경 분리 (`portone.api-key`, `api-secret`, `merchant-code`)

### 2. Next.js 프론트엔드 -> 테스트 용이라 컴포넌트 안 나누고 함 
- `IMP.request_pay()` PortOne SDK 연동
- `① 일회성 결제` 버튼 → Spring `/prepare` + 결제창 호출
- 결제 성공 후 `/complete` API 호출 → DB 검증 완료
- `② 환불` 버튼 → Spring `/refund` API 호출
- 최신 결제 결과 화면 출력 (테이블로 결제 시간 , 결제 내역, 환불 내역같은 필수 정보 등등....)

### 3. 테스트 결과
-  PortOne 결제창 호출 성공
- 결제 완료 후 Spring 서버에서 검증 처리 완료
-  DB에 `RentalPayment` 데이터 저장 정상 동작
- 환불 API 정상 처리 확인 (아직 버튼만 누르면 바로 환불 및 직전 결제만 환불 가능)


---

