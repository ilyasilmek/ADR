package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiVisionService
import com.example.data.database.AdrLocalDatabase
import com.example.data.model.PlateAnalysisResult
import com.example.data.model.ScanSource
import com.example.data.model.UnSubstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdrPlateViewModel : ViewModel() {

    private val _activeResult = MutableStateFlow<PlateAnalysisResult?>(null)
    val activeResult: StateFlow<PlateAnalysisResult?> = _activeResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lastImageBitmap = MutableStateFlow<Bitmap?>(null)
    val lastImageBitmap: StateFlow<Bitmap?> = _lastImageBitmap.asStateFlow()

    private val _scanHistory = MutableStateFlow<List<PlateAnalysisResult>>(emptyList())
    val scanHistory: StateFlow<List<PlateAnalysisResult>> = _scanHistory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UnSubstance>>(AdrLocalDatabase.getAllSubstances())
    val searchResults: StateFlow<List<UnSubstance>> = _searchResults.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        // Pre-populate with initial example plate (33 / 1203 - Benzin)
        selectPresetSample("33", "1203", saveToHistory = false)
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun clearActiveResult() {
        _activeResult.value = null
        _errorMessage.value = null
        _lastImageBitmap.value = null
    }

    fun analyzeBitmap(bitmap: Bitmap, imageUri: String? = null, source: ScanSource = ScanSource.CAMERA) {
        _isAnalyzing.value = true
        _errorMessage.value = null
        _lastImageBitmap.value = bitmap

        viewModelScope.launch {
            try {
                val result = GeminiVisionService.analyzeHazmatPlate(
                    bitmap = bitmap,
                    imagePath = imageUri,
                    source = source
                )

                if (result.isSuccess) {
                    val analysis = result.getOrThrow()
                    _activeResult.value = analysis
                    addHistory(analysis)
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Görsel analiz edilemedi."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Hata oluştu: ${e.localizedMessage}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun selectPresetSample(kemler: String, un: String, saveToHistory: Boolean = true) {
        val kemlerDetail = AdrLocalDatabase.decodeKemlerCode(kemler)
        val unSubstance = AdrLocalDatabase.findSubstanceByUn(un) ?: UnSubstance(
            unNumber = un,
            nameTr = "UN $un Maddesi",
            nameEn = "UN $un Substance",
            adrClass = "ADR Tehlike Sınıfı",
            classDescription = "ADR Kapsamında Taşınan Madde",
            packingGroup = "PG II",
            hazardDescription = kemlerDetail.fullDescription,
            fireAction = "Uygun söndürücü madde kullanın.",
            spillAction = "Kıvılcım kaynaklarını kapatın, alanı tahliye edin.",
            firstAid = "Temiz havaya çıkarın, acil tıbbi yardım çağırın.",
            ppeRequired = listOf("Koruyucu eldiven", "Gözlük", "Maske"),
            isolationDistance = "İlk izolasyon: 50 metre.",
            defaultKemler = kemler
        )

        val result = PlateAnalysisResult(
            kemlerCode = kemler,
            unNumber = un,
            kemlerDetail = kemlerDetail,
            unSubstance = unSubstance,
            source = ScanSource.SAMPLE,
            aiNotes = "Hazır Referans Numunesi"
        )

        _activeResult.value = result
        if (saveToHistory) {
            addHistory(result)
        }
    }

    fun lookupSmartCode(query: String) {
        val raw = query.trim()
        if (raw.isEmpty()) {
            _errorMessage.value = "Lütfen sorgulamak için bir ANR, UN veya Kemler kodu giriniz."
            return
        }

        // Clean prefix if user typed "ANR", "ADR", "UN", "NO:"
        var cleaned = raw.replace(Regex("^(ANR|ADR|UN|NO|KEMLER|KOD)[\\s:#-]*", RegexOption.IGNORE_CASE), "").trim()
        if (cleaned.isEmpty()) cleaned = raw.trim()

        // Case 1: Split pattern like "33/1203", "33-1203", "33 1203", "X338/1830"
        val splitParts = cleaned.split(Regex("[/\\s,-]+")).filter { it.isNotBlank() }
        if (splitParts.size >= 2) {
            val part1 = splitParts[0].uppercase()
            val part2 = splitParts[1].uppercase()
            // check which one is UN (usually 4 digits) and which is Kemler (2-4 chars, digits or X)
            val (kemler, un) = if (part2.length == 4 && part2.all { it.isDigit() }) {
                Pair(part1, part2)
            } else if (part1.length == 4 && part1.all { it.isDigit() }) {
                Pair(part2, part1)
            } else {
                Pair(part1, part2)
            }
            lookupCustomCodes(kemler, un)
            return
        }

        // Case 2: 4-digit UN/ANR number (e.g. "1203", "1830")
        if (cleaned.length == 4 && cleaned.all { it.isDigit() }) {
            val substance = AdrLocalDatabase.findSubstanceByUn(cleaned)
            val kemler = substance?.defaultKemler?.split("/")?.firstOrNull()?.trim() ?: "30"
            lookupCustomCodes(kemler, cleaned)
            return
        }

        // Case 3: Kemler code (e.g. "33", "80", "88", "X338", "268")
        if (cleaned.matches(Regex("^[Xx]?[0-9]{2,3}$"))) {
            val kemlerUpper = cleaned.uppercase()
            // Try to find a substance that uses this Kemler code
            val matchingSubstances = AdrLocalDatabase.getAllSubstances().filter { it.defaultKemler.contains(kemlerUpper) }
            val defaultUn = matchingSubstances.firstOrNull()?.unNumber ?: "----"
            lookupCustomCodes(kemlerUpper, defaultUn)
            return
        }

        // Case 4: Search chemical name or partial code in database
        val results = AdrLocalDatabase.searchSubstances(cleaned)
        if (results.isNotEmpty()) {
            val best = results.first()
            val kemler = best.defaultKemler.split("/").firstOrNull()?.trim() ?: "30"
            lookupCustomCodes(kemler, best.unNumber)
        } else {
            // Fallback generic code lookup
            lookupCustomCodes("30", cleaned)
        }
    }

    fun lookupCustomCodes(kemler: String, un: String) {
        val cleanKemler = kemler.trim().uppercase()
        val cleanUn = un.trim()

        if (cleanKemler.isEmpty() && cleanUn.isEmpty()) {
            _errorMessage.value = "Lütfen en az bir kod giriniz."
            return
        }

        val kemlerDetail = AdrLocalDatabase.decodeKemlerCode(cleanKemler.ifEmpty { "30" })
        val unSubstance = AdrLocalDatabase.findSubstanceByUn(cleanUn) ?: UnSubstance(
            unNumber = cleanUn.ifEmpty { "0000" },
            nameTr = if (cleanUn.isNotEmpty()) "UN $cleanUn Maddesi" else "Bilinmeyen Madde",
            nameEn = if (cleanUn.isNotEmpty()) "UN $cleanUn Substance" else "Unknown Substance",
            adrClass = "Özel Tanımlı Sınıf",
            classDescription = "Kullanıcı Tarafından Sorgulanan Kod",
            packingGroup = "PG II",
            hazardDescription = kemlerDetail.fullDescription,
            fireAction = "Uygun söndürücü kullanın.",
            spillAction = "Alanı tahliye edin.",
            firstAid = "Temiz havaya çıkarın.",
            ppeRequired = listOf("Koruyucu Donanım"),
            isolationDistance = "Güvenlik mesafesi: 50 metre.",
            defaultKemler = cleanKemler
        )

        val result = PlateAnalysisResult(
            kemlerCode = cleanKemler.ifEmpty { "30" },
            unNumber = cleanUn.ifEmpty { "----" },
            kemlerDetail = kemlerDetail,
            unSubstance = unSubstance,
            source = ScanSource.MANUAL,
            aiNotes = "Manuel Plaka Oluşturucu ile Çözümlendi"
        )

        _activeResult.value = result
        addHistory(result)
        _selectedTab.value = 0 // Navigate to result view on main tab
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _searchResults.value = AdrLocalDatabase.searchSubstances(query)
    }

    private fun addHistory(item: PlateAnalysisResult) {
        val current = _scanHistory.value.toMutableList()
        // Deduplicate recent
        current.removeAll { it.kemlerCode == item.kemlerCode && it.unNumber == item.unNumber }
        current.add(0, item)
        if (current.size > 30) {
            current.removeAt(current.size - 1)
        }
        _scanHistory.value = current
    }

    fun removeFromHistory(item: PlateAnalysisResult) {
        val current = _scanHistory.value.toMutableList()
        current.remove(item)
        _scanHistory.value = current
    }

    fun clearHistory() {
        _scanHistory.value = emptyList()
    }
}
