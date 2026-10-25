# VelunGram — unofficial Telegram Android fork

VelunGram is an **unofficial** fork of Telegram for Android. It includes a local-only
appearance demo under **Settings → ⋮ → VelunGram — Set**.

- **Local demo controls:** Premium preview, verification preview, rating, NFT username,
  NFT number, Stars, TON, GRAM.
- **No server privileges:** does not modify real account verification, gift inventory,
  Telegram Premium entitlement, billing, or blockchain balances.
- Settings persist on the device via Android SharedPreferences.
- The visual values are displayed in the VelunGram settings preview only.

## Build

GitHub Actions: [Actions tab](../../actions/workflows/build.yml)

Workflow runs on commits and via manual **Run workflow**.

Artifacts contain `VelunGram.apk` after a **successful** build.

### Credentials before real use

Set repository Actions secrets:
- `VELUN_API_ID` and `VELUN_API_HASH`: your own credentials from https://my.telegram.org
- `VELUN_GOOGLE_SERVICES_B64`: base64-encoded personal Firebase google-services.json
  with Android clients for `org.velungram.messenger` and `org.velungram.messenger.web`
  (required for working Firebase push).
- For stable APK updates, set `VELUN_KEYSTORE_B64`, `VELUN_KEYSTORE_PASSWORD`,
  `VELUN_KEY_ALIAS`, and `VELUN_KEY_PASSWORD`.

Without API secrets, builds may succeed, but **Telegram login will not work**.
Without Firebase setup, push notifications are not expected to work.
Without signing secrets, each build receives a fresh test key, so updating an installed
build may require uninstalling the previous one.

Telegram has additional platform/service dependencies; a successful build does not
by itself establish that all Telegram features work.

Licensing and Telegram attribution remain governed by upstream LICENSE/README.

## iOS-inspired appearance

Toggle **VelunGram — Set → iOS-оформление**. The local theme uses iOS-style
blue accents, system gray grouped surfaces, green switches, and blue outgoing
bubbles in light/dark modes. Reopen screens or restart the app after changing
the setting. This is an appearance **approximation**, not an iOS UI port:
Android navigation/components remain Android-native.
