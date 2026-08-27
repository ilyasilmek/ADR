package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AdrLocalDatabase
import com.example.data.model.UnSubstance
import com.example.ui.components.AdrPlateView
import com.example.ui.components.HazardBadge
import com.example.ui.components.HazardPlacardDiamond
import com.example.ui.theme.AdrOrange
import com.example.ui.viewmodel.AdrPlateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualLookupScreen(
    viewModel: AdrPlateViewModel,
    modifier: Modifier = Modifier
) {
    var quickCodeInput by remember { mutableStateOf("") }
    var kemlerInput by remember { mutableStateOf("33") }
    var unInput by remember { mutableStateOf("1203") }
    var selectedClassFilter by remember { mutableStateOf("TÜMÜ") }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val rawSearchResults by viewModel.searchResults.collectAsState()
    val focusManager = LocalFocusManager.current

    val classFilterChips = listOf("TÜMÜ", "Sınıf 2 Gaz", "Sınıf 3 Sıvı", "Sınıf 4 Katı", "Sınıf 5 Oksitleyici", "Sınıf 6 Toksik", "Sınıf 8 Aşındırıcı", "Sınıf 9 Muhtelif")
    val quickCodeChips = listOf("1203", "1202", "1830", "1075", "33/1203", "X338/1830", "80", "33", "268", "1005")
    val commonKemlerChips = listOf("33", "30", "80", "88", "23", "268", "X338", "60", "66", "50", "90")
    val commonUnChips = listOf("1203", "1202", "1075", "1830", "1789", "1005", "1978", "1230", "1090", "3480")

    val searchResults = remember(rawSearchResults, selectedClassFilter) {
        if (selectedClassFilter == "TÜMÜ") {
            rawSearchResults
        } else {
            val classNum = when (selectedClassFilter) {
                "Sınıf 2 Gaz" -> "2"
                "Sınıf 3 Sıvı" -> "3"
                "Sınıf 4 Katı" -> "4"
                "Sınıf 5 Oksitleyici" -> "5"
                "Sınıf 6 Toksik" -> "6"
                "Sınıf 8 Aşındırıcı" -> "8"
                "Sınıf 9 Muhtelif" -> "9"
                else -> ""
            }
            rawSearchResults.filter { it.adrClass.contains(classNum) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Direct Quick ANR / ADR / UN Code Search Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_code_search_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AdrOrange.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AdrOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manuel ANR / ADR / UN Kodu Arama",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "ANR/UN No (ör. 1203), Plaka Kombinasyonu (ör. 33/1203) veya Kemler Kodu (ör. X338) girerek anında aratın:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quickCodeInput,
                            onValueChange = { quickCodeInput = it.uppercase() },
                            placeholder = { Text("ör. 1203 veya 33/1203") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_code_input_field"),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    if (quickCodeInput.isNotBlank()) {
                                        viewModel.lookupSmartCode(quickCodeInput)
                                        viewModel.setSelectedTab(0)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AdrOrange
                            )
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (quickCodeInput.isNotBlank()) {
                                    viewModel.lookupSmartCode(quickCodeInput)
                                    viewModel.setSelectedTab(0)
                                }
                            },
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("quick_code_search_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AdrOrange,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Sorgula", fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Popüler Kodlar:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(quickCodeChips) { chip ->
                            SuggestionChip(
                                onClick = {
                                    quickCodeInput = chip
                                    viewModel.lookupSmartCode(chip)
                                    viewModel.setSelectedTab(0)
                                },
                                label = { Text(chip, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Live Custom ADR Plate Creator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plate_designer_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Özel ADR Plakası Oluşturucu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Üst satıra Kemler tehlike kodunu, alt satıra UN madde kodunu girerek canlı plakayı görün:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Plate Preview
                    AdrPlateView(
                        kemlerCode = kemlerInput.ifEmpty { "---" },
                        unNumber = unInput.ifEmpty { "----" },
                        height = 140.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input fields for Kemler and UN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Kemler Code Input
                        OutlinedTextField(
                            value = kemlerInput,
                            onValueChange = {
                                if (it.length <= 4) kemlerInput = it.uppercase()
                            },
                            label = { Text("Kemler Kodu") },
                            placeholder = { Text("33") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("kemler_input_field"),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AdrOrange
                            )
                        )

                        // UN Number Input
                        OutlinedTextField(
                            value = unInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) unInput = it
                            },
                            label = { Text("UN Madde No") },
                            placeholder = { Text("1203") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("un_input_field"),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (kemlerInput.isNotBlank() && unInput.isNotBlank()) {
                                        viewModel.selectPresetSample(kemlerInput, unInput)
                                        viewModel.setSelectedTab(0)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AdrOrange
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (kemlerInput.isNotBlank() && unInput.isNotBlank()) {
                                viewModel.selectPresetSample(kemlerInput, unInput)
                                viewModel.setSelectedTab(0)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_plate_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AdrOrange,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bu Plakayı Analiz Et ve Göster", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Chemical Database Search Section (4-b: Full Offline Database)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ÇEVRİMDIŞI UN KİMYASAL VERİTABANI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${searchResults.size} Madde Bulundu",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Kimyasal adı veya UN No ara (ör. Benzin, 1830, Asit, LPG...)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Ara", tint = AdrOrange)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("database_search_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdrOrange
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sınıf Filtreleme Çipleri
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(classFilterChips) { chip ->
                        FilterChip(
                            selected = selectedClassFilter == chip,
                            onClick = { selectedClassFilter = chip },
                            label = { Text(chip, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        // Search Results List
        items(searchResults) { substance ->
            SubstanceSearchResultItem(
                substance = substance,
                onClick = {
                    val kemler = substance.defaultKemler.split("/").firstOrNull()?.trim() ?: "30"
                    kemlerInput = kemler
                    unInput = substance.unNumber
                    viewModel.selectPresetSample(kemler, substance.unNumber)
                    viewModel.setSelectedTab(0) // jump to main decoded screen
                }
            )
        }
    }
}

@Composable
private fun SubstanceSearchResultItem(
    substance: UnSubstance,
    onClick: () -> Unit
) {
    val placard = AdrLocalDatabase.getPlacardForSubstance(substance)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("substance_item_${substance.unNumber}"),
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
            // Placard Diamond
            HazardPlacardDiamond(
                placard = placard,
                size = 64.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "UN ${substance.unNumber}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (substance.defaultKemler.isNotEmpty()) {
                        HazardBadge(text = "Kemler: ${substance.defaultKemler}")
                    }
                }

                Text(
                    text = substance.nameTr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = substance.nameEn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HazardBadge(text = substance.adrClass)
                    HazardBadge(
                        text = "Tünel: ${substance.tunnelCode}",
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
