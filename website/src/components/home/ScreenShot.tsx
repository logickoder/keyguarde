interface ScreenShotProps {
  light: string;
  dark: string;
  alt: string;
  className?: string;
}

/** An app screenshot that follows the visitor's light or dark setting, like the app does. */
export default function ScreenShot({ light, dark, alt, className }: ScreenShotProps) {
  return (
    <picture>
      <source srcSet={dark} media="(prefers-color-scheme: dark)" />
      <img src={light} alt={alt} loading="lazy" className={className} width={540} height={1139} />
    </picture>
  );
}
