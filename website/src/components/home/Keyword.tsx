interface KeywordProps {
  children: string;
}

/** A matched word, in teal like the app. The only place the accent colour appears. */
export default function Keyword({ children }: KeywordProps) {
  return <span className="font-semibold text-accent">{children}</span>;
}
