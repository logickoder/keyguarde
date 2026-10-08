import type { ReactNode } from 'react';

interface PhoneFrameProps {
  children: ReactNode;
  className?: string;
}

/**
 * A plain phone outline around a screenshot. The screenshots are cropped below Android's status
 * bar, so the frame draws a quiet one of its own.
 */
export default function PhoneFrame({ children, className }: PhoneFrameProps) {
  return (
    <div className={`rounded-[2.75rem] bg-device p-2.5 shadow-medium ${className ?? ''}`}>
      <div className="overflow-hidden rounded-[2.25rem] bg-paper">
        <div
          aria-hidden="true"
          className="flex items-center justify-between px-6 pt-3 pb-1 text-xs font-semibold text-ink"
        >
          <span>9:41</span>
          <span className="h-4 w-16 rounded-full bg-device" />
          <span className="flex gap-1">
            <span className="h-2.5 w-2.5 rounded-full bg-ink-muted" />
            <span className="h-2.5 w-4 rounded-sm bg-ink-muted" />
          </span>
        </div>
        {children}
      </div>
    </div>
  );
}
