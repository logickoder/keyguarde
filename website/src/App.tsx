import { HashRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import HomePage from './pages/HomePage';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import PrivacyPolicyPage from './pages/PrivacyPolicyPage';
import TermsOfUsePage from './pages/TermsOfUsePage';

export default function App() {
  return (
    <div className="min-h-screen bg-paper font-sans text-ink">
      <HashRouter>
        <ScrollToTop />
        <Navbar />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
          <Route path="/terms" element={<TermsOfUsePage />} />
          {/* Contact moved to the footer; old links land on the home page. */}
          <Route path="/contact" element={<Navigate to="/" replace />} />
        </Routes>
        <Footer />
      </HashRouter>
    </div>
  );
}

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    const canControlScrollRestoration = 'scrollRestoration' in window.history;
    if (canControlScrollRestoration) {
      window.history.scrollRestoration = 'manual';
    }

    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
}
