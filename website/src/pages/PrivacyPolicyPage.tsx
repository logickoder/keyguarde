import LegalPage from '../components/LegalPage';

const googleServices = [
  {
    name: 'Firebase Analytics',
    sends:
      'App opens, screens viewed, which features you use (such as pausing, filtering or running the setup test), device model, Android version, country and an app instance ID. Never message text, chat names or keywords.',
    policy: 'https://firebase.google.com/support/privacy'
  },
  {
    name: 'Firebase Crashlytics',
    sends: 'What broke when the app crashes, with device model and Android version.',
    policy: 'https://firebase.google.com/support/privacy'
  },
  {
    name: 'Firebase Performance Monitoring',
    sends: 'How long the app takes to start and to load screens.',
    policy: 'https://firebase.google.com/support/privacy'
  },
  {
    name: 'Google AdMob',
    sends:
      'Ad requests for the banner in the free version, using your advertising ID. You can reset or delete the ID in Android settings, under Privacy, then Ads.',
    policy: 'https://policies.google.com/technologies/ads'
  }
];

export default function PrivacyPolicyPage() {
  return (
    <LegalPage
      title="Privacy policy"
      summary="What Keyguarde keeps on your phone, what leaves it, and how to remove it."
      updated="October 8, 2026"
    >
      <h2>Summary</h2>
      <p>
        Keyguarde reads notifications from the apps you pick, checks them for your keywords, and
        keeps the messages that match on your phone. Your messages, matches and keywords never leave
        your phone. Keyguarde has no server and no account.
      </p>
      <p>
        The app uses Google services for usage statistics, crash reports, performance data and ads.
        None of them receive your messages, matches or keywords.
      </p>

      <h2>What stays on your phone</h2>
      <ul>
        <li>Your keywords</li>
        <li>The apps you chose to watch</li>
        <li>
          Matched messages: the message text, the chat name, the app it came from and the time
        </li>
        <li>Chat pictures from the notifications of matched messages</li>
        <li>Your settings</li>
      </ul>
      <p>
        Keyguarde reads notifications as they arrive. It can&#39;t open your chats, see older
        messages or read media. Notifications from apps you didn&#39;t pick, and messages without a
        keyword, are ignored and not stored.
      </p>
      <p>
        None of this is included in Android backups or phone-to-phone transfers. A new phone starts
        with a fresh setup.
      </p>

      <h2>What leaves your phone</h2>
      <p>
        Keyguarde uses these Google services. Each one sends the data below to Google, under
        Google&#39;s own privacy terms.
      </p>
      <ul>
        {googleServices.map((service) => (
          <li key={service.name}>
            <strong>{service.name}</strong>: {service.sends}{' '}
            <a href={service.policy} target="_blank" rel="noopener noreferrer">
              Google&#39;s policy
            </a>
          </li>
        ))}
      </ul>
      <p>
        I don&#39;t sell your data, and I don&#39;t share it with anyone other than the Google
        services above.
      </p>

      <h2>Permissions</h2>
      <ul>
        <li>
          <strong>Notification access</strong>: to read new notifications from the apps you pick and
          check them for your keywords.
        </li>
        <li>
          <strong>Notifications</strong>: to alert you when a message matches.
        </li>
        <li>
          <strong>Ignore battery optimizations</strong>: to ask Android not to pause Keyguarde while
          your phone is idle.
        </li>
        <li>
          <strong>Internet and advertising ID</strong>: for the Google services listed above.
        </li>
      </ul>

      <h2>How long data is kept, and how to remove it</h2>
      <ul>
        <li>
          Matched messages stay on your phone until you delete them in Matches, or clear them all.
        </li>
        <li>You can edit or delete keywords and watched apps at any time.</li>
        <li>
          Uninstalling Keyguarde, or clearing its storage in Android settings, removes everything it
          stored on your phone.
        </li>
        <li>Data sent to Google is kept under Google&#39;s retention terms, linked above.</li>
      </ul>

      <h2>Ads</h2>
      <p>
        The free version shows a banner ad from Google AdMob. Where the law requires consent, such
        as in the EEA and the UK, Keyguarde shows Google&#39;s consent message first and requests no
        ad until you answer. Change your answer in Settings, then Privacy, then Ad privacy choices.
        A one-time purchase to remove ads is planned. This policy will be updated before it ships.
      </p>

      <h2>This website</h2>
      <p>
        If you accept cookies in the banner, this website uses Google Analytics, through Firebase,
        to count visits, pages viewed and taps on the Google Play and contact links. It records your
        browser, device type and country, and sets cookies to tell repeat visits apart. If you
        decline, none of this loads. Change your answer at any time with Cookie settings at the
        bottom of every page.{' '}
        <a
          href="https://policies.google.com/technologies/partner-sites"
          target="_blank"
          rel="noopener noreferrer"
        >
          Google&#39;s policy
        </a>
      </p>

      <h2>Contact</h2>
      <p>
        Questions about this policy:{' '}
        <a href="mailto:jeffery@logickoder.dev">jeffery@logickoder.dev</a>
      </p>

      <h2>Changes to this policy</h2>
      <p>When this policy changes, the new version is posted here with a new date.</p>
    </LegalPage>
  );
}
