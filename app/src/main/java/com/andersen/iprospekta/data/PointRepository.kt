package com.andersen.iprospekta.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Stockage local des points en JSON (fondation).
 * Sera remplace par Room (SQLite) + export GeoJSON automatique dans les
 * phases suivantes. Ecriture immediate = sauvegarde automatique.
 */
class PointRepository(context: Context) {

    private val file = File(context.filesDir, "points.json")

    fun load(): List<FieldPoint> {
        if (!file.exists()) return emptyList()
        return runCatching {
            val arr = JSONArray(file.readText())
            (0 until arr.length()).map { i -> fromJson(arr.getJSONObject(i)) }
        }.getOrDefault(emptyList())
    }

    fun saveAll(points: List<FieldPoint>) {
        val arr = JSONArray()
        points.forEach { arr.put(toJson(it)) }
        file.writeText(arr.toString())
    }

    fun add(point: FieldPoint): List<FieldPoint> {
        val updated = load() + point
        saveAll(updated)
        return updated
    }

    fun update(point: FieldPoint): List<FieldPoint> {
        val updated = load().map { if (it.id == point.id) point else it }
        saveAll(updated)
        return updated
    }

    fun delete(id: Long): List<FieldPoint> {
        val updated = load().filterNot { it.id == id }
        saveAll(updated)
        return updated
    }

    private fun toJson(p: FieldPoint): JSONObject = JSONObject().apply {
        put("id", p.id)
        put("lat", p.latitude)
        put("lon", p.longitude)
        put("alt", p.altitude)
        put("acc", p.accuracy.toDouble())
        put("name", p.name)
        put("lithologie", p.lithologie)
        put("pendage", p.pendage ?: JSONObject.NULL)
        put("direction", p.direction ?: JSONObject.NULL)
        put("note", p.note)
        put("createdAt", p.createdAt)
    }

    private fun fromJson(o: JSONObject): FieldPoint = FieldPoint(
        id = o.getLong("id"),
        latitude = o.getDouble("lat"),
        longitude = o.getDouble("lon"),
        altitude = o.optDouble("alt", 0.0),
        accuracy = o.optDouble("acc", 0.0).toFloat(),
        name = o.optString("name", "Point"),
        lithologie = o.optString("lithologie", ""),
        pendage = if (o.isNull("pendage")) null else o.optInt("pendage"),
        direction = if (o.isNull("direction")) null else o.optInt("direction"),
        note = o.optString("note", ""),
        createdAt = o.optLong("createdAt", System.currentTimeMillis())
    )
}
