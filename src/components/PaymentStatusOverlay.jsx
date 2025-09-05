"use client"

import { AnimatePresence, motion } from "framer-motion"
import { useEffect, useState } from "react"
import Lottie from "lottie-react"

const PATHS = {
  processing: "/lottie/payment-processing.json",
  success: "/lottie/payment-success.json",
  failed: "/lottie/payment-failed.json",
  refunded: "/lottie/payment-refunded.json",
}

export default function PaymentStatusOverlay({ open, status = "processing", onClose }) {
  const [ani, setAni] = useState(null)

  useEffect(() => {
    if (!open) return
    fetch(PATHS[status])
        .then((r) => r.json())
        .then(setAni)
        .catch(console.error)
  }, [open, status])

  return (
      <AnimatePresence>
        {open && (
            <motion.div
                className="fixed inset-0 z-[70] flex items-center justify-center bg-black/80"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
            >
              <motion.div
                  className="w-[min(92vw,520px)] rounded-2xl border border-white/10 bg-neutral-950/70 backdrop-blur-xl p-6"
                  initial={{ scale: 0.95, opacity: 0, y: 20 }}
                  animate={{ scale: 1, opacity: 1, y: 0 }}
                  exit={{ scale: 0.98, opacity: 0, y: -20 }}
              >
                <div className="flex flex-col items-center gap-4">
                  <div className="w-48 h-48">{ani && <Lottie animationData={ani} loop={status==="processing"} />}</div>
                  <h3 className="text-2xl font-semibold text-pink-500">
                    {status === "processing" && "결제 중..."}
                    {status === "success" && "결제 완료!"}
                    {status === "failed" && "결제 실패"}
                    {status === "refunded" && "환불 완료"}
                  </h3>
                  {status !== "processing" && (
                      <button
                          onClick={onClose}
                          className="mt-2 px-4 py-2 rounded-xl border border-pink-500/50 text-pink-400 hover:bg-pink-500/10 transition"
                      >
                        확인
                      </button>
                  )}
                </div>
              </motion.div>
            </motion.div>
        )}
      </AnimatePresence>
  )
}
