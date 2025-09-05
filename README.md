# 모션 적용 

## 1. 결제 플로우 점검
- Next.js → PortOne SDK(IMP.request_pay) 호출
- 결제 순서
  1. /api/payments/prepare 호출 → merchantUid 발급
  2. PortOne 결제창 실행 (pg: nice_v2, 카드)
  3. 결제 완료 후 impUid 반환
  4. /api/payments/complete 호출 → 검증 및 DB 저장(tbl_payment 상태 갱신)
---
## 2. Lottie 애니메이션 적용 (실제로 결제 로딩/ 실패/ 성공/ 에러시 애니메이션 다르게 적용)
- public/lottie/ 경로에 JSON 파일 추가
  - payment-processing.json
  - payment-success.json
  - payment-failed.json
  - payment-refunded.json
- PaymentStatusOverlay.jsx에서 상태값에 따라 매핑
  - processing → 결제 중
  - success → 결제 성공
  - failed → 결제 실패
  - refunded → 환불
---
## 3. 컴포넌트 구조 (기존 실제 백엔드 서버와 연동해서 실제로 결제/ 환불 API 적용)
components/
- PaymentPage.jsx (메인 결제 로직)
- PaymentStatusOverlay.jsx (Lottie 상태 모달)
- PayButton.jsx (결제 버튼)
- Header.jsx / Footer.jsx / MotionWrapper.jsx

app/payment/
- page.js (PaymentPage 임포트)
- loading.js (로딩 애니메이션)
---
