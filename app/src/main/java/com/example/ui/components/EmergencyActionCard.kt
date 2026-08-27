package com.example.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AdrLocalDatabase
import com.example.data.model.UnSubstance
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardYellow

@Composable
fun UnSubstanceCard(
    substance: UnSubstance,
    modifier: Modifier = Modifier
) {
    val placard = AdrLocalDatabase.getPlacardForSubstance(substance)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("un_substance_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Birleşmiş Milletler Maddesi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "UN ${substance.unNumber}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HazardPlacardDiamond(
                    placard = placard,
                    size = 90.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = substance.nameTr,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 22.sp
            )

            Text(
                text = substance.nameEn,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Class & PG & Tunnel Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HazardBadge(text = substance.classDescription)
                if (substance.packingGroup.isNotEmpty()) {
                    HazardBadge(text = substance.packingGroup)
                }
                HazardBadge(
                    text = "Tünel: ${substance.tunnelCode}",
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hazard overview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "TEHLİKE VE RİSKLER:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = substance.hazardDescription,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ACİL MÜDAHALE VE GÜVENLİK REHBERİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmergencySectionItem(
                icon = Icons.Default.LocalFireDepartment,
                iconTint = HazardRed,
                title = "Yangın Söndürme Talimatı",
                content = substance.fireAction
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmergencySectionItem(
                icon = Icons.Default.Dangerous,
                iconTint = HazardYellow,
                title = "Dökülme ve Sızıntı Önlemleri",
                content = substance.spillAction
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmergencySectionItem(
                icon = Icons.Default.MedicalServices,
                iconTint = Color(0xFF4CAF50),
                title = "İlk Yardım Prosedürü",
                content = substance.firstAid
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmergencySectionItem(
                icon = Icons.Default.Shield,
                iconTint = Color(0xFF2196F3),
                title = "Gerekli Kişisel Koruyucu Donanım (KKD)",
                content = substance.ppeRequired.joinToString(" • ")
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmergencySectionItem(
                icon = Icons.Default.NearMe,
                iconTint = Color(0xFF9C27B0),
                title = "Güvenli Tahliye ve İzolasyon Mesafesi",
                content = substance.isolationDistance
            )
        }
    }
}

@Composable
private fun EmergencySectionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
