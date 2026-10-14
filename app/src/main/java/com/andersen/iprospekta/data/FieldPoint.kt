package com.andersen.iprospekta.data

/**
 * Point de prospection releve sur le terrain.
 * Les champs geologiques sont volontairement simples dans cette fondation
 * (ils seront etendus : alteration, structures, echantillons, photos...).
 */
data class FieldPoint(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val name: String,
    val lithologie: String = "",
    val pendage: Int? = null,     // dip 0-90
    val direction: Int? = null,   // 0-360
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
