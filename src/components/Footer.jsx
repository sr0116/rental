export default function Footer() {
  return (
      <footer className="border-t border-white/10">
        <div className="max-w-6xl mx-auto px-4 py-6 text-sm text-white/60 flex flex-col sm:flex-row items-center justify-between gap-3">
          <p>© {new Date().getFullYear()} BIZSite. All rights reserved.</p>
          <p>
            <span className="text-white/40">Theme:</span> Black/White ·
            <span className="text-pink-500"> Magenta</span>
          </p>
        </div>
      </footer>
  )
}
