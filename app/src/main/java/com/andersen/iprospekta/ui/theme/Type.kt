package com.andersen.iprospekta.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Typographie : police systeme (sans-serif) avec graisses marquees.
// Pour une police de marque, deposer les .ttf dans res/font et remplacer
// FontFamily.SansSerif par la famille personnalisee.
private val Brand = FontFamily.SansSerif

val IpTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = Brand, fontWeight = FontWeight.Bold, fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Brand, fontWeight = FontWeight.SemiBold, fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Brand, fontWeight = FontWeight.Normal, fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Brand, fontWeight = FontWeight.Normal, fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Brand, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
    )
)
