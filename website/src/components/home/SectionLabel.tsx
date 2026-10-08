interface SectionLabelProps {
  number?: string;
  children: string;
  /** On the dark privacy band, where the usual muted grey would be too dark. */
  onBand?: boolean;
}

/** The numbered small-caps label above each section, in the app's keyword-pill face. */
export default function SectionLabel({ number, children, onBand = false }: SectionLabelProps) {
  return (
    <p
      className={`font-label text-sm font-medium tracking-[0.12em] uppercase ${onBand ? 'text-on-band-muted' : 'text-ink-muted'}`}
    >
      {number ? `${number} · ${children}` : children}
    </p>
  );
}
