# I-Prospekta

Application Android native de **prospection géologique de terrain**.
Construite entièrement à partir de zéro, sur des briques **open source** —
aucun code tiers propriétaire.

> Version **0.1.0 (fondation)**. Cette base correspond aux phases 0 à 2 du
> plan technique : socle, cartographie, collecte de points, boussole,
> carnet, système de licence et page À propos. Les exports SIG, le pipeline
> photo complet et la synchronisation arrivent dans les phases suivantes.

## Ce que fait déjà l'application

- **Carte** OpenStreetMap + Topographique (osmdroid), zoom/déplacement tactile.
- **Points GPS** : ajout à la position GPS (bouton +) ou par appui long sur la carte ; marqueurs persistants.
- **Carnet de terrain** : liste des points, édition lithologie / direction / pendage / note, suppression.
- **Boussole + clinomètre** logiciels (capteur de rotation).
- **Licence mensuelle** : activation par code signé, expiration automatique, verrouillage à l'échéance, anti-recul d'horloge.
- **À propos** : Andersen · 034 16 894 03 · version.
- **Thème** vert foncé / gris / orange / blanc, Material 3, mode clair et sombre.

## Pile

Kotlin · Jetpack Compose (Material 3) · osmdroid · LocationManager · SensorManager.
`minSdk 29` (Android 10) · `targetSdk 35`. Pour viser Android 16 (API 36, requis
par Google Play), passez AGP à 8.9+, Gradle à 8.11+ et `compileSdk`/`targetSdk` à 36
(voir le commentaire dans `app/build.gradle.kts`).

## Obtenir l'APK

### Option A — GitHub Actions (aucune installation)
1. Créez un dépôt GitHub et poussez ce projet.
2. L'onglet **Actions** lance le workflow « Build I-Prospekta » (ou cliquez *Run workflow*).
3. À la fin, téléchargez l'artefact **`iprospekta-debug-apk`** : c'est l'APK installable.

Pour un **APK/AAB signés** (release), ajoutez dans *Settings → Secrets → Actions* :
`KEYSTORE_BASE64` (votre `.jks` encodé en base64), `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
Le workflow produira alors aussi l'artefact **`iprospekta-release`** (APK + AAB).

### Option B — Android Studio
Ouvrez le dossier, laissez Gradle se synchroniser, puis **Build → Build APK(s)**,
ou `./gradlew assembleDebug`. L'APK est dans `app/build/outputs/apk/debug/`.

### Option C — ligne de commande
```bash
./gradlew assembleDebug      # APK debug installable
./gradlew assembleRelease    # APK release (signé si keystore configuré)
./gradlew bundleRelease      # AAB pour Google Play
```

## Signer (release)

```bash
keytool -genkeypair -v -keystore release.jks -keyalg RSA -keysize 2048 \
        -validity 10000 -alias iprospekta
```
Puis copiez `keystore.properties.example` en `keystore.properties` et renseignez-le,
**ou** définissez les variables d'environnement `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`,
`KEY_ALIAS`, `KEY_PASSWORD`. Ces fichiers sont ignorés par git.

## Licences — outil d'administration

Générez des codes d'activation avec l'outil `tools/generate_license.py` :
```bash
python3 tools/generate_license.py --client "Société X" --days 30
python3 tools/generate_license.py --client demo --days 30 --count 10
```
En production, remplacez l'HMAC partagé par une signature **Ed25519** (clé privée
côté serveur, clé publique seule dans l'app). Voir `license/LicenseManager.kt`.

## Structure

```
app/src/main/
├─ java/com/andersen/iprospekta/
│  ├─ MainActivity.kt          # Navigation + verrou de licence
│  ├─ IProspektaApp.kt         # Init osmdroid
│  ├─ license/LicenseManager.kt
│  ├─ data/                    # FieldPoint + stockage JSON
│  ├─ location/                # GPS (LocationManager)
│  ├─ sensors/                 # Boussole / clinomètre
│  └─ ui/{theme,screens}/      # Thème + écrans
└─ res/                        # Couleurs, thème, icône/logo
```

Voir `GUIDE_INSTALLATION.md` pour installer et activer, et `MODIFICATIONS.md`
pour la liste des changements.
