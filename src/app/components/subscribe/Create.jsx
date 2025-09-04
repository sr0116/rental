"use client";

export default function SubscribeCreate({ onResult }) {
  const createSubscribe = async () => {
    const res = await fetch("http://localhost:8080/api/subscribe", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        orderId: 1,
        memberId: 1,
        status: "WAITING",
        depositSnapshot: 100000,
        monthlyFeeSnapshot: 30000
      })
    });
    const data = await res.json();
    onResult(data.response);
  };

  return (
      <button
          onClick={createSubscribe}
          className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800 transition"
      >
        구독 생성
      </button>
  );
}
