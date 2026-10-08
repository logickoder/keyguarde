import PlayBadge from './PlayBadge';

export default function DownloadBand() {
  return (
    <section id="download" className="scroll-mt-20 border-t border-line bg-surface">
      <div className="mx-auto flex max-w-6xl flex-col items-start gap-6 px-4 py-16 md:px-6">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-ink md:text-3xl">
            Set it up in a minute.
          </h2>
          <p className="mt-2 text-ink-muted">
            Free on Android. Pick a few words, tick your chat apps, done.
          </p>
        </div>
        <PlayBadge placement="download_band" />
      </div>
    </section>
  );
}
