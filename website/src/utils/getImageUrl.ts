export function getImageUrl(name: string): string {
  return new URL(`../app/assets/${name}`, import.meta.url).href;
}
