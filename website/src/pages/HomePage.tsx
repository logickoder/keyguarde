import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import DownloadBand from '../components/home/DownloadBand';
import Faq from '../components/home/Faq';
import Hero from '../components/home/Hero';
import HowItWorks from '../components/home/HowItWorks';
import PrivacyBand from '../components/home/PrivacyBand';
import WhoItsFor from '../components/home/WhoItsFor';
import useSmoothScroll from '../hooks/useSmoothScroll';

export default function HomePage() {
  const location = useLocation();
  const scrollTo = useSmoothScroll('/');

  // Arriving from another page with a section in mind, such as the nav's FAQ link.
  useEffect(() => {
    const sectionId: unknown = location.state?.scrollTo;
    if (typeof sectionId === 'string') {
      document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth' });
    }
  }, [location.state]);

  return (
    <main>
      <Hero onHowItWorks={() => scrollTo('how-it-works')} />
      <HowItWorks />
      <PrivacyBand />
      <WhoItsFor />
      <Faq />
      <DownloadBand />
    </main>
  );
}
