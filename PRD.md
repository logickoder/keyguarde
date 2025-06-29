---

## 📄 Product Requirements Document (PRD)

# Keyguarde – Smart Chat Alerts

### Purpose

Help users filter noisy chat notifications and get alerted only when specific keywords are detected, without compromising privacy.

---

### Core Features

* **Notification Listening**

    * Monitors notifications from selected apps (WhatsApp, Telegram, etc.)
    * Requires Notification Listener permission

* **Keyword Matching**

    * Users define a list of keywords
    * Matching is **case-insensitive**
    * Matches only **whole words** (e.g., “react” doesn’t match “reacted”)
    * Multiple keywords can be matched in the same message

* **Notification Alerts**

    * Optional persistent silent notification showing the number of matches since last opened
    * Optional heads-up alerts
    * Tapping a match opens the corresponding app

* **App Selection**

    * User selects which apps to monitor (default: WhatsApp and Telegram)

* **Match Counter**

    * Resets when the app is opened (configurable)

* **Privacy**

    * No data ever leaves the device
    * No accounts or cloud storage
    * No analytics collecting notification data

---

### Future Enhancements (v2+)

* Chat-specific filters (enable filtering by chat name)
* Fuzzy matching and typo tolerance
* Threshold-based semantic tags matching (e.g., related keywords)

---

### Permissions

* Notification Listener
* Post Notifications

---

### Monetization

* Free with ads
* Optional in-app purchase to remove ads

---

### Target Users

* Job seekers in busy WhatsApp/Telegram groups
* Traders monitoring keywords
* Power users who need focused alerts

---