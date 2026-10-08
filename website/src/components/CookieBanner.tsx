import { Link } from 'react-router-dom';
import { setConsent, useConsent } from '../analytics/consent';
import { pillButtonClass } from './styles';

/**
 * Asks before any analytics cookie is set. Accept and Decline carry equal weight, as consent
 * rules expect; the choice can be changed from Cookie settings in the footer.
 */
export default function CookieBanner() {
  const consent = useConsent();
  if (consent !== null) return null;

  const buttonClass = `${pillButtonClass} flex-1 sm:flex-none`;

  return (
    <section aria-label="Cookie choice" className="fixed inset-x-0 bottom-0 z-50 px-4 pb-4 md:px-6">
      <div className="mx-auto flex max-w-3xl flex-col gap-4 rounded-2xl border border-line bg-surface p-5 shadow-medium sm:flex-row sm:items-center">
        <p className="text-sm leading-relaxed text-ink-muted">
          Can this site use Google Analytics cookies to count visits and taps on the Play link?
          Nothing is set unless you accept.{' '}
          <Link
            to="/privacy-policy"
            className="font-semibold text-ink underline underline-offset-4"
          >
            Privacy policy
          </Link>
        </p>
        <div className="flex shrink-0 gap-3">
          <button type="button" className={buttonClass} onClick={() => setConsent('denied')}>
            Decline
          </button>
          <button type="button" className={buttonClass} onClick={() => setConsent('granted')}>
            Accept
          </button>
        </div>
      </div>
    </section>
  );
}
