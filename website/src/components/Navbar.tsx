import { Link } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
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
          <ReactSVG src={Logo} className="h-6 w-6" />
          <span className="text-lg font-bold">Keyguarde</span>
        </Link>
        <div className="hidden gap-8 md:flex">
          {sections.map((section) => (
            <button
              key={section.id}
              type="button"
              onClick={() => scrollTo(section.id)}
              className="text-sm text-ink-muted transition-colors hover:text-ink"
            >
              {section.label}
            </button>
          ))}
        </div>
        <a
          href={playStoreUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="rounded-full bg-ink px-4 py-2 text-sm font-semibold text-paper transition-opacity hover:opacity-90"
        >
          Get the app
        </a>
      </nav>
    </header>
  );
}
