import "./globals.css"
import Header from "@/components/Header"
import Footer from "@/components/Footer"

export const metadata = {
  title: "Biz-Style Layout",
  description: "Black/White + Magenta theme",
}

export default function RootLayout({ children }) {
  return (
      <html lang="ko">
      <body className="bg-black text-white selection:bg-pink-500/30 selection:text-white">
      <Header />
      <main className="pt-16 min-h-[calc(100vh-128px)]">
        {children}
      </main>
      <Footer />
      </body>
      </html>
  )
}
