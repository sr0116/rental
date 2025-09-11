"use client";

import { useEffect, useState } from "react";

export default function PaymentList() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const API_BASE = "http://localhost:8080/api/payments"; // 백엔드 결제 조회 API

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const res = await fetch(API_BASE, { method: "GET" });

        if (!res.ok) {
          console.error("결제 리스트 요청 실패:", res.status);
          return;
        }

        const result = await res.json();
        console.log("결제 리스트 응답:", result);

        if (result.ok && result.data) {
          setPayments(result.data);
        } else {
          console.error("결제 리스트 조회 실패:", result.message);
        }
      } catch (err) {
        console.error("결제 리스트 요청 에러:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchPayments();
  }, []);

  if (loading) return <p className="p-4">불러오는 중...</p>;

  return (
      <div className="p-6">
        <h2 className="text-xl font-semibold mb-4">결제 내역 리스트</h2>
        {payments.length === 0 ? (
            <p>결제 내역이 없습니다.</p>
        ) : (
            <table className="w-full border-collapse border text-sm">
              <thead>
              <tr className="bg-gray-100">
                <th className="border p-2">ID</th>
                <th className="border p-2">회원</th>
                <th className="border p-2">주문</th>
                <th className="border p-2">Merchant UID</th>
                <th className="border p-2">Imp UID</th>
                <th className="border p-2">금액</th>
                <th className="border p-2">상태</th>
                <th className="border p-2">등록일</th>
              </tr>
              </thead>
              <tbody>
              {payments.map((p) => (
                  <tr key={p.paymentId} className="text-center">
                    <td className="border p-2">{p.paymentId}</td>
                    <td className="border p-2">{p.memberId}</td>
                    <td className="border p-2">{p.orderId}</td>
                    <td className="border p-2">{p.merchantUid}</td>
                    <td className="border p-2">{p.impUid || "-"}</td>
                    <td className="border p-2">{p.amount}</td>
                    <td className="border p-2">{p.payStatus}</td>
                    <td className="border p-2">{p.regdate}</td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}
