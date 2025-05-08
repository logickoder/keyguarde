import { BrowserRouter, Route, Routes, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import HomePage from './pages/HomePage';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import PrivacyPolicyPage from './pages/PrivacyPolicyPage';
import TermsOfUsePage from './pages/TermsOfUsePage';
import ContactPage from './pages/ContactPage';

export default function App() {
    return (
        <div className="bg-background min-h-screen font-sans text-primary">
            <BrowserRouter basename='/keyguarde'>
                <ScrollToTop />
                <Navbar />
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
                    <Route path="/terms" element={<TermsOfUsePage />} />
                    <Route path="/contact" element={<ContactPage />} />
                </Routes>
                <Footer />
            </BrowserRouter>
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