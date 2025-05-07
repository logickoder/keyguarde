export default function PrivacyPolicyPage() {
    return (
        <div className="max-w-3xl mx-auto px-4 py-12 text-gray-800">
            <article className="prose prose-headings:font-semibold prose-h1:text-3xl prose-h2:text-2xl prose-h3:text-xl prose-p:leading-relaxed">
                <h1 id="keyguarde-privacy-policy">Keyguarde Privacy Policy</h1>
                <p><strong>Last Updated: May 6, 2025</strong></p>

                <h2 id="introduction">Introduction</h2>
                <p>
                    Keyguarde ("we," "our," or "us") is committed to protecting your privacy.
                    This Privacy Policy explains how we handle information when you use our Keyguarde application ("App").
                </p>
                <p>
                    Keyguarde is designed with privacy as a core principle.
                    Our app processes notifications locally on your device to detect user-defined keywords without transmitting or storing your data externally.
                </p>

                <h2 id="information-we-process">Information We Process</h2>

                <h3 id="notification-content">Notification Content</h3>
                <p>Keyguarde processes your device's notification content locally to detect keyword matches. This includes:</p>
                <ul className="list-disc pl-6">
                    <li>Text content of notifications from apps you've selected for monitoring</li>
                    <li>Notification source information (app name, chat name)</li>
                </ul>
                <p><strong>Important</strong>: All processing happens locally on your device. No notification content is transmitted to any server or third party.</p>

                <h3 id="user-settings">User Settings</h3>
                <p>The App stores the following information locally on your device:</p>
                <ul className="list-disc pl-6">
                    <li>Your list of keywords</li>
                    <li>Your selected apps for monitoring</li>
                    <li>Your app preferences and settings</li>
                </ul>
                <p>This information never leaves your device unless you manually export it (if available in future versions).</p>

                <h2 id="how-we-use-information">How We Use Information</h2>
                <p>We use notification content solely to:</p>
                <ul className="list-disc pl-6">
                    <li>Detect and alert you to keyword matches in real-time</li>
                    <li>Display match counts in the persistent notification</li>
                </ul>
                <p>All matching is done in real-time. No notification history or content is stored beyond temporary system caches managed by Android.</p>

                <h2 id="data-storage-and-retention">Data Storage and Retention</h2>

                <h3 id="local-storage">Local Storage</h3>
                <p>Keyguarde stores minimal data locally on your device:</p>
                <ul className="list-disc pl-6">
                    <li>App settings and preferences</li>
                    <li>Your keyword list</li>
                    <li>Selected apps for monitoring</li>
                    <li>Match counter (reset when you open the app)</li>
                </ul>

                <h3 id="external-storage">External Storage</h3>
                <p><strong>We do not store or transmit any of your data externally.</strong> There are:</p>
                <ul className="list-disc pl-6">
                    <li>No remote servers storing your data</li>
                    <li>No cloud backups of your settings or matches</li>
                    <li>No analytics collecting your notification data</li>
                </ul>

                <h2 id="data-sharing">Data Sharing</h2>

                <h3 id="third-party-sharing">Third-Party Sharing</h3>
                <p>We do not share your data with any third parties. Since your data never leaves your device, it cannot be shared.</p>

                <h3 id="service-providers">Service Providers</h3>
                <p>
                    Keyguarde does not use any service providers that would have access to your notification data.
                </p>
                <p>
                    If you use the ad-supported version of the app, our ad provider may collect standard device information and identifiers as described in their privacy policy, but they never receive access to your notification content or keywords.
                </p>

                <h2 id="permissions">Permissions</h2>

                <h3 id="notification-access">Notification Access</h3>
                <p>
                    Required to read incoming notifications and detect keywords. This permission allows the app to access notification content as they appear on your device.
                </p>

                <h3 id="post-notifications">Post Notifications</h3>
                <p>
                    Required to display the persistent notification showing match counts and to alert you when matches occur.
                </p>

                <h2 id="your-rights">Your Rights</h2>
                <p>You can control your data by:</p>
                <ul className="list-disc pl-6">
                    <li>Uninstalling the app (removes all local data)</li>
                    <li>Disabling notification access in your device settings</li>
                    <li>Managing your keywords and app selection within the app</li>
                </ul>

                <h2 id="children-s-privacy">Children's Privacy</h2>
                <p>
                    Keyguarde is not intended for children under 13. We do not knowingly collect personal information from children under 13.
                </p>

                <h2 id="changes-to-this-privacy-policy">Changes to This Privacy Policy</h2>
                <p>
                    We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the "Last Updated" date.
                </p>

                <h2 id="contact-us">Contact Us</h2>
                <p>If you have any questions about this Privacy Policy, please contact us:</p>
                <ul className="list-disc pl-6">
                    <li>Email: <a href="mailto:jefferyorazulike@gmail.com">jefferyorazulike@gmail.com</a></li>
                    <li>GitHub: <a href="https://github.com/logickoder/keyguarde">github.com/logickoder/keyguarde</a></li>
                </ul>

                <hr className="my-6" />

                <p className="italic">
                    By using Keyguarde, you agree to the collection and use of information in accordance with this Privacy Policy.
                </p>
            </article>
        </div>
    );
}
