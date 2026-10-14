package com.andersen.iprospekta.license

import android.content.Context
import android.util.Base64
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Etat de la licence a un instant donne.
 */
sealed class LicenseState {
    /** Aucune licence active -> ecran d'activation. */
    object NotActivated : LicenseState()

    /** Licence valide. [daysLeft] jours restants, expire le [expiry]. */
    data class Active(val subject: String, val expiry: LocalDate, val daysLeft: Long) : LicenseState()

    /** Licence expiree -> application verrouillee. */
    data class Expired(val subject: String, val expiry: LocalDate) : LicenseState()

    /** Horloge reculee : triche probable -> revalidation requise. */
    object ClockTampered : LicenseState()

    /** Code refuse (signature ou format invalide). */
    object Invalid : LicenseState()
}

/**
 * Gestion de la licence mensuelle.
 *
 * DEMO : les codes sont des jetons signes en HMAC-SHA256 avec une cle partagee
 * ([DEMO_SECRET]). Format : IPK1.<payloadBase64Url>.<signatureBase64Url>
 * ou payload = {"sub": "...", "iat": "YYYY-MM-DD", "exp": "YYYY-MM-DD"}.
 *
 * PRODUCTION : remplacer l'HMAC par une signature asymetrique Ed25519 (Tink),
 * n'embarquer que la cle PUBLIQUE, et valider aupres du serveur quand le reseau
 * est disponible. L'outil tools/generate_license.py genere les codes de demo.
 */
class LicenseManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS = "iprospekta_license"
        private const val KEY_TOKEN = "token"
        private const val KEY_LAST_SEEN = "last_seen_epoch"
        private const val PREFIX = "IPK1"
        // Tolerance d'horloge (un leger recul reste acceptable).
        private const val CLOCK_TOLERANCE_MS = 24L * 60 * 60 * 1000

        // DEMO uniquement -> a remplacer par une cle Ed25519 publique en production.
        private const val DEMO_SECRET = "IPK-DEMO-SECRET-CHANGE-ME-9f2b7c"

        private val DATE: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    /** Verifie l'etat courant de la licence (appele a chaque lancement). */
    fun currentState(): LicenseState {
        val token = prefs.getString(KEY_TOKEN, null) ?: return LicenseState.NotActivated

        // Anti-triche horloge : marqueur "plus haut vu".
        val now = System.currentTimeMillis()
        val lastSeen = prefs.getLong(KEY_LAST_SEEN, 0L)
        if (lastSeen > 0 && now < lastSeen - CLOCK_TOLERANCE_MS) {
            return LicenseState.ClockTampered
        }
        prefs.edit().putLong(KEY_LAST_SEEN, maxOf(lastSeen, now)).apply()

        val payload = verify(token) ?: return LicenseState.Invalid
        val subject = payload.optString("sub", "client")
        val expiry = runCatching { LocalDate.parse(payload.getString("exp"), DATE) }
            .getOrNull() ?: return LicenseState.Invalid

        val today = LocalDate.now()
        return if (today.isAfter(expiry)) {
            LicenseState.Expired(subject, expiry)
        } else {
            LicenseState.Active(subject, expiry, ChronoUnit.DAYS.between(today, expiry))
        }
    }

    /** Tente d'activer avec [code]. Retourne l'etat resultant et persiste si valide. */
    fun activate(code: String): LicenseState {
        val clean = code.trim()
        val payload = verify(clean) ?: return LicenseState.Invalid
        val expiry = runCatching { LocalDate.parse(payload.getString("exp"), DATE) }
            .getOrNull() ?: return LicenseState.Invalid
        if (LocalDate.now().isAfter(expiry)) {
            return LicenseState.Expired(payload.optString("sub", "client"), expiry)
        }
        prefs.edit()
            .putString(KEY_TOKEN, clean)
            .putLong(KEY_LAST_SEEN, System.currentTimeMillis())
            .apply()
        return currentState()
    }

    /** Supprime la licence (deconnexion / test). */
    fun clear() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    /**
     * Verifie la signature du jeton et renvoie le payload JSON, ou null si invalide.
     */
    private fun verify(code: String): JSONObject? {
        val parts = code.split(".")
        if (parts.size != 3 || parts[0] != PREFIX) return null
        val signingInput = "${parts[0]}.${parts[1]}"
        val expected = hmac(signingInput)
        if (!constantTimeEquals(expected, parts[2])) return null
        return runCatching {
            val json = String(b64dec(parts[1]), Charsets.UTF_8)
            JSONObject(json)
        }.getOrNull()
    }

    private fun hmac(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(DEMO_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return b64enc(mac.doFinal(data.toByteArray(Charsets.UTF_8)))
    }

    private fun b64enc(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

    private fun b64dec(s: String): ByteArray =
        Base64.decode(s, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].code xor b[i].code)
        return result == 0
    }
}
