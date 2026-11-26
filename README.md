# Nusantara TV — Android TV 14 Custom Launcher & Dashboard

Aplikasi Android TV Box modern berbasis **Kotlin**, **Jetpack Compose for TV**, **Media3 ExoPlayer**, **Retrofit**, dan **DataStore Preferences** yang berfungsi sebagai **Home Launcher** sekaligus **Dashboard Pintar**.

## Fitur Utama
- **UI/UX Android TV 14**: Desain dark glassmorphism dengan navigasi D-Pad penuh, focus ring cyan, Quick Settings (Wi-Fi, Jam Digital, Cuaca ringkas, Mode Anak, Screensaver).
- **Perkiraan Cuaca Canggih (Open-Meteo API)**: Suhu real-time, ikon cuaca animasi, kelembapan, kecepatan angin, prakiraan per jam & 5 hari ke depan untuk kota-kota di Indonesia (tanpa perlu API key).
- **Jadwal Sepak Bola Lengkap (TheSportsDB + Fallback Lokal)**: BRI Liga 1, Timnas Indonesia, UEFA Champions League (UCL), Premier League, dan Bundesliga lengkap dalam zona waktu **WIB**.
- **Jadwal Waktu Sholat & Adzan Digital (Aladhan API Kemenag RI)**: Jam digital real-time, hitung mundur waktu sholat berikutnya, pop-up notifikasi & audio adzan otomatis (bisa di-mute/unmute).
- **TV Nasional & Live Makkah/Madinah 24 Jam (Media3 ExoPlayer)**: Mendukung stream `.m3u8` (HLS) dengan pergantian kanal via remote D-Pad dan fitur tambah kanal kustom di Pengaturan.
- **Kids Lock (Mode Anak)**: Penguncian Pengaturan dan pembatasan aplikasi (whitelist) dengan PIN 4-digit (default: `1234`).
- **Live Wallpaper & Ambient Screensaver (`DreamService`)**: Latar animasi Ken Burns / Aurora OLED serta layanan Screensaver Android TV sistem.

---

## Cara Push ke GitHub & Build APK Otomatis

1. Inisialisasi dan push proyek ke repository GitHub Anda:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Nusantara TV Android TV Launcher"
   git branch -M main
   git remote add origin https://github.com/USERNAME/REPO_ANDA.git
   git push -u origin main
   ```
2. Buka tab **Actions** di repository GitHub Anda. Workflow `.github/workflows/build-apk.yml` akan berjalan otomatis:
   - Menjalankan unit test (`testDebugUnitTest`).
   - Mem-build `NusantaraTV-AndroidTV-debug.apk`.
   - Mengunggah file `.apk` ke **Artifacts** dan membuat **GitHub Release** (`v1.0.<run_number>`).
3. Unduh `NusantaraTV-AndroidTV-debug.apk` dari halaman **Releases** atau **Actions Artifacts**.

---

## Cara Install di Android TV Box & Jadikan Home Launcher

1. Salin file `.apk` ke flashdisk USB atau unduh langsung di Android TV Box menggunakan aplikasi **Downloader** / **Send Files to TV**.
2. Aktifkan *Install Unknown Apps* di Pengaturan Android TV Box, lalu pasang APK.
3. Untuk menjadikannya tampilan utama (Home Launcher), buka **Pengaturan** di dalam aplikasi lalu klik **Jadikan Launcher Utama TV Box**, atau tekan tombol **Home** pada remote dan pilih **Nusantara TV -> Selalu**.
4. Untuk mengaktifkan Screensaver sistem, buka **Pengaturan -> Atur Screensaver Sistem (Daydream)** dan pilih **Nusantara TV**.
