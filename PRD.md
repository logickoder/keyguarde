# Keyguarde: product requirements

## Purpose

Help people in busy chat groups catch the few messages that matter, without reading everything and without their messages leaving the phone.

## Target users

- Job seekers in busy WhatsApp and Telegram groups
- Freelancers and small businesses waiting on invoices, orders and payments
- Traders and community members watching for specific words

## Core features

### Notification listening

- Reads new notifications from the apps the user picks, through Android's notification listener.
- Needs notification access. Can't open chats, read older messages or read media.
- Notifications from other apps, and messages without a keyword, are ignored and never stored.

### Keyword matching

- The user keeps a list of keywords.
- Matching is case-insensitive and whole word: "rent" doesn't match "current".
- One message can match several keywords.

### Matches

- Every matched message is saved on the device, newest first, with its keywords highlighted.
- A divider marks matches that arrived since the last visit.
- Search, filter by app, and see one keyword's matches from the Keywords tab.
- Open a match in its app while the original notification is still live. Otherwise, open the app.
- Select and delete matches, with undo.

### Keywords

- Add, edit and delete keywords. Delete has undo.
- Each keyword shows when it last matched.
- Sort by recent match, A to Z, or recently added.

### Alerts

- Optional pop-up alert for each match.
- Optional silent count notification ("8 new matches in 2 chats"), cleared by a reset.
- Optional reset of the count each time Keyguarde opens.

### Reliability

- A live test posts a notification and checks the listener catches it.
- Warnings when notification access is off, when Android stops the listener, or when alerts are blocked.
- A battery screen walks the user through lifting Android's battery limits.
- Pause stops catching messages without losing setup.

### Setup

Onboarding walks through: what Keyguarde does, picking keywords, picking apps, granting access, and a live test.

## Privacy

- Messages, matches and keywords never leave the phone. No server, no account.
- App data is excluded from Android backups and device transfers.
- Google services: Firebase Analytics (screens viewed, features used), Crashlytics, Performance Monitoring and AdMob. None receive message text, chat names or keywords.
- The website loads analytics only after the visitor accepts cookies.

## Permissions

- Notification listener
- Post notifications (Android 13+)
- Ignore battery optimizations (to ask Android not to pause the listener)
- Internet and advertising ID (for the Google services above)

## Monetization

- Free with a banner ad.
- Planned: a one-time purchase to remove ads.

## Future ideas

- Filters per chat, not only per app
- Typo-tolerant matching
- Related keyword groups
- Export and import of keywords and settings
