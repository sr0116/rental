"use client";

import Script from "next/script";
import { useState } from "react";
import PaymentStatusOverlay from "@/components/PaymentStatusOverlay";
import PaymentList from "@/components/PaymentList"; // 결제 리스트 컴포넌트

export default function PaymentPage() {
  const [loading, setLoading] = useState(false);
  const [lastPayment, setLastPayment] = useState(null);
  const [status, setStatus] = useState(null); // processing | success | failed | refunded
  const [open, setOpen] = useState(false);
  const [showList, setShowList] = useState(false); // 결제 리스트 토글 상태

  const MERCHANT_CODE = "imp52145352"; // PortOne 고객사 식별코드
  const API_BASE = "http://localhost:8080/api/payments";

  const handlePayment = async () => {
    setLoading(true);
    setStatus("processing");
    setOpen(true);

    try {
      // 1. 결제 준비 API 호출
      const prepareRes = await fetch(`${API_BASE}/prepare`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          orderId: 1,
          memberId: 1,
          amount: 100, // 테스트 금액
          payType: "CARD",
        }),
      });
      const prepared = await prepareRes.json();

      if (!prepared.ok || !prepared.data) {
        setLoading(false);
        setStatus("failed");
        console.error("결제 준비 실패:", prepared.message || "알 수 없는 오류");
        return;
      }

      const { paymentId, merchantUid, amount } = prepared.data;

      // 2. PortOne 결제창 실행
      if (!window.IMP) {
        setLoading(false);
        setStatus("failed");
        console.error("PortOne SDK 로드 실패");
        return;
      }

      const { IMP } = window;
      IMP.init(MERCHANT_CODE);

      IMP.request_pay(
          {
            pg: "nice_v2",
            pay_method: "card",
            merchant_uid: merchantUid,
            name: "테스트 상품",
            amount,
            buyer_email: "test@example.com",
            buyer_name: "사랑",
          },
          async (rsp) => {
            setLoading(false);
            console.log("=== PortOne 전체 응답 ===", rsp);

            // imp_uid 없으면 결제 실패
            if (!rsp.imp_uid) {
              setStatus("failed");
              console.error("PortOne 결제 실패: imp_uid 없음", rsp.error_msg || "사용자 취소");
              return;
            }

            // 3. 결제 완료 검증 API 호출 (백엔드로 위임)
            try {
              const completeRes = await fetch(
                  `${API_BASE}/${paymentId}/complete?imp_uid=${rsp.imp_uid}`,
                  { method: "POST" }
              );
              const result = await completeRes.json();
              console.log("결제 완료 검증 응답:", result);

              if (result.ok) {
                switch (result.data?.payStatus) {
                  case "PAID":
                    setStatus("success");
                    setLastPayment(result.data);
                    break;
                  case "FAILED":
                    setStatus("failed");
                    console.error("결제 실패 처리:", result.message || "사용자 취소/실패");
                    break;
                  case "REFUNDED":
                  case "PARTIAL_REFUNDED":
                    setStatus("refunded");
                    break;
                  case "PENDING":
                    setStatus("processing");
                    break;
                  default:
                    setStatus("failed");
                    console.error("알 수 없는 결제 상태:", result.data?.payStatus);
                }
              } else {
                setStatus("failed");
                console.error("결제 검증 실패:", result.message || "서버 응답 없음");
              }
            } catch (err) {
              setStatus("failed");
              console.error("결제 검증 요청 중 예외 발생:", err);
            }
          }
      );
    } catch (err) {
      setLoading(false);
      setStatus("failed");
      console.error("결제 처리 중 예외 발생:", err);
    }
  };

  return (
      <main className="p-6 space-y-4 bg-gray-100 min-h-screen">
        {/* PortOne SDK 로드 */}
        <Script
            src="https://cdn.iamport.kr/v1/iamport.js"
            strategy="afterInteractive"
        />

        <h1 className="text-xl font-semibold">구독 결제 테스트 플로우</h1>

        <div className="space-x-4">
          {/* 결제 버튼 */}
          <button
              disabled={loading}
              onClick={handlePayment}
              className="bg-gray-800 text-white px-6 py-3 rounded-lg"
          >
            {loading ? "처리 중..." : "결제 테스트"}
          </button>

          {/* 결제 리스트 보기 버튼 */}
          <button
              onClick={() => setShowList(!showList)}
              className="bg-blue-600 text-white px-6 py-3 rounded-lg"
          >
            {showList ? "결제 리스트 닫기" : "결제 리스트 보기"}
          </button>
        </div>

        {/* 최근 결제 결과 표시 */}
        {lastPayment && (
            <div className="mt-6 border rounded p-4 bg-white shadow">
              <h2 className="font-semibold mb-2">최근 결제 결과</h2>
              <p>paymentId: {lastPayment.paymentId}</p>
              <p>merchantUid: {lastPayment.merchantUid}</p>
              <p>impUid: {lastPayment.impUid}</p>
              <p>status: {lastPayment.payStatus}</p>
              <p>amount: {lastPayment.amount}</p>
            </div>
        )}

        {/* 결제 리스트 컴포넌트 */}
        {showList && <PaymentList />}

        {/* 결제 상태 오버레이 */}
        <PaymentStatusOverlay
            open={open}
            status={status}
            onClose={() => setOpen(false)}
        />
      </main>
  );
}
