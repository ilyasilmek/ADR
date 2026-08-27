package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardRed

@Composable
fun HazardBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    isWarning: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isWarning) HazardRed.copy(alpha = 0.15f) else backgroundColor)
            .border(
                width = 1.dp,
                color = if (isWarning) HazardRed else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isWarning) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = HazardRed,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isWarning) HazardRed else textColor
        )
    }
}

@Composable
fun WaterReactiveWarningBanner(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HazardBlue.copy(alpha = 0.15f))
            .border(1.5.dp, HazardBlue, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("water_reactive_warning"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HazardBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Su ile Reaksiyon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DİKKAT: 'X' ÖN EKİ - SU KESİNLİKLE YASAKTIR!",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = HazardBlue
            )
            Text(
                text = "Madde su veya nem ile temas ettiğinde şiddetli reaksiyon veya patlayıcı gaz açığa çıkarır. Asla su sıkmayınız!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
