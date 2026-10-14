# Liste des modifications — I-Prospekta

## Version 0.1.0 — Fondation (2026-10-14)

Projet **créé intégralement à partir de zéro**. Aucun code repris d'une
application existante (notamment aucun code d'Avenza Maps). Toutes les
dépendances sont open source sous licence permissive.

### Identité
- Nouvelle application nommée **I-Prospekta**.
- Package / applicationId : `com.andersen.iprospekta`.
- Logo et icône adaptative créés (hexagone minéral + aiguille de boussole, vector).
- Palette de marque : vert foncé, gris, orange, blanc (`res/values/colors.xml`, `ui/theme/`).
- Thème Material 3, clair et sombre.

### Fonctionnalités implémentées
- Cartographie osmdroid : fonds OpenStreetMap et Topographique, multi-touch.
- Points GPS : ajout à la position courante et par appui long ; marqueurs persistants.
- Stockage local JSON des points (sauvegarde automatique).
- Carnet de terrain : édition lithologie / direction / pendage / note, suppression.
- Boussole + clinomètre (capteur de rotation).
- Accès GPS via `LocationManager` (sans dépendance Google Play Services).
- Système de licence mensuelle : jetons signés HMAC-SHA256, expiration
  automatique, verrouillage à l'échéance, détection de recul d'horloge.
- Outil d'administration `tools/generate_license.py` (génération de codes).
- Page « À propos » : Andersen · 034 16 894 03 · version.

### Qualité / architecture
- Kotlin + Jetpack Compose, séparation UI / données / capteurs / licence.
- Code commenté en français, noms explicites.
- `minSdk 29` (Android 10) → `targetSdk 35`.

### Outillage de livraison
- Workflow GitHub Actions : build automatique de l'APK (debug) et, si un
  keystore est fourni, de l'APK + AAB signés (release).
- Configuration de signature release via `keystore.properties` ou variables
  d'environnement.

## À venir (phases suivantes du plan technique)
- Pipeline photo géotaguée complet (CameraX + watermark + EXIF + galerie).
- Migration MapLibre (styles vectoriels) + packs hors ligne téléchargeables.
- Lignes et polygones, calculs distance/surface (JTS).
- Exports GPX / KML / GeoJSON / Shapefile / CSV / PDF.
- Suivi GPS en arrière-plan et enregistrement de traces.
- Base Room + synchronisation serveur.
- Licence en production : signature Ed25519 (clé publique seule dans l'app) + validation serveur.
- Passage à `targetSdk 36` (Android 16) pour Google Play.
