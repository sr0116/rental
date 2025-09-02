"use client";

import { useState } from "react";

export default function TestPage() {
  const BASE_URL = "http://localhost:8080/api";

  const [mno] = useState(1);
  const [addresses, setAddresses] = useState([]);
  const [deliveries, setDeliveries] = useState([]);
  const [log, setLog] = useState("");

  // -------- Address API --------
  const createAddress = async () => {
    const res = await fetch(`${BASE_URL}/addresses`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        mno,
        name: "홍길동",
        tel: "010-1234-5678",
        zipcode: "06236",
        addr: "서울 강남구 테헤란로 152",
        addrDetail: "101동 1001호",
        isDefault: true,
        memo: "테스트 배송지"
      }),
    });
    setLog(JSON.stringify(await res.json(), null, 2));
  };

  const listAddresses = async () => {
    const res = await fetch(`${BASE_URL}/addresses?mno=${mno}`);
    const data = await res.json();
    setAddresses(data.data || []);
    setLog(JSON.stringify(data, null, 2));
  };

  const setDefaultAddress = async (addrno) => {
    const res = await fetch(`${BASE_URL}/addresses/${addrno}/default?mno=${mno}`, {
      method: "PATCH",
    });
    setLog(JSON.stringify(await res.json(), null, 2));
  };

  // -------- Delivery API --------
  const createDelivery = async () => {
    const res = await fetch(`${BASE_URL}/deliveries`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        orderId: 123,
        subscribeId: null,
        mno,
        addrno: 1,
        pno: 1,
        memo: "경비실에 맡겨주세요",
        carrierCode: "CJ"
      }),
    });
    setLog(JSON.stringify(await res.json(), null, 2));
  };

  const listDeliveries = async () => {
    const res = await fetch(`${BASE_URL}/deliveries?mno=${mno}`);
    const data = await res.json();
    setDeliveries(data.data || []);
    setLog(JSON.stringify(data, null, 2));
  };

  const updateStatus = async (dno, status) => {
    const res = await fetch(`${BASE_URL}/deliveries/${dno}/status`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status }),
    });
    setLog(JSON.stringify(await res.json(), null, 2));
  };

  return (
      <div className="p-6 space-y-6">
        <h1 className="text-2xl font-bold"> API 통합 테스트</h1>

        {/* Address Section */}
        <div className="p-4 border rounded-lg shadow space-y-4">
          <h2 className="text-xl font-semibold"> 배송지(Address)</h2>
          <div className="space-x-2">
            <button
                onClick={createAddress}
                className="px-3 py-1 bg-blue-600 text-white rounded"
            >
              배송지 등록
            </button>
            <button
                onClick={listAddresses}
                className="px-3 py-1 bg-green-600 text-white rounded"
            >
              배송지 목록 조회
            </button>
          </div>

          {addresses.length > 0 && (
              <table className="w-full text-sm border mt-4">
                <thead className="bg-gray-100">
                <tr>
                  <th className="border px-2 py-1">ID</th>
                  <th className="border px-2 py-1">이름</th>
                  <th className="border px-2 py-1">주소</th>
                  <th className="border px-2 py-1">기본여부</th>
                  <th className="border px-2 py-1">액션</th>
                </tr>
                </thead>
                <tbody>
                {addresses.map((a) => (
                    <tr key={a.addrno}>
                      <td className="border px-2 py-1">{a.addrno}</td>
                      <td className="border px-2 py-1">{a.name}</td>
                      <td className="border px-2 py-1">{a.addr}</td>
                      <td className="border px-2 py-1">
                        {a.isDefault ? "✔️" : ""}
                      </td>
                      <td className="border px-2 py-1">
                        <button
                            onClick={() => setDefaultAddress(a.addrno)}
                            className="px-2 py-1 bg-yellow-500 text-white rounded"
                        >
                          기본 지정
                        </button>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
          )}
        </div>

        {/* Delivery Section */}
        <div className="p-4 border rounded-lg shadow space-y-4">
          <h2 className="text-xl font-semibold">배송(Delivery)</h2>
          <div className="space-x-2">
            <button
                onClick={createDelivery}
                className="px-3 py-1 bg-blue-600 text-white rounded"
            >
              배송 생성
            </button>
            <button
                onClick={listDeliveries}
                className="px-3 py-1 bg-green-600 text-white rounded"
            >
              배송 목록 조회
            </button>
          </div>

          {deliveries.length > 0 && (
              <table className="w-full text-sm border mt-4">
                <thead className="bg-gray-100">
                <tr>
                  <th className="border px-2 py-1">DNO</th>
                  <th className="border px-2 py-1">상품</th>
                  <th className="border px-2 py-1">상태</th>
                  <th className="border px-2 py-1">액션</th>
                </tr>
                </thead>
                <tbody>
                {deliveries.map((d) => (
                    <tr key={d.dno}>
                      <td className="border px-2 py-1">{d.dno}</td>
                      <td className="border px-2 py-1">{d.pno}</td>
                      <td className="border px-2 py-1">{d.status}</td>
                      <td className="border px-2 py-1 space-x-1">
                        <button
                            onClick={() => updateStatus(d.dno, "PREPARING")}
                            className="px-2 py-1 bg-yellow-500 text-white rounded"
                        >
                          PREPARING
                        </button>
                        <button
                            onClick={() => updateStatus(d.dno, "SHIPPING")}
                            className="px-2 py-1 bg-orange-500 text-white rounded"
                        >
                          SHIPPING
                        </button>
                        <button
                            onClick={() => updateStatus(d.dno, "DELIVERED")}
                            className="px-2 py-1 bg-purple-600 text-white rounded"
                        >
                          DELIVERED
                        </button>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
          )}
        </div>

        {/* Log Section */}
        <div className="p-4 border rounded-lg shadow">
          <h2 className="text-xl font-semibold"> 응답 로그</h2>
          <pre className="bg-gray-100 p-2 text-xs overflow-x-auto">
          {log}
        </pre>
        </div>
      </div>
  );
}
