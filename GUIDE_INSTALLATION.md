# Guide d'installation — I-Prospekta

## 1. Obtenir le fichier APK

Trois voies, détaillées dans `README.md` :
- **GitHub Actions** (recommandé, rien à installer) → artefact `iprospekta-debug-apk`.
- **Android Studio** → `Build → Build APK(s)`.
- **Ligne de commande** → `./gradlew assembleDebug`.

Le fichier produit s'appelle `app-debug.apk`.

## 2. Installer sur le téléphone (Android 10 à 16)

1. Copiez `app-debug.apk` sur le téléphone (USB, e-mail, cloud…).
2. Ouvrez le fichier depuis **Fichiers**.
3. À la première installation hors Play Store, Android demande d'autoriser
   « Installer des applications inconnues » pour l'application qui ouvre le fichier
   (Fichiers ou Chrome) : acceptez, puis **Installer**.
4. Lancez **I-Prospekta**.

> La version **debug** est signée automatiquement et sert à la démonstration/terrain.
> Pour une diffusion large, utilisez l'APK **release** signé, ou l'AAB sur Google Play.

## 3. Activer la licence

Au premier lancement, l'écran d'activation demande un **code**.

**Code de démonstration (valable 30 jours, jusqu'au 2026-11-13) :**
```
IPK1.eyJzdWIiOiJkZW1vIiwiaWF0IjoiMjAyNi0xMC0xNCIsImV4cCI6IjIwMjYtMTEtMTMifQ.1Id7GOMhyV1VnRxKxcXW1OJi8eOGcvfCdlK1p2Ktgfo
```

Collez-le, puis **Activer la licence**. L'application se déverrouille et affiche
les jours restants en haut à droite. À l'expiration, elle se reverrouille sur
l'écran d'activation.

Pour générer vos propres codes : voir `tools/generate_license.py` (README).

## 4. Autoriser la localisation

Au premier affichage de la carte, acceptez la demande de **localisation** pour
centrer sur votre position et enregistrer des points GPS avec altitude et précision.

## 5. Premiers pas

- **Carte** : bouton **＋** (orange) = enregistrer un point à votre position GPS ;
  **appui long** sur la carte = point à l'endroit touché ; icône calques = changer
  de fond (OSM / Topo) ; cible = centrer sur ma position.
- **Carnet** : ouvrez un point pour saisir lithologie, direction, pendage, note.
- **Boussole** : cap magnétique + inclinaison (clinomètre).
- **À propos** : coordonnées du développeur et version.

## Dépannage

- *« Position GPS indisponible »* : activez le GPS et sortez à ciel ouvert ;
  la première acquisition peut prendre une minute.
- *Carte grise* : vérifiez la connexion Internet (les tuiles OSM se chargent en
  ligne ; le cache permet ensuite un usage hors ligne des zones déjà vues).
- *Code refusé* : vérifiez qu'il est collé en entier, sans espace.
