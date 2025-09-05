"use client"

import { useState } from "react"
import toast, { Toaster } from "react-hot-toast"

export default function RefundPage() {
  const API_BASE = "http://localhost:8080/api/payments"

  const [paymentId, setPaymentId] = useState("")
  const [amount, setAmount] = useState("")
  const [reason, setReason] = useState("")
  const [result, setResult] = useState(null)
  const [rawResponse, setRawResponse] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleRefund = async () => {
    if (!paymentId || !amount) {
      toast.error("결제 ID와 환불 금액을 입력하세요.")
      return
    }

    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/refund`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          paymentId: Number(paymentId), // 내부 PK
          amount: Number(amount),
          reason,
        }),
      })

      const text = await res.text()
      let data
      try {
        data = JSON.parse(text)
      } catch {
        data = { message: text }
      }

      if (!res.ok) {
        throw new Error(data.message || "환불 실패")
      }

      setResult(data)
      setRawResponse(data)
      toast.success("환불 요청 성공! (백엔드 로그에서 impUid 확인하세요)")
    } catch (err) {
      console.error(err)
      toast.error("환불 요청 실패: " + err.message)
      setRawResponse({ error: err.message })
    } finally {
      setLoading(false)
    }
  }

  return (
      <main className="p-6 space-y-6 bg-gray-100 min-h-screen">
        <Toaster position="top-right" reverseOrder={false} />

        <h1 className="text-2xl font-bold">환불 테스트</h1>

        <div className="space-y-3 bg-white p-6 rounded-lg shadow">
          <input
              type="number"
              placeholder="내부 결제 ID (paymentId)"
              value={paymentId}
              onChange={(e) => setPaymentId(e.target.value)}
              className="border p-2 rounded w-full"
          />
          <input
              type="number"
              placeholder="환불 금액"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="border p-2 rounded w-full"
          />
          <input
              type="text"
              placeholder="환불 사유"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="border p-2 rounded w-full"
          />
          <button
              onClick={handleRefund}
              disabled={loading}
              className={`w-full px-6 py-3 rounded-lg text-white ${
                  loading ? "bg-gray-400" : "bg-red-600 hover:bg-red-700"
              }`}
          >
            {loading ? "처리 중..." : "환불 요청"}
          </button>
        </div>

        {/* 환불 결과 요약 */}
        {result && result.data && (
            <div className="bg-white p-6 rounded-lg shadow">
              <h2 className="font-semibold mb-2">환불 결과</h2>
              <p>결제 ID: {result.data.paymentId}</p>
              <p>상태: {result.data.paystatus}</p>
              <p>금액: {result.data.amount}</p>
            </div>
        )}


        {/* 원본 응답 JSON */}
        {rawResponse && (
            <div className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <h3 className="font-semibold mb-2">백엔드 응답 원본</h3>
              <pre>{JSON.stringify(rawResponse, null, 2)}</pre>
            </div>
        )}
      </main>
  )
}
