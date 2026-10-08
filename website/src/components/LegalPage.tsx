import type { ReactNode } from 'react';

interface LegalPageProps {
  title: string;
  summary: string;
  updated: string;
  children: ReactNode;
}

/** Plain reading layout for the legal pages: one column, the app's greys, nothing to decorate. */
export default function LegalPage({ title, summary, updated, children }: LegalPageProps) {
  return (
    <main className="mx-auto max-w-3xl px-4 py-16 md:px-6">
      <p className="text-xs font-semibold tracking-[0.14em] text-ink-muted uppercase">
        Last updated · {updated}
      </p>
      <h1 className="mt-3 text-4xl font-bold tracking-tight text-ink md:text-5xl">{title}</h1>
      <p className="mt-4 text-lg leading-relaxed text-ink-muted">{summary}</p>
      <article className="prose legal mt-12 max-w-none">{children}</article>
    </main>
  );
}
