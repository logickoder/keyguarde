import { useSyncExternalStore } from 'react';

/** The visitor's answer to the cookie banner; null until they answer. */
export type Consent = 'granted' | 'denied' | null;

const STORAGE_KEY = 'keyguarde-analytics-consent';

const listeners = new Set<() => void>();

// Storage can throw (private windows, blocked site data); treat that as "not answered yet".
function readStored(): Consent {
  try {
    const value = window.localStorage.getItem(STORAGE_KEY);
    return value === 'granted' || value === 'denied' ? value : null;
  } catch {
    return null;
  }
}

let current: Consent = readStored();

export function getConsent(): Consent {
  return current;
}

/** Saves the answer, or clears it with null so the banner asks again. */
export function setConsent(consent: Consent): void {
  current = consent;
  try {
    if (consent === null) window.localStorage.removeItem(STORAGE_KEY);
    else window.localStorage.setItem(STORAGE_KEY, consent);
  } catch {
    // Not saved: the answer holds for this visit, and the banner asks again next time.
  }
  listeners.forEach((listener) => listener());
}

export function subscribeConsent(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function useConsent(): Consent {
  return useSyncExternalStore(subscribeConsent, getConsent, () => null);
}
