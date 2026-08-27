package com.example.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ScanSource
import com.example.ui.components.AdrPlateView
import com.example.ui.components.EmergencySosCard
import com.example.ui.components.ErgCalculatorCard
import com.example.ui.components.KemlerBreakdownCard
import com.example.ui.components.UnSubstanceCard
import com.example.ui.components.WaterReactiveWarningBanner
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.AdrOrangeDark
import com.example.ui.theme.HazardRed
import com.example.ui.viewmodel.AdrPlateViewModel

data class SamplePlatePreset(
    val title: String,
    val kemler: String,
    val un: String,
    val substance: String,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: AdrPlateViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeResult by viewModel.activeResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lastBitmap by viewModel.lastImageBitmap.collectAsState()
    var showManualCodeDialog by remember { mutableStateOf(false) }
    var dialogCodeInput by remember { mutableStateOf("") }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            viewModel.analyzeBitmap(it, source = ScanSource.CAMERA)
        }
    }

    fun launchCameraSafely() {
        try {
            cameraLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Cihazda aktif kamera uygulaması bulunamadı. Lütfen galeriden fotoğraf seçin veya kodu manuel girin.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Kamera açılamadı: ${e.localizedMessage ?: "Bilinmeyen hata"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCameraSafely()
        } else {
            Toast.makeText(
                context,
                "Kamera izni verilmedi. Galeriden fotoğraf seçebilir veya manuel kod arayabilirsiniz.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestCameraAndLaunch() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraSafely()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                viewModel.analyzeBitmap(bitmap, imageUri = it.toString(), source = ScanSource.GALLERY)
            } catch (e: Exception) {
                Toast.makeText(context, "Görsel yüklenemedi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val samplePresets = remember {
        listOf(
            SamplePlatePreset("Benzin / Yakıt", "33", "1203", "Motor Ruhu (Alevlenir)", "Sınıf 3"),
            SamplePlatePreset("Sülfürik Asit", "80", "1830", "Zaç Yağı (Aşındırıcı)", "Sınıf 8"),
            SamplePlatePreset("LPG / Propan", "23", "1978", "Sıvılaştırılmış Gaz", "Sınıf 2.1"),
            SamplePlatePreset("Reaktif Kimyasal", "X338", "1993", "Suyla Reaktif Sıvı", "Su Yasak!"),
            SamplePlatePreset("Susuz Amonyak", "268", "1005", "Toksik & Aşındırıcı", "Sınıf 2.3"),
            SamplePlatePreset("Dizel / Motorin", "30", "1202", "Isıtma / Akaryakıt", "Sınıf 3")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Hero Scan Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scanner_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ADR Tehlike Plakası Okuyucu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Turuncu tabelayı kamerayla çekin veya galeriden görsel seçin",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    // Scanner Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Camera Button
                        Button(
                            onClick = { requestCameraAndLaunch() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("camera_scan_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AdrOrange,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Kamera",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kamera",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }

                        // Gallery Button
                        FilledTonalButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("gallery_picker_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Galeri",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Galeri",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Manual Code Input Button
                    OutlinedButton(
                        onClick = {
                            dialogCodeInput = ""
                            showManualCodeDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("manual_code_quick_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AdrOrange.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = AdrOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manuel ANR / ADR / UN Kodu Gir",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

                    // Quick Preset Sample Plates Horizontal Selector
        item {
            Column {
                Text(
                    text = "HIZLI TEST NUMUNELERİ (Örnek Plakalar):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(samplePresets) { preset ->
                        SamplePresetItem(
                            preset = preset,
                            isSelected = activeResult?.kemlerCode == preset.kemler && activeResult?.unNumber == preset.un,
                            onClick = {
                                viewModel.selectPresetSample(preset.kemler, preset.un)
                            }
                        )
                    }
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("error_card"),
                    colors = CardDefaults.cardColors(containerColor = HazardRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = HazardRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 13.sp,
                            color = HazardRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Analyzing State Loader
        if (isAnalyzing) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analyzing_loader_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = AdrOrange,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Görsel ADR Plakası Çözümleniyor...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kemler Tehlike Kodu ve BM UN Numarası ayrıştırılıyor",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Decoded Result Section
        activeResult?.let { result ->
            // Realistic Orange ADR Plate
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OKUNAN ADR TURUNCU PLAKASI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Kodları düzenlemek için dokunun",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AdrPlateView(
                        kemlerCode = result.kemlerCode,
                        unNumber = result.unNumber,
                        height = 175.dp
                    )
                }
            }

            // Water Reactive Warning if starts with X
            if (result.kemlerDetail.isWaterReactive) {
                item {
                    WaterReactiveWarningBanner()
                }
            }

            // Emergency SOS Call and Report Card (1-a)
            item {
                EmergencySosCard(result = result)
            }

            // Kemler Digit by Digit Breakdown Card
            item {
                KemlerBreakdownCard(kemlerDetail = result.kemlerDetail)
            }

            // UN Substance & Emergency Guidance Card
            item {
                UnSubstanceCard(substance = result.unSubstance)
            }

            // ERG Dynamic Evacuation Distance Calculator (1-b)
            item {
                ErgCalculatorCard(substance = result.unSubstance)
            }
        }
    }

    // Manual Code Dialog
    if (showManualCodeDialog) {
        AlertDialog(
            onDismissRequest = { showManualCodeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pin, contentDescription = null, tint = AdrOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manuel Kod Sorgulama", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "ANR / UN No (ör. 1203), Plaka Kombinasyonu (ör. 33/1203) veya Kemler Kodu (ör. X338) giriniz:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dialogCodeInput,
                        onValueChange = { dialogCodeInput = it.uppercase() },
                        placeholder = { Text("ör. 1203 veya 33/1203") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_code_input_field"),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AdrOrange
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Hızlı Seç:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(listOf("1203", "1202", "1830", "33/1203", "X338/1830", "80")) { chip ->
                            SuggestionChip(
                                onClick = { dialogCodeInput = chip },
                                label = { Text(chip, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManualCodeDialog = false
                        if (dialogCodeInput.isNotBlank()) {
                            viewModel.lookupSmartCode(dialogCodeInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AdrOrange,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Sorgula", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualCodeDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun SamplePresetItem(
    preset: SamplePlatePreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
            .testTag("preset_${preset.kemler}_${preset.un}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AdrOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, AdrOrange)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Mini plate preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AdrOrange)
                    .border(1.5.dp, Color(0xFF1E1E1E), RoundedCornerShape(6.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = preset.kemler,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Black)
                    )
                    Text(
                        text = preset.un,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = preset.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = preset.tag,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
