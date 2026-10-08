import { useCallback, useEffect, useMemo, useState } from 'react';
import { Bell, CheckCircle, ChevronDown, ChevronUp, Lock, Shield, Zap } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';
import { getImageUrl } from '../utils/getImageUrl';
import useSmoothScroll from '../hooks/useSmoothScroll';

export default function HomePage() {
  const location = useLocation();
  const [activeAccordion, setActiveAccordion] = useState(0);

  const toggleAccordion = useCallback((index: number) => {
    setActiveAccordion((prev) => (prev === index ? -1 : index));
  }, []);

  const handleClick = useSmoothScroll('/');

  const howItWorks = useMemo(
    () => [
      {
        icon: Bell,
        title: 'Listens to notifications',
        description: 'Monitors your selected apps for new notifications.',
        image: '/api/placeholder/150/120',
        alt: 'Notification permissions'
      },
      {
        icon: Shield,
        title: 'Matches your keywords',
        description: 'Checks messages for words important to you.',
        image: '/api/placeholder/150/120',
        alt: 'Keywords matched'
      },
      {
        icon: Zap,
        title: 'Notifies you quietly',
        description: 'Alerts only when your keywords appear.',
        image: '/api/placeholder/150/120',
        alt: 'Notification alert'
      },
      {
        icon: Lock,
        title: 'Messages stay on your phone',
        description: 'Matching happens on your phone. Nothing you receive is uploaded.',
        image: '/api/placeholder/150/120',
        alt: 'Privacy features'
      }
    ],
    []
  );

  const whoItsFor = useMemo(
    () => [
      {
        title: 'Job Seekers',
        description:
          'Never miss that important interview invitation or job offer. Setup keywords like "position," "interview," or company names.'
      },
      {
        title: 'Market Traders',
        description:
          'Stay on top of market updates in busy group chats. Set keywords for stock symbols, "buy," "sell," or specific market events.'
      },
      {
        title: 'Busy Group Members',
        description:
          'Filter through noisy group chats to find messages that matter. Set keywords for your name or important topics.'
      }
    ],
    []
  );

  const features = useMemo(
    () => [
      {
        title: 'Matches without internet',
        description: 'Notifications are checked on your phone, so matching works offline too.'
      },
      {
        title: 'Battery Efficient',
        description: 'Designed to consume minimal resources while running in the background.'
      },
      // {
      //     title: 'Chat-Specific Filters',
      //     description: 'Monitor only the chats that matter most to you.',
      // },
      {
        title: 'Customizable Alerts',
        description: 'Tailor notifications to match your personal preferences.'
      }
    ],
    []
  );

  const faqs = useMemo(
    () => [
      {
        question: 'Why am I not getting matches?',
        answer:
          "Open Settings and tap Run a test. If the test fails, turn Keyguarde's notification access off, then on. Also check the app is ticked under Apps, and Battery use says Unrestricted."
      },
      {
        question: 'How do matches work?',
        answer:
          'Whole words, any case. “rent” matches “Rent due” but not “current”. One message can match several keywords.'
      },
      {
        question: 'How do I add or remove keywords?',
        answer:
          'Open the Keywords tab. Type a word at the top to add it. Tap a keyword to edit it, or swipe it left to delete it.'
      },
      {
        question: 'Can I pick which apps it watches?',
        answer: 'Yes. Open Settings, then Apps. Chat apps on your phone are listed first.'
      },
      {
        question: 'Why does Keyguarde need notification access?',
        answer:
          "It checks each new notification for your keywords. Without access, Android doesn't show it any."
      },
      {
        question: 'Does it read my messages?',
        answer:
          "Only the text of notifications from the apps you pick, as they arrive. It can't open your chats, see older messages or read media."
      },
      {
        question: 'Where does my data go?',
        answer:
          "Messages, matches and keywords stay on your phone, and they're left out of Android backups. Keyguarde sends usage stats, crash reports and performance data to Google Firebase, and shows Google AdMob ads. The privacy policy lists exactly what."
      },
      {
        question: 'Will it drain my battery?',
        answer: 'No. It does nothing between notifications, and each check is a short text search.'
      }
    ],
    []
  );

  useEffect(() => {
    if (location.state?.scrollTo) {
      const sectionId = location.state.scrollTo;
      document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth' });
    }
  }, [location.state]);

  return (
    <>
      <section className="py-20 bg-linear-to-br from-primary-container to-secondary-container">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-5xl md:text-6xl font-bold mb-6 text-on-background">
            Smart Chat Alerts
          </h1>
          <p className="text-xl md:text-2xl text-muted max-w-4xl mx-auto mb-10 leading-relaxed md:leading-8">
            Filter noisy chat notifications and get alerted only when keywords that matter to you
            appear. All processing happens locally on your device.
          </p>
          <div className="flex flex-wrap justify-center gap-6">
            <Link
              to="https://play.google.com/store/apps/details?id=dev.logickoder.keyguarde"
              target="_blank"
              className="bg-primary hover:bg-blue-600 text-on-primary px-8 py-4 rounded-xl transition-all duration-300 inline-flex items-center font-semibold text-lg shadow-soft hover:shadow-medium"
            >
              Download Now
            </Link>
            <button
              onClick={() => handleClick('how-it-works')}
              className="bg-background border-2 border-primary text-primary hover:bg-primary hover:text-on-primary px-8 py-4 rounded-xl transition-all duration-300 inline-flex items-center font-semibold text-lg shadow-soft hover:shadow-medium"
            >
              Learn More
            </button>
          </div>
        </div>
      </section>

      <section id="how-it-works" className="py-20 bg-surface">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-6 text-on-surface">How Keyguarde Works</h2>
            <p className="text-xl text-muted max-w-3xl mx-auto leading-relaxed">
              Four simple steps to never miss important messages again
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {howItWorks.map((step, index) => {
              const IconComponent = step.icon;
              return (
                <div
                  key={index}
                  className="bg-background p-8 rounded-2xl shadow-soft hover:shadow-medium transition-all duration-300 text-center border border-gray-100"
                >
                  <div className="bg-primary-container p-4 rounded-xl inline-flex mb-6">
                    <IconComponent className="text-primary" size={32} />
                  </div>
                  <h3 className="text-xl font-bold mb-4 text-on-background">{step.title}</h3>
                  <p className="text-muted leading-relaxed">{step.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="py-20">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-6 text-on-background">Perfect For</h2>
            <p className="text-xl text-muted max-w-3xl mx-auto leading-relaxed">
              Whether you&#39;re job hunting, trading, or just trying to stay organized
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {whoItsFor.map((user, index) => (
              <div
                key={index}
                className="bg-surface p-8 rounded-2xl shadow-soft border border-gray-100"
              >
                <h3 className="text-2xl font-bold mb-4 text-on-surface">{user.title}</h3>
                <p className="text-muted leading-relaxed">{user.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section id="features" className="py-20 bg-surface">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-6 text-on-surface">Why Choose Keyguarde</h2>
            <p className="text-xl text-muted max-w-3xl mx-auto leading-relaxed">
              Built with privacy and efficiency in mind
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <div
                key={index}
                className="bg-background p-8 rounded-2xl shadow-soft border border-gray-100"
              >
                <div className="flex items-center mb-4">
                  <CheckCircle className="text-success mr-3" size={24} />
                  <h3 className="text-xl font-bold text-on-background">{feature.title}</h3>
                </div>
                <p className="text-muted leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section id="faq" className="py-20">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-6 text-on-background">
              Frequently Asked Questions
            </h2>
            <p className="text-xl text-muted max-w-3xl mx-auto leading-relaxed">
              Everything you need to know about Keyguarde
            </p>
          </div>

          <div className="max-w-3xl mx-auto space-y-4">
            {faqs.map((faq, index) => (
              <div
                key={index}
                className="bg-surface rounded-2xl shadow-soft border border-gray-100 overflow-hidden"
              >
                <button
                  onClick={() => toggleAccordion(index)}
                  className="w-full p-6 text-left flex justify-between items-center hover:bg-gray-50 transition-colors duration-200"
                >
                  <h3 className="text-lg font-semibold text-on-surface">{faq.question}</h3>
                  {activeAccordion === index ? (
                    <ChevronUp size={20} className="text-primary" />
                  ) : (
                    <ChevronDown size={20} className="text-muted" />
                  )}
                </button>

                {activeAccordion === index && (
                  <div className="bg-background p-6 border-t border-gray-100">
                    <p className="text-muted leading-relaxed">{faq.answer}</p>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      <section
        id="download"
        className="py-20 bg-linear-to-br from-primary to-blue-600 text-on-primary"
      >
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-4xl font-bold mb-6">Get Keyguarde Today</h2>
          <p className="text-xl mb-10 max-w-3xl mx-auto leading-relaxed">
            Never miss an important message again. Download Keyguarde and start catching what
            matters.
          </p>

          <div className="flex justify-center">
            <Link
              to="https://play.google.com/store/apps/details?id=dev.logickoder.keyguarde"
              target="_blank"
              className="hover:opacity-90 transition-opacity duration-300"
            >
              <img src={getImageUrl('play-store.png')} alt="Google Play Badge" className="h-16" />
            </Link>
          </div>
        </div>
      </section>
    </>
  );
}
