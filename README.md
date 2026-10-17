# Luma — Premium Photobooth & Retro Camera Studio

**Luma** is a modern, aesthetic, lightweight, and professional offline photobooth and retro camera Android application built using **Java**, **XML Layout**, and **CameraX**.

---

## 🌟 Key Features

1. **Home Dashboard Hub**: Centralized navigation to all studio features with warm pastel aesthetics (cream, warm white, soft pink, lavender).
2. **Live Photobooth Session**: Multi-shot sequence (2, 3, 4, or 6 shots) with Pose Timer Assistant hints, shutter sound effects, and per-photo retake capabilities.
3. **Analog Camera & Retro Filters**: 15+ custom film presets (Vintage Film, Kodak Warm, Fuji Soft, Retro 90s, Y2K, Disposable Camera, Polaroid Look) with fine-tuning sliders (Intensity, Brightness, Contrast, Saturation, Vignette, Film Grain, Date Stamp).
4. **Creative Studio**: Interactive touch canvas supporting text overlays, stickers, and doodle drawing with undo/redo stacks.
5. **Custom Frame Designer**: Workbench to design custom photostrip frames (colors, border thickness, slot count) and save presets locally via JSON/SharedPreferences.
6. **Smart Local Album**: Device MediaStore media viewer with date grouping, search, sorting, favorites, and multi-select batch sharing.
7. **Print-Ready Export**: High-resolution rendering and export in PNG, high-quality JPEG, and PDF document formats.
8. **100% Offline**: No internet required for camera capture, filters, storage, compression, or editing.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Java 11
- **UI**: XML Layouts with Material3 Day/Night Themes & Custom Vector Drawables
- **Camera**: CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-video`, `camera-view`)
- **Storage**: Scoped Storage via `MediaStore` & Storage Access Framework (SAF)
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 35 (API 35)

---

## 🚀 Getting Started & Build Instructions

1. Open **Android Studio**.
2. Select **Open an Existing Project** and choose the `LumaStudio` root folder.
3. Allow Gradle to synchronize dependencies.
4. Connect an Android physical device or start an Emulator.
5. Click **Run** (`Shift + F10`) to build and launch the app.

### Building Debug APK
To build the debug APK via command line:
```bash
./gradlew assembleDebug
```
The compiled debug APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 GitHub Setup & Contribution

To push this project to GitHub:

1. Initialize Git repository (if not already initialized):
   ```bash
   git init
   ```
2. Add files and commit:
   ```bash
   git add .
   git commit -m "Initial commit: Luma Premium Photobooth Studio v1.0"
   ```
3. Link your remote repository and push:
   ```bash
   git remote add origin https://github.com/your-username/LumaStudio.git
   git branch -M main
   git push -u origin main
   ```

---

## 📄 License
100% Open Source & Offline-First.
