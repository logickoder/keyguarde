import { ChevronDown } from 'lucide-react';
import SectionLabel from './SectionLabel';

// The same answers as Help in the app, so the two never disagree. Ordered for someone deciding
// whether to install: trust first, troubleshooting last.
const faqs = [
  {
    question: 'Does it read my messages?',
    answer:
      'Only the text of notifications from the apps you pick, as they arrive. It can’t open your chats, see older messages or read media.'
  },
  {
    question: 'Where does my data go?',
    answer:
      'Messages, matches and keywords stay on your phone, and they’re left out of Android backups. Keyguarde sends usage stats, crash reports and performance data to Google Firebase, and shows Google AdMob ads. The privacy policy lists exactly what.'
  },
  {
    question: 'Will it drain my battery?',
    answer: 'No. It does nothing between notifications, and each check is a short text search.'
  },
  {
    question: 'Why does Keyguarde need notification access?',
    answer:
      'It checks each new notification for your keywords. Without access, Android doesn’t show it any.'
  },
  {
    question: 'How do matches work?',
    answer:
      'Whole words, any case. “rent” matches “Rent due” but not “current”. One message can match several keywords.'
  },
  {
    question: 'Can I pick which apps it watches?',
    answer: 'Yes. Open Settings, then Apps. Chat apps on your phone are listed first.'
  },
  {
    question: 'How do I add or remove keywords?',
    answer:
      'Open the Keywords tab. Type a word at the top to add it. Tap a keyword to edit it, or swipe it left to delete it.'
  },
  {
    question: 'Why am I not getting matches?',
    answer:
      'Open Settings and tap Run a test. If the test fails, turn Keyguarde’s notification access off, then on. Also check the app is ticked under Apps, and Battery use says Unrestricted.'
  }
];

export default function Faq() {
  return (
    <section id="faq" className="mx-auto max-w-3xl scroll-mt-20 px-4 py-20 md:px-6">
      <SectionLabel number="04">Questions</SectionLabel>
      <h2 className="mt-3 text-3xl font-bold tracking-tight text-ink md:text-4xl">
        Before you install
      </h2>
      <div className="mt-10 divide-y divide-line border-y border-line">
        {faqs.map((faq) => (
          <details key={faq.question} className="group">
            <summary className="flex cursor-pointer list-none items-center justify-between gap-4 py-5 text-lg font-medium text-ink [&::-webkit-details-marker]:hidden">
              {faq.question}
              <ChevronDown
                aria-hidden="true"
                className="h-5 w-5 shrink-0 text-ink-muted transition-transform group-open:rotate-180"
              />
            </summary>
            <p className="pb-5 leading-relaxed text-ink-muted">{faq.answer}</p>
          </details>
        ))}
      </div>
    </section>
  );
}
