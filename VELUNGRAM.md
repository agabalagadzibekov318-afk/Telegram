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

## Public VelunGram builds for other users

VelunGram is **one shared application APK**, not a special build per Telegram
account. Once the developer provides a valid pair of Telegram application
credentials, each person logs in **using their own Telegram phone number and
their own verification code**. Telegram decides how codes are delivered; SMS
is not guaranteed for third-party apps and can be unavailable. Some codes
arrive in another Telegram session, by email, or through other methods.

The app developer must register their own api_id and api_hash at
https://my.telegram.org/apps (users do **not** create them).

For production distribution you must set **all** the following GitHub Actions
repository secrets (Settings → Secrets and variables → Actions):
- VELUN_API_ID and VELUN_API_HASH: application credentials for this client
- VELUN_GOOGLE_SERVICES_B64: Base64 of your own Firebase google-services.json
  containing Android clients for org.telegram.messenger (library module)\n  and org.velungram.messenger.web (standalone application)
- VELUN_KEYSTORE_B64: Base64 of your own release signing keystore
- VELUN_KEYSTORE_PASSWORD: keystore password
- VELUN_KEY_PASSWORD: signing key password
- VELUN_KEY_ALIAS: signing alias

Do **not** put these secrets in GitHub commits, issues or chat. Do not
use the bundled upstream Telegram signing certificate or Telegram's
official API credentials in a public release.

When secrets are configured, trigger
[Publish VelunGram for everyone](../../actions/workflows/public-release.yml)
with "Run workflow". When its APK Artifact is created, the same single
VelunGram.apk can be shared with multiple Android users.

To get a public GitHub Release download URL, push a version tag such as
v0.1.0; the workflow will publish the signed APK under
[Releases](../../releases) **only after the build succeeds**.

Important: a compiled APK is not proof that login, Firebase notifications,
calls, media and every Telegram feature work. Test all major flows before
public distribution. Obey https://core.telegram.org/api/terms, identify
VelunGram as unofficial, and do not misuse user verification codes.

Build note (2026-10-26): the shared library module applies the Google Services plugin with package `org.telegram.messenger`, so Firebase configuration must include that Android client as well. Development placeholders now include it. Firebase projects without it fail at `:TMessagesProj:processStandaloneGoogleServices`.
