package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AdrLocalDatabase
import com.example.data.model.AdrClassInfo
import com.example.data.model.HazardPlacard
import com.example.ui.components.AdrPlateView
import com.example.ui.components.HazardBadge
import com.example.ui.components.HazardPlacardDiamond
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardYellow

@Composable
fun AdrGuideScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedPlacardForDialog by remember { mutableStateOf<HazardPlacard?>(null) }

    val adrClasses = AdrLocalDatabase.getAdrClasses()
    val placards = AdrLocalDatabase.getHazardPlacards()

    val kemlerDigitRules = listOf(
        "X" to "Madde SU İLE TEHLİKELİ TEPKİME VERİR (Söndürmede asla su kullanılmaz)",
        "2" to "Gaz salımı (Basınç veya kimyasal reaksiyon sonucu gaz çıkışı)",
        "3" to "Alevlenir sıvı veya gaz",
        "4" to "Alevlenir katı veya kendiliğinden ısınan katı",
        "5" to "Oksitleyici (Yangını körükleyen / besleyen) madde",
        "6" to "Toksik (Zehirli) veya bulaşıcı madde",
        "7" to "Radyoaktif madde",
        "8" to "Aşındırıcı (Korozif / Asidik-Bazik) madde",
        "9" to "Kendiliğinden ani şiddetli reaksiyon riski / Çevreye zararlı madde",
        "0" to "İkincil tehlike yok (Tehlikenin tekil olduğunu teyit eder)"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AdrOrange,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)) },
            modifier = Modifier.testTag("guide_tab_row")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Kemler & Plaka", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Tehlike Plakatları", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("ERG & İzolasyon", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            when (selectedTab) {
                // Tab 0: Kemler and Standard Plate
                0 -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("guide_header_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "ADR & Kemler Plaka Standartları",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "UNECE ST/SG/AC.10/1 Rev.24 ve ADR Bölüm 5.3.2 standartları",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                                )

                                AdrPlateView(
                                    kemlerCode = "33",
                                    unNumber = "1203",
                                    height = 130.dp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AdrOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Üst satır: Tehlike Tanımlama (Kemler) Kodu\nAlt satır: Birleşmiş Milletler (UN) Madde No",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "KEMLER KODU BASAMAKLARI VE ANLAMLARI",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rakam tekrarı (örn. 33, 66, 88) tehlikenin şiddetlendiğini gösterir.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                kemlerDigitRules.forEach { (digit, meaning) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(if (digit == "X") HazardBlue else AdrOrange),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = digit,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color.Black
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = meaning,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "ADR TEHLİKE SINIFLARI (1 - 9):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(adrClasses) { adrClass ->
                        AdrClassCardItem(adrClass = adrClass)
                    }
                }

                // Tab 1: Hazard Placards (Eşkenar Dörtgen Baklava Levhalar)
                1 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "ADR Tehlike Etiketleri & Plakatları",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Araçların ve ambalajların üzerinde yer alan 25x25 cm eşkenar dörtgen (baklava) plakatlar ve sembol anlamları (ADR Bölüm 5.2 & 5.3):",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    items(placards) { placard ->
                        PlacardCatalogCard(
                            placard = placard,
                            onClick = { selectedPlacardForDialog = placard }
                        )
                    }
                }

                // Tab 2: ERG Response & Isolation Guide
                2 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = HazardBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ERG Acil Müdahale & İzolasyon İlkeleri",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Emergency Response Guidebook (Kuzey Amerika & UNECE) saha güvenlik protokolleri:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    item {
                        ErgGuidelineCard(
                            stepNumber = "1",
                            title = "Rüzgar Yönü ve Yaklaşma",
                            description = "Olay yerine DAİMA rüzgarı arkanıza alarak (rüzgar üstü - upwind) yaklaşın. Çukur, menfez ve bodrum katlar gibi ağır gazların birikebileceği alçak yerlerden uzak durun.",
                            icon = Icons.Default.Air,
                            tint = HazardBlue
                        )
                    }

                    item {
                        ErgGuidelineCard(
                            stepNumber = "2",
                            title = "İlk İzolasyon Mesafesi",
                            description = "Kimyasal sızıntı/döküntü tespit edildiğinde tehlikeli maddenin ERG tablosuna göre derhal minimum 50 - 150 metre çapındaki tüm alanı sivil girişine kapatın ve şeritle çevirin.",
                            icon = Icons.Default.HighlightOff,
                            tint = HazardRed
                        )
                    }

                    item {
                        ErgGuidelineCard(
                            stepNumber = "3",
                            title = "Tank Yangını & BLEVE Riski",
                            description = "Basınçlı gaz veya sıvı yakıt tankerlerinde yangın varsa, tank patlaması (BLEVE) ihtimaline karşı tahliye mesafesini derhal minimum 800 ila 1600 metreye genişletin.",
                            icon = Icons.Default.LocalFireDepartment,
                            tint = AdrOrange
                        )
                    }

                    item {
                        ErgGuidelineCard(
                            stepNumber = "4",
                            title = "Su Yasağı Kontrolü ('X' Kodu)",
                            description = "Plakada Kemler kodu 'X' ile başlıyorsa (ör. X338, X423), madde suyla patlayıcı ve zehirli gaz reaksiyonu verir. Söndürmede veya soğutmada ASLA doğrudan su kullanmayın; kuru kimyevi toz veya kuru kum uygulayın.",
                            icon = Icons.Default.Dangerous,
                            tint = HazardRed
                        )
                    }

                    item {
                        ErgGuidelineCard(
                            stepNumber = "5",
                            title = "Kişisel Koruyucu Donanım (KKD)",
                            description = "Seviye A (Tam gaz geçirmez kapsül elbise + SCBA tüpü), Seviye B (Sıvı sıçramasına dayanıklı + SCBA), Seviye C (Hava temizleyici filtreli maske + tulum), Seviye D (Standart itfaiyeci iş elbisesi).",
                            icon = Icons.Default.Shield,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }

    // Placard Detail Modal Dialog
    selectedPlacardForDialog?.let { placard ->
        AlertDialog(
            onDismissRequest = { selectedPlacardForDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HazardPlacardDiamond(placard = placard, size = 64.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = placard.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = placard.symbolName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = placard.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Birincil Risk:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(placard.primaryRisk, fontSize = 12.sp, color = HazardRed, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tünel Kodu:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        HazardBadge(text = placard.tunnelRestrictionCode)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ambalaj Grupları:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(placard.packingGroups.joinToString(", "), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedPlacardForDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AdrOrange, contentColor = Color.Black)
                ) {
                    Text("Kapat", fontWeight = FontWeight.Black)
                }
            }
        )
    }
}

@Composable
private fun PlacardCatalogCard(
    placard: HazardPlacard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("placard_card_${placard.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HazardPlacardDiamond(
                placard = placard,
                size = 78.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = placard.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = placard.symbolName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = placard.primaryRisk,
                    fontSize = 12.sp,
                    color = HazardRed,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HazardBadge(text = "Tünel: ${placard.tunnelRestrictionCode}")
                }
            }

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Detay",
                tint = AdrOrange,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ErgGuidelineCard(
    stepNumber: String,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$stepNumber. $title",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun AdrClassCardItem(
    adrClass: AdrClassInfo
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("class_item_${adrClass.classNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(adrClass.colorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = adrClass.classNumber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sınıf ${adrClass.classNumber}: ${adrClass.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = adrClass.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
