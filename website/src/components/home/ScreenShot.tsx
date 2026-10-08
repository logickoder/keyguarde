interface ScreenShotProps {
  light: string;
  dark: string;
  alt: string;
  className?: string;
  /** Above the fold: load right away instead of when scrolled near. */
  priority?: boolean;
}

/** An app screenshot that follows the visitor's light or dark setting, like the app does. */
export default function ScreenShot({
  light,
  dark,
  alt,
  className,
  priority = false
}: ScreenShotProps) {
  return (
    <picture>
      <source srcSet={dark} media="(prefers-color-scheme: dark)" />
      <img
        src={light}
        alt={alt}
        loading={priority ? 'eager' : 'lazy'}
        fetchPriority={priority ? 'high' : 'auto'}
        className={className}
        width={540}
        height={1139}
      />
    </picture>
  );
}
