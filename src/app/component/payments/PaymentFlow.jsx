"use client";

import Script from "next/script";
import { useState } from "react";

export default function PaymentPage() {
  const [loading, setLoading] = useState(false);
  const [lastPayment, setLastPayment] = useState(null);

  const MERCHANT_CODE = "imp52145352"; // PortOne 고객사 식별코드
  const API_BASE = "http://localhost:8080/api/payments";

  const handlePayment = async () => {
    setLoading(true);

    // 1. 서버에 결제 준비 요청
    const prepareRes = await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ orderId: 1, amount: 1000 }),
    });
    const prepared = await prepareRes.json();
    console.log("prepare 응답:", prepared);

    if (!prepared.merchantUid) {
      alert("merchantUid 없음");
      setLoading(false);
      return;
    }

    // 2. PortOne 결제창 실행
    if (!window.IMP) {
      alert("PortOne SDK 로드 실패");
      setLoading(false);
      return;
    }
    window.IMP.init(MERCHANT_CODE);

    window.IMP.request_pay(
        {
          pg: "nice_v2",
          pay_method: "card",
          merchant_uid: prepared.merchantUid, // 서버에서 받은 값
          name: "테스트 상품",
          amount: prepared.amount,
          buyer_email: "test@example.com",
          buyer_name: "홍길동",
        },
        async (rsp) => {
          console.log("PortOne 응답:", rsp);
          setLoading(false);

          if (!rsp.imp_uid) {
            alert(`결제 실패: ${rsp.error_msg || "imp_uid 없음"}`);
            return;
          }

          // 3. 서버 검증 요청
          const completeRes = await fetch(`${API_BASE}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              impUid: rsp.imp_uid,
              merchantUid: rsp.merchant_uid,
            }),
          });
          const result = await completeRes.json();
          console.log("complete 응답:", result);

          if (result.paystatus === "PAID") {
            alert("결제 성공 & 검증 완료!");
            setLastPayment(result);
          } else {
            alert("검증 실패");
          }
        }
    );
  };

  return (
      <main className="p-6 space-y-4 text-white min-h-screen">
        <Script src="https://cdn.iamport.kr/v1/iamport.js" strategy="afterInteractive" />
        <h1 className="text-xl font-semibold">구독 결제 테스트 플로우</h1>

        <button
            disabled={loading}
            onClick={handlePayment}
            className="bg-gray-800  px-6 py-3 rounded-lg"
        > 버튼
          {loading ? "처리 중..." : "결제 테스트"}
        </button>

        {lastPayment && (
            <div className="mt-6 border rounded p-4 bg-white shadow">
              <h2 className="font-semibold mb-2">최근 결제 결과</h2>
              <p>merchantUid: {lastPayment.merchantUid}</p>
              <p>impUid: {lastPayment.impUid}</p>
              <p>status: {lastPayment.paystatus}</p>
              <p>amount: {lastPayment.amount}</p>
            </div>
        )}
      </main>
  );
}
