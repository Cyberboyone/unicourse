# All University Lecture Notes

Android app (Kotlin + Jetpack Compose) for browsing and reading lecture notes
by faculty → department → course, with in-app-only encrypted downloads and
AdMob monetization.

## What's fully wired
- **Catalog:** Supabase (via Retrofit/PostgREST) → Room cache → Compose UI,
  with offline-first reads and a `refreshCatalog()` sync on launch
- **Downloads:** R2 fetch → AES-256/GCM encryption (Android Keystore) →
  app-private storage; decrypted bytes only ever live in memory
- **Reader:** renders the decrypted PDF with android-pdf-viewer, continuous
  vertical scroll, fit to width; `FLAG_SECURE` blocks screenshots/recording;
  no ad slot on this screen, ever
- **Update badge:** version check compares the cached catalog's
  `latestVersion` against each downloaded course; manual update only
- **Ads:** `AdSlottedGrid` inserts a native ad after every 4th card on every
  list screen (Faculty, Department, Course, Search, My Downloads);
  `InterstitialAdPolicy` implements the 1st/3rd/6th/9th... cadence with a
  3-minute floor, persisted across sessions; fires from `CourseDetailScreen`
  right after a download completes, never inside the reader
- **Architecture:** clean MVVM (Compose UI → ViewModel → Repository →
  Room/Retrofit/R2), a small hand-rolled DI container (`AppContainer`), one
  shared `AppViewModelFactory`

## ⚠️ Before your first run: Firebase will crash the app

`firebase-analytics` and `firebase-crashlytics` are in `app/build.gradle.kts`,
but there's no `google-services.json` and the `google-services` Gradle
plugin isn't applied — neither is included here since both require a real
Firebase project tied to you. Firebase's SDKs auto-initialize through their
own ContentProvider the moment the app process starts, regardless of
whether any of your code calls a Firebase API, so as the project sits now
**it will crash immediately on launch** with something like `Default
FirebaseApp is not initialized`. Before running on a device or emulator,
either:

- **Set it up properly:** create a Firebase project, add an Android app
  with package name `com.cyberboyone.lecturenotes`, download
  `google-services.json` into `app/`, then add
  `id("com.google.gms.google-services") version "4.4.2"` and
  `id("com.google.firebase.crashlytics") version "3.0.2"` to
  `app/build.gradle.kts`'s `plugins {}` block (and the matching
  `apply false` versions to the root `build.gradle.kts`), **or**
- **Defer it:** comment out the `firebase-analytics` and
  `firebase-crashlytics` lines in `app/build.gradle.kts` until you're ready
  to wire up a real Firebase project.

## What's still a placeholder (needs your values, not more code)
- `NetworkModule.kt` — `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `R2_PUBLIC_BASE_URL`
- `AdManager.kt` — swap Google's test ad unit IDs for your real AdMob unit IDs
- `AndroidManifest.xml` — swap the test AdMob App ID
- `ui/theme/Theme.kt` — swap in real brand colors
- `ui/components/NativeAdCard.kt` — the native ad layout is minimal
  (headline only); a real build should use AdMob's full native ad template
  (icon, body, star rating, CTA button) via an XML layout

## Not included (deliberately out of scope for this repo)
- The **admin dashboard** (separate project, per the spec) for uploading
  notes, covers, and publishing new course versions to Supabase + R2
- App icon, splash screen, and Play Store listing assets
- Automated tests

## Running it
This environment has no Android SDK, Gradle, or emulator, so none of this
has been compiled. Open the project in Android Studio (or point Claude Code
at it, which can run Gradle builds and an emulator) and:

1. Let Gradle sync — fix any version mismatches for your installed SDK/AGP.
2. Fill in the real values in `NetworkModule.kt` and `AdManager.kt`.
3. Run `supabase/schema.sql` in the Supabase SQL editor (seeds the nine
   launch faculties; add departments/courses via the admin dashboard once
   it exists, or directly in the SQL editor for now).
4. Create an R2 bucket with public read access at
   `covers/{course_id}.jpg` and `notes/{course_id}/v{version}.pdf`.
5. Run on a device or emulator.
