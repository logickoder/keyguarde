import { Linkedin, Mail, Shield } from "lucide-react";
import Github from '../assets/github.svg';
import Twitter from '../assets/x.svg';
import { ReactSVG } from 'react-svg';

export default function Footer() {
    const itemClass = "text-white hover:text-secondary transition-colors duration-300 ease-in-out";
    const iconClass = itemClass + " w-5 h-5";

    return (

        <footer className="bg-primary text-white py-8">
            <div className="container mx-auto px-4">
                <div className="flex flex-col md:flex-row justify-between items-center">
                    <div className="flex items-center mb-4 md:mb-0">
                        <Shield className="text-white mr-2" size={24} />
                        <span className="text-xl font-bold">Keyguarde</span>
                    </div>
                    <div className="flex flex-wrap justify-center gap-4">
                        <a href="/" className={itemClass}>Home</a>
                        <a href="/contact" className={itemClass}>Contact</a>
                        <a href="/terms" className={itemClass}>Terms</a>
                        <a href="/privacy-policy" className={itemClass}>Privacy Policy</a>
                    </div>
                    <div className="flex space-x-4 mt-4 md:mt-0">
                        <a href="https://github.com/logickoder/keyguarde" className={itemClass}>
                            <ReactSVG src={Github} className={iconClass} />
                        </a>
                        <a href="https://x.com/logickoder" className={itemClass}>
                            <ReactSVG src={Twitter} className={iconClass} />
                        </a>
                        <a href="https://linkedin.com/in/logickoder" className={itemClass}>
                            <Linkedin size={20} />
                        </a>
                        <a href="mailto:jefferyorazulike@gmail.com" className={itemClass}>
                            <Mail size={20} />
                        </a>
                    </div>
                </div>
                <div className="text-center mt-8 text-sm opacity-75">
                    © {new Date().getFullYear()} Jeffery Orazulike. All rights reserved.
                </div>
            </div>
        </footer>
    )
}