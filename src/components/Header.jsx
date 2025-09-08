"use client"

import { useEffect, useState } from "react"
import { motion } from "framer-motion"

export default function Header() {
  const [scrolled, setScrolled] = useState(false)
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 4)
    onScroll()
    window.addEventListener("scroll", onScroll)
    return () => window.removeEventListener("scroll", onScroll)
  }, [])

  return (
      <motion.header
          initial={false}
          animate={{ backgroundColor: scrolled ? "rgba(0,0,0,0.7)" : "rgba(0,0,0,0)" }}
          className={`fixed top-0 left-0 w-full z-50 backdrop-blur-md border-b 
        ${scrolled ? "border-white/10" : "border-transparent"}`}
      >
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <a href="/public" className="font-bold text-xl">
            <span className="text-pink-500">SAYREN</span>
          </a>
          <nav className="hidden sm:flex items-center gap-6">
            <a href="/public" className="hover:text-pink-400">Home</a>
            <a href="/payment" className="hover:text-pink-400">Payment</a>
            <a href="#features" className="hover:text-pink-400">Features</a>
          </nav>
        </div>
      </motion.header>
  )
}
