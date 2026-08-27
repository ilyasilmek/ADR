package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.PlateAnalysisResult

object EmergencySosHelper {

    fun dial112(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:112")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "112 arama ekranı açılamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun buildEmergencyReportText(result: PlateAnalysisResult): String {
        val s = result.unSubstance
        val k = result.kemlerDetail
        val erg = s.ergInfo
        val waterWarning = if (k.isWaterReactive) "⚠️ [KRİTİK UYARI: MADDE SU İLE TEHLİKELİ TEPKİME VERİR! SÖNDÜRMEDE VE MÜDAHALEDE ASLA SU KULLANMAYIN!]" else "Su Yasağı Yok (Standart Prosedür)"

        return """
            🚨 ADR ACİL DURUM KİMYASAL MÜDAHALE BİLDİRİMİ 🚨
            ------------------------------------------------
            📍 UN MADDE NO: UN ${result.unNumber}
            🧪 MADDE ADI: ${s.nameTr} (${s.nameEn})
            🏷️ ADR TEHLİKE SINIFI: ${s.adrClass} - ${s.classDescription}
            📦 AMBALAJ GRUBU: ${s.packingGroup.ifEmpty { "Belirtilmemiş" }}
            
            ⚠️ KEMLER TEHLİKE TANIM KODU: ${result.kemlerCode}
            📌 TEHLİKE TANIMI: ${k.title}
            ${k.fullDescription}
            
            🚨 SU TEPKİMESİ: $waterWarning
            
            🛡️ ERG ACİL İZOLASYON & TAHLİYE MESAFELERİ:
            • ERG Rehber No: ${erg.guideNumber}
            • İlk İzolasyon Alanı: ${erg.initialIsolationMeters} metre çapında tahliye
            • Küçük Döküntü Tahliye Mesafesi: Gündüz ${erg.smallSpillDayMeters}m / Gece ${erg.smallSpillNightMeters}m
            • Büyük Döküntü Tahliye Mesafesi: Gündüz ${erg.largeSpillDayMeters}m / Gece ${erg.largeSpillNightMeters}m
            • Tank/Yangın Güvenlik Mesafesi: ${erg.fireTankEvacuationMeters} metre
            
            🚒 YANGINDA MÜDAHALE: ${s.fireAction}
            ⚠️ DÖKÜLME / SIZINTI: ${s.spillAction}
            🚑 İLK YARDIM: ${s.firstAid}
            🦺 GEREKLİ KKD: ${s.ppeRequired.joinToString(", ")}
            
            ℹ️ Bu bildirim ADR Levha Okuyucu uygulaması tarafından UNECE & ERG standartlarına göre otomatik oluşturulmuştur.
        """.trimIndent()
    }

    fun shareEmergencyReport(context: Context, result: PlateAnalysisResult) {
        try {
            val shareText = buildEmergencyReportText(result)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "ACİL DURUM: UN ${result.unNumber} ADR Kimyasal Bildirimi")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Acil Durum Raporunu Paylaş (112 / AFAD / SMS / WhatsApp)")
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Paylaşım başlatılamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
