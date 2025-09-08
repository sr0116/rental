"use client"
import Lottie from "lottie-react"
import processing from "@/../public/lottie/payment-processing.json"

export default function Loading() {
  return (
      <div className="fixed inset-0 flex items-center justify-center bg-black/80">
        <div className="w-48 h-48">
          <Lottie animationData={processing} loop />
        </div>
      </div>
  )
}
