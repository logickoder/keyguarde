export default function TermsOfUsePage() {
  return (
    <div className="bg-background min-h-screen font-sans text-on-background">
      {/* Header */}
      <section className="py-16 bg-gradient-to-br from-primary-container to-secondary-container">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-4xl font-bold mb-4 text-on-background">Terms of Use</h1>
          <p className="text-lg text-muted max-w-2xl mx-auto">
            Please read these terms carefully before using Keyguarde.
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
                  Agreement to Terms
                </h2>
                <div className="bg-primary-container p-6 rounded-xl mb-8">
                  <p className="text-on-surface">
                    By downloading, installing, or using the Keyguarde application ("App"), you
                    agree to be bound by these Terms of Use ("Terms"). If you do not agree to these
                    Terms, do not use the App.
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Description of Service
                </h2>
                <p className="mb-4">Keyguarde is a notification monitoring application that:</p>
                <ul className="list-disc pl-6 mb-8 space-y-2">
                  <li>
                    Listens to notifications from selected apps (primarily WhatsApp and Telegram)
                  </li>
                  <li>Alerts users when messages contain user-defined keywords</li>
                  <li>Operates locally on your device with privacy as a priority</li>
                </ul>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  User Responsibilities
                </h2>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Account and Security</h3>
                <p className="mb-4">You are responsible for:</p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>Maintaining the confidentiality of your device</li>
                  <li>All activities that occur through your device while using the App</li>
                  <li>
                    Ensuring you have proper authorization to monitor notifications on your device
                  </li>
                </ul>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">
                  Prohibited Activities
                </h3>
                <div className="bg-danger-container p-6 rounded-xl mb-6">
                  <p className="text-on-surface mb-4">
                    <strong>When using the App, you agree NOT to:</strong>
                  </p>
                  <ul className="list-disc pl-6 space-y-2 text-on-surface">
                    <li>Use the App for any illegal purpose</li>
                    <li>Attempt to reverse engineer, decompile, or disassemble the App</li>
                    <li>Use the App to infringe on others' privacy</li>
                    <li>
                      Use the App to monitor communications without proper consent where required by
                      law
                    </li>
                    <li>
                      Remove any copyright, trademark, or other proprietary notices from the App
                    </li>
                    <li>Transfer, distribute, or sublicense the App to any third party</li>
                  </ul>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Intellectual Property
                </h2>
                <p className="mb-4">
                  The App, including its code, graphics, user interface, and content, is owned by me
                  and is protected by copyright, trademark, and other intellectual property laws.
                </p>
                <p className="mb-8">
                  You are granted a limited, non-exclusive, non-transferable license to use the App
                  for personal, non-commercial purposes.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  In-App Purchases and Subscriptions
                </h2>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">
                  Free Version with Ads
                </h3>
                <p className="mb-6">The free version of the App includes advertisements.</p>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Premium Version</h3>
                <div className="bg-secondary-container p-6 rounded-xl mb-6">
                  <p className="text-on-surface">
                    A one-time purchase is available to remove advertisements permanently.
                  </p>
                </div>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Refund Policy</h3>
                <p className="mb-8">
                  Refunds are handled according to the Google Play Store refund policy.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Privacy and Data Protection
                </h2>
                <div className="bg-success-container p-6 rounded-xl mb-8">
                  <p className="text-on-surface">
                    Your privacy is important. Please review the Privacy Policy to understand how
                    your information is handled. All data processing occurs locally on your device.
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Disclaimers and Limitations
                </h2>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">Service Availability</h3>
                <p className="mb-6">
                  I strive to keep the App available at all times, but I cannot guarantee
                  uninterrupted service due to factors beyond my control, including device
                  limitations and Android system changes.
                </p>

                <h3 className="text-xl font-semibold mb-4 text-on-surface">
                  Limitation of Liability
                </h3>
                <p className="mb-8">
                  The App is provided "as is" without warranties of any kind. I shall not be liable
                  for any damages arising from the use or inability to use the App.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Termination
                </h2>
                <p className="mb-4">
                  These Terms remain in effect until terminated by either party. You may terminate
                  by:
                </p>
                <ul className="list-disc pl-6 mb-6 space-y-2">
                  <li>Uninstalling the App from your device</li>
                  <li>Discontinuing use of the App</li>
                </ul>
                <p className="mb-8">I may terminate your access if you violate these Terms.</p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Changes to Terms
                </h2>
                <p className="mb-8">
                  I may update these Terms from time to time. Continued use of the App after changes
                  constitutes acceptance of the new Terms.
                </p>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-secondary rounded-full mr-4"></div>
                  Contact Information
                </h2>
                <p className="mb-4">
                  If you have questions about these Terms, please contact me at:
                </p>
                <div className="bg-surface p-4 rounded-xl border border-gray-200 mb-8">
                  <p className="text-on-surface">
                    <strong>Email:</strong> jefferyorazulike@gmail.com
                  </p>
                </div>

                <h2 className="text-2xl font-bold mb-6 text-on-surface flex items-center">
                  <div className="w-1 h-8 bg-primary rounded-full mr-4"></div>
                  Governing Law
                </h2>
                <p className="mb-8">
                  These Terms are governed by applicable laws. Any disputes will be resolved in
                  accordance with local jurisdiction requirements.
                </p>
              </article>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
