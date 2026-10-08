import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { trackEvent } from './analytics';
import { useConsent } from './consent';

/**
 * Logs a page view on each route change, and for the current page once cookies are accepted.
 * Renders nothing.
 */
export default function PageViews(): null {
  const { pathname } = useLocation();
  const consent = useConsent();

  useEffect(() => {
    trackEvent('page_view', { page_path: pathname, page_location: window.location.href });
  }, [pathname, consent]);

  return null;
}
