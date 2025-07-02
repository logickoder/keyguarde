export default function PrivacyPolicyPage() {
  return (
    <div className="bg-background min-h-screen font-sans text-on-background">
      {/* Header */}
      <section className="py-16 bg-gradient-to-br from-primary-container to-secondary-container">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-4xl font-bold mb-4 text-on-background">Privacy Policy</h1>
          <p className="text-lg text-muted max-w-2xl mx-auto">
            Your privacy is our priority. Learn how Keyguarde protects your data.
          </p>
        </div>
      </section>

      {/* Content */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="max-w-4xl mx-auto">
            <div className="bg-surface p-10 rounded-2xl shadow-soft border border-gray-100">
              <article className="prose prose-lg max-w-none prose-headings:text-on-surface prose-p:text-muted prose-li:text-muted prose-strong:text-on-surface">
                <p className="text-sm text-muted mb-8">
                  <strong>Last Updated: May 6, 2025</strong>
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Introduction
                </h2>
                <div className="bg-primary-container p-6 rounded-xl mb-8">
                  <p className="text-on-surface mb-4">
                    Keyguarde is committed to protecting your privacy. This Privacy Policy explains
                    how I handle information when you use the Keyguarde application.
                  </p>
                  <p className="text-on-surface">
                    Keyguarde is designed with privacy as a core principle. The app processes
                    notifications locally on your device to detect user-defined keywords without
                    transmitting or storing your data externally.
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Information I Process
                </h2>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Notification Content</h3>
                <p className="mb-4">
                  Keyguarde processes your device&#39;s notification content locally to detect
                  keyword matches. This includes:
                </p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>
                    Text content of notifications from apps you&#39;ve selected for monitoring
                  </li>
                  <li>Notification source information (app name, chat name)</li>
                </ul>
                <div className="bg-success-container p-4 rounded-xl mb-6">
                  <p className="text-on-surface">
                    <strong>Important</strong>: All processing happens locally on your device. No
                    notification content is transmitted to any server or third party.
                  </p>
                </div>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">User Settings</h3>
                <p className="mb-4">
                  The App stores the following information locally on your device:
                </p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>Your list of keywords</li>
                  <li>Your selected apps for monitoring</li>
                  <li>Your app preferences and settings</li>
                </ul>
                <p className="mb-8">
                  This information never leaves your device unless you manually export it (if
                  available in future versions).
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  How I Use Information
                </h2>
                <p className="mb-4">I use notification content solely to:</p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>Detect and alert you to keyword matches in real-time</li>
                  <li>Display match counts in the persistent notification</li>
                </ul>
                <p className="mb-8">
                  All matching is done in real-time. No notification history or content is stored
                  beyond temporary system caches managed by Android.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Data Storage and Retention
                </h2>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Local Storage</h3>
                <p className="mb-4">Keyguarde stores minimal data locally on your device:</p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>App settings and preferences</li>
                  <li>Your keyword list</li>
                  <li>Selected apps for monitoring</li>
                </ul>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">No Remote Storage</h3>
                <div className="bg-secondary-container p-6 rounded-xl mb-8">
                  <p className="text-on-surface">
                    Keyguarde does not store any data on remote servers. All your data remains on
                    your device at all times.
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Data Sharing
                </h2>
                <div className="bg-success-container p-6 rounded-xl mb-8">
                  <p className="text-on-surface">
                    <strong>
                      I do not share, sell, or transmit your data to any third parties.
                    </strong>{' '}
                    Your notification content and personal information never leave your device.
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Permissions
                </h2>
                <p className="mb-4">Keyguarde requires the following permissions:</p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>
                    <strong>Notification Access</strong>: To read notification content for keyword
                    matching
                  </li>
                  <li>
                    <strong>Post Notifications</strong>: To display alerts when keywords are matched
                  </li>
                </ul>
                <p className="mb-8">
                  These permissions are used solely for the app&#39;s core functionality and no data
                  is transmitted externally.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Your Rights
                </h2>
                <p className="mb-4">
                  Since all data is stored locally on your device, you have complete control:
                </p>
                <ul className="list-disc pl-6 mb-8 space-y-2">
                  <li>You can uninstall the app at any time to remove all data</li>
                  <li>You can clear app data through Android settings</li>
                  <li>You can modify or delete your keywords and settings within the app</li>
                </ul>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Contact Information
                </h2>
                <p className="mb-4">
                  If you have any questions about this Privacy Policy, please contact me at:
                </p>
                <div className="bg-surface p-4 rounded-xl border border-gray-200 mb-8">
                  <p className="text-on-surface">
                    <strong>Email:</strong> jefferyorazulike@gmail.com
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Changes to This Policy
                </h2>
                <p className="mb-8">
                  I may update this Privacy Policy from time to time. Any changes will be posted on
                  this page with an updated revision date.
                </p>
              </article>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
