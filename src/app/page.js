"use client"

import { useState } from "react"
import MotionWrapper from "@/components/MotionWrapper"
import PaymentPage from "@/components/PaymentPage"
import RefundPage from "@/components/Refund"

export default function Home() {
  const [view, setView] = useState("payment") // payment | refund

  return (
      <MotionWrapper>
        <div className="max-w-4xl mx-auto px-4 py-12 space-y-8">
          <h1 className="text-3xl font-bold">
            메인 홈 화면 <span className="text-pink-500">Demo</span>
          </h1>
          <p className="text-gray-600">
            아래 버튼으로 결제 / 환불 테스트 컴포넌트를 전환</p>
          <button> 결제 <PaymentPage /> </button>

        </div>
      </MotionWrapper>
  )
}
