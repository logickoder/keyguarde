# Theming

The app and the website share one look: a cool grey ramp, and teal only on matched keywords. Source of truth: `app/src/main/java/dev/logickoder/keyguarde/app/theme/` for the app, `website/src/main.css` for the site.

## Colour

Teal means "this word matched". Nothing else is teal: buttons, links, switches and icons stay neutral, and status uses icons and words, never colour alone.

| Role | Light | Dark |
|---|---|---|
| Keyword (`primary`) | `#0E716A` Teal 700 | `#2DD4BF` Teal 400 |
| Background, surface | `#FFFFFF` | `#121214` |
| Text (`onSurface`) | `#18181B` | `#F4F4F5` |
| Secondary text (`onSurfaceVariant`) | `#52525B` | `#A1A1AA` |
| Outline | `#8A8A93` | `#71717A` |
| Divider (`outlineVariant`) | `#E4E4E7` | `#3F3F46` |
| Error | `#B42318` | `#F97066` |

- Every Material colour slot is set in `Theme.kt`, so nothing falls back to the stock palette.
- Every text pair clears 4.5:1, and icons and outlines clear 3:1, in both themes.
- Primary buttons are dark neutral (`onSurface` fill, `surface` text): `PrimaryButton`. Checkboxes, switches and text fields use `neutralCheckboxColors()`, `neutralSwitchColors()` and `neutralTextFieldColors()`.
- Fallback avatars use grey tones picked from the chat name.
- The theme follows the system setting by default. Settings, then Theme, can force light or dark.

## Type

- **Inter** for everything: regular, medium, semibold, bold.
- **Oswald** only for keyword pills and the Keywords list.
- Both are Google downloadable fonts, not bundled.

## Spacing and shape

- Spacing: `xs 4`, `s 8`, `m 12`, `l 16`, `xl 24`, `xxl 32` (dp). Use `Spacing`, not ad hoc values.
- Radius: `s 8`, `m 12`, `l 20`, `pill`. Use `Radius`.
- Touch targets are at least 48dp.

## Icon

The Keyhole mark: a teal chat bubble with a keyhole. Sources are in `docs/brand/`:

- `keyguarde-icon-master.svg`: the full-colour icon.
- `keyguarde-mark-mono.svg`: one colour, for themed icons and the notification icon.
- `play-store-icon-512.png`: the Play Store icon.

The launcher icon is adaptive, with a monochrome layer for Android themed icons.

## Notifications

- **Match alert:** names the matched keywords and the chat. Tapping opens Keyguarde.
- **Match count:** a silent, ongoing notification, "8 new matches in 2 chats". It hides while Keyguarde is paused and clears when the count resets.
