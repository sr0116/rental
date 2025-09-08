"use client";

export default function Prepare({ onResult }) {
  const preparePayment = async () => {
    const res = await fetch("http://localhost:8080/api/payments/prepare", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        orderId: 1,
        memberId: 1,
        amount: 100000
      })
    });
    const data = await res.json();
    onResult(data.response);
  };

  return (
      <button
          onClick={preparePayment}
          className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800 transition"
      >
        결제 준비
      </button>
  );
}
