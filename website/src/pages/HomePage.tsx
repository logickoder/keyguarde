import { useCallback, useEffect, useMemo, useState } from 'react';
import { Bell, CheckCircle, ChevronDown, ChevronUp, ExternalLink, Lock, Shield, Zap } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';
import { getImageUrl } from '../utils/getImageUrl';
import useSmoothScroll from '../hooks/useSmoothScroll';

export default function HomePage() {
    const location = useLocation();
    const [activeAccordion, setActiveAccordion] = useState(0);

    const toggleAccordion = useCallback((index: number) => {
        setActiveAccordion((prev) => prev === index ? -1 : index);
    }, []);

    const handleClick = useSmoothScroll("/");

    const privacy = useMemo(
        () => [
            'All notification processing happens locally on your device',
            'No messages or notification data is stored externally',
            'No cloud syncing of your personal data',
            'We never collect, store, or transmit your notification data',
        ],
        []
    )

    const faqs = useMemo(
        () => [
            {
                question: 'Why does it need notification access?',
                answer: 'Keyguarde needs notification access to read the content of your notifications and match them against your keywords. This permission is essential for the app to function, but all processing happens locally on your device.',
            },
            {
                question: 'Does it read my messages?',
                answer: 'Keyguarde only reads the text content of notifications as they appear. It does not access your message history, media, or any other data within your messaging apps. All processing is done locally on your device.',
            },
            {
                question: 'Will it drain my battery?',
                answer: 'Keyguarde is designed to be lightweight and battery-efficient. It only activates when new notifications arrive, and uses minimal resources while running in the background. The app has been optimized to have negligible impact on your device\'s battery life.',
            },
            {
                question: 'Can I disable alerts for certain chats?',
                answer: 'Yes! Keyguarde lets you filter notifications by chat names. You can specify which individual chats or groups to monitor, so you only receive alerts from the conversations that matter most to you.',
            },
        ],
        []
    );

    useEffect(() => {
        if (location.state?.scrollTo) {
            const sectionId = location.state.scrollTo;
            document.getElementById(sectionId)?.scrollIntoView({ behavior: "smooth" });
        }
    }, [location.state]);

    return (
        <>
            {/* Hero Section */}
            <section className="py-16 md:py-24 bg-gradient-to-b from-background to-secondary bg-opacity-10">
                <div className="container mx-auto px-4">
                    <div className="flex flex-col md:flex-row items-center">
                        <div className="md:w-1/2 mb-8 md:mb-0">
                            <h1 className="text-4xl md:text-5xl font-bold mb-4">
                                Stay Notified, Not Distracted
                            </h1>
                            <p className="text-lg md:text-xl mb-6 text-muted">
                                Keyguarde monitors your WhatsApp and Telegram notifications for important keywords, so
                                you never miss what matters.
                            </p>
                            <div className="flex flex-col sm:flex-row gap-4">
                                <button
                                    onClick={() => handleClick("download")}
                                    className="bg-primary hover:bg-opacity-90 text-white px-6 py-3 rounded-md transition-colors text-center flex items-center justify-center"
                                >
                                    <Zap size={18} className="mr-2" />
                                    Download Now
                                </button>
                                <button
                                    onClick={() => handleClick("how-it-works")}
                                    className="border border-primary text-primary hover:bg-primary hover:text-white px-6 py-3 rounded-md transition-colors text-center"
                                >
                                    Learn More
                                </button>
                            </div>
                        </div>
                        <div className="md:w-1/2 flex justify-center">
                            {/* App Screenshot/Mockup */}
                            <div
                                className="w-64 h-96 bg-surface rounded-3xl shadow-lg border border-gray-200 overflow-hidden relative">
                                <div className="bg-primary h-8 w-full"></div>
                                <div className="p-4">
                                    <img src="/api/placeholder/250/450" alt="Keyguarde App Screenshot"
                                        className="rounded-lg" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* How It Works */}
            <section id="how-it-works" className="py-16 bg-surface">
                <div className="container mx-auto px-4">
                    <h2 className="text-3xl font-bold text-center mb-12">How It Works</h2>

                    <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
                        <div className="bg-background rounded-lg p-6 text-center shadow-sm">
                            <div
                                className="bg-secondary bg-opacity-20 p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                                <Bell className="text-primary" size={24} />
                            </div>
                            <h3 className="text-xl font-bold mb-2">Listens to notifications</h3>
                            <p className="text-muted">Monitors your selected apps for new notifications.</p>
                            <div className="mt-4 h-36 bg-gray-100 rounded-lg flex items-center justify-center">
                                {/* Screenshot placeholder */}
                                <img src="/api/placeholder/150/120" alt="Notification permissions"
                                    className="rounded-lg" />
                            </div>
                        </div>

                        <div className="bg-background rounded-lg p-6 text-center shadow-sm">
                            <div
                                className="bg-secondary bg-opacity-20 p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                                <Shield className="text-primary" size={24} />
                            </div>
                            <h3 className="text-xl font-bold mb-2">Matches your keywords</h3>
                            <p className="text-muted">Checks messages for words important to you.</p>
                            <div className="mt-4 h-36 bg-gray-100 rounded-lg flex items-center justify-center">
                                {/* Screenshot placeholder */}
                                <img src="/api/placeholder/150/120" alt="Keywords matched" className="rounded-lg" />
                            </div>
                        </div>

                        <div className="bg-background rounded-lg p-6 text-center shadow-sm">
                            <div
                                className="bg-secondary bg-opacity-20 p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                                <Zap className="text-primary" size={24} />
                            </div>
                            <h3 className="text-xl font-bold mb-2">Notifies you quietly</h3>
                            <p className="text-muted">Alerts only when your keywords appear.</p>
                            <div className="mt-4 h-36 bg-gray-100 rounded-lg flex items-center justify-center">
                                {/* Screenshot placeholder */}
                                <img src="/api/placeholder/150/120" alt="Notification alert" className="rounded-lg" />
                            </div>
                        </div>

                        <div className="bg-background rounded-lg p-6 text-center shadow-sm">
                            <div
                                className="bg-secondary bg-opacity-20 p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                                <Lock className="text-primary" size={24} />
                            </div>
                            <h3 className="text-xl font-bold mb-2">All offline, no tracking</h3>
                            <p className="text-muted">Everything stays on your device.</p>
                            <div className="mt-4 h-36 bg-gray-100 rounded-lg flex items-center justify-center">
                                {/* Screenshot placeholder */}
                                <img src="/api/placeholder/150/120" alt="Privacy features" className="rounded-lg" />
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Who It's For */}
            <section className="py-16 bg-background">
                <div className="container mx-auto px-4">
                    <h2 className="text-3xl font-bold text-center mb-12">Who It's For</h2>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <div className="bg-surface rounded-lg p-6 shadow-sm">
                            <h3 className="text-xl font-bold mb-4">Job Seekers</h3>
                            <p className="text-muted">Never miss that important interview invitation or job offer. Set
                                up keywords like "position," "interview," or company names.</p>
                        </div>

                        <div className="bg-surface rounded-lg p-6 shadow-sm">
                            <h3 className="text-xl font-bold mb-4">Market Traders</h3>
                            <p className="text-muted">Stay on top of market updates in busy group chats. Set keywords
                                for stock symbols, "buy," "sell," or specific market events.</p>
                        </div>

                        <div className="bg-surface rounded-lg p-6 shadow-sm">
                            <h3 className="text-xl font-bold mb-4">Busy Group Members</h3>
                            <p className="text-muted">Filter through noisy group chats to find messages that matter. Set
                                keywords for your name or important topics.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Features */}
            <section id="features" className="py-16 bg-surface">
                <div className="container mx-auto px-4">
                    <h2 className="text-3xl font-bold text-center mb-12">Why Keyguarde?</h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div className="flex items-start">
                            <div className="mr-4 mt-1">
                                <CheckCircle className="text-success" size={20} />
                            </div>
                            <div>
                                <h3 className="text-xl font-bold mb-2">Works Offline</h3>
                                <p className="text-muted">Everything happens on your device with no internet connection
                                    required.</p>
                            </div>
                        </div>

                        <div className="flex items-start">
                            <div className="mr-4 mt-1">
                                <CheckCircle className="text-success" size={20} />
                            </div>
                            <div>
                                <h3 className="text-xl font-bold mb-2">Battery Efficient</h3>
                                <p className="text-muted">Designed to consume minimal resources while running in the
                                    background.</p>
                            </div>
                        </div>

                        {/* <div className="flex items-start">
                            <div className="mr-4 mt-1">
                                <CheckCircle className="text-success" size={20}/>
                            </div>
                            <div>
                                <h3 className="text-xl font-bold mb-2">Chat-Specific Filters</h3>
                                <p className="text-muted">Monitor only the chats that matter most to you.</p>
                            </div>
                        </div>

                        <div className="flex items-start">
                            <div className="mr-4 mt-1">
                                <CheckCircle className="text-success" size={20}/>
                            </div>
                            <div>
                                <h3 className="text-xl font-bold mb-2">Customizable Alerts</h3>
                                <p className="text-muted">Tailor notifications to match your personal preferences.</p>
                            </div>
                        </div> */}
                    </div>

                    <div className="mt-12 bg-background rounded-xl p-6 shadow-sm">
                        <div className="flex flex-col md:flex-row items-center">
                            <div className="md:w-1/2 mb-6 md:mb-0">
                                <h3 className="text-2xl font-bold mb-4">All Features</h3>
                                <ul className="space-y-3">
                                    <li className="flex items-start">
                                        <CheckCircle className="text-success mr-2 mt-1 flex-shrink-0" size={16} />
                                        <span>Monitor notifications from WhatsApp & Telegram</span>
                                    </li>
                                    <li className="flex items-start">
                                        <CheckCircle className="text-success mr-2 mt-1 flex-shrink-0" size={16} />
                                        <span>Custom keyword matching</span>
                                    </li>
                                    <li className="flex items-start">
                                        <CheckCircle className="text-success mr-2 mt-1 flex-shrink-0" size={16} />
                                        <span>Select which apps to monitor</span>
                                    </li>
                                    <li className="flex items-start">
                                        <CheckCircle className="text-success mr-2 mt-1 flex-shrink-0" size={16} />
                                        <span>Persistent but silent notifications</span>
                                    </li>
                                    <li className="flex items-start">
                                        <CheckCircle className="text-success mr-2 mt-1 flex-shrink-0" size={16} />
                                        <span>Privacy-focused design</span>
                                    </li>
                                </ul>
                            </div>
                            <div className="md:w-1/2 flex justify-center">
                                <div
                                    className="h-64 w-32 bg-primary bg-opacity-10 rounded-lg flex items-center justify-center">
                                    {/* App screenshot placeholder */}
                                    <img src="/api/placeholder/120/220" alt="Feature highlights"
                                        className="rounded-lg" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Privacy First */}
            <section id="privacy" className="py-16 bg-background">
                <div className="container mx-auto px-4">
                    <div className="max-w-3xl mx-auto text-center">
                        <div
                            className="bg-secondary bg-opacity-20 p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                            <Lock className="text-primary" size={24} />
                        </div>
                        <h2 className="text-3xl font-bold mb-6">Privacy First</h2>
                        <p className="text-xl mb-8">Your data stays on your device. Always.</p>

                        <div className="bg-surface rounded-xl p-8 shadow-sm text-left">
                            <h3 className="text-xl font-bold mb-4">Our Privacy Commitment</h3>
                            <ul className="space-y-4">
                                {
                                    privacy.map((item, index) => (
                                        <li key={index} className="flex items-start">
                                            <CheckCircle className="text-success mr-3 mt-1" size={18} />
                                            <span>{item}</span>
                                        </li>
                                    ))
                                }
                            </ul>

                            <div className="mt-6">
                                <Link to="/privacy-policy" className="text-primary hover:underline flex items-center">
                                    Read our full privacy policy <ExternalLink size={16} className="ml-1" />
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* FAQ */}
            <section id="faq" className="py-16 bg-surface">
                <div className="container mx-auto px-4">
                    <h2 className="text-3xl font-bold text-center mb-12">Frequently Asked Questions</h2>

                    <div className="max-w-3xl mx-auto">
                        {
                            faqs.map((faq, index) => (
                                <div key={index} className="mb-4">
                                    <button
                                        className="w-full flex justify-between items-center bg-background p-4 rounded-lg focus:outline-none"
                                        onClick={() => toggleAccordion(index)}
                                    >
                                        <span className="font-bold text-lg">{faq.question}</span>
                                        {activeAccordion === index ? (
                                            <ChevronUp size={20} />
                                        ) : (
                                            <ChevronDown size={20} />
                                        )}
                                    </button>

                                    {activeAccordion === index && (
                                        <div className="bg-background p-4 rounded-b-lg mt-px">
                                            <p className="text-muted">{faq.answer}</p>
                                        </div>
                                    )}
                                </div>
                            ))
                        }
                    </div>
                </div>
            </section>

            {/* Download CTA */}
            <section id="download" className="py-16 bg-primary text-white">
                <div className="container mx-auto px-4 text-center">
                    <h2 className="text-3xl font-bold mb-6">Get Keyguarde Today</h2>
                    <p className="text-xl mb-8 max-w-2xl mx-auto">
                        Never miss an important message again. Download Keyguarde and start catching what matters.
                    </p>

                    <div className="flex justify-center">
                        <Link
                            to="https://play.google.com/store/apps/details?id=dev.logickoder.keyguarde"
                            target="_blank"
                            className="hover:bg-opacity-90"
                        >
                            <img src={getImageUrl('play-store.png')} alt="Google Play Badge" className="h-16" />
                        </Link>
                    </div>
                </div>
            </section>
        </>
    );
}