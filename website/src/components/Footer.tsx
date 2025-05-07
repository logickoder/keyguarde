import { Github, Linkedin, Mail, Shield, Twitter } from "lucide-react";

export default function Footer() {
    return (

        <footer className="bg-primary text-white py-8">
            <div className="container mx-auto px-4">
                <div className="flex flex-col md:flex-row justify-between items-center">
                    <div className="flex items-center mb-4 md:mb-0">
                        <Shield className="text-white mr-2" size={24} />
                        <span className="text-xl font-bold">Keyguarde</span>
                    </div>
                    <div className="flex flex-wrap justify-center gap-4">
                        <a href="/" className="text-white hover:text-secondary transition-colors">Home</a>
                        <a href="/#features" className="text-white hover:text-secondary transition-colors">Features</a>
                        <a href="/contact" className="text-secondary">Contact</a>
                        <a href="/terms" className="text-white hover:text-secondary transition-colors">Terms</a>
                        <a href="/privacy-policy" className="text-white hover:text-secondary transition-colors">Privacy Policy</a>
                    </div>
                    <div className="flex space-x-4 mt-4 md:mt-0">
                        <a href="https://github.com/keyguarde" className="text-white hover:text-secondary transition-colors">
                            <Github size={20} />
                        </a>
                        <a href="https://twitter.com/keyguarde" className="text-white hover:text-secondary transition-colors">
                            <Twitter size={20} />
                        </a>
                        <a href="https://linkedin.com/company/keyguarde" className="text-white hover:text-secondary transition-colors">
                            <Linkedin size={20} />
                        </a>
                        <a href="mailto:contact@keyguarde.app" className="text-white hover:text-secondary transition-colors">
                            <Mail size={20} />
                        </a>
                    </div>
                </div>
                <div className="text-center mt-8 text-sm opacity-75">
                    © {new Date().getFullYear()} Keyguarde. All rights reserved.
                </div>
            </div>
        </footer>
    )
}