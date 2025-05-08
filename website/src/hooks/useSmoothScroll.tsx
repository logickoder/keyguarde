import { useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";

/**
 * 
 * @param path - The path to check if the current location is the same.
 * @returns A function that scrolls to the specified section ID smoothly if the current path matches the provided path, otherwise navigates to the provided path with the section ID in the state.
 */
export default function useSmoothScroll(path: string) {
    const location = useLocation();
    const navigate = useNavigate();
    const canSmoothScroll = location.pathname === path;

    const smoothScroll = useCallback((sectionId: string) => {
        if (canSmoothScroll) {
            document.getElementById(sectionId)?.scrollIntoView({ behavior: "smooth" });
        } else {
            navigate(path, { state: { scrollTo: sectionId } });
        }
    }, [canSmoothScroll, navigate]);

    return smoothScroll;
}