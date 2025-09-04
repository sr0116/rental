"use client";

import { useState } from "react";
import Script from "next/script";
import Complete from "../components/payments/Complete";

export default function PaymentPage() {
  const [lastPayment, setLastPayment] = useState(null);
  const [loading, setLoading] = useState(false);

  const API_BASE = "http://localhost:8080/api/payments";
  const MERCHANT_CODE = "imp52145352"; // 포트원 식별코드

  // PortOne SDK 초기화
  const initIMP = () => {
    if (window.IMP) {
      window.IMP.init(MERCHANT_CODE);
    } else {
      alert("PortOne SDK 로드 실패");
    }
  };

  // 일회성 결제 실행
  const handlePayment = async () => {
    initIMP();
    const merchant_uid = `MCHT-${Date.now()}`;

    // (1) 서버에 결제 준비 요청
    const prepareRes = await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        merchantUid: merchant_uid,
        amount: 1000,
      }),
    }).then((r) => r.json());

    console.log("prepare 응답:", prepareRes);

    setLoading(true);

    // (2) PortOne 결제창 실행
    window.IMP.request_pay(
        {
          pg: "nice_v2",
          pay_method: "card",
          merchant_uid,
          name: "테스트 상품",
          amount: 1000,
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

          // (3) 결제 완료 검증 요청
          const completeBody = {
            impUid: rsp.imp_uid,       // snake_case → camelCase 매핑
            merchantUid: rsp.merchant_uid,
          };

          console.log("백엔드 전달 바디:", completeBody);

          const completeRes = await fetch(`${API_BASE}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(completeBody),
          }).then((r) => r.json());

          console.log("백엔드 응답:", completeRes);

          if (completeRes?.success) {
            alert("결제 성공 & 검증 완료!");
            setLastPayment(completeRes.data);
          } else {
            alert(`검증 실패: ${completeRes?.message || "오류"}`);
          }
        }
    );
  };

  return (
      <main className="min-h-screen bg-gray-100 p-8 space-y-6">
        {/* PortOne SDK */}
        <Script
            src="https://cdn.iamport.kr/v1/iamport.js"
            strategy="afterInteractive"
        />

        <h1 className="text-2xl font-bold text-gray-800 mb-6">
          구독 결제 테스트 플로우
        </h1>

        <div className="flex gap-4 flex-wrap">
          <button
              onClick={handlePayment}
              disabled={loading}
              className="bg-gray-800 text-white px-6 py-3 rounded-md hover:bg-gray-700"
          >
            {loading ? "처리 중..." : "결제 실행"}
          </button>
        </div>

        {lastPayment && <Complete result={lastPayment} />}
      </main>
  );
}
