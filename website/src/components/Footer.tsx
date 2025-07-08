import { Mail } from 'lucide-react';
import { ReactSVG } from 'react-svg';
import { Link } from 'react-router-dom';
import Logo from '../assets/logo.svg';
import Github from '../assets/github.svg';
import Twitter from '../assets/x.svg';
import Linkedin from '../assets/linkedin.svg';

export default function Footer() {
  const itemClass = 'text-white hover:text-secondary transition-colors duration-300 ease-in-out';
  const iconClass = itemClass + ' w-5 h-5';

  return (
    <footer className="bg-primary text-white py-8">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <Link to="/" className="flex items-center mb-4 md:mb-0">
            <ReactSVG src={Logo} className="text-white h-6 w-6 mr-2" />
            <span className="text-xl font-bold">Keyguarde</span>
          </Link>
          <div className="flex flex-wrap justify-center gap-4">
            <Link to="/contact" className={itemClass}>
              Contact
            </Link>
            <Link to="/terms" className={itemClass}>
              Terms
            </Link>
            <Link to="/privacy-policy" className={itemClass}>
              Privacy Policy
            </Link>
          </div>
          <div className="flex space-x-4 mt-4 md:mt-0">
            <Link
              to="https://github.com/logickoder/keyguarde"
              className={itemClass}
              target="_blank"
            >
              <ReactSVG src={Github} className={iconClass} />
            </Link>
            <Link to="https://x.com/logickoder" className={itemClass} target="_blank">
              <ReactSVG src={Twitter} className={iconClass} />
            </Link>
            <Link to="https://linkedin.com/in/logickoder" className={itemClass} target="_blank">
              <ReactSVG src={Linkedin} className={iconClass} />
            </Link>
            <Link to="mailto:jeffery@logickoder.dev" className={itemClass} target="_blank">
              <Mail size={20} />
            </Link>
          </div>
        </div>
        <div className="text-center mt-8 text-sm opacity-75">
          © 2025 <Link to="https://logickoder.dev">Jeffery Orazulike</Link>. All rights reserved.
        </div>
      </div>
    </footer>
  );
}
