# Regles ProGuard/R8 pour I-Prospekta

# osmdroid
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Garde les noms des modeles de donnees serialises manuellement
-keep class com.andersen.iprospekta.data.** { *; }
