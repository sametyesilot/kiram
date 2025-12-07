# KİRÂM - Kiracı ve Ev Sahibi Yönetim Uygulaması

KİRÂM, kiracılar, ev sahipleri ve apartman yöneticileri için geliştirilmiş modern bir Android uygulamasıdır.

## 🚀 Özellikler

### Kiracı Özellikleri
- Ev bilgilerini görüntüleme
- Ev sahibi ile iletişim
- Hasar bildirimi
- Hasar geçmişi görüntüleme

### Ev Sahibi Özellikleri
- Ev ekleme ve yönetimi
- Kiracı yönetimi
- Anlaşmazlık takibi
- Değerlendirme sistemi

### Apartman Yöneticisi Özellikleri
- Duyuru yayınlama
- Aidat yönetimi
- Bina sorunları takibi
- Bina istatistikleri

## 🛠️ Teknolojiler

- **Kotlin** - Ana programlama dili
- **Jetpack Compose** - Modern UI framework
- **Firebase Authentication** - Kullanıcı kimlik doğrulama
- **Firebase Firestore** - NoSQL veritabanı
- **Firebase Storage** - Dosya depolama
- **Firebase Cloud Messaging** - Push bildirimleri
- **Navigation Compose** - Uygulama içi navigasyon
- **DataStore** - Yerel veri saklama
- **Coil** - Resim yükleme
- **Coroutines** - Asenkron işlemler

## 📋 Kurulum

### Gereksinimler
- Android Studio Hedgehog | 2023.1.1 veya üzeri
- JDK 11 veya üzeri
- Android SDK 24 veya üzeri

### Adımlar

1. **Projeyi klonlayın**
   ```bash
   git clone https://github.com/[kullanıcı-adınız]/kiram.git
   cd kiram
   ```

2. **Firebase Yapılandırması**
   
   > ⚠️ **ÖNEMLİ**: `google-services.json` dosyası güvenlik nedeniyle repository'de bulunmamaktadır.
   
   Firebase yapılandırmasını tamamlamak için:
   
   a. [Firebase Console](https://console.firebase.google.com/) adresine gidin
   
   b. Yeni bir proje oluşturun veya mevcut projeyi kullanın
   
   c. Android uygulaması ekleyin:
      - Package name: `com.example.kiram`
      - App nickname: KİRÂM (opsiyonel)
      - SHA-1 sertifikası (opsiyonel, ancak önerilir)
   
   d. `google-services.json` dosyasını indirin
   
   e. İndirilen dosyayı `app/` klasörüne kopyalayın:
      ```
      kiram/
      └── app/
          └── google-services.json  ← Buraya
      ```
   
   f. Firebase Console'da şu servisleri aktif edin:
      - **Authentication** (Email/Password provider'ı etkinleştirin)
      - **Cloud Firestore** (Production mode'da başlatın)
      - **Storage** (Varsayılan kurallarla başlatın)
      - **Cloud Messaging** (Otomatik olarak aktiftir)

3. **Firestore Güvenlik Kuralları**
   
   Firebase Console → Firestore Database → Rules bölümünden aşağıdaki kuralları ekleyin:
   
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       // Users collection
       match /users/{userId} {
         allow read: if request.auth != null;
         allow write: if request.auth != null && request.auth.uid == userId;
       }
       
       // Properties collection
       match /properties/{propertyId} {
         allow read: if request.auth != null;
         allow create: if request.auth != null;
         allow update, delete: if request.auth != null && 
           (resource.data.landlordId == request.auth.uid || 
            resource.data.managerId == request.auth.uid);
       }
       
       // Reviews collection
       match /reviews/{reviewId} {
         allow read: if request.auth != null;
         allow create: if request.auth != null;
         allow update, delete: if request.auth != null && 
           resource.data.fromUserId == request.auth.uid;
       }
       
       // Messages collection
       match /messages/{messageId} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

4. **Storage Güvenlik Kuralları**
   
   Firebase Console → Storage → Rules bölümünden:
   
   ```javascript
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /{allPaths=**} {
         allow read: if request.auth != null;
         allow write: if request.auth != null && 
           request.resource.size < 5 * 1024 * 1024; // 5MB limit
       }
     }
   }
   ```

5. **Projeyi derleyin**
   ```bash
   ./gradlew build
   ```

6. **Uygulamayı çalıştırın**
   - Android Studio'da "Run" butonuna tıklayın veya
   - `./gradlew installDebug` komutunu çalıştırın

## 📱 Kullanıcı Rolleri

Uygulama üç farklı kullanıcı rolünü destekler:

1. **TENANT (Kiracı)** - Ev kiralayan kullanıcılar
2. **LANDLORD (Ev Sahibi)** - Ev kiralayan kullanıcılar
3. **MANAGER (Apartman Yöneticisi)** - Bina yöneticileri

## 🏗️ Proje Yapısı

```
app/src/main/java/com/example/kiram/
├── data/
│   ├── model/          # Veri modelleri
│   └── repository/     # Repository sınıfları
├── navigation/         # Navigasyon yapılandırması
├── ui/
│   ├── components/     # Yeniden kullanılabilir UI bileşenleri
│   ├── screens/        # Ekran composable'ları
│   │   ├── auth/       # Kimlik doğrulama ekranları
│   │   ├── tenant/     # Kiracı ekranları
│   │   ├── landlord/   # Ev sahibi ekranları
│   │   └── manager/    # Yönetici ekranları
│   └── theme/          # Tema ve stil tanımlamaları
└── util/               # Yardımcı fonksiyonlar ve sabitler
```

## 🔐 Güvenlik Notları

- `google-services.json` dosyası **asla** Git'e commit edilmemelidir
- API anahtarları ve hassas bilgiler environment variables olarak saklanmalıdır
- Production build için ProGuard/R8 kullanılmalıdır
- Release keystore dosyası güvenli bir yerde saklanmalıdır

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

