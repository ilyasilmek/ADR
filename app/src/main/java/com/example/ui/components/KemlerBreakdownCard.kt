package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitExplanation
import com.example.data.model.KemlerCodeDetail
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardRed

@Composable
fun KemlerBreakdownCard(
    kemlerDetail: KemlerCodeDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kemler_breakdown_card"),
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
                Column {
                    Text(
                        text = "Kemler Kodu / Tehlike No",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = kemlerDetail.code,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (kemlerDetail.isWaterReactive) {
                    HazardBadge(
                        text = "SU KULLANMAYIN",
                        backgroundColor = HazardBlue,
                        textColor = Color.White,
                        isWarning = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = kemlerDetail.fullDescription,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "KOD BASAMAKLARI ANALİZİ (TEK TEK ANLAMI):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            kemlerDetail.digitBreakdowns.forEach { item ->
                DigitRowItem(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DigitRowItem(
    item: DigitExplanation
) {
    val isPrefixX = item.digit == "X"
    val badgeColor = when {
        isPrefixX -> HazardBlue
        item.isWarning -> HazardRed
        else -> AdrOrange
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Digit Badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.digit,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.position,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.meaning,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 17.sp
            )
        }
    }
}
