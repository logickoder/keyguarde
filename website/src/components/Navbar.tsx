import { Link } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
import Logo from '../assets/logo.svg';
import useSmoothScroll from '../hooks/useSmoothScroll';

export default function Navbar() {
  const handleClick = useSmoothScroll('/');

  return (
    <nav className="bg-surface shadow-sm">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link to="/" className="flex items-center">
          <ReactSVG src={Logo} className="text-primary h-6 w-6 mr-2" />
          <span className="text-xl font-bold">Keyguarde</span>
        </Link>
        <div className="hidden md:flex space-x-6">
          <button
            onClick={() => handleClick('features')}
            className="hover:text-secondary transition-colors"
          >
            Features
          </button>
          <button
            onClick={() => handleClick('how-it-works')}
            className="hover:text-secondary transition-colors"
          >
            How It Works
          </button>
          <button
            onClick={() => handleClick('faq')}
            className="hover:text-secondary transition-colors"
          >
            FAQ
          </button>
        </div>
        <button
          onClick={() => handleClick('download')}
          className="bg-primary hover:bg-opacity-90 text-white px-4 py-2 rounded-md transition-colors"
        >
          Download
        </button>
      </div>
    </nav>
  );
}
