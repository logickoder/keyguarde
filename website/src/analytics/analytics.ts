import type { Analytics, logEvent, setAnalyticsCollectionEnabled } from 'firebase/analytics';
import { getConsent, subscribeConsent } from './consent';

type EventParams = Record<string, string | number>;

interface Loaded {
  instance: Analytics;
  logEvent: typeof logEvent;
  setAnalyticsCollectionEnabled: typeof setAnalyticsCollectionEnabled;
}

// The Firebase web config is public by design; it identifies the project, it doesn't grant access.
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
  measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID
};

let loaded: Promise<Loaded | null> | null = null;

/**
 * Loads Firebase on first use, in its own chunks, so it never delays the page. Resolves to null
 * without a config (local builds) or where analytics can't run. Only called once the visitor has
 * accepted cookies, so nothing loads and no cookie is set before that.
 */
function load(): Promise<Loaded | null> {
  if (!firebaseConfig.measurementId) return Promise.resolve(null);
  loaded ??= (async () => {
    const [{ initializeApp }, firebaseAnalytics] = await Promise.all([
      import('firebase/app'),
      import('firebase/analytics')
    ]);
    if (!(await firebaseAnalytics.isSupported())) return null;
    // Page views are logged per route (PageViews); the automatic one can't see hash routes.
    const instance = firebaseAnalytics.initializeAnalytics(initializeApp(firebaseConfig), {
      config: { send_page_view: false }
    });
    return {
      instance,
      logEvent: firebaseAnalytics.logEvent,
      setAnalyticsCollectionEnabled: firebaseAnalytics.setAnalyticsCollectionEnabled
    };
  })().catch((error: unknown) => {
    console.error('[Analytics] init failed:', error);
    return null;
  });
  return loaded;
}

/** Logs one event if the visitor accepted cookies. Fire and forget: never blocks or breaks the page. */
export function trackEvent(name: string, params?: EventParams): void {
  if (getConsent() !== 'granted') return;
  void load()
    .then((firebase) => {
      // Checked again: the visitor may have declined while Firebase was loading.
      if (firebase && getConsent() === 'granted')
        firebase.logEvent(firebase.instance, name, params);
    })
    .catch((error: unknown) => {
      console.error('[Analytics] log failed:', error);
    });
}

// Follows the visitor's answer, so whoever changes it only calls setConsent. Accepting again
// after a withdrawal turns collection back on; withdrawing turns it off and clears its cookies.
subscribeConsent(() => {
  const granted = getConsent() === 'granted';
  if (!granted) clearAnalyticsCookies();
  void loaded
    ?.then((firebase) => firebase?.setAnalyticsCollectionEnabled(firebase.instance, granted))
    .catch((error: unknown) => {
      console.error('[Analytics] consent change failed:', error);
    });
});

/** Google Analytics cookies live on this host or a parent domain, so both are cleared. */
function clearAnalyticsCookies(): void {
  const hostParts = window.location.hostname.split('.');
  const domains = hostParts
    .map((_, i) => hostParts.slice(i).join('.'))
    .filter((domain) => domain.includes('.'));
  document.cookie
    .split(';')
    .map((cookie) => cookie.split('=')[0]?.trim() ?? '')
    .filter((name) => name.startsWith('_ga'))
    .forEach((name) => {
      const expired = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
      document.cookie = expired;
      domains.forEach((domain) => {
        document.cookie = `${expired}; domain=.${domain}`;
      });
    });
}
