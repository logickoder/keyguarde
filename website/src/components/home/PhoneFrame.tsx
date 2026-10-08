import type { ReactNode } from 'react';

interface PhoneFrameProps {
  children: ReactNode;
  className?: string;
}

/**
 * An Android phone outline around a screenshot: a centred punch-hole camera and an Android-style
 * status bar. The screenshots are cropped below the real status bar, so the frame draws its own.
 */
export default function PhoneFrame({ children, className }: PhoneFrameProps) {
  return (
    <div className={`rounded-[2.25rem] bg-device p-2 shadow-medium ${className ?? ''}`}>
      <div className="overflow-hidden rounded-[1.85rem] bg-paper">
        <div
          aria-hidden="true"
          className="relative flex items-center justify-between px-5 pt-2.5 pb-1 text-[11px] font-medium text-ink"
        >
          <span>9:41</span>
          <span className="absolute top-2 left-1/2 h-3.5 w-3.5 -translate-x-1/2 rounded-full bg-device" />
          <span className="flex items-end gap-1">
            {/* Signal, then battery. */}
            <span className="flex items-end gap-px">
              <span className="h-1 w-0.5 rounded-sm bg-ink" />
              <span className="h-1.5 w-0.5 rounded-sm bg-ink" />
              <span className="h-2 w-0.5 rounded-sm bg-ink" />
              <span className="h-2.5 w-0.5 rounded-sm bg-ink" />
            </span>
            <span className="ml-1 h-2.5 w-1.5 rounded-[2px] bg-ink" />
          </span>
        </div>
        {children}
      </div>
    </div>
  );
}
