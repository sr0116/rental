"use client"

import Script from "next/script"
import { useState } from "react"
import PaymentStatusOverlay from "@/components/PaymentStatusOverlay"

export default function PaymentPage() {
  const [loading, setLoading] = useState(false)
  const [lastPayment, setLastPayment] = useState(null)
  const [status, setStatus] = useState(null) // processing | success | failed | refunded
  const [open, setOpen] = useState(false)

  const MERCHANT_CODE = "imp52145352" // PortOne 고객사 식별코드
  const API_BASE = "http://localhost:8080/api/payments"

  const handlePayment = async () => {
    setLoading(true)
    setStatus("processing")
    setOpen(true)

    // 1. 서버에 결제 준비 요청
    const prepareRes = await fetch(`${API_BASE}/prepare`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ orderId: 1, amount: 1000 }),
    })
    const prepared = await prepareRes.json()

    if (!prepared.merchantUid) {
      setLoading(false)
      setStatus("failed")
      return
    }

    // 2. PortOne 결제창 실행
    if (!window.IMP) {
      setLoading(false)
      setStatus("failed")
      return
    }
    window.IMP.init(MERCHANT_CODE)

    window.IMP.request_pay(
        {
          pg: "nice_v2",
          pay_method: "card",
          merchant_uid: prepared.merchantUid,
          name: "테스트 상품",
          amount: prepared.amount,
          buyer_email: "test@example.com",
          buyer_name: "홍길동",
        },
        async (rsp) => {
          setLoading(false)

          if (!rsp.imp_uid) {
            setStatus("failed")
            return
          }

          // 3. 서버 검증 요청
          const completeRes = await fetch(`${API_BASE}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              impUid: rsp.imp_uid,
              merchantUid: rsp.merchant_uid,
            }),
          })
          const result = await completeRes.json()

          if (result.paystatus === "PAID") {
            setStatus("success")
            setLastPayment(result)
          } else {
            setStatus("failed")
          }
        }
    )
  }

  return (
      <main className="p-6 space-y-4 bg-gray-100 min-h-screen">
        <Script src="https://cdn.iamport.kr/v1/iamport.js" strategy="afterInteractive" />

        <h1 className="text-xl font-semibold">구독 결제 테스트 플로우</h1>

        <button
            disabled={loading}
            onClick={handlePayment}
            className="bg-gray-800 text-white px-6 py-3 rounded-lg"
        >
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

        {/* 상태 오버레이 */}
        <PaymentStatusOverlay open={open} status={status} onClose={() => setOpen(false)} />
      </main>
  )
}
