package com.example.data.model

data class DigitExplanation(
    val digit: String,
    val position: String,
    val meaning: String,
    val isWarning: Boolean = false
)

data class KemlerCodeDetail(
    val code: String,
    val title: String,
    val fullDescription: String,
    val isWaterReactive: Boolean,
    val primaryHazard: String,
    val secondaryHazards: List<String> = emptyList(),
    val digitBreakdowns: List<DigitExplanation> = emptyList()
)

data class ErgIsolationInfo(
    val guideNumber: String = "128",
    val initialIsolationMeters: Int = 50,
    val smallSpillDayMeters: Int = 100,
    val smallSpillNightMeters: Int = 200,
    val largeSpillDayMeters: Int = 300,
    val largeSpillNightMeters: Int = 800,
    val fireTankEvacuationMeters: Int = 800,
    val generalAdvice: String = "Rüzgarı arkanıza alın. Çukur ve bodrum alanlardan uzak durun."
)

data class HazardPlacard(
    val id: String,
    val classNumber: String,
    val title: String,
    val symbolName: String,
    val backgroundColorHex: Long,
    val secondaryColorHex: Long? = null,
    val textColorHex: Long = 0xFFFFFFFF,
    val symbolType: PlacardSymbolType = PlacardSymbolType.FLAME,
    val description: String,
    val primaryRisk: String,
    val tunnelRestrictionCode: String = "(D/E)",
    val packingGroups: List<String> = listOf("PG I", "PG II", "PG III")
)

enum class PlacardSymbolType {
    EXPLOSION,
    FLAME,
    GAS_CYLINDER,
    SKULL,
    OXIDIZER,
    ORGANIC_PEROXIDE,
    RADIOACTIVE,
    CORROSIVE,
    ENVIRONMENT,
    MISCELLANEOUS,
    HOT_SUBSTANCE
}

data class UnSubstance(
    val unNumber: String,
    val nameTr: String,
    val nameEn: String,
    val adrClass: String,
    val classDescription: String,
    val packingGroup: String,
    val hazardDescription: String,
    val fireAction: String,
    val spillAction: String,
    val firstAid: String,
    val ppeRequired: List<String>,
    val isolationDistance: String,
    val defaultKemler: String = "",
    val tunnelCode: String = "(D/E)",
    val ergInfo: ErgIsolationInfo = ErgIsolationInfo()
)

enum class ScanSource {
    CAMERA,
    GALLERY,
    SAMPLE,
    MANUAL
}

data class PlateAnalysisResult(
    val kemlerCode: String,
    val unNumber: String,
    val kemlerDetail: KemlerCodeDetail,
    val unSubstance: UnSubstance,
    val imagePath: String? = null,
    val source: ScanSource = ScanSource.MANUAL,
    val timestamp: Long = System.currentTimeMillis(),
    val aiNotes: String? = null
)

data class AdrClassInfo(
    val classNumber: String,
    val title: String,
    val description: String,
    val colorHex: Long,
    val iconName: String
)
