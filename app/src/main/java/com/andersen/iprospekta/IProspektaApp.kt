package com.andersen.iprospekta

import android.app.Application
import org.osmdroid.config.Configuration

/**
 * Application I-Prospekta.
 * Initialise osmdroid (user-agent requis par la politique de tuiles OSM)
 * et son cache dans le dossier prive de l'application.
 */
class IProspektaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val cfg = Configuration.getInstance()
        cfg.userAgentValue = packageName
        cfg.osmdroidBasePath = cacheDir
        cfg.osmdroidTileCache = cacheDir.resolve("tiles")
    }
}
