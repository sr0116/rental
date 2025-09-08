"use client"

import { useState, useCallback } from "react"
import Script from "next/script"
import PaymentStatusOverlay from "@/components/PaymentStatusOverlay"

export default function PayButton({ amount = 1000, merchantUid, apiBase }) {
  const [open, setOpen] = useState(false)
  const [status, setStatus] = useState("processing") // processing | success | failed | refunded

  const requestPay = useCallback(() => {
    setOpen(true)
    setStatus("processing")

    if (!window.IMP) {
      console.error("PortOne SDK not loaded")
      setStatus("failed")
      return
    }
    const IMP = window.IMP
    IMP.init(process.env.NEXT_PUBLIC_MERCHANT_CODE) // 예: "imp00000000"

    IMP.request_pay(
        {
          pg: "nice_v2",             // 테스트 모드
          pay_method: "card",
          merchant_uid: merchantUid, // /prepare에서 받은 값 권장
          name: "테스트 결제",
          amount,
          buyer_email: "test@imchobo.com",
          buyer_name: "Tester",
        },
        async (rsp) => {
          try {
            const { imp_uid } = rsp || {} // rsp.success 믿지 말고 imp_uid만 확인
            if (!imp_uid) {
              setStatus("failed")
              return
            }

            const res = await fetch(`${apiBase}/payment/complete`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              credentials: "include",
              body: JSON.stringify({ impUid: imp_uid, merchantUid }),
            })
            const data = await res.json()

            if (res.ok && data?.ok) setStatus("success")
            else setStatus("failed")
          } catch (e) {
            console.error(e)
            setStatus("failed")
          }
        }
    )
  }, [amount, merchantUid, apiBase])

  return (
      <>
        {/* PortOne SDK는 afterInteractive로 로드 (팀 규칙) */}
        <Script src="https://cdn.iamport.kr/v1/iamport.js" strategy="afterInteractive" />
        <button
            onClick={requestPay}
            className="px-5 py-3 rounded-2xl bg-pink-600 hover:bg-pink-500 text-white font-semibold transition"
        >
          결제하기
        </button>

        <PaymentStatusOverlay open={open} status={status} onClose={() => setOpen(false)} />
      </>
  )
}
