"use client";

export default function SubscribeStatus({ onResult }) {
  const updateStatus = async () => {
    const res = await fetch("http://localhost:8080/api/subscribe/1/status?status=ACTIVE", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" }
    });
    const data = await res.json();
    onResult(data.response);
  };

  return (
      <button
          onClick={updateStatus}
          className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800 transition"
      >
        구독 활성화
      </button>
  );
}
