"use client";

export default function Order({ onResult }) {
  const createOrder = async () => {
    const res = await fetch("http://localhost:8080/api/orders", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        memberId: 1,
        addrId: 1,
        status: "PENDING"
      })
    });
    const data = await res.json();
    onResult(data.response);
  };

  return (
      <button
          onClick={createOrder}
          className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800 transition"
      >
        주문 생성
      </button>
  );
}
