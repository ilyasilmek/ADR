package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErgIsolationInfo
import com.example.data.model.UnSubstance
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardYellow

@Composable
fun ErgCalculatorCard(
    substance: UnSubstance,
    modifier: Modifier = Modifier
) {
    val erg = substance.ergInfo
    var isLargeSpill by remember { mutableStateOf(false) }
    var isNightTime by remember { mutableStateOf(false) }

    val currentEvacDistance = remember(isLargeSpill, isNightTime, erg) {
        if (!isLargeSpill) {
            if (isNightTime) erg.smallSpillNightMeters else erg.smallSpillDayMeters
        } else {
            if (isNightTime) erg.largeSpillNightMeters else erg.largeSpillDayMeters
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("erg_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HazardBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = HazardBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ERG İZOLASYON & TAHLİYE HESAPLAYICI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Acil Müdahale Rehberi (ERG Rehber #${erg.guideNumber})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "ERG ${erg.guideNumber}",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selectors: Spill Size and Day/Night
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spill Size Toggle
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (!isLargeSpill) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    onClick = { isLargeSpill = false }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null,
                            tint = if (!isLargeSpill) AdrOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Küçük Döküntü",
                            fontSize = 12.sp,
                            fontWeight = if (!isLargeSpill) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "< 208 Litre (Varil)",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isLargeSpill) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    onClick = { isLargeSpill = true }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDamage,
                            contentDescription = null,
                            tint = if (isLargeSpill) HazardRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Büyük Döküntü",
                            fontSize = 12.sp,
                            fontWeight = if (isLargeSpill) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tanker / Çoklu Varil",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day / Night Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (!isNightTime) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    onClick = { isNightTime = false }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = HazardYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gündüz",
                            fontSize = 12.sp,
                            fontWeight = if (!isNightTime) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isNightTime) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    border = CardDefaults.outlinedCardBorder(enabled = true),
                    onClick = { isNightTime = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = HazardBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gece",
                            fontSize = 12.sp,
                            fontWeight = if (isNightTime) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Values Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Initial Isolation Zone
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HazardRed.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "İLK İZOLASYON",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = HazardRed,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${erg.initialIsolationMeters} m",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = HazardRed
                        )
                        Text(
                            text = "Her Yöne Çap",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Downwind Evacuation Zone
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AdrOrange.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RÜZGAR ALTI TAHLİYE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AdrOrange,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentEvacDistance m",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Rüzgar Yönünde",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tank Fire Safety
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YANGIN/TANK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${erg.fireTankEvacuationMeters} m",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tank Patlama Çapı",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Perimeter Diagram
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2

                    // Fire radius circle
                    drawCircle(
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        radius = size.height * 0.45f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color(0xFFEF4444).copy(alpha = 0.5f),
                        radius = size.height * 0.45f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
                    )

                    // Evacuation wind ellipse
                    drawCircle(
                        color = Color(0xFFF59E0B).copy(alpha = 0.25f),
                        radius = size.height * 0.3f,
                        center = Offset(cx + 20f, cy)
                    )

                    // Center Incident Hot Zone
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 8f,
                        center = Offset(cx, cy)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(
                        text = "🚨 Olay Yeri Merkez Güvenlik Çemberi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Rüzgarı arkanıza alarak minimum $currentEvacDistance metre mesafede konuşlanın",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
