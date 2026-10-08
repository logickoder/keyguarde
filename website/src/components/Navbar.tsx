import { Link } from 'react-router-dom';
import Logo from '../assets/logo.svg';
import useSmoothScroll from '../hooks/useSmoothScroll';
import { playStoreUrl } from './home/PlayBadge';

const sections = [
  { id: 'how-it-works', label: 'How it works' },
  { id: 'privacy', label: 'Privacy' },
  { id: 'faq', label: 'FAQ' }
];

export default function Navbar() {
  const scrollTo = useSmoothScroll('/');

  return (
    <header className="sticky top-0 z-10 border-b border-line bg-paper/90 backdrop-blur">
      <nav className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 md:px-6">
        <Link to="/" className="flex items-center gap-2 text-ink">
          <img src={Logo} alt="" className="h-7 w-7" width={28} height={28} />
          <span className="text-lg font-bold">Keyguarde</span>
        </Link>
        <div className="hidden gap-8 md:flex">
          {sections.map((section) => (
            <button
              key={section.id}
              type="button"
              onClick={() => scrollTo(section.id)}
              className="inline-flex min-h-11 items-center text-sm text-ink-muted transition-colors hover:text-ink"
            >
              {section.label}
            </button>
          ))}
        </div>
        <a
          href={playStoreUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex min-h-11 items-center rounded-full border border-ink px-4 text-sm font-semibold text-ink transition-colors hover:bg-ink hover:text-paper"
        >
          Get the app
        </a>
      </nav>
    </header>
  );
}
