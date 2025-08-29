"use client";

import Script from "next/script";
import { useState } from "react";

export default function PayTestPage() {
  const [loading, setLoading] = useState(false);
  const [lastPayment, setLastPayment] = useState(null);

  const MERCHANT_CODE = "imp52145352"; // PortOne 콘솔 고객사 식별코드
  const API_BASE = "http://localhost:8080/api/payments"; // Spring 서버 API

  // PortOne 초기화
  const initIMP = () => {
    if (window.IMP) {
      window.IMP.init(MERCHANT_CODE);
    } else {
      alert("PortOne SDK 로드 실패");
    }
  };

  // ① 일회성 결제
  const oneTime = async () => {
    initIMP();
    const merchant_uid = `one_${Date.now()}`;

    // (1) 결제 준비 요청
    await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        merchantUid: merchant_uid,
        amount: 1000,
        planType: "ONE_TIME",
      }),
    });

    setLoading(true);

    // (2) PortOne 결제창 실행
    const pgOption = "nice_v2"; // 또는 "nice_v2"

    window.IMP.request_pay(
        {
          pg: pgOption,
          pay_method: "card",
          merchant_uid,
          name: "일회성 결제 테스트",
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
          const res = await fetch(`${API_BASE}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              impUid: rsp.imp_uid,
              merchantUid: rsp.merchant_uid,
            }),
          }).then((r) => r.json());

          if (res.ok) {
            alert("결제 성공 & 검증 완료!");
            setLastPayment(res.data);
          } else {
            alert(`검증 실패: ${res.message}`);
          }
        }
    );
  }; // 🔥 여기 중괄호 닫음!!

  // ② 환불 테스트
  const refund = async () => {
    if (!lastPayment) {
      alert("먼저 결제를 완료하세요!");
      return;
    }
    const res = await fetch(`${API_BASE}/refund`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        merchantUid: lastPayment.merchantUid,
        reason: "테스트 환불",
      }),
    }).then((r) => r.json());

    if (res.ok) {
      alert("환불 성공!");
      setLastPayment(res.data);
    } else {
      alert(`환불 실패: ${res.message}`);
      console.error("환불 실패:", res);
    }
  };

  return (
      <main className="p-6 space-y-4">
        {/* PortOne SDK */}
        <Script
            src="https://cdn.iamport.kr/v1/iamport.js"
            strategy="afterInteractive"
        />

        <h1 className="text-xl font-semibold">
          PortOne 결제 테스트 (Next.js + Spring)
        </h1>

        <div className="flex gap-2 flex-wrap">
          <button
              disabled={loading}
              onClick={oneTime}
              className="border rounded px-4 py-2"
          >
            {loading ? "처리 중..." : "① 일회성 결제"}
          </button>
          <button
              disabled={loading}
              onClick={refund}
              className="border rounded px-4 py-2"
          >
            ② 환불
          </button>
        </div>

        {lastPayment && (
            <div className="border rounded p-4 mt-4 bg-gray-50">
              <h2 className="font-semibold mb-2">최근 결제 결과</h2>
              <p>merchantUid: {lastPayment.merchantUid}</p>
              <p>status: {lastPayment.status}</p>
              <p>amount: {lastPayment.amount}</p>
              <p>paidAmount: {lastPayment.paidAmount}</p>
              <p>refundAmount: {lastPayment.refundAmount}</p>
              <p>paidAt: {lastPayment.paidAt}</p>
              <p>refundedAt: {lastPayment.refundedAt}</p>
            </div>
        )}
      </main>
  );
}
