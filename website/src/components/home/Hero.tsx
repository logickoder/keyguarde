import { useEffect, useState } from 'react';
import Keyword from './Keyword';
import NotificationCard from './NotificationCard';
import PhoneFrame from './PhoneFrame';
import ScreenShot from './ScreenShot';
import SectionLabel from './SectionLabel';
import PlayBadge from './PlayBadge';
import matchesLight from '../../assets/app/matches-light.webp';
import matchesDark from '../../assets/app/matches-dark.webp';

// All take "an", so the sentence reads right whichever word is showing.
const heroWords = ['invoice', 'interview', 'offer', 'order'];
const heroWordMillis = 2400;

interface HeroProps {
  onHowItWorks: () => void;
}

export default function Hero({ onHowItWorks }: HeroProps) {
  const word = useRotatingWord(heroWords, heroWordMillis);

  return (
    <section className="mx-auto grid max-w-6xl items-center gap-16 px-4 pt-12 pb-20 md:px-6 lg:grid-cols-[1.1fr_1fr] lg:pt-20">
      <div>
        <SectionLabel>Android app · Free</SectionLabel>
        <h1 className="mt-4 text-5xl leading-[1.05] font-bold tracking-tight text-ink md:text-6xl">
          Never miss an <Keyword>{word}</Keyword> again
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-relaxed text-ink-muted">
          Keyguarde watches your chat notifications for the words you pick, and keeps every message
          that has one. Everything else stays quiet.
        </p>
        <div className="mt-8 flex flex-wrap items-center gap-6">
          <PlayBadge />
          <button
            type="button"
            onClick={onHowItWorks}
            className="font-semibold text-ink underline decoration-line-strong underline-offset-4 hover:decoration-ink"
          >
            How it works
          </button>
        </div>
        <p className="mt-6 text-sm text-ink-muted">Your messages never leave your phone.</p>
      </div>

      <div className="relative mx-auto w-full max-w-sm">
        <PhoneFrame className="mx-auto w-64 md:w-72">
          <ScreenShot
            light={matchesLight}
            dark={matchesDark}
            alt="Keyguarde's Matches screen: messages from WhatsApp, Telegram and Messages, each with its keyword highlighted"
            className="h-auto w-full"
          />
        </PhoneFrame>
        <NotificationCard
          app="WhatsApp"
          chat="Landlord"
          before="Reminder: "
          keyword="rent"
          after=" is due on Friday."
          className="absolute top-28 -left-32 hidden lg:block"
        />
        <NotificationCard
          app="Telegram"
          chat="Hiring team"
          before="Your "
          keyword="interview"
          after=" is moved to 10am tomorrow."
          className="absolute -right-20 bottom-14 hidden lg:block"
        />
      </div>
    </section>
  );
}

/**
 * Cycles through [words], or stays on the first one when the visitor asks for reduced motion.
 */
function useRotatingWord(words: string[], intervalMillis: number): string {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    const timer = window.setInterval(() => setIndex((i) => (i + 1) % words.length), intervalMillis);
    return () => window.clearInterval(timer);
  }, [words.length, intervalMillis]);

  return words[index] ?? words[0] ?? '';
}
