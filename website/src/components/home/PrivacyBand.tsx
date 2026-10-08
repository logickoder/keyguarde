import { Link } from 'react-router-dom';
import SectionLabel from './SectionLabel';

const stays = ['The messages it catches', 'Your keywords', 'The apps you watch', 'Your settings'];
const leaves = [
  'Usage stats and crash reports, to Google Firebase',
  'App start and load times, to Google Firebase',
  'Ad requests with your advertising ID, to Google AdMob'
];

/** The one dark band on the page. Privacy is the claim people check, so it gets the contrast. */
export default function PrivacyBand() {
  return (
    <section id="privacy" className="scroll-mt-20 border-y border-line bg-band text-on-band">
      <div className="mx-auto max-w-6xl px-4 py-20 md:px-6">
        <SectionLabel number="02" onBand>
          Privacy
        </SectionLabel>
        <h2 className="mt-3 max-w-2xl text-3xl font-bold tracking-tight md:text-4xl">
          Your messages stay on your phone.
        </h2>
        <p className="mt-4 max-w-2xl leading-relaxed text-on-band-muted">
          Keyguarde reads notifications on the phone and keeps matches there. No server, no account,
          and matches are left out of Android backups. Here’s the full list.
        </p>
        <div className="mt-12 grid gap-10 md:grid-cols-2">
          <PrivacyList title="Stays on your phone" items={stays} />
          <PrivacyList title="Leaves your phone" items={leaves} />
        </div>
        <Link
          to="/privacy-policy"
          className="mt-10 inline-block font-semibold underline decoration-on-band-muted underline-offset-4 hover:decoration-on-band"
        >
          Read the privacy policy
        </Link>
      </div>
    </section>
  );
}

interface PrivacyListProps {
  title: string;
  items: string[];
}

function PrivacyList({ title, items }: PrivacyListProps) {
  return (
    <div>
      <h3 className="font-semibold">{title}</h3>
      <ul className="mt-4 divide-y divide-white/10 border-y border-white/10">
        {items.map((item) => (
          <li key={item} className="py-3 text-on-band-muted">
            {item}
          </li>
        ))}
      </ul>
    </div>
  );
}
