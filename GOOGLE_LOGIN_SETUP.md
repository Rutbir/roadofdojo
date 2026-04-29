# Google Login + Supabase Setup (Android)

## 1) Jangan simpan credential sensitif di source code

Kamu sempat share beberapa secret (DB password dan Google client secret). Anggap itu sudah bocor dan **rotate sekarang**:

- Supabase DB password
- Google OAuth client secret

Yang boleh ada di app Android hanya:

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `GOOGLE_WEB_CLIENT_ID`

## 2) Isi `local.properties`

Tambahkan ini di file `local.properties` (root project):

```properties
SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
SUPABASE_ANON_KEY=YOUR_SUPABASE_ANON_KEY
GOOGLE_WEB_CLIENT_ID=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

Contoh URL untuk project ref kamu:

```properties
SUPABASE_URL=https://farpoovjetesiwiuxnfq.supabase.co
```

## 3) Setup Google provider di Supabase

Di Supabase Dashboard:

1. `Authentication` -> `Providers` -> `Google`
2. Enable Google provider
3. Isi:
   - Client ID: dari Google OAuth **Web application**
   - Client Secret: dari Google OAuth **Web application**
4. Save

## 4) Setup OAuth client di Google Cloud

Kamu butuh minimal:

1. OAuth client type **Web application** (dipakai oleh Supabase + request ID token)
2. OAuth client type **Android** (package + SHA-1 debug/release)

### Authorized JavaScript origins

Untuk skenario Android native ini, biasanya **tidak wajib** isi origin.
Kalau diminta, boleh isi URL supabase kamu:

- `https://farpoovjetesiwiuxnfq.supabase.co`

### Authorized redirect URIs (yang penting)

Isi callback Supabase berikut:

- `https://farpoovjetesiwiuxnfq.supabase.co/auth/v1/callback`

> Ini redirect utama agar auth Google -> Supabase valid.

## 5) Build dan test

Setelah konfigurasi selesai:

- Sync Gradle
- Jalankan app
- Buka halaman login
- Tekan tombol "Lanjut dengan Google"

Kalau sukses, status di layar berubah jadi "Login berhasil. Mantap!"

## 6) Langkah lanjut yang disarankan

- Simpan session/access token secara aman (EncryptedSharedPreferences)
- Tambahkan logout
- Setelah login sukses, navigate ke halaman utama app
- Tambahkan verifikasi session saat app start

