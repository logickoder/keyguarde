import ScreenShot from './ScreenShot';
import SectionLabel from './SectionLabel';
import keywordsLight from '../../assets/app/keywords-light.webp';
import keywordsDark from '../../assets/app/keywords-dark.webp';
import matchesLight from '../../assets/app/matches-light.webp';
import matchesDark from '../../assets/app/matches-dark.webp';
import settingsLight from '../../assets/app/settings-light.webp';
import settingsDark from '../../assets/app/settings-dark.webp';

const steps = [
  {
    title: 'Pick your words',
    body: 'Type the words you can’t miss: a name, “invoice”, “interview”. Whole words, any case. Then tick the chat apps to watch.',
    light: keywordsLight,
    dark: keywordsDark,
    alt: 'The Keywords tab: ten keywords, each showing when it last matched'
  },
  {
    title: 'Matches collect in one list',
    body: 'Every message with a keyword lands in Matches, with the word lit up. Tap one to read it in full or open the chat.',
    light: matchesLight,
    dark: matchesDark,
    alt: 'The Matches tab: recent messages with their keywords highlighted'
  },
  {
    title: 'See that it’s working',
    body: 'Settings shows whether Keyguarde is listening and how much it has caught, and runs a live test when you want proof.',
    light: settingsLight,
    dark: settingsDark,
    alt: 'Settings: a card reading Listening, caught 287 messages so far, with Pause and Run a test'
  }
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="mx-auto max-w-6xl scroll-mt-20 px-4 py-20 md:px-6">
      <SectionLabel number="01">How it works</SectionLabel>
      <h2 className="mt-3 max-w-2xl text-3xl font-bold tracking-tight text-ink md:text-4xl">
        Three steps. Then it stays out of the way.
      </h2>
      <ol className="mt-12 grid gap-6 md:grid-cols-3">
        {steps.map((step, index) => (
          <li
            key={step.title}
            className="flex flex-col overflow-hidden rounded-2xl border border-line bg-surface"
          >
            <div className="p-6">
              <p className="text-sm font-semibold text-ink-muted tabular-nums">
                {String(index + 1).padStart(2, '0')}
              </p>
              <h3 className="mt-2 text-xl font-semibold text-ink">{step.title}</h3>
              <p className="mt-2 leading-relaxed text-ink-muted">{step.body}</p>
            </div>
            {/* The top of each screen is the part that tells the story; the rest fades out. */}
            <div className="relative mt-auto h-72 overflow-hidden px-6">
              <div className="overflow-hidden rounded-t-2xl border border-b-0 border-line shadow-soft">
                <ScreenShot
                  light={step.light}
                  dark={step.dark}
                  alt={step.alt}
                  className="h-auto w-full"
                />
              </div>
              <div
                aria-hidden="true"
                className="absolute inset-x-0 bottom-0 h-16 bg-linear-to-t from-surface to-transparent"
              />
            </div>
          </li>
        ))}
      </ol>
    </section>
  );
}
