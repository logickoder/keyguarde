import {Link, useLocation, useNavigate} from "react-router-dom";
import {ReactSVG} from "react-svg";
import Logo from "../assets/logo.svg";

export default function Navbar() {
    const location = useLocation();
    const navigate = useNavigate();
    const isHomePage = location.pathname === "/" || location.pathname === "/home";

    const handleLinkClick = (sectionId: string) => {
        if (isHomePage) {
            document.getElementById(sectionId)?.scrollIntoView({behavior: "smooth"});
        } else {
            navigate("/", {state: {scrollTo: sectionId}});
        }
    };

    return (
        <nav className="bg-surface shadow-sm">
            <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                <Link to="/" className="flex items-center">
                    <ReactSVG src={Logo} className="text-primary h-6 w-6 mr-2"/>
                    <span className="text-xl font-bold">Keyguarde</span>
                </Link>
                <div className="hidden md:flex space-x-6">
                    <button onClick={() => handleLinkClick("features")}
                            className="hover:text-secondary transition-colors">
                        Features
                    </button>
                    <button onClick={() => handleLinkClick("how-it-works")}
                            className="hover:text-secondary transition-colors">
                        How It Works
                    </button>
                    <button onClick={() => handleLinkClick("faq")} className="hover:text-secondary transition-colors">
                        FAQ
                    </button>
                    <button onClick={() => handleLinkClick("privacy")}
                            className="hover:text-secondary transition-colors">
                        Privacy
                    </button>
                </div>
                <button
                    onClick={() => handleLinkClick("download")}
                    className="bg-primary hover:bg-opacity-90 text-white px-4 py-2 rounded-md transition-colors"
                >
                    Download
                </button>
            </div>
        </nav>
    );
}