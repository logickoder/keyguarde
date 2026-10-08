import playBadge from '../../assets/play-store.png';

export const playStoreUrl =
  'https://play.google.com/store/apps/details?id=dev.logickoder.keyguarde';

/** The one download action, used in the hero and the closing band. */
export default function PlayBadge() {
  return (
    <a href={playStoreUrl} target="_blank" rel="noopener noreferrer" className="inline-block">
      <img
        src={playBadge}
        alt="Get it on Google Play"
        className="h-12 w-auto"
        width={162}
        height={48}
      />
    </a>
  );
}
