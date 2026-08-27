package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlateAnalysisResult
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.HazardRed
import com.example.util.EmergencySosHelper

@Composable
fun EmergencySosCard(
    result: PlateAnalysisResult,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("emergency_sos_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HazardRed.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HazardRed.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = HazardRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ACİL MÜDAHALE & YETKİLİ BİLDİRİMİ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = HazardRed,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Kaza / sızıntı durumunda tek dokunuşla bildirin:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 112 Call Button
                Button(
                    onClick = {
                        EmergencySosHelper.dial112(context)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("call_112_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HazardRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("112 Ara", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                // Share Incident Report via WhatsApp/SMS
                Button(
                    onClick = {
                        EmergencySosHelper.shareEmergencyReport(context, result)
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("share_sos_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AdrOrange,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Raporu Paylaş", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }

                // Copy to clipboard
                OutlinedIconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ADR Acil Bildirimi", EmergencySosHelper.buildEmergencyReportText(result))
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Acil durum raporu panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("copy_sos_report_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Raporu Kopyala",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
