import Keyword from './Keyword';

interface NotificationCardProps {
  app: string;
  chat: string;
  before: string;
  keyword: string;
  after: string;
  className?: string;
}

/** A chat notification as Keyguarde sees it, with the matched word lit up. */
export default function NotificationCard({
  app,
  chat,
  before,
  keyword,
  after,
  className
}: NotificationCardProps) {
  return (
    <div
      className={`w-56 rounded-2xl border border-line bg-paper p-4 shadow-medium ${className ?? ''}`}
    >
      <div className="flex items-center gap-2 text-xs text-ink-muted">
        <span className="flex h-5 w-5 items-center justify-center rounded-full bg-surface-high text-[10px] font-bold text-ink">
          {app.charAt(0)}
        </span>
        <span>
          {app} · {chat}
        </span>
      </div>
      <p className="mt-2 text-sm leading-snug text-ink">
        {before}
        <Keyword>{keyword}</Keyword>
        {after}
      </p>
    </div>
  );
}
