"use client";

import Script from "next/script";
import { useState } from "react";

declare global { interface Window { IMP: any } }

const MERCHANT_CODE = "imp12345678"; // PortOne 식별코드(테스트용). 운영은 .env 권장
const API_BASE = "http://localhost:8080/api/payments"; // 스프링 서버

export default function PayTestPage() {
  const [loading, setLoading] = useState(false);

  const initIMP = () => { if (window.IMP) window.IMP.init(MERCHANT_CODE); };

  // ① 일회성 결제
  const oneTime = async () => {
    initIMP();
    const merchant_uid = `one_${Date.now()}`;
    await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ merchantUid: merchant_uid, amount: 1000, planType: "ONE_TIME" })
    });
    setLoading(true);
    window.IMP.request_pay({
      pg: "html5_inicis",
      pay_method: "card",
      merchant_uid,
      name: "일회성 결제 테스트",
      amount: 1000,
      buyer_email: "test@example.com",
      buyer_name: "홍길동",
    }, async (rsp: any) => {
      setLoading(false);
      if (!rsp.success) return alert(`실패: ${rsp.error_msg}`);
      const res = await fetch(`${API_BASE}/complete`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ impUid: rsp.imp_uid, merchantUid: rsp.merchant_uid })
      }).then(r=>r.json());
      alert(res.ok ? "결제 검증 OK" : `검증 실패: ${res.message}`);
    });
  };

  // ② 정기 첫 결제(빌링키 저장)
  const firstRecurring = async () => {
    initIMP();
    const merchant_uid = `sub_first_${Date.now()}`;
    const customer_uid = "customer_demo_001";
    await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ merchantUid: merchant_uid, amount: 1200, planType: "RECURRING", customerUid: customer_uid })
    });
    setLoading(true);
    window.IMP.request_pay({
      pg: "html5_inicis",
      pay_method: "card",
      merchant_uid,
      customer_uid,
      name: "정기 첫 결제",
      amount: 1200,
      buyer_email: "test-sub@example.com",
      buyer_name: "정기구독자",
    }, async (rsp: any) => {
      setLoading(false);
      if (!rsp.success) return alert(`실패: ${rsp.error_msg}`);
      const res = await fetch(`${API_BASE}/complete`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ impUid: rsp.imp_uid, merchantUid: rsp.merchant_uid })
      }).then(r=>r.json());
      alert(res.ok ? "초기 결제/빌링키 OK" : `검증 실패: ${res.message}`);
    });
  };

  // ③ 정기 재청구(API)
  const chargeAgain = async () => {
    const res = await fetch(`${API_BASE}/recurring/charge`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ customerUid: "customer_demo_001", amount: 1500, name: "재청구" })
    }).then(r=>r.json());
    alert(res.ok ? "재청구 성공" : `재청구 실패: ${res.message}`);
  };

  // ④ 환불(부분/전액)
  const refund = async () => {
    const merchant_uid = prompt("환불 merchant_uid 입력:", "one_...") || "";
    const amountStr = prompt("부분 환불 금액(전액이면 비움):", "");
    const amount = amountStr ? Number(amountStr) : undefined;
    const res = await fetch(`${API_BASE}/refund`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ merchantUid: merchant_uid, amount, reason: "테스트 환불" })
    }).then(r=>r.json());
    alert(res.ok ? "환불 성공" : `환불 실패: ${res.message}`);
  };

  return (
      <main className="p-6 space-y-4">
        <Script src="https://cdn.iamport.kr/v1/iamport.js" strategy="afterInteractive" />
        <h1 className="text-xl font-semibold">PortOne 결제 테스트(Next.js → Spring)</h1>
        <div className="flex gap-2 flex-wrap">
          <button disabled={loading} className="border rounded px-3 py-2" onClick={oneTime}>① 일회성 결제</button>
          <button disabled={loading} className="border rounded px-3 py-2" onClick={firstRecurring}>② 정기 첫 결제(빌링키)</button>
          <button disabled={loading} className="border rounded px-3 py-2" onClick={chargeAgain}>③ 정기 재청구(API)</button>
          <button disabled={loading} className="border rounded px-3 py-2" onClick={refund}>④ 환불</button>
        </div>
        {loading && <p>처리 중…</p>}
      </main>
  );
}