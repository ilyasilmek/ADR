package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AdrPlateBorder
import com.example.ui.theme.AdrPlateSurface
import com.example.ui.theme.AdrPlateText

@Composable
fun AdrPlateView(
    kemlerCode: String,
    unNumber: String,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    showEmbossEffect: Boolean = true,
    onTopClicked: (() -> Unit)? = null,
    onBottomClicked: (() -> Unit)? = null
) {
    val cornerRadius = 12.dp
    val borderWidth = 4.dp

    val plateGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF7A00),
            Color(0xFFFF6400),
            Color(0xFFE65100)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), clip = false)
            .clip(RoundedCornerShape(cornerRadius))
            .background(plateGradient)
            .border(borderWidth, AdrPlateBorder, RoundedCornerShape(cornerRadius))
            .testTag("adr_plate_view")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Upper half: Kemler Code (Hazard ID)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(
                        if (onTopClicked != null) Modifier.clickable { onTopClicked() }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = kemlerCode.ifEmpty { "---" },
                    fontSize = (height.value * 0.28).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp,
                    color = AdrPlateText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("plate_kemler_code")
                )
            }

            // Central dividing black bar (ADR standard)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(borderWidth)
                    .background(AdrPlateBorder)
            )

            // Lower half: UN Number (4 digits)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(
                        if (onBottomClicked != null) Modifier.clickable { onBottomClicked() }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unNumber.ifEmpty { "----" },
                    fontSize = (height.value * 0.28).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp,
                    color = AdrPlateText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("plate_un_number")
                )
            }
        }

        // Realistic ADR plate certification / corner stamp
        Text(
            text = "ADR 5.3.2",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0x66000000),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 6.dp)
        )
    }
}
