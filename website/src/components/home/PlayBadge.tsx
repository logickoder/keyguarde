import type { ReactNode } from 'react';
import playBadge from '../../assets/play-store.png';
import { trackEvent } from '../../analytics/analytics';

const playStoreUrl = 'https://play.google.com/store/apps/details?id=dev.logickoder.keyguarde';

interface PlayStoreLinkProps {
  /** Where the link sits, for analytics: "hero", "download_band" or "navbar". */
  placement: string;
  className?: string;
  children: ReactNode;
}

/** Opens the Play Store listing in a new tab and counts the tap. */
export function PlayStoreLink({ placement, className, children }: PlayStoreLinkProps) {
  return (
    <a
      href={playStoreUrl}
      target="_blank"
      rel="noopener noreferrer"
      className={className}
      onClick={() => trackEvent('play_store_click', { placement })}
    >
      {children}
    </a>
  );
}

/** The one download action, used in the hero and the closing band. */
export default function PlayBadge({ placement }: Pick<PlayStoreLinkProps, 'placement'>) {
  return (
    <PlayStoreLink placement={placement} className="inline-block">
      <img
        src={playBadge}
        alt="Get it on Google Play"
        className="h-12 w-auto"
        width={162}
        height={48}
      />
    </PlayStoreLink>
  );
}
