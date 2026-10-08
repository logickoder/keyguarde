import SectionLabel from './SectionLabel';

const people = [
  {
    who: 'Job seekers',
    body: 'Recruiter messages get buried in group chats. Catch them the minute they land.',
    words: ['interview', 'offer', 'shortlisted']
  },
  {
    who: 'Traders',
    body: 'Busy signal groups move fast. Keep only the calls you act on.',
    words: ['buy', 'sell', 'BTC']
  },
  {
    who: 'Anyone in loud groups',
    body: 'Mute the chatter without missing the one message meant for you.',
    words: ['your name', 'urgent', 'meeting']
  }
];

export default function WhoItsFor() {
  return (
    <section className="mx-auto max-w-6xl px-4 py-20 md:px-6">
      <SectionLabel number="03">Who it’s for</SectionLabel>
      <h2 className="mt-3 max-w-2xl text-3xl font-bold tracking-tight text-ink md:text-4xl">
        Anyone with a chat that’s too loud to read.
      </h2>
      <div className="mt-12 grid gap-6 md:grid-cols-3">
        {people.map((person) => (
          <div key={person.who} className="rounded-2xl border border-line p-6">
            <h3 className="text-xl font-semibold text-ink">{person.who}</h3>
            <p className="mt-2 leading-relaxed text-ink-muted">{person.body}</p>
            <ul aria-label="Example keywords" className="mt-5 flex flex-wrap gap-2">
              {person.words.map((word) => (
                <li
                  key={word}
                  className="rounded-full bg-surface-high px-3 py-1 font-label text-sm text-accent"
                >
                  {word}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </section>
  );
}
