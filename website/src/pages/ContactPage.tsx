import { ArrowRight, Check, Code, ExternalLink, Mail, MessageSquare } from 'lucide-react';
import { ReactSVG } from 'react-svg';
import Github from '../assets/github.svg';
import Twitter from '../assets/x.svg';
import Linkedin from '../assets/linkedin.svg';
import { useMemo } from 'react';
import useSmoothScroll from '../hooks/useSmoothScroll.tsx';

export default function ContactPage() {
  const socials = useMemo(
    () => [
      {
        name: 'Email',
        icon: <Mail size={24} />,
        link: 'mailto:jeffery@logickoder.dev',
        description: 'jeffery@logickoder.dev'
      },
      {
        name: 'Twitter',
        icon: <ReactSVG src={Twitter} className="w-6 h-6" />,
        link: 'https://x.com/logickoder',
        description: '@logickoder'
      },
      {
        name: 'GitHub',
        icon: <ReactSVG src={Github} className="w-6 h-6" />,
        link: 'https://github.com/logickoder/keyguarde',
        description: 'github.com/logickoder/keyguarde'
      },
      {
        name: 'LinkedIn',
        icon: <ReactSVG src={Linkedin} className="w-6 h-6" />,
        link: 'https://linkedin.com/in/logickoder',
        description: 'linkedin.com/in/logickoder'
      }
    ],
    []
  );
  const smoothScroll = useSmoothScroll('/');

  return (
    <div className="bg-background min-h-screen font-sans text-on-background">
      {/* Contact Header */}
      <section className="py-20 bg-gradient-to-br from-primary-container to-secondary-container">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-5xl font-bold mb-6 text-on-background">Get In Touch</h1>
          <p className="text-xl text-muted max-w-3xl mx-auto leading-relaxed">
            Have questions about Keyguarde? Want to report an issue or suggest a feature? I&#39;d
            love to hear from you through any of these channels.
          </p>
        </div>
      </section>

      {/* Social Links */}
      <section className="py-12 bg-surface">
        <div className="container mx-auto px-4">
          <div className="flex flex-wrap justify-center gap-6 max-w-4xl mx-auto">
            {socials.map((social, index) => (
              <a
                key={index}
                href={social.link}
                className="flex items-center bg-background px-8 py-6 rounded-2xl shadow-soft hover:shadow-medium transition-all duration-300 group border border-gray-100"
              >
                <div className="bg-primary-container p-4 rounded-xl mr-5 group-hover:bg-primary group-hover:text-on-primary transition-all duration-300">
                  {social.icon}
                </div>
                <div>
                  <h3 className="font-semibold text-lg text-on-background">{social.name}</h3>
                  <p className="text-muted text-sm">{social.description}</p>
                </div>
              </a>
            ))}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="flex flex-col lg:flex-row gap-12">
            {/* Contact Info */}
            <div className="lg:w-1/2 bg-surface p-10 rounded-2xl shadow-soft border border-gray-100">
              <h2 className="text-3xl font-bold mb-8 flex items-center text-on-surface">
                <MessageSquare className="mr-3 text-primary" size={28} />
                Contact Me
              </h2>

              <div className="space-y-8">
                <div className="flex items-start">
                  <div className="bg-primary-container p-3 rounded-xl mr-4">
                    <Mail className="text-primary" size={20} />
                  </div>
                  <div>
                    <p className="font-semibold text-lg text-on-surface">Email</p>
                    <a
                      href="mailto:jeffery@logickoder.dev"
                      className="text-primary hover:text-secondary transition-colors duration-200 font-medium"
                    >
                      jeffery@logickoder.dev
                    </a>
                  </div>
                </div>

                <div className="bg-primary-container p-6 rounded-xl">
                  <p className="font-semibold text-lg mb-4 text-on-surface">
                    For support inquiries:
                  </p>
                  <ul className="space-y-3">
                    <li className="flex items-start">
                      <Check size={18} className="text-success mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        Please include details about your device model and Android version
                      </span>
                    </li>
                    <li className="flex items-start">
                      <Check size={18} className="text-success mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        Screenshots of any issues help me diagnose problems faster
                      </span>
                    </li>
                    <li className="flex items-start">
                      <Check size={18} className="text-success mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        For bug reports, using the GitHub issue tracker is preferred
                      </span>
                    </li>
                  </ul>
                </div>

                <div className="bg-secondary-container p-6 rounded-xl">
                  <p className="font-semibold text-lg mb-4 text-on-surface">
                    For feature requests:
                  </p>
                  <ul className="space-y-3">
                    <li className="flex items-start">
                      <Check size={18} className="text-secondary mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        Describe the problem you&#39;re trying to solve
                      </span>
                    </li>
                    <li className="flex items-start">
                      <Check size={18} className="text-secondary mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        Explain how your suggested feature would work
                      </span>
                    </li>
                    <li className="flex items-start">
                      <Check size={18} className="text-secondary mr-3 mt-0.5 flex-shrink-0" />
                      <span className="text-on-surface">
                        Consider opening a feature request on GitHub
                      </span>
                    </li>
                  </ul>
                </div>

                <div className="pt-6 mt-8 border-t border-gray-200">
                  <div className="flex items-center">
                    <div className="w-2 h-2 bg-success rounded-full mr-3"></div>
                    <p className="text-muted font-medium">
                      I aim to respond to all inquiries within 48 hours during business days.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* GitHub Repository */}
            <div className="lg:w-1/2">
              <div className="bg-surface p-8 rounded-2xl shadow-soft mb-8 border border-gray-100">
                <h3 className="text-2xl font-bold mb-6 flex items-center text-on-surface">
                  <ReactSVG src={Github} className="w-6 h-6 mr-3" />
                  GitHub Repository
                </h3>

                <p className="text-muted mb-6 text-lg leading-relaxed">
                  Keyguarde is an open-source project. Check out the code, report issues, or
                  contribute on GitHub.
                </p>

                <a
                  href="https://github.com/logickoder/keyguarde"
                  className="inline-flex items-center text-primary hover:text-secondary font-semibold text-lg transition-colors duration-200 mb-6"
                >
                  Visit the GitHub repository
                  <ArrowRight size={20} className="ml-2" />
                </a>

                <div className="mt-8 p-6 bg-background rounded-xl border border-gray-200">
                  <h4 className="font-bold mb-3 text-on-background">Repository</h4>
                  <p className="font-mono text-sm mb-6 text-muted">
                    github.com/logickoder/keyguarde
                  </p>

                  <div className="flex flex-wrap gap-2">
                    <span className="bg-primary text-on-primary text-sm px-4 py-2 rounded-full font-medium">
                      Android
                    </span>
                    <span className="bg-secondary text-on-secondary text-sm px-4 py-2 rounded-full font-medium">
                      Kotlin
                    </span>
                    <span className="bg-success text-white text-sm px-4 py-2 rounded-full font-medium">
                      Open Source
                    </span>
                    <span className="bg-muted text-white text-sm px-4 py-2 rounded-full font-medium">
                      Privacy-Focused
                    </span>
                  </div>
                </div>
              </div>

              {/* Contributing */}
              <div className="bg-surface p-8 rounded-2xl shadow-soft border border-gray-100">
                <h3 className="text-2xl font-bold mb-6 flex items-center text-on-surface">
                  <Code className="mr-3 text-primary" size={24} />
                  Contributing
                </h3>
                <p className="text-muted mb-8 text-lg leading-relaxed">
                  I welcome contributions of all kinds, from bug fixes to feature enhancements.
                </p>

                <div className="space-y-6">
                  <div className="flex items-start">
                    <div className="bg-primary text-on-primary p-3 rounded-xl mt-1 mr-4 h-12 w-12 flex items-center justify-center font-bold text-lg">
                      1
                    </div>
                    <div className="flex-1">
                      <p className="font-semibold text-lg text-on-surface">Fork the repository</p>
                      <p className="text-muted">Create your own copy to work on</p>
                    </div>
                  </div>

                  <div className="flex items-start">
                    <div className="bg-secondary text-on-secondary p-3 rounded-xl mt-1 mr-4 h-12 w-12 flex items-center justify-center font-bold text-lg">
                      2
                    </div>
                    <div className="flex-1">
                      <p className="font-semibold text-lg text-on-surface">Make your changes</p>
                      <p className="text-muted">Fix bugs or add new features</p>
                    </div>
                  </div>

                  <div className="flex items-start">
                    <div className="bg-success text-white p-3 rounded-xl mt-1 mr-4 h-12 w-12 flex items-center justify-center font-bold text-lg">
                      3
                    </div>
                    <div className="flex-1">
                      <p className="font-semibold text-lg text-on-surface">Submit a pull request</p>
                      <p className="text-muted">
                        I&#39;ll review your changes and merge if appropriate
                      </p>
                    </div>
                  </div>
                </div>

                <a
                  href="https://github.com/logickoder/keyguarde/blob/main/CONTRIBUTING.md"
                  className="mt-8 text-primary hover:text-secondary font-semibold flex items-center transition-colors duration-200"
                >
                  Read the contribution guidelines
                  <ExternalLink size={18} className="ml-2" />
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-20 bg-gradient-to-br from-secondary-container to-primary-container">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-4xl font-bold mb-6 text-on-background">
            Join the Keyguarde Community
          </h2>
          <p className="text-xl text-muted max-w-3xl mx-auto mb-10 leading-relaxed">
            Help me make Keyguarde better by contributing code, reporting issues, or sharing
            feedback.
          </p>
          <div className="flex flex-wrap justify-center gap-6">
            <a
              href="https://github.com/logickoder/keyguarde"
              className="bg-primary hover:bg-blue-600 text-on-primary px-8 py-4 rounded-xl transition-all duration-300 inline-flex items-center font-semibold text-lg shadow-soft hover:shadow-medium"
            >
              <ReactSVG src={Github} className="w-6 h-6 mr-3" />
              View on GitHub
            </a>
            <div
              onClick={() => smoothScroll('download')}
              className="bg-background border-2 border-primary text-primary hover:bg-primary hover:text-on-primary px-8 py-4 rounded-xl transition-all duration-300 inline-flex items-center font-semibold text-lg shadow-soft hover:shadow-medium cursor-pointer"
            >
              Download Keyguarde
              <ArrowRight size={24} className="ml-3" />
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
