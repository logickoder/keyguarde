import { ArrowRight, Check, Code, ExternalLink, Mail, MessageSquare } from 'lucide-react';
import { ReactSVG } from 'react-svg';
import Github from '../assets/github.svg';
import Twitter from '../assets/x.svg';
import Linkedin from '../assets/linkedin.svg';
import { useMemo } from 'react';


export default function ContactPage() {
    const socials = useMemo(() => [
        {
            name: 'Email',
            icon: <Mail size={24} />,
            link: 'mailto:jefferyorazulike@gmail.com',
            description: 'jefferyorazulike@gmail.com',
        },
        {
            name: 'Twitter',
            icon: <ReactSVG src={Twitter} className="w-6 h-6" />,
            link: 'https://x.com/logickoder',
            description: '@logickoder',
        },
        {
            name: 'GitHub',
            icon: <ReactSVG src={Github} className="w-6 h-6" />,
            link: 'https://github.com/logickoder/keyguarde',
            description: 'github.com/logickoder/keyguarde',
        },
        {
            name: 'LinkedIn',
            icon: <ReactSVG src={Linkedin} className="w-6 h-6" />,
            link: 'https://linkedin.com/in/logickoder',
            description: 'linkedin.com/in/logickoder',
        },
    ], []);

    return (
        <div className="bg-background min-h-screen font-sans text-primary">

            {/* Contact Header */}
            <section className="py-16 bg-gradient-to-b from-background to-secondary bg-opacity-10">
                <div className="container mx-auto px-4 text-center">
                    <h1 className="text-4xl font-bold mb-4">Get In Touch</h1>
                    <p className="text-lg text-muted max-w-2xl mx-auto">
                        Have questions about Keyguarde? Want to report an issue or suggest a feature?
                        We'd love to hear from you through any of our channels.
                    </p>
                </div>
            </section>

            {/* Social Links */}
            <section className="py-8 bg-surface">
                <div className="container mx-auto px-4">
                    <div className="flex flex-wrap justify-center gap-6 max-w-3xl mx-auto">
                        {
                            socials.map((social, index) => (
                                <a key={index} href={social.link} className="flex items-center bg-background px-6 py-4 rounded-lg shadow-sm hover:shadow-md transition-all group">
                                    <div className="bg-primary bg-opacity-10 p-3 rounded-full mr-4 group-hover:bg-primary group-hover:text-white transition-all">
                                        {social.icon}
                                    </div>
                                    <div>
                                        <h3 className="font-bold">{social.name}</h3>
                                        <p className="text-muted text-sm">{social.description}</p>
                                    </div>
                                </a>
                            ))
                        }
                    </div>
                </div>
            </section>

            {/* Main Content */}
            <section className="py-12">
                <div className="container mx-auto px-4">
                    <div className="flex flex-col md:flex-row gap-8">
                        {/* Contact Info */}
                        <div className="md:w-1/2 bg-surface p-8 rounded-lg shadow-sm">
                            <h2 className="text-2xl font-bold mb-6 flex items-center">
                                <MessageSquare className="mr-2" size={24} />
                                Contact Us
                            </h2>

                            <div className="space-y-6">
                                <div className="flex items-start">
                                    <Mail className="text-primary mr-3 mt-1" size={20} />
                                    <div>
                                        <p className="font-medium">Email</p>
                                        <a href="mailto:jefferyorazulike@gmail.com"
                                            className="text-secondary hover:underline">
                                            jefferyorazulike@gmail.com
                                        </a>
                                    </div>
                                </div>

                                <div>
                                    <p className="font-medium mt-6 mb-3">For support inquiries:</p>
                                    <ul className="space-y-2 pl-5">
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>Please include details about your device model and Android version</span>
                                        </li>
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>Screenshots of any issues help us diagnose problems faster</span>
                                        </li>
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>For bug reports, use our GitHub issue tracker when possible</span>
                                        </li>
                                    </ul>
                                </div>

                                <div>
                                    <p className="font-medium mt-6 mb-3">For feature requests:</p>
                                    <ul className="space-y-2 pl-5">
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>Describe the problem you're trying to solve</span>
                                        </li>
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>Explain how your suggested feature would work</span>
                                        </li>
                                        <li className="flex items-start">
                                            <Check size={16} className="text-success mr-2 mt-1 flex-shrink-0" />
                                            <span>Consider opening a feature request on GitHub</span>
                                        </li>
                                    </ul>
                                </div>

                                <div className="pt-4 mt-6 border-t border-gray-200">
                                    <p className="text-sm text-muted">
                                        We aim to respond to all inquiries within 48 hours during business days.
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* GitHub Repository */}
                        <div className="md:w-1/2">
                            <div className="bg-surface p-6 rounded-lg shadow-sm mb-6">
                                <h3 className="text-xl font-bold mb-4 flex items-center">
                                    <ReactSVG src={Github} className="w-5 h-5 mr-2" />
                                    GitHub Repository
                                </h3>

                                <p className="text-muted mb-4">
                                    Keyguarde is an open-source project. Check out our code, report issues, or contribute on GitHub.
                                </p>

                                <a
                                    href="https://github.com/logickoder/keyguarde"
                                    className="flex items-center text-primary hover:underline font-medium"
                                >
                                    Visit our GitHub repository
                                    <ArrowRight size={16} className="ml-2" />
                                </a>

                                <div className="mt-6 p-4 bg-background rounded-md">
                                    <h4 className="font-bold mb-2">Repository</h4>
                                    <p className="font-mono text-sm mb-4">github.com/logickoder/keyguarde</p>

                                    <div className="flex flex-wrap gap-2">
                                        <span className="bg-primary bg-opacity-10 text-primary text-xs px-3 py-1 rounded-full">Android</span>
                                        <span className="bg-primary bg-opacity-10 text-primary text-xs px-3 py-1 rounded-full">Kotlin</span>
                                        <span className="bg-primary bg-opacity-10 text-primary text-xs px-3 py-1 rounded-full">Open Source</span>
                                        <span className="bg-primary bg-opacity-10 text-primary text-xs px-3 py-1 rounded-full">Privacy-Focused</span>
                                    </div>
                                </div>
                            </div>

                            {/* Contributing */}
                            <div className="bg-surface p-6 rounded-lg shadow-sm">
                                <h3 className="text-xl font-bold mb-4 flex items-center">
                                    <Code className="mr-2" size={20} />
                                    Contributing
                                </h3>
                                <p className="text-muted mb-4">
                                    We welcome contributions of all kinds, from bug fixes to feature enhancements.
                                </p>

                                <div className="space-y-4">
                                    <div className="flex items-start">
                                        <div className="bg-secondary bg-opacity-20 p-2 rounded-full mt-1 mr-3 h-8 w-8 flex items-center justify-center">
                                            <span className="text-primary font-bold">1</span>
                                        </div>
                                        <div>
                                            <p className="font-medium">Fork the repository</p>
                                            <p className="text-sm text-muted">Create your own copy to work on</p>
                                        </div>
                                    </div>

                                    <div className="flex items-start">
                                        <div className="bg-secondary bg-opacity-20 p-2 rounded-full mt-1 mr-3 h-8 w-8 flex items-center justify-center">
                                            <span className="text-primary font-bold">2</span>
                                        </div>
                                        <div>
                                            <p className="font-medium">Make your changes</p>
                                            <p className="text-sm text-muted">Fix bugs or add new features</p>
                                        </div>
                                    </div>

                                    <div className="flex items-start">
                                        <div className="bg-secondary bg-opacity-20 p-2 rounded-full mt-1 mr-3 h-8 w-8 flex items-center justify-center">
                                            <span className="text-primary font-bold">3</span>
                                        </div>
                                        <div>
                                            <p className="font-medium">Submit a pull request</p>
                                            <p className="text-sm text-muted">We'll review your changes and merge if appropriate</p>
                                        </div>
                                    </div>
                                </div>

                                <a
                                    href="https://github.com/keyguarde/keyguarde-android/blob/main/CONTRIBUTING.md"
                                    className="mt-6 inline-block text-primary hover:underline font-medium flex items-center"
                                >
                                    Read our contribution guidelines
                                    <ExternalLink size={16} className="ml-2" />
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Call to Action */}
            <section className="py-16 bg-gradient-to-b from-secondary bg-opacity-10 to-background">
                <div className="container mx-auto px-4 text-center">
                    <h2 className="text-3xl font-bold mb-4">Join the Keyguarde Community</h2>
                    <p className="text-lg text-muted max-w-2xl mx-auto mb-8">
                        Help us make Keyguarde better by contributing code, reporting issues, or sharing feedback.
                    </p>
                    <div className="flex flex-wrap justify-center gap-4">
                        <a
                            href="https://github.com/logickoder/keyguarde"
                            className="bg-primary hover:bg-opacity-90 text-white px-6 py-3 rounded-md transition-colors inline-flex items-center"
                        >
                            <ReactSVG src={Github} className="w-5 h-5 mr-2" />
                            View on GitHub
                        </a>
                        <a
                            href="/#download"
                            className="bg-white border border-primary text-primary hover:bg-primary hover:text-white px-6 py-3 rounded-md transition-colors inline-flex items-center"
                        >
                            Download Keyguarde
                            <ArrowRight size={20} className="ml-2" />
                        </a>
                    </div>
                </div>
            </section>
        </div>
    );
}