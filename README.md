# 768 Glass TV

An Android TV launcher prototype built with Kotlin and Jetpack Compose.

## Included in this first prototype
- Android TV HOME / Leanback launcher entry point
- Installed app discovery through Leanback and standard launcher intents
- App launching
- D-pad focusable navigation with spring focus scaling
- Liquid-glass-inspired translucent panels, gradients and borders
- Wallpaper gallery with category filters
- Optional timed wallpaper rotation
- Persistent top-right “Powered by 768 AI” identity badge
- Local 768 AI command prototype for app launching and wallpaper actions

## Not implemented yet
- Cloud AI provider, voice assistant, or remote AI actions
- Live weather
- Online wallpaper search/discovery or guaranteed offline wallpaper cache
- Draggable/resizable widgets and user-arranged dock
- Real media history and recommendations
- Verified default-launcher takeover on Sony BRAVIA

## Build
Open this project in Android Studio with JDK 17 and Android SDK 35. Sync Gradle, then select **Build > Build APK(s)**.

The project uses Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Jetpack Compose and Coil.

## Safety and testing
This is a starter source project, not a tested release APK. It has not yet been compiled or tested on the target Sony BRAVIA. Installing it does not guarantee that the TV will select it as the default HOME app. Keep the stock Sony launcher available as a fallback; do not disable system launcher or setup components.

Wallpaper images are remote Unsplash URLs and require internet access. They are demonstration sources and are not guaranteed to be 4K.
