import { Mail } from 'lucide-react';
import { ReactSVG } from 'react-svg';
import { Link } from 'react-router-dom';
import Logo from '../assets/logo.svg';
import Github from '../assets/github.svg';
import Twitter from '../assets/x.svg';
import Linkedin from '../assets/linkedin.svg';
import { trackEvent } from '../analytics/analytics';
import { setConsent } from '../analytics/consent';

const socials = [
  { href: 'https://github.com/logickoder/keyguarde', label: 'Keyguarde on GitHub', icon: Github },
  { href: 'https://x.com/logickoder', label: 'logickoder on X', icon: Twitter },
  { href: 'https://linkedin.com/in/logickoder', label: 'logickoder on LinkedIn', icon: Linkedin }
];

export default function Footer() {
  const linkClass =
    'inline-flex min-h-11 items-center text-sm text-ink-muted transition-colors hover:text-ink';

  return (
    <footer className="border-t border-line bg-paper">
      <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-10 md:flex-row md:items-center md:justify-between md:px-6">
        <Link to="/" className="flex items-center gap-2 text-ink">
          <img src={Logo} alt="" className="h-6 w-6" width={24} height={24} />
          <span className="font-bold">Keyguarde</span>
        </Link>
        <div className="flex flex-wrap gap-6">
          <Link to="/privacy-policy" className={linkClass}>
            Privacy policy
          </Link>
          <Link to="/terms" className={linkClass}>
            Terms
          </Link>
          <a
            href="mailto:jeffery@logickoder.dev"
            className={linkClass}
            onClick={() => trackEvent('contact_click', { placement: 'footer_link' })}
          >
            Contact
          </a>
          {/* Withdraws any earlier answer and asks again. */}
          <button type="button" className={linkClass} onClick={() => setConsent(null)}>
            Cookie settings
          </button>
        </div>
        <div className="-mx-3 flex items-center text-ink-muted">
          {socials.map((social) => (
            <a
              key={social.href}
              href={social.href}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={social.label}
              className="inline-flex h-11 w-11 items-center justify-center transition-colors hover:text-ink"
            >
              <ReactSVG src={social.icon} className="h-5 w-5" />
            </a>
          ))}
          <a
            href="mailto:jeffery@logickoder.dev"
            aria-label="Email jeffery@logickoder.dev"
            onClick={() => trackEvent('contact_click', { placement: 'footer_icon' })}
            className="inline-flex h-11 w-11 items-center justify-center transition-colors hover:text-ink"
          >
            <Mail size={20} aria-hidden="true" />
          </a>
        </div>
      </div>
      <p className="pb-8 text-center text-xs text-ink-muted">
        © {new Date().getFullYear()} <a href="https://logickoder.dev">Jeffery Orazulike</a>
      </p>
    </footer>
  );
}
