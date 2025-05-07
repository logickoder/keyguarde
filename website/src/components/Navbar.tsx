import { Shield } from "lucide-react";

export default function Navbar() {
    return (
        <nav className="bg-surface shadow-sm">
            <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                <div className="flex items-center">
                    <Shield className="text-primary mr-2" size={24} />
                    <span className="text-xl font-bold">Keyguarde</span>
                </div>
                <div className="hidden md:flex space-x-6">
                    <a href="#features" className="hover:text-secondary transition-colors">Features</a>
                    <a href="#how-it-works" className="hover:text-secondary transition-colors">How It Works</a>
                    <a href="#faq" className="hover:text-secondary transition-colors">FAQ</a>
                    <a href="#privacy" className="hover:text-secondary transition-colors">Privacy</a>
                </div>
                <a
                    href="#download"
                    className="bg-primary hover:bg-opacity-90 text-white px-4 py-2 rounded-md transition-colors"
                >
                    Download
                </a>
            </div>
        </nav>
    )
}