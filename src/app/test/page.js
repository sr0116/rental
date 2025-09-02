"use client";

import { useState, useEffect } from "react";
import Script from "next/script";

export default function AddressPage() {
  const BASE_URL = "http://localhost:8080/api";
  const [mno] = useState(1); // 테스트용 회원 ID

  // 입력 폼 상태
  const [name, setName] = useState("");
  const [tel, setTel] = useState("");
  const [zipcode, setZipcode] = useState("");
  const [addr, setAddr] = useState("");
  const [addrDetail, setAddrDetail] = useState("");
  const [memo, setMemo] = useState("");
  const [isDefault, setIsDefault] = useState(false);

  // 리스트 상태
  const [addresses, setAddresses] = useState([]);

  // 주소 검색 (카카오 API)
  const openDaumPostcode = () => {
    new window.daum.Postcode({
      oncomplete: (data) => {
        setZipcode(data.zonecode);
        setAddr(data.roadAddress || data.jibunAddress);
        document.getElementById("addrDetail").focus();
      },
    }).open();
  };

  // 배송지 등록
  const createAddress = async (e) => {
    e.preventDefault();
    const res = await fetch(`${BASE_URL}/addresses`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        mno,
        name,
        tel,
        zipcode,
        addr,
        addrDetail,
        isDefault,
        memo,
      }),
    });
    if (res.ok) {
      await listAddresses();
      resetForm();
    }
  };

  // 배송지 목록 조회
  const listAddresses = async () => {
    const res = await fetch(`${BASE_URL}/addresses?mno=${mno}`);
    const data = await res.json();
    setAddresses(data.data || []);
  };

  // 기본 배송지 설정
  const setDefaultAddress = async (addrno) => {
    await fetch(`${BASE_URL}/addresses/${addrno}/default?mno=${mno}`, {
      method: "PATCH",
    });
    await listAddresses();
  };

  // 폼 초기화
  const resetForm = () => {
    setName("");
    setTel("");
    setZipcode("");
    setAddr("");
    setAddrDetail("");
    setMemo("");
    setIsDefault(false);
  };

  useEffect(() => {
    listAddresses();
  }, []);

  return (
      <div className="p-6 space-y-8">
        {/* 카카오 주소검색 API 스크립트 */}
        <Script
            src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"
            strategy="afterInteractive"
        />

        <h1 className="text-2xl font-bold">배송지 관리</h1>

        {/* 배송지 등록 폼 */}
        <form
            onSubmit={createAddress}
            className="space-y-3 p-4 border rounded shadow"
        >
          <div>
            <label className="block mb-1 font-medium">수령인</label>
            <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                className="border px-2 py-1 w-full"
            />
          </div>
          <div>
            <label className="block mb-1 font-medium">연락처</label>
            <input
                value={tel}
                onChange={(e) => setTel(e.target.value)}
                required
                className="border px-2 py-1 w-full"
            />
          </div>
          <div className="flex space-x-2 items-center">
            <div className="flex-1">
              <label className="block mb-1 font-medium">우편번호</label>
              <input
                  value={zipcode}
                  readOnly
                  required
                  className="border px-2 py-1 w-full"
              />
            </div>
            <button
                type="button"
                onClick={openDaumPostcode}
                className="mt-6 px-3 py-1 bg-blue-600 text-white rounded"
            >
              우편번호 검색
            </button>
          </div>
          <div>
            <label className="block mb-1 font-medium">도로명 주소</label>
            <input
                value={addr}
                readOnly
                required
                className="border px-2 py-1 w-full"
            />
          </div>
          <div>
            <label className="block mb-1 font-medium">상세 주소</label>
            <input
                id="addrDetail"
                value={addrDetail}
                onChange={(e) => setAddrDetail(e.target.value)}
                className="border px-2 py-1 w-full"
            />
          </div>
          <div>
            <label className="block mb-1 font-medium">배송 메모</label>
            <input
                value={memo}
                onChange={(e) => setMemo(e.target.value)}
                className="border px-2 py-1 w-full"
            />
          </div>
          <div className="flex items-center space-x-2">
            <input
                type="checkbox"
                checked={isDefault}
                onChange={(e) => setIsDefault(e.target.checked)}
            />
            <span>기본 배송지로 설정</span>
          </div>
          <button
              type="submit"
              className="px-4 py-2 bg-green-600 text-white rounded"
          >
            배송지 등록
          </button>
        </form>

        {/* 배송지 목록 */}
        <div className="p-4 border rounded shadow">
          <h2 className="text-xl font-semibold mb-3">등록된 배송지</h2>
          {addresses.length === 0 ? (
              <p>등록된 배송지가 없습니다.</p>
          ) : (
              <table className="w-full text-sm border">
                <thead className="bg-gray-100">
                <tr>
                  <th className="border px-2 py-1">ID</th>
                  <th className="border px-2 py-1">수령인</th>
                  <th className="border px-2 py-1">주소</th>
                  <th className="border px-2 py-1">연락처</th>
                  <th className="border px-2 py-1">기본 여부</th>
                  <th className="border px-2 py-1">액션</th>
                </tr>
                </thead>
                <tbody>
                {addresses.map((a) => (
                    <tr key={a.addrno}>
                      <td className="border px-2 py-1">{a.addrno}</td>
                      <td className="border px-2 py-1">{a.name}</td>
                      <td className="border px-2 py-1">
                        {a.addr} {a.addrDetail}
                      </td>
                      <td className="border px-2 py-1">{a.tel}</td>
                      <td className="border px-2 py-1">
                        {a.isDefault ? "기본" : ""}
                      </td>
                      <td className="border px-2 py-1">
                        {!a.isDefault && (
                            <button
                                onClick={() => setDefaultAddress(a.addrno)}
                                className="px-2 py-1 bg-yellow-500 text-white rounded"
                            >
                              기본 지정
                            </button>
                        )}
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
          )}
        </div>
      </div>
  );
}
