package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HazardPlacard
import com.example.data.model.PlacardSymbolType

@Composable
fun HazardPlacardDiamond(
    placard: HazardPlacard,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp
) {
    val bg = Color(placard.backgroundColorHex)
    val textCol = Color(placard.textColorHex)

    Box(
        modifier = modifier
            .size(size)
            .padding(size * 0.1f),
        contentAlignment = Alignment.Center
    ) {
        // Rotated Diamond Background
        Box(
            modifier = Modifier
                .size(size * 0.72f)
                .shadow(4.dp, RoundedCornerShape(6.dp))
                .rotate(45f)
                .clip(RoundedCornerShape(6.dp))
                .background(bg)
                .border(2.dp, if (bg == Color.White) Color.Black else Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
        ) {
            // Special custom styling if dual color (like 4.2 half white half red, or 5.2, or 8)
            if (placard.id == "4.2" || placard.id == "5.2" || placard.id == "8" || placard.id == "7") {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    if (placard.id == "4.2") {
                        // Top half white, bottom half red
                        drawRect(Color.White, size = Size(w, h / 2))
                        drawRect(Color(0xFFD32F2F), topLeft = Offset(0f, h / 2), size = Size(w, h / 2))
                    } else if (placard.id == "8") {
                        // Top half white, bottom half black
                        drawRect(Color.White, size = Size(w, h / 2))
                        drawRect(Color.Black, topLeft = Offset(0f, h / 2), size = Size(w, h / 2))
                    } else if (placard.id == "7") {
                        // Top yellow, bottom white
                        drawRect(Color(0xFFFFD600), size = Size(w, h / 2))
                        drawRect(Color.White, topLeft = Offset(0f, h / 2), size = Size(w, h / 2))
                    }
                }
            } else if (placard.id == "9") {
                // Class 9 top 7 black vertical stripes
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    drawRect(Color.White, size = Size(w, h))
                    val stripeCount = 7
                    val stripeWidth = w / (stripeCount * 2)
                    for (i in 0 until stripeCount) {
                        drawRect(
                            Color.Black,
                            topLeft = Offset(i * stripeWidth * 2, 0f),
                            size = Size(stripeWidth, h / 2)
                        )
                    }
                }
            } else if (placard.id == "4.1") {
                // Class 4.1 Red and White vertical stripes
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val stripeCount = 7
                    val stripeWidth = w / stripeCount
                    for (i in 0 until stripeCount) {
                        drawRect(
                            if (i % 2 == 0) Color(0xFFD32F2F) else Color.White,
                            topLeft = Offset(i * stripeWidth, 0f),
                            size = Size(stripeWidth, h)
                        )
                    }
                }
            }
        }

        // Unrotated Content on top of diamond
        Column(
            modifier = Modifier
                .size(size * 0.7f)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            val icon = getSymbolVector(placard.symbolType)
            Icon(
                imageVector = icon,
                contentDescription = placard.title,
                tint = if (placard.id == "4.2" || placard.id == "8" || placard.id == "6.1" || placard.id == "9") Color.Black else textCol,
                modifier = Modifier.size(size * 0.28f)
            )

            // Class Number
            Text(
                text = placard.classNumber,
                fontSize = (size.value * 0.16f).sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (placard.id == "8") Color.White else if (placard.id == "6.1" || placard.id == "7" || placard.id == "4.1") Color.Black else textCol
            )
        }
    }
}

private fun getSymbolVector(symbolType: PlacardSymbolType): ImageVector {
    return when (symbolType) {
        PlacardSymbolType.EXPLOSION -> Icons.Default.LocalFireDepartment
        PlacardSymbolType.FLAME -> Icons.Default.Whatshot
        PlacardSymbolType.GAS_CYLINDER -> Icons.Default.Science
        PlacardSymbolType.SKULL -> Icons.Default.Dangerous
        PlacardSymbolType.OXIDIZER -> Icons.Default.Brightness5
        PlacardSymbolType.ORGANIC_PEROXIDE -> Icons.Default.Whatshot
        PlacardSymbolType.RADIOACTIVE -> Icons.Default.Warning
        PlacardSymbolType.CORROSIVE -> Icons.Default.Coronavirus
        PlacardSymbolType.ENVIRONMENT -> Icons.Default.Eco
        PlacardSymbolType.MISCELLANEOUS -> Icons.Default.WarningAmber
        PlacardSymbolType.HOT_SUBSTANCE -> Icons.Default.Thermostat
    }
}
