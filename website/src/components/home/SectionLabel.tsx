interface SectionLabelProps {
  number?: string;
  children: string;
  /** On the dark privacy band, where the usual muted grey would be too dark. */
  onBand?: boolean;
}

/**
 * The numbered label above each section. Body face on purpose: the condensed face means "a word
 * you picked" everywhere else, so it stays on keywords only.
 */
export default function SectionLabel({ number, children, onBand = false }: SectionLabelProps) {
  return (
    <p
      className={`text-xs font-semibold tracking-[0.14em] uppercase ${onBand ? 'text-on-band-muted' : 'text-ink-muted'}`}
    >
      {number ? `${number} · ${children}` : children}
    </p>
  );
}
