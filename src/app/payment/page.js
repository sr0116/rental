"use client";

import { useState } from "react";
import Script from "next/script";
import Order from "../components/orders/Order";
import SubscribeCreate from "../components/subscribe/Create";
import SubscribeStatus from "../components/subscribe/Status";
import PaymentFlow from "../components/payments/PaymentFlow";

export default function PaymentPage() {
  const [logs, setLogs] = useState([]);

  const addLog = (title, data) => {
    setLogs((prev) => [...prev, { title, data }]);
  };

  return (
      <div className="p-6 max-w-6xl mx-auto">
        {/* PortOne SDK 로드 */}
        <Script
            src="https://cdn.iamport.kr/v1/iamport.js"
            strategy="afterInteractive"
        />

        <h1 className="text-2xl font-bold mb-6 text-gray-800">구독 결제 테스트 플로우</h1>

        <div className="grid grid-cols-4 gap-4 mb-8">
          <Order onResult={(data) => addLog("주문 생성", data)} />
          <PaymentFlow onResult={(title, data) => addLog(title, data)} />
          <SubscribeCreate onResult={(data) => addLog("구독 생성", data)} />
          <SubscribeStatus onResult={(data) => addLog("구독 상태 변경", data)} />
        </div>

        <h2 className="text-xl font-semibold mb-4 text-gray-700">실행 로그</h2>
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 space-y-4">
          {logs.length === 0 && <p className="text-gray-500">아직 실행된 API 없음</p>}
          {logs.map((log, idx) => (
              <div key={idx} className="p-3 border border-gray-300 rounded bg-white shadow-sm">
                <div className="font-medium text-gray-700 mb-2">{log.title}</div>
                <pre className="text-sm text-gray-600 whitespace-pre-wrap">
              {JSON.stringify(log.data, null, 2)}
            </pre>
              </div>
          ))}
        </div>
      </div>
  );
}
