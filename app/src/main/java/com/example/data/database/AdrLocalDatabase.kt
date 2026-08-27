package com.example.data.database

import com.example.data.model.AdrClassInfo
import com.example.data.model.DigitExplanation
import com.example.data.model.ErgIsolationInfo
import com.example.data.model.HazardPlacard
import com.example.data.model.KemlerCodeDetail
import com.example.data.model.PlacardSymbolType
import com.example.data.model.UnSubstance

object AdrLocalDatabase {

    // Known standard Kemler codes with official ADR definitions
    private val standardKemlerCodes = mapOf(
        "20" to "Boğucu gaz veya ikincil riski olmayan gaz",
        "22" to "Soğutulmuş sıvılaştırılmış gaz, boğucu",
        "223" to "Soğutulmuş sıvılaştırılmış alevlenir gaz",
        "225" to "Soğutulmuş sıvılaştırılmış oksitleyici (yangını körükleyen) gaz",
        "23" to "Alevlenir gaz",
        "238" to "Alevlenir ve aşındırıcı gaz",
        "239" to "Alevlenir gaz, kendiliğinden ani şiddetli reaksiyona girebilir",
        "25" to "Oksitleyici (yangını tetikleyen) gaz",
        "26" to "Toksik (zehirli) gaz",
        "263" to "Toksik ve alevlenir gaz",
        "265" to "Toksik ve oksitleyici gaz",
        "268" to "Toksik ve aşındırıcı (korozif) gaz",
        "30" to "Alevlenir sıvı (parlama noktası 23°C ila 60°C arası) veya kendiliğinden ısınan sıvı",
        "323" to "Alevlenir sıvı, su ile temasında alevlenir gaz çıkarır",
        "X323" to "Alevlenir sıvı, su ile tehlikeli reaksiyona girerek alevlenir gaz çıkarır (SU KULLANMAYIN)",
        "33" to "Çok alevlenir sıvı (parlama noktası 23°C altında)",
        "333" to "Piroforik sıvı (havayla temasında kendiliğinden alev alır)",
        "X333" to "Piroforik sıvı, su ile tehlikeli reaksiyona girer (SU KULLANMAYIN)",
        "336" to "Çok alevlenir ve toksik (zehirli) sıvı",
        "338" to "Çok alevlenir ve aşındırıcı (korozif) sıvı",
        "X338" to "Çok alevlenir ve aşındırıcı sıvı, su ile tehlikeli reaksiyona girer (SU KULLANMAYIN)",
        "339" to "Çok alevlenir sıvı, kendiliğinden ani şiddetli reaksiyona girebilir",
        "36" to "Alevlenir sıvı, toksik veya kendiliğinden ısınan toksik sıvı",
        "362" to "Alevlenir sıvı, toksik, su ile temasında alevlenir gaz çıkarır",
        "X362" to "Alevlenir ve toksik sıvı, su ile tehlikeli tepkimeye girer (SU KULLANMAYIN)",
        "368" to "Alevlenir sıvı, toksik ve aşındırıcı",
        "38" to "Alevlenir sıvı, aşındırıcı",
        "382" to "Alevlenir sıvı, aşındırıcı, su ile temasında alevlenir gaz çıkarır",
        "X382" to "Alevlenir ve aşındırıcı sıvı, su ile tehlikeli tepkimeye girer (SU KULLANMAYIN)",
        "39" to "Alevlenir sıvı, kendiliğinden ani polimerizasyona veya şiddetli reaksiyona girebilir",
        "40" to "Alevlenir katı veya kendiliğinden ısınan katı veya kendiliğinden tepkimeye giren madde",
        "423" to "Katı madde, su ile temas ettiğinde alevlenir gaz çıkarır",
        "X423" to "Katı madde, su ile tehlikeli reaksiyona girip alevlenir gaz çıkarır (SU KULLANMAYIN)",
        "44" to "Alevlenir katı, erimiş halde yüksek sıcaklıkta",
        "446" to "Alevlenir katı, toksik, erimiş halde yüksek sıcaklıkta",
        "46" to "Alevlenir veya kendiliğinden ısınan katı, toksik",
        "462" to "Toksik katı, su ile temasında alevlenir gaz çıkarır",
        "X462" to "Toksik katı madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "48" to "Alevlenir veya kendiliğinden ısınan katı, aşındırıcı",
        "482" to "Aşındırıcı katı, su ile temasında alevlenir gaz çıkarır",
        "X482" to "Aşındırıcı katı madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "50" to "Oksitleyici (yangını başlatan / körükleyen) madde",
        "539" to "Alevlenir organik peroksit",
        "55" to "Kuvvetli oksitleyici madde",
        "556" to "Kuvvetli oksitleyici ve toksik madde",
        "558" to "Kuvvetli oksitleyici ve aşındırıcı madde",
        "559" to "Kuvvetli oksitleyici madde, kendiliğinden ani şiddetli reaksiyona girebilir",
        "56" to "Oksitleyici madde, toksik",
        "568" to "Oksitleyici madde, toksik ve aşındırıcı",
        "58" to "Oksitleyici madde, aşındırıcı",
        "59" to "Oksitleyici madde, kendiliğinden ani reaksiyona girebilir",
        "60" to "Toksik (zehirli) veya zararlı madde",
        "606" to "Bulaşıcı (enfeksiyöz) madde",
        "623" to "Toksik sıvı, su ile temasında alevlenir gaz çıkarır",
        "63" to "Toksik ve alevlenir madde",
        "638" to "Toksik, alevlenir ve aşındırıcı madde",
        "639" to "Toksik ve alevlenir madde, kendiliğinden ani reaksiyona girebilir",
        "64" to "Toksik ve alevlenir katı madde",
        "65" to "Toksik ve oksitleyici madde",
        "66" to "Çok toksik (yüksek derecede zehirli / öldürücü) madde",
        "663" to "Çok toksik ve alevlenir madde",
        "664" to "Çok toksik ve alevlenir katı madde",
        "665" to "Çok toksik ve oksitleyici madde",
        "668" to "Çok toksik ve aşındırıcı madde",
        "X668" to "Çok toksik ve aşındırıcı madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "669" to "Çok toksik madde, kendiliğinden ani reaksiyona girebilir",
        "68" to "Toksik ve aşındırıcı madde",
        "69" to "Toksik madde, kendiliğinden ani reaksiyona girebilir",
        "70" to "Radyoaktif madde",
        "768" to "Radyoaktif, toksik ve aşındırıcı madde",
        "78" to "Radyoaktif ve aşındırıcı madde",
        "80" to "Aşındırıcı (korozif) veya hafif aşındırıcı madde",
        "X80" to "Aşındırıcı madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "823" to "Aşındırıcı sıvı, su ile temasında alevlenir gaz çıkarır",
        "83" to "Aşındırıcı ve alevlenir madde",
        "X83" to "Aşındırıcı ve alevlenir madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "839" to "Aşındırıcı ve alevlenir madde, kendiliğinden ani reaksiyona girebilir",
        "84" to "Aşındırıcı ve alevlenir katı",
        "85" to "Aşındırıcı ve oksitleyici madde",
        "856" to "Aşındırıcı, oksitleyici ve toksik madde",
        "86" to "Aşındırıcı ve toksik madde",
        "88" to "Çok aşındırıcı (kuvvetli korozif) madde",
        "X88" to "Çok aşındırıcı madde, su ile tehlikeli reaksiyona girer (SU KULLANMAYIN)",
        "883" to "Çok aşındırıcı ve alevlenir madde",
        "884" to "Çok aşındırıcı ve alevlenir katı",
        "885" to "Çok aşındırıcı ve oksitleyici madde",
        "886" to "Çok aşındırıcı ve toksik madde",
        "X886" to "Çok aşındırıcı ve toksik madde, su ile tehlikeli reaksiyon verir (SU KULLANMAYIN)",
        "90" to "Çevre için tehlikeli madde veya muhtelif tehlikeli madde",
        "99" to "Yüksek sıcaklıkta taşınan muhtelif tehlikeli madde (örn. sıvı asfalt/bitüm)"
    )

    private val digitBaseMeanings = mapOf(
        '2' to "Gaz salımı (basınç veya reaksiyon kaynaklı gaz)",
        '3' to "Alevlenir sıvı veya gaz",
        '4' to "Alevlenir katı veya kendiliğinden ısınan katı",
        '5' to "Oksitleyici (yangını körükleyen / başlatan) madde",
        '6' to "Toksik (zehirli) veya bulaşıcı madde",
        '7' to "Radyoaktif madde",
        '8' to "Aşındırıcı (korozif / asidik-bazik) madde",
        '9' to "Kendiliğinden ani reaksiyon riski / Çevreye zararlı madde",
        '0' to "İkincil risk yok (tehlikenin tekil olduğunu gösterir)"
    )

    fun decodeKemlerCode(rawCode: String): KemlerCodeDetail {
        val cleanCode = rawCode.trim().uppercase()
        val isWaterReactive = cleanCode.startsWith("X")
        val numberPart = if (isWaterReactive) cleanCode.substring(1) else cleanCode

        val breakdowns = mutableListOf<DigitExplanation>()

        if (isWaterReactive) {
            breakdowns.add(
                DigitExplanation(
                    digit = "X",
                    position = "Ön Ek (Prefix)",
                    meaning = "Madde su ile tehlikeli şekilde reaksiyona girer! Söndürmede veya müdahalede KESİNLİKLE SU KULLANILMAMALIDIR.",
                    isWarning = true
                )
            )
        }

        var primaryHazard = ""
        val secondaryHazards = mutableListOf<String>()

        for (i in numberPart.indices) {
            val char = numberPart[i]
            val isDuplicate = (i > 0 && char == numberPart[i - 1] && char != '0')
            val baseMeaning = digitBaseMeanings[char] ?: "Bilinmeyen tehlike parametresi ($char)"

            val meaningStr = when {
                i == 0 -> {
                    primaryHazard = baseMeaning
                    "Birincil Tehlike: $baseMeaning"
                }
                isDuplicate -> {
                    val intensified = when (char) {
                        '2' -> "Soğutulmuş gaz veya yoğun gaz salımı"
                        '3' -> "Çok alevlenir sıvı (Parlama noktası < 23°C) - Tehlike Şiddetlenmesi"
                        '4' -> "Erimiş halde yüksek sıcaklıkta alevlenir katı"
                        '5' -> "Kuvvetli oksitleyici madde - Tehlike Şiddetlenmesi"
                        '6' -> "Çok toksik / öldürücü zehirli madde - Tehlike Şiddetlenmesi"
                        '8' -> "Çok aşındırıcı (kuvvetli asit/baz) - Tehlike Şiddetlenmesi"
                        '9' -> "Yüksek sıcaklıkta taşınan madde"
                        else -> "Tehlike derecesinin yüksek olduğunu belirtir ($char)"
                    }
                    secondaryHazards.add(intensified)
                    "Rakam Tekrarı (Şiddetlenme): $intensified"
                }
                char == '0' -> {
                    "İkincil Tehlike Yok: Maddenin ek bir ikincil riski bulunmadığını teyit eder."
                }
                else -> {
                    val sec = "Ek Tehlike: $baseMeaning"
                    secondaryHazards.add(baseMeaning)
                    sec
                }
            }

            breakdowns.add(
                DigitExplanation(
                    digit = char.toString(),
                    position = "${i + 1}. Hane",
                    meaning = meaningStr,
                    isWarning = (char == '6' || char == '8' || isDuplicate)
                )
            )
        }

        val knownDescription = standardKemlerCodes[cleanCode]
        val fullDescription = knownDescription ?: buildString {
            append("Özel Tehlike Kodu: ")
            if (isWaterReactive) append("Suyla tehlikeli reaksiyon veren, ")
            append(breakdowns.filter { it.digit != "X" }.joinToString(", ") { it.meaning })
        }

        val title = when {
            cleanCode == "33" -> "Çok Alevlenir Sıvı (Benzin vb.)"
            cleanCode == "30" -> "Alevlenir Sıvı (Dizel vb.)"
            cleanCode == "23" -> "Alevlenir Gaz (LPG, Propan)"
            cleanCode == "80" -> "Aşındırıcı Madde (Asit/Baz)"
            cleanCode == "88" -> "Çok Aşındırıcı Madde"
            cleanCode == "X338" -> "Suyla Reaktif, Çok Alevlenir & Aşındırıcı"
            cleanCode == "268" -> "Toksik ve Aşındırıcı Gaz"
            cleanCode == "60" -> "Toksik Madde"
            cleanCode == "66" -> "Çok Toksik (Öldürücü) Madde"
            cleanCode == "50" -> "Oksitleyici Madde"
            cleanCode == "90" -> "Çevreye Zararlı Madde"
            isWaterReactive -> "Su ile Reaktif Tehlikeli Madde ($cleanCode)"
            else -> "Tehlike Tanımlama Kodu: $cleanCode"
        }

        return KemlerCodeDetail(
            code = cleanCode,
            title = title,
            fullDescription = fullDescription,
            isWaterReactive = isWaterReactive,
            primaryHazard = primaryHazard.ifEmpty { "Belirtilen sınıfa ait tehlike" },
            secondaryHazards = secondaryHazards,
            digitBreakdowns = breakdowns
        )
    }

    // Comprehensive UNECE Rev.24 Hazardous Goods List
    private val unDatabase = listOf(
        UnSubstance(
            unNumber = "1203",
            nameTr = "BENZİN / MOTOR RUHU / KURŞUNSUZ BENZİN",
            nameEn = "MOTOR SPIRIT / GASOLINE / PETROL",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar (Flash Point < 23°C)",
            packingGroup = "PG II (Orta Tehlike Derecesi)",
            hazardDescription = "Çok alevlenir sıvı ve buhar. Buharları hava ile patlayıcı karışım oluşturur. Statik elektrik veya kıvılcımla kolayca tutuşur. Solunduğunda sağlığa zararlıdır, suda yüzerek yangını yayabilir.",
            fireAction = "DİKKAT: Doğrudan basınçlı su jeti KULLANMAYIN (yangını yayar). Yangın söndürmede köpük, kuru kimyevi toz (KKT) veya CO2 kullanın. Tankı uzaktan su sisiyle soğutun.",
            spillAction = "Tüm tutuşma kaynaklarını derhal kapatın. Sigara içmeyin. Statik boşalma önlemleri alın. Dökülen sıvıyı kum, toprak veya yanmaz emici maddeyle çevreleyin. Kanalizasyona karışmasını engelleyin.",
            firstAid = "Maruz kalan kişiyi temiz havaya çıkarın. Nefes almıyorsa suni solunum uygulayın. Cilde temas ederse kirlenmiş giysileri çıkarıp bol su ve sabunla en az 15 dk yıkayın.",
            ppeRequired = listOf("Antistatik & Alev Geciktirici Giysi", "Organik Buhar Maskesi (A2)", "Nitril / Kimyasal Koruma Eldiveni", "Koruyucu Gözlük / Yüz Siperi"),
            isolationDistance = "İlk izolasyon yarıçapı: 50 metre. Büyük tank yangınlarında tahliye yarıçapı: 800 metre.",
            defaultKemler = "33"
        ),
        UnSubstance(
            unNumber = "1202",
            nameTr = "DİZEL YAKITI / MOTORİN / GAZYAĞI / ISITMA YAĞI (HAFİF)",
            nameEn = "DIESEL FUEL / GAS OIL / HEATING OIL, LIGHT",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar (Flash Point > 60°C)",
            packingGroup = "PG III (Düşük Tehlike Derecesi)",
            hazardDescription = "Alevlenebilir sıvı. Isıtıldığında veya aerosol/sis halindeyken kolayca alev alır. Su kaynaklarına ve çevreye zararlıdır.",
            fireAction = "Köpük, kuru kimyevi toz veya su sisi kullanın. Düz su jeti kullanmaktan kaçının.",
            spillAction = "Kıvılcım kaynaklarını uzaklaştırın. Dökülen maddeyi kum veya toprakla barajlayarak kanalizasyon ve su yollarından uzak tutun.",
            firstAid = "Temiz havaya çıkarın. Cilde temas halinde sabunlu su ile yıkayın. Yutulması durumunda KUSTURMAYIN, derhal tıbbi yardım alın.",
            ppeRequired = listOf("Kimyasal dayanıklı eldiven", "Koruyucu gözlük", "İş tulumu", "Gerektiğinde buhar filtresi"),
            isolationDistance = "İlk izolasyon mesafesi: 25 metre. Tank yangınlarında: 300 metre.",
            defaultKemler = "30"
        ),
        UnSubstance(
            unNumber = "1075",
            nameTr = "PETROL GAZLARI, SIVILAŞTIRILMIŞ (LPG)",
            nameEn = "PETROLEUM GASES, LIQUEFIED (LPG)",
            adrClass = "Sınıf 2.1",
            classDescription = "Alevlenir Gazlar",
            packingGroup = "Gazlar (Paketleme Grubu Yok)",
            hazardDescription = "Aşırı alevlenir basınçlı gaz. Havadan ağırdır, çukur alanlarda ve bodrumlarda birikir. Kaçak halinde patlayıcı bulut oluşturur. Sıvı ile temas soğuk yanıklarına neden olur.",
            fireAction = "Gaz kaçağı güvenli şekilde KESİLMEDEN alevi söndürmeyin! Yangın söndürülürse kontrolsüz gaz birikip infilak edebilir. Tankı uzaktan sürekli su sıkarak soğutun. İnsansız hortum monitörleri kullanın.",
            spillAction = "Tüm alanı derhal tahliye edin. Çukur alanları havalandırın. Kıvılcım ve elektrik anahtarlarını KULLANMAYIN. Gaz bulutunu su sisi ile dağıtın.",
            firstAid = "Soğuk yanıklarında ovuşturmayın, ılık suyla yıkayın. Boğulma riski olan ortamdan hemen açık havaya taşıyın.",
            ppeRequired = listOf("Kriyojenik / Isı Yalıtımlı Eldiven", "Tam Yüz SCBA (Temiz Hava Solunum Cihazı)", "Antistatik Yangına Dayanıklı Elbise"),
            isolationDistance = "Küçük sızıntı: 100m. Büyük sızıntı / Tank tehlikesi: 800-1600m BLEVE riski!",
            defaultKemler = "23"
        ),
        UnSubstance(
            unNumber = "1965",
            nameTr = "HİDROKARBON GAZ KARIŞIMI, SIVILAŞTIRILMIŞ, B.B.B. (LPG / Karışım A, A0, A1, B, C)",
            nameEn = "HYDROCARBON GAS MIXTURE, LIQUEFIED, N.O.S.",
            adrClass = "Sınıf 2.1",
            classDescription = "Alevlenir Gazlar",
            packingGroup = "Gazlar",
            hazardDescription = "Aşırı derecede alevlenir sıvılaştırılmış gaz. Basınç altında depolanır. Havayla kolayca patlayıcı gaz karışımı üretir. BLEVE (Kaynayan Sıvı Genleşen Buhar Patlaması) tehlikesi mevcuttur.",
            fireAction = "Gaz akışı kesilmeden yangını söndürmeyin. Tank gövdesini su püskürterek serin tutun.",
            spillAction = "Acil tahliye uygulayın. Havalandırma sağlayın. Kıvılcım oluşturan alet kullanmayın.",
            firstAid = "Açık havaya çıkarın, suni solunum gerekirse oksijen verin. Donma yaralanmalarında ılık su uygulayın.",
            ppeRequired = listOf("Isı yalıtımlı koruyucu eldiven", "SCBA solunum seti", "Antistatik yangın elbisesi"),
            isolationDistance = "İlk izolasyon: 100 metre. Tank yangınında: 1600 metre tahliye.",
            defaultKemler = "23"
        ),
        UnSubstance(
            unNumber = "1978",
            nameTr = "PROPAN",
            nameEn = "PROPANE",
            adrClass = "Sınıf 2.1",
            classDescription = "Alevlenir Gazlar",
            packingGroup = "Gazlar",
            hazardDescription = "Çok alevlenir gaz. Basınçlı kapta sıvı haldedir. Kaçak durumunda buharlaşarak hızla genişler ve zemin boyunca yayılır.",
            fireAction = "Gaz akışı durdurulabiliyorsa söndürün. Aksi halde tankı uzaktan suyla soğutmaya devam edin.",
            spillAction = "Patlama tehlikesi! Tüm elektrik ve motorları kapatın. Su perdesiyle gazı dağıtın.",
            firstAid = "Hastayı temiz havaya nakledin. Sıvı temasında derhal ılık su banyosu yaptırın.",
            ppeRequired = listOf("Kriyojenik koruyucu eldiven", "Basınçlı solunum cihazı", "Alev geciktirici tulum"),
            isolationDistance = "İzolasyon: 100 metre. Büyük tehlikede 800 metre.",
            defaultKemler = "23"
        ),
        UnSubstance(
            unNumber = "1011",
            nameTr = "BÜTAN",
            nameEn = "BUTANE",
            adrClass = "Sınıf 2.1",
            classDescription = "Alevlenir Gazlar",
            packingGroup = "Gazlar",
            hazardDescription = "Alevlenir gaz. Havadan ağırdır. Havalandırmasız bodrumlarda birikip patlama yaratır.",
            fireAction = "Gaz kaynağı kesilmeden söndürülmez. Kuru kimyevi toz, CO2 veya su sisi ile soğutma.",
            spillAction = "Alan boşaltılmalı, statik kıvılcım önlenmelidir.",
            firstAid = "Temiz havaya çıkarın, solunumu destekleyin.",
            ppeRequired = listOf("Soğuk korumalı eldiven", "SCBA", "Antistatik giysi"),
            isolationDistance = "İzolasyon: 100 metre.",
            defaultKemler = "23"
        ),
        UnSubstance(
            unNumber = "1830",
            nameTr = "SÜLFÜRİK ASİT (%51'den fazla asit içeren) / ZAÇ YAĞI",
            nameEn = "SULPHURIC ACID with more than 51% acid",
            adrClass = "Sınıf 8",
            classDescription = "Aşındırıcı (Korozif) Maddeler",
            packingGroup = "PG II (Orta Tehlike)",
            hazardDescription = "Çok kuvvetli asit. Ciltte, gözde ve dokularda şiddetli kimyasal yanıklara ve körlüğe neden olur. Su ile temas ettiğinde ŞİDDETLİ EKZOTERMİK ISI VE SIÇRAMA yapar. Metallerle reaksiyona girerek alevlenir hidrojen gazı çıkarır.",
            fireAction = "DİKKAT: Asidin doğrudan içine su DÖKMEYİN! Yangın çevresindeki alevleri söndürmek için CO2 veya kuru toz kullanın. Su sisi yalnızca kapları dışarıdan soğutmak için uzaktan kullanılabilir.",
            spillAction = "Asidi kireç, sodyum bikarbonat veya soda külü ile yavaşça nötralize edin. Asidin üzerine su dökmeyin! Dökülen alanı kumla kapatıp toplayın.",
            firstAid = "Cilt ve göze temas halinde derhal EN AZ 20-30 DAKİKA kesintisiz bol tazyiksiz suyla yıkayın. Gözleri açık tutun. Derhal acil servise sevk edin.",
            ppeRequired = listOf("Tam Kimyasal Koruyucu Asit Elbisesi (Tip 1/2)", "Butil Kauçuk / Neopren Eldiven", "Tam Yüz Siperi ve Asit Gözlüğü", "Asit Gazı Filtreli Maske"),
            isolationDistance = "İlk izolasyon: 50 metre. Büyük sızıntılarda rüzgar altı: 150 metre.",
            defaultKemler = "80"
        ),
        UnSubstance(
            unNumber = "1789",
            nameTr = "HİDROKLORİK ASİT / TUZ RUHU",
            nameEn = "HYDROCHLORIC ACID",
            adrClass = "Sınıf 8",
            classDescription = "Aşındırıcı Maddeler",
            packingGroup = "PG II (Orta Tehlike)",
            hazardDescription = "Kuvvetli aşındırıcı sıvı. Havayla temas ettiğinde yoğun boğucu ve tahriş edici beyaz asit buharları (HCl gazı) çıkarır. Solunum yollarında ağır hasara yol açar.",
            fireAction = "Madde alev almaz. Çevre yangınlarında kuru kimyevi toz veya CO2 kullanın. Asit dumanlarını bastırmak için su sisi püskürtün.",
            spillAction = "Asit buharını bastırmak için su sisi perdesi oluşturun. Sıvıyı kireç veya sodyum karbonat ile nötralize edin.",
            firstAid = "Buhar solunduysa derhal temiz havaya çıkarın, yarı oturur pozisyonda dinlendirin. Cildi bol suyla yıkayın.",
            ppeRequired = listOf("Asit tulumu ve önlük", "Asit buharı filtreli tam yüz maskesi", "PVC/Neopren eldiven", "Asit çizmesi"),
            isolationDistance = "Küçük sızıntı: 30 metre. Büyük dökülmede rüzgar yönü: 200 metre.",
            defaultKemler = "80"
        ),
        UnSubstance(
            unNumber = "1824",
            nameTr = "SODYUM HİDROKSİT ÇÖZELTİSİ / KOSTİK SODA",
            nameEn = "SODIUM HYDROXIDE SOLUTION",
            adrClass = "Sınıf 8",
            classDescription = "Aşındırıcı Maddeler (Kuvvetli Baz)",
            packingGroup = "PG II",
            hazardDescription = "Kuvvetli alkali/bazik aşındırıcı. Dokuları derinden eriterek geri dönüşümsüz kimyasal yanık ve görme kaybına sebep olur. Alüminyum ve çinko metalleri ile reaksiyona girerek patlayıcı hidrojen gazı açığa çıkarır.",
            fireAction = "Madde yanmaz. Yangın söndürmede uygun genel söndürücüler kullanılır.",
            spillAction = "Dökülen alanı zayıf asit (sitrik asit veya seyreltik asetik asit) ile nötralize edin.",
            firstAid = "Göz ve cilde temasında en az 20 dakika kesintisiz bol suyla yıkayın. Sabun kullanmayın.",
            ppeRequired = listOf("Alkaliye dayanıklı eldiven (Kauçuk/Nitril)", "Yüz siperliği ve kimyasal gözlük", "Alkali geçirmez koruyucu önlük/tulum"),
            isolationDistance = "İlk izolasyon: 30 metre.",
            defaultKemler = "80"
        ),
        UnSubstance(
            unNumber = "2031",
            nameTr = "NİTRİK ASİT (%70'ten az / %70'ten fazla dumanlı)",
            nameEn = "NITRIC ACID",
            adrClass = "Sınıf 8 (5.1)",
            classDescription = "Aşındırıcı ve Oksitleyici Sıvılar",
            packingGroup = "PG I / II",
            hazardDescription = "Kuvvetli aşındırıcı ve oksitleyici. Organik maddelerle (ahşap, kumaş vb.) temas ettiğinde kendiliğinden yangın çıkarabilir. Ölümcül toksik azot oksit (kızıl-kahverengi NOx gazı) çıkarır.",
            fireAction = "Su sisi, köpük veya kuru kimyevi toz kullanın. Yangın dumanlarını solumayın.",
            spillAction = "Talaş veya ahşap emiciler KULLANMAYIN (yangın çıkarır). İnorganik emiciler (kum) kullanın.",
            firstAid = "Toksik duman solunmuşsa belirtiler saatler sonra ortaya çıkabilir (akciğer ödemi riski), hemen hastaneye götürün.",
            ppeRequired = listOf("Ağır hizmet asit/oksitleyici giysisi", "SCBA solunum seti", "Butil eldiven"),
            isolationDistance = "İlk izolasyon: 100 metre. Rüzgar altı: 300 metre.",
            defaultKemler = "85 / 885"
        ),
        UnSubstance(
            unNumber = "1005",
            nameTr = "SUSUZ AMONYAK (ANHİDRÖZ)",
            nameEn = "AMMONIA, ANHYDROUS",
            adrClass = "Sınıf 2.3 (8)",
            classDescription = "Toksik ve Aşındırıcı Gazlar",
            packingGroup = "Gazlar",
            hazardDescription = "Keskin kokulu, çok toksik ve aşındırıcı gaz. Basınç altında sıvılaştırılmıştır. Solunduğunda solunum yollarını yakar ve boğar. Gözlerde kalıcı körlük yapar. Belirli konsantrasyonlarda alevlenebilir.",
            fireAction = "Su sisi püskürterek amonyak buharlarını absorbe edin ve tankı soğutun. Doğrudan sıvı gölüne su sıkmayın.",
            spillAction = "Rüzgar yönünde geniş tahliye yapın. Amonyak bulutunu su sisi perdeleri ile çökertin.",
            firstAid = "Hastayı acilen temiz havaya alın, nemli oksijen verin. Gözleri bol suyla yıkayın.",
            ppeRequired = listOf("Tam korumalı gaz geçirmez kimyasal elbise (Tip 1)", "SCBA pozitif basınçlı solunum cihazı"),
            isolationDistance = "Küçük kaçak: 150m. Büyük tanker sızıntısı: Gündüz 1000m, Gece 2500m tahliye!",
            defaultKemler = "268"
        ),
        UnSubstance(
            unNumber = "1017",
            nameTr = "KLOR GAZI",
            nameEn = "CHLORINE",
            adrClass = "Sınıf 2.3 (5.1, 8)",
            classDescription = "Toksik, Oksitleyici ve Aşındırıcı Gaz",
            packingGroup = "Gazlar",
            hazardDescription = "Yeşilimsi-sarı renkli, havadan ağır, son derece toksik ve ölümcül boğucu gaz. Güçlü oksitleyicidir, alev almayı kolaylaştırır. Suyla temasında hidroklorik asit ve hipokloröz asit oluşturur.",
            fireAction = "Klor yanmaz fakat yangını şiddetlendirir. Klor kabına DOĞRUDAN SU SIKMAYIN (asit oluşturup sızıntıyı büyütür). Çevreyi soğutun.",
            spillAction = "Derhal rüzgar üstüne kaçın. Kaçak kabını sızıntı yukarı gelecek şekilde döndürün (sıvı yerine gaz çıksın).",
            firstAid = "Yaralıyı derhal gazlı ortamdan çıkarın. Dinlendirin ve asla hareket ettirmeyin. Tıbbi oksijen desteği sağlayın.",
            ppeRequired = listOf("Seviye A Gaz Geçirmez Koruyucu Kıyafet", "SCBA Bağımsız Solunum Cihazı"),
            isolationDistance = "Küçük sızıntı: 200m. Tank sızıntısı: Gündüz 1500m, Gece 4000m acil tahliye!",
            defaultKemler = "265 / 268"
        ),
        UnSubstance(
            unNumber = "1230",
            nameTr = "METANOL / METİL ALKOL",
            nameEn = "METHANOL / METHYL ALCOHOL",
            adrClass = "Sınıf 3 (6.1)",
            classDescription = "Alevlenir Sıvılar ve Toksik Maddeler",
            packingGroup = "PG II",
            hazardDescription = "Çok alevlenir ve toksik sıvı. Alevi gün ışığında neredeyse görünmezdir! Yutulması, solunması veya ciltten emilmesi körlüğe ve ölüme sebep olur.",
            fireAction = "Alkol dayanıklı köpük (AR-AFFF), kuru toz veya CO2 kullanın. Alev görünmeyebileceğinden termal kamera ile kontrol edin.",
            spillAction = "Kıvılcım kaynaklarını kapatın. Buhar solumaktan kaçının. Sıvıyı absorbe edin.",
            firstAid = "Yutulması durumunda acil tıbbi panzehir (etanol/fomepizol tedavisi) gereklidir. Zaman kaybetmeden hastaneye sevk edin.",
            ppeRequired = listOf("Organik buhar filtreli gaz maskesi", "Butil kauçuk eldiven", "Alev almaz antistatik tulum"),
            isolationDistance = "İlk izolasyon: 50 metre.",
            defaultKemler = "336"
        ),
        UnSubstance(
            unNumber = "1170",
            nameTr = "ETANOL / ETİL ALKOL ÇÖZELTİSİ",
            nameEn = "ETHANOL / ETHYL ALCOHOL SOLUTION",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar",
            packingGroup = "PG II / III",
            hazardDescription = "Alevlenir sıvı ve buhar. Buharları hava ile patlayıcı karışım oluşturur.",
            fireAction = "Alkole dayanıklı köpük, kuru kimyevi toz veya CO2 kullanın.",
            spillAction = "Kıvılcım kaynaklarını kapatın. Kumla absorbe edin.",
            firstAid = "Temiz havaya çıkarın. Göz temasında yıkayın.",
            ppeRequired = listOf("Koruyucu eldiven", "Güvenlik gözlüğü", "Antistatik elbise"),
            isolationDistance = "İlk izolasyon: 50 metre.",
            defaultKemler = "33"
        ),
        UnSubstance(
            unNumber = "1090",
            nameTr = "ASETON",
            nameEn = "ACETONE",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar",
            packingGroup = "PG II",
            hazardDescription = "Kolay alevlenir sıvı ve buhar. Parlama noktası -18°C'dir. Gözlerde şiddetli tahrişe ve uyuşukluğa sebep olur.",
            fireAction = "Köpük, kuru kimyevi toz, CO2 veya su sisi kullanın.",
            spillAction = "Havalandırın, kıvılcım kaynaklarını kapatın.",
            firstAid = "Temiz havaya çıkarın, gözleri yıkayın.",
            ppeRequired = listOf("Bütil eldiven", "Organik buhar maskesi", "Gözlük"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "33"
        ),
        UnSubstance(
            unNumber = "1294",
            nameTr = "TOLUEN",
            nameEn = "TOLUENE",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar",
            packingGroup = "PG II",
            hazardDescription = "Alevlenir sıvı ve buhar. Doğmamış çocuğa zarar verme şüphesi vardır. Merkezi sinir sistemini etkiler.",
            fireAction = "Köpük, kuru toz veya CO2 kullanın.",
            spillAction = "Kanalizasyona karışmasını önleyin. Toprak veya kumla çevreleyin.",
            firstAid = "Temiz havaya çıkarın. Yutulursa kusturmayın.",
            ppeRequired = listOf("Nitril/Viton eldiven", "A2 buhar maskesi", "Kimyasal tulum"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "33"
        ),
        UnSubstance(
            unNumber = "1114",
            nameTr = "BENZEN",
            nameEn = "BENZENE",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar ve Kanserojen Madde",
            packingGroup = "PG II",
            hazardDescription = "Çok alevlenir sıvı. Kanserojendir (Lösemiye sebep olur) ve genetik hasara yol açabilir. Buharını kesinlikle solumayın.",
            fireAction = "Köpük, kuru kimyasal veya CO2. Tam koruyucu solunum cihazı kullanın.",
            spillAction = "Alanı derhal boşaltın. Buharın yayılmasını su sisiyle engelleyin.",
            firstAid = "Temiz havaya alın, acil tıbbi gözetim altına alın.",
            ppeRequired = listOf("SCBA Solunum Cihazı", "Viton kimyasal eldiven", "Tam sızdırmaz tulum"),
            isolationDistance = "İlk izolasyon: 100 metre.",
            defaultKemler = "33"
        ),
        UnSubstance(
            unNumber = "1993",
            nameTr = "ALEVLENİR SIVI, B.B.B. (Başka Türlü Belirtilmemiş)",
            nameEn = "FLAMMABLE LIQUID, N.O.S.",
            adrClass = "Sınıf 3",
            classDescription = "Alevlenir Sıvılar",
            packingGroup = "PG I / II / III",
            hazardDescription = "Alevlenir sıvı bileşik veya karışım. Tutuşma kaynaklarıyla temasında alev alır.",
            fireAction = "Köpük, kuru kimyevi toz veya CO2 kullanın.",
            spillAction = "Kıvılcım kaynaklarını kapatın, emici malzeme ile toplayın.",
            firstAid = "Temiz havaya çıkarın, temas eden cildi yıkayın.",
            ppeRequired = listOf("Antistatik elbise", "Nitril eldiven", "Organik gaz maskesi"),
            isolationDistance = "İlk izolasyon: 50 metre.",
            defaultKemler = "33 / 30"
        ),
        UnSubstance(
            unNumber = "3480",
            nameTr = "LİTYUM İYON PİLLER / BATARYALAR (Şarj Edilebilir)",
            nameEn = "LITHIUM ION BATTERIES",
            adrClass = "Sınıf 9",
            classDescription = "Muhtelif Tehlikeli Maddeler",
            packingGroup = "Sınıf 9 Özel Hükümler",
            hazardDescription = "Termal kaçak (Thermal Runaway) riski. Hasarlı veya aşırı ısınmış piller şiddetli patlama, yoğun alev ve toksik hidroflorik asit (HF) gazı çıkarır. Söndürülmesi çok güçtür.",
            fireAction = "Bol miktarda kesintisiz su ile uzun süreli soğutma yapın veya Lityum batarya söndürme ajanı (F-500 / Vermikülit) kullanın. Yeniden alevlenme riski günlerce sürebilir.",
            spillAction = "Hasarlı pilleri metal kaplara veya yanmaz kum içerisine alın. Buharları solumayın.",
            firstAid = "Pil sızıntısı ve dumanına maruz kalanları derhal temiz havaya çıkarın.",
            ppeRequired = listOf("Termal ve alev dayanıklı eldiven", "SCBA solunum maskesi", "Yüz siperi"),
            isolationDistance = "Yangın halinde: 100 metre izolasyon.",
            defaultKemler = "90"
        ),
        UnSubstance(
            unNumber = "1049",
            nameTr = "HİDROJEN, SIKIŞTIRILMIŞ",
            nameEn = "HYDROGEN, COMPRESSED",
            adrClass = "Sınıf 2.1",
            classDescription = "Alevlenir Gazlar",
            packingGroup = "Gazlar",
            hazardDescription = "Son derece alevlenir ve patlayıcı gaz. Havadan çok hafiftir. Tutuşma aralığı çok geniştir (%4 - %75). Alevi neredeyse renksiz ve görünmezdir.",
            fireAction = "Gaz akışı kesilmeden söndürmeyin. Çevredeki kapları suyla soğutun. Termal kamera ile alev konumunu tespit edin.",
            spillAction = "Alanı tahliye edin. Çatı ve tavan havalandırmalarını açın.",
            firstAid = "Temiz havaya çıkarın.",
            ppeRequired = listOf("SCBA", "Antistatik alev geciktirici kıyafet"),
            isolationDistance = "İzolasyon: 100 metre. Tank yangınında: 800 metre.",
            defaultKemler = "23"
        ),
        UnSubstance(
            unNumber = "1072",
            nameTr = "OKSİJEN, SIKIŞTIRILMIŞ",
            nameEn = "OXYGEN, COMPRESSED",
            adrClass = "Sınıf 2.2 (5.1)",
            classDescription = "Boğucu Olmayan, Oksitleyici Gaz",
            packingGroup = "Gazlar",
            hazardDescription = "Kuvvetli yangın hızlandırıcı. Yağ ve gres ile temasında kendiliğinden patlamalı yanmaya yol açar. Yanıcı maddelerin tutuşma sıcaklığını düşürür.",
            fireAction = "Oksijen kaynağını kapatın. Asla yağlı malzeme temas ettirmeyin.",
            spillAction = "Kaçak olan bölgeyi havalandırın, giysilere sinerse 30 dk açık havada kalın, sigara yakmayın.",
            firstAid = "Açık havada dinlendirin.",
            ppeRequired = listOf("Yağsız temiz eldivenler", "Koruyucu gözlük"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "25"
        ),
        UnSubstance(
            unNumber = "1066",
            nameTr = "AZOT / NİTROJEN, SIKIŞTIRILMIŞ",
            nameEn = "NITROGEN, COMPRESSED",
            adrClass = "Sınıf 2.2",
            classDescription = "Alevlenir Olmayan, Toksik Olmayan Boğucu Gaz",
            packingGroup = "Gazlar",
            hazardDescription = "Boğucu gaz. Havadaki oksijen oranını düşürerek uyarısız bayılma ve ölüme sebep olur.",
            fireAction = "Kapları su ile soğutun.",
            spillAction = "Kapalı alanları zorunlu olarak havalandırın.",
            firstAid = "Derhal açık havaya taşıyın, suni solunum yapın.",
            ppeRequired = listOf("SCBA Bağımsız Solunum Maskesi"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "20"
        ),
        UnSubstance(
            unNumber = "1402",
            nameTr = "KALSİYUM KARBÜR / KARPİT",
            nameEn = "CALCIUM CARBIDE",
            adrClass = "Sınıf 4.3",
            classDescription = "Su ile Temas Ettiğinde Alevlenir Gaz Çıkaran Maddeler",
            packingGroup = "PG I / II",
            hazardDescription = "Su ve nem ile temas ettiğinde son derece patlayıcı ve alevlenir ASETİLEN GAZI açığa çıkarır. Su KESİNLİKLE YASAKTIR!",
            fireAction = "DİKKAT: KESİNLİKLE SU VEYA KÖPÜK KULLANMAYIN! Yalnızca kuru kum, dolomit tozu veya D-sınıfı özel kuru toz söndürücü kullanın.",
            spillAction = "Kuru tutun. Suyla temasını engelleyin. Kuru metal kürekle toplayıp kapalı kuru kaba alın.",
            firstAid = "Ciltteki tozu kuru fırçalayın, ardından yıkayın.",
            ppeRequired = listOf("Toz maskesi", "Su geçirmez kuru koruyucu elbise", "Deri eldiven"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "423 / X423"
        ),
        UnSubstance(
            unNumber = "1428",
            nameTr = "SODYUM (METALİK)",
            nameEn = "SODIUM",
            adrClass = "Sınıf 4.3",
            classDescription = "Su ile Temas Ettiğinde Alevlenir Gaz Çıkaran Madde",
            packingGroup = "PG I",
            hazardDescription = "Su ile temasında ŞİDDETLİ PATLAMA ile yanar, hidrojen gazı ve aşındırıcı kostik oluşturur. SU KULLANILAMAZ!",
            fireAction = "KESİNLİKLE SU, KÖPÜK, CO2 VEYA HALON KULLANMAYIN! Yalnızca kuru sodyum klorür tuzu veya Sınıf D söndürücü toz kullanın.",
            spillAction = "Su ve nemden kesinlikle koruyun.",
            firstAid = "Cilde yapışan parçaları kuru pensle temizleyin.",
            ppeRequired = listOf("D-Sınıfı yangın koruyucu donanım", "Tam yüz siperi"),
            isolationDistance = "İzolasyon: 100 metre.",
            defaultKemler = "X423"
        ),
        UnSubstance(
            unNumber = "1942",
            nameTr = "AMONYUM NİTRAT (%0.2'den az yanıcı madde içeren)",
            nameEn = "AMMONIUM NITRATE",
            adrClass = "Sınıf 5.1",
            classDescription = "Oksitleyici Maddeler",
            packingGroup = "PG III",
            hazardDescription = "Kuvvetli oksitleyici gübre ve kimyasal. Kapalı kapta yangına maruz kaldığında veya kirleticilerle karıştığında KİTLESEL İNFİLAK (Patlama) tehlikesi mevcuttur (Örn: Beyrut Limanı patlaması).",
            fireAction = "Büyük miktarda su ile uzaktan söndürün. Söndürmede buhar veya kimyasal toz yetersiz kalabilir. İnfilak riski varsa derhal TÜM EKİBİ GERİ ÇEKİN!",
            spillAction = "Yanıcı maddelerden (yağ, akaryakıt) uzak tutun.",
            firstAid = "Temiz havaya çıkarın, cildi yıkayın.",
            ppeRequired = listOf("Toz ve duman maskesi", "Koruyucu gözlük"),
            isolationDistance = "Yangın durumunda en az 1600 METRE acil tahliye!",
            defaultKemler = "50"
        ),
        UnSubstance(
            unNumber = "1689",
            nameTr = "SODYUM SİYANÜR, KATI",
            nameEn = "SODIUM CYANIDE, SOLID",
            adrClass = "Sınıf 6.1",
            classDescription = "Toksik (Zehirli) Maddeler",
            packingGroup = "PG I (Yüksek Tehlike)",
            hazardDescription = "Son derece ölümcül zehirdir. Asitlerle veya nemle temas ettiğinde aşırı toksik Hidrojen Siyanür (HCN) gazı çıkarır. Çok küçük dozu bile dakikalar içinde kalbi ve solunumu durdurur.",
            fireAction = "Asitli söndürücü KULLANMAYIN. Kuru toz veya su sisi ile dikkatlice söndürün.",
            spillAction = "Özel gaz geçirmez giysilerle müdahale edin. Asitlerle karışmasını engelleyin.",
            firstAid = "ACİL SİYANÜR ANTİDOT KİTİ (Hidroksokobalamin) uygulanmalıdır. Kazazedeye suni solunum yaparken AĞIZDAN AĞIZA ASLA YAPMAYIN!",
            ppeRequired = listOf("A Seviyesi Gaz Sızdırmaz Giysi", "SCBA Bağımsız Solunum Cihazı"),
            isolationDistance = "İlk izolasyon: 100 metre. Rüzgar altı: 500 metre.",
            defaultKemler = "66"
        ),
        UnSubstance(
            unNumber = "3082",
            nameTr = "ÇEVRE İÇİN TEHLİKELİ MADDE, SIVI, B.B.B.",
            nameEn = "ENVIRONMENTALLY HAZARDOUS SUBSTANCE, LIQUID, N.O.S.",
            adrClass = "Sınıf 9",
            classDescription = "Muhtelif Tehlikeli Maddeler ve Çevre Kirleticiler",
            packingGroup = "PG III",
            hazardDescription = "Sucul yaşam ve toprak organizmaları için uzun süreli zararlı ve zehirlidir. İçme suyu kaynaklarını kirletir.",
            fireAction = "Köpük, kuru toz veya su sisi. Söndürme suyunun çevreye akmasını engelleyin.",
            spillAction = "Dökülen sıvının drenaj, nehir ve kanalizasyona ulaşmasını bentlerle kesin.",
            firstAid = "Temas eden cildi yıkayın.",
            ppeRequired = listOf("Kimyasal koruyucu eldiven", "Tulum"),
            isolationDistance = "İzolasyon: 25 metre.",
            defaultKemler = "90"
        ),
        UnSubstance(
            unNumber = "3257",
            nameTr = "YÜKSEK SICAKLIKTA SIVI, B.B.B. (100°C veya üzeri, Sıvı Asfalt, Katran)",
            nameEn = "ELEVATED TEMPERATURE LIQUID, N.O.S. (Liquid asphalt / bitumen)",
            adrClass = "Sınıf 9",
            classDescription = "Muhtelif Tehlikeli Maddeler",
            packingGroup = "PG III",
            hazardDescription = "100°C - 250°C sıcaklıkta taşınır. Ciltle temasında ağır 3. derece termal yanıklara yol açar. Su ile temas ettiğinde ani buhar patlaması ve köpürme yapar.",
            fireAction = "Sıcak maddeye doğrudan su sıkmayın (fışkırma yapar). Kuru toz veya köpük kullanın.",
            spillAction = "Katılaşması için kumla baraj oluşturun.",
            firstAid = "Termal yanıklarda yapışan zifti zorla soymayın, hemen soğuk suyla soğutun.",
            ppeRequired = listOf("Yüksek ısıya dayanıklı eldiven ve önlük", "Tam yüz siperi"),
            isolationDistance = "İzolasyon: 50 metre.",
            defaultKemler = "99"
        )
    )

    fun findSubstanceByUn(unNumber: String): UnSubstance? {
        val cleanUn = unNumber.trim().padStart(4, '0')
        return unDatabase.firstOrNull { it.unNumber == cleanUn || it.unNumber == unNumber.trim() }
    }

    fun searchSubstances(query: String): List<UnSubstance> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return unDatabase
        return unDatabase.filter {
            it.unNumber.contains(q) ||
            it.nameTr.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.adrClass.lowercase().contains(q) ||
            it.defaultKemler.contains(q)
        }
    }

    fun getAllSubstances(): List<UnSubstance> = unDatabase

    private val hazardPlacardsList: List<HazardPlacard> = listOf(
        HazardPlacard(
            id = "1",
            classNumber = "1",
            title = "Sınıf 1: Patlayıcı Maddeler ve Nesneler",
            symbolName = "Patlayan Bomba Sembolü",
            backgroundColorHex = 0xFFFF9800,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.EXPLOSION,
            description = "Ani gaz ve ısı açığa çıkararak infilak etme riski taşıyan katı veya sıvı patlayıcı kimyasallar.",
            primaryRisk = "Toplu infilak, şarapnel ve basınç dalgası",
            tunnelRestrictionCode = "(B1000C / E)",
            packingGroups = listOf("Grup I (Özel)")
        ),
        HazardPlacard(
            id = "2.1",
            classNumber = "2.1",
            title = "Sınıf 2.1: Alevlenir Gazlar",
            symbolName = "Alev Sembolü",
            backgroundColorHex = 0xFFD32F2F,
            textColorHex = 0xFFFFFFFF,
            symbolType = PlacardSymbolType.FLAME,
            description = "20°C ve 101.3 kPa'da havayla %13 veya daha az oranda alevlenebilir karışım oluşturan gazlar (LPG, Propan, Hidrojen, Asetilen).",
            primaryRisk = "Yangın, BLEVE (kaynayan sıvı buhar patlaması)",
            tunnelRestrictionCode = "(B/D)",
            packingGroups = listOf("Gazlar (PG Yok)")
        ),
        HazardPlacard(
            id = "2.2",
            classNumber = "2.2",
            title = "Sınıf 2.2: Alevlenmeyen ve Toksik Olmayan Gazlar",
            symbolName = "Gaz Tüpü Sembolü",
            backgroundColorHex = 0xFF2E7D32,
            textColorHex = 0xFFFFFFFF,
            symbolType = PlacardSymbolType.GAS_CYLINDER,
            description = "Boğucu veya oksitleyici olan ancak alevlenir ve toksik sınıfına girmeyen gazlar (Azot, Argon, Helyum, Karbondioksit).",
            primaryRisk = "Kapalı alanlarda oksijensizlik (boğulma), yüksek basınç",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("Gazlar (PG Yok)")
        ),
        HazardPlacard(
            id = "2.3",
            classNumber = "2.3",
            title = "Sınıf 2.3: Toksik (Zehirli) Gazlar",
            symbolName = "Kuru Kafa ve Çapraz Kemikler",
            backgroundColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.SKULL,
            description = "İnsan sağlığına zararlı veya öldürücü olduğu bilinen toksik gazlar (Klor, Amonyak, Fosgen, Karbonmonoksit).",
            primaryRisk = "Solunum yoluyla ölümcül zehirlenme ve kimyasal yanık",
            tunnelRestrictionCode = "(D)",
            packingGroups = listOf("Gazlar (PG Yok)")
        ),
        HazardPlacard(
            id = "3",
            classNumber = "3",
            title = "Sınıf 3: Alevlenir Sıvılar",
            symbolName = "Alev Sembolü",
            backgroundColorHex = 0xFFD32F2F,
            textColorHex = 0xFFFFFFFF,
            symbolType = PlacardSymbolType.FLAME,
            description = "Parlama noktası en fazla 60°C olan sıvılar ve erimiş katılar (Benzin, Motorin, Alkol, Aseton, Tiner, Boyalar).",
            primaryRisk = "Kolay alev alma, buhar parlama yangını",
            tunnelRestrictionCode = "(D/E)",
            packingGroups = listOf("PG I (Yüksek)", "PG II (Orta)", "PG III (Düşük)")
        ),
        HazardPlacard(
            id = "4.1",
            classNumber = "4.1",
            title = "Sınıf 4.1: Alevlenir Katılar, Kendiliğinden Tepkimeye Girenler",
            symbolName = "Alev Sembolü (Kırmızı-Beyaz Dikey Çizgili)",
            backgroundColorHex = 0xFFD32F2F,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.FLAME,
            description = "Sürtünmeyle kolayca tutuşabilen katılar ve duyarsızlaştırılmış katı patlayıcılar (Kükürt, Naftalin, Kibrit).",
            primaryRisk = "Sürtünmeyle tutuşma, zehirli duman yayma",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "4.2",
            classNumber = "4.2",
            title = "Sınıf 4.2: Kendiliğinden Yanan Maddeler",
            symbolName = "Alev Sembolü (Üstü Beyaz, Altı Kırmızı)",
            backgroundColorHex = 0xFFD32F2F,
            secondaryColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.FLAME,
            description = "Havayla temas ettiğinde 5 dakika içinde veya büyük miktarlarda kendi kendine ısınarak alev alan maddeler (Aktif karbon, beyaz fosfor).",
            primaryRisk = "Havayla kendiliğinden alev alma",
            tunnelRestrictionCode = "(D/E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "4.3",
            classNumber = "4.3",
            title = "Sınıf 4.3: Su ile Temasında Alevlenir Gaz Çıkaranlar",
            symbolName = "Alev Sembolü (Mavi Zemin)",
            backgroundColorHex = 0xFF1976D2,
            textColorHex = 0xFFFFFFFF,
            symbolType = PlacardSymbolType.FLAME,
            description = "Su ile temas ettiğinde tehlikeli miktarlarda alevlenir gazlar (hidrojen, asetilen) açığa çıkaran maddeler (Kalsiyum karbür/karpit, Sodyum).",
            primaryRisk = "SU KULLANILMAZ! Su ile temasında patlayıcı gaz çıkışı",
            tunnelRestrictionCode = "(D/E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "5.1",
            classNumber = "5.1",
            title = "Sınıf 5.1: Oksitleyici (Yakıcı) Maddeler",
            symbolName = "Daire Üzerinde Alev Sembolü",
            backgroundColorHex = 0xFFFFD600,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.OXIDIZER,
            description = "Genellikle oksijen açığa çıkararak diğer maddelerin yanmasına neden olan veya katkıda bulunan maddeler (Amonyum nitrat gübre, Hidrojen peroksit).",
            primaryRisk = "Yanıcı maddelerle temasında şiddetli patlama ve yangını besleme",
            tunnelRestrictionCode = "(D/E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "5.2",
            classNumber = "5.2",
            title = "Sınıf 5.2: Organik Peroksitler",
            symbolName = "Alev Sembolü (Üst Kırmızı, Alt Sarı)",
            backgroundColorHex = 0xFFFFD600,
            secondaryColorHex = 0xFFD32F2F,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.ORGANIC_PEROXIDE,
            description = "Isıl olarak kararsız, ekzotermik olarak kendi kendine ayrışabilen peroksit yapıdaki organik bileşikler.",
            primaryRisk = "Isı, sürtünme veya darbeyle patlayıcı ayrışma",
            tunnelRestrictionCode = "(B/D / E)",
            packingGroups = listOf("Tip B-F")
        ),
        HazardPlacard(
            id = "6.1",
            classNumber = "6.1",
            title = "Sınıf 6.1: Zehirli (Toksik) Maddeler",
            symbolName = "Kuru Kafa ve Çapraz Kemikler",
            backgroundColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.SKULL,
            description = "Yutulduğunda, solunduğunda veya deriden emildiğinde ölüme veya ciddi sağlık hasarına yol açan maddeler (Siyanür, Pestisitler, Arsenik).",
            primaryRisk = "Akut ve ölümcül kimyasal zehirlenme",
            tunnelRestrictionCode = "(C/D / E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "6.2",
            classNumber = "6.2",
            title = "Sınıf 6.2: Bulaşıcı Maddeler",
            symbolName = "Biyolojik Tehlike (Biyohazard) Sembolü",
            backgroundColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.SKULL,
            description = "Patojen (bakteri, virüs, mantar vb.) içerdiği bilinen veya şüphelenilen biyolojik ve tıbbi atık maddeler.",
            primaryRisk = "Bulaşıcı salgın hastalık riski",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("Kategori A / B")
        ),
        HazardPlacard(
            id = "7",
            classNumber = "7",
            title = "Sınıf 7: Radyoaktif Malzemeler",
            symbolName = "Radyasyon Yoncası (Üst Sarı, Alt Beyaz)",
            backgroundColorHex = 0xFFFFFFFF,
            secondaryColorHex = 0xFFFFD600,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.RADIOACTIVE,
            description = "Spontan olarak iyonlaştırıcı radyasyon yayan radyoaktif izotoplar ve nükleer malzemeler (Uranyum, Sezyum, İridyum-192).",
            primaryRisk = "Görünmez iyonlaştırıcı radyasyon hasarı ve doku tahribatı",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("Kategori I, II, III")
        ),
        HazardPlacard(
            id = "8",
            classNumber = "8",
            title = "Sınıf 8: Aşındırıcı (Korozif) Maddeler",
            symbolName = "Cam Tüplerden Ele ve Metale Damlayan Sıvı",
            backgroundColorHex = 0xFF000000,
            secondaryColorHex = 0xFFFFFFFF,
            textColorHex = 0xFFFFFFFF,
            symbolType = PlacardSymbolType.CORROSIVE,
            description = "Kimyasal etkisiyle canlı dokulara ve metal yüzeylere ciddi aşındırıcı zarar veren asitler ve bazlar (Sülfürik asit, Hidroklorik asit, Kostik).",
            primaryRisk = "Ağır kimyasal doku yanıkları ve metal aşınması",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("PG I", "PG II", "PG III")
        ),
        HazardPlacard(
            id = "9",
            classNumber = "9",
            title = "Sınıf 9: Muhtelif Tehlikeli Maddeler ve Nesneler",
            symbolName = "Üst Kısımda 7 Dikey Siyah Çizgi",
            backgroundColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.MISCELLANEOUS,
            description = "Diğer sınıflara girmeyen ancak taşıma esnasında tehlike arz eden maddeler (Lityum-iyon piller, Sıcak asfalt/katran, Kuru buz).",
            primaryRisk = "Termal kaçak yangını, çevre kirliliği, derin donuk",
            tunnelRestrictionCode = "(E)",
            packingGroups = listOf("PG II", "PG III")
        ),
        HazardPlacard(
            id = "ENV",
            classNumber = "Çevre",
            title = "Çevreye Zararlı Madde İşareti (Marine Pollutant)",
            symbolName = "Ölü Balık ve Kurumuş Ağaç Sembolü",
            backgroundColorHex = 0xFFFFFFFF,
            textColorHex = 0xFF000000,
            symbolType = PlacardSymbolType.ENVIRONMENT,
            description = "Sucul ortama (balıklar, mikroorganizmalar) uzun süreli kalıcı toksik etki yapan kimyasal ve petrol türevleri.",
            primaryRisk = "Su kaynakları ve ekosistem için kitlesel zehirlilik",
            tunnelRestrictionCode = "(-)",
            packingGroups = listOf("Ek Tehlike İşareti")
        )
    )

    fun getHazardPlacards(): List<HazardPlacard> = hazardPlacardsList

    fun getPlacardForSubstance(substance: UnSubstance): HazardPlacard {
        val cls = substance.adrClass.uppercase()
        return when {
            cls.contains("2.1") -> hazardPlacardsList.first { it.id == "2.1" }
            cls.contains("2.2") -> hazardPlacardsList.first { it.id == "2.2" }
            cls.contains("2.3") -> hazardPlacardsList.first { it.id == "2.3" }
            cls.contains("1") -> hazardPlacardsList.first { it.id == "1" }
            cls.contains("3") -> hazardPlacardsList.first { it.id == "3" }
            cls.contains("4.1") -> hazardPlacardsList.first { it.id == "4.1" }
            cls.contains("4.2") -> hazardPlacardsList.first { it.id == "4.2" }
            cls.contains("4.3") -> hazardPlacardsList.first { it.id == "4.3" }
            cls.contains("5.1") -> hazardPlacardsList.first { it.id == "5.1" }
            cls.contains("5.2") -> hazardPlacardsList.first { it.id == "5.2" }
            cls.contains("6.1") -> hazardPlacardsList.first { it.id == "6.1" }
            cls.contains("6.2") -> hazardPlacardsList.first { it.id == "6.2" }
            cls.contains("7") -> hazardPlacardsList.first { it.id == "7" }
            cls.contains("8") -> hazardPlacardsList.first { it.id == "8" }
            cls.contains("9") -> hazardPlacardsList.first { it.id == "9" }
            else -> hazardPlacardsList.first { it.id == "3" }
        }
    }

    fun getAdrClasses(): List<AdrClassInfo> = listOf(
        AdrClassInfo("1", "Patlayıcı Maddeler", "Dinamit, barut, havai fişekler", 0xFFD32F2F, "hazard_explosive"),
        AdrClassInfo("2", "Gazlar", "Sıkıştırılmış, sıvılaştırılmış, çözünmüş gazlar (LPG, Amonyak)", 0xFF1976D2, "hazard_gas"),
        AdrClassInfo("3", "Alevlenir Sıvılar", "Benzin, motorin, tiner, alkol, solventler", 0xFFFF6D00, "hazard_flammable_liquid"),
        AdrClassInfo("4.1", "Alevlenir Katılar", "Kibrit, kükürt, kendiliğinden tepkimeye giren maddeler", 0xFFE53935, "hazard_flammable_solid"),
        AdrClassInfo("4.2", "Kendiliğinden Yanan Maddeler", "Beyaz fosfor, aktif kömür", 0xFFD81B60, "hazard_spontaneous"),
        AdrClassInfo("4.3", "Suyla Gaz Çıkaran Maddeler", "Karpit, metalik sodyum (SU YASAKTIR)", 0xFF1E88E5, "hazard_water_reactive"),
        AdrClassInfo("5.1", "Oksitleyici Maddeler", "Amonyum nitrat, hidrojen peroksit", 0xFFFBC02D, "hazard_oxidizer"),
        AdrClassInfo("5.2", "Organik Peroksitler", "Isıya duyarlı, patlayıcı ayrışabilen peroksitler", 0xFFF57C00, "hazard_organic_peroxide"),
        AdrClassInfo("6.1", "Toksik (Zehirli) Maddeler", "Siyanür, pestisitler, ağır zehirler", 0xFF757575, "hazard_toxic"),
        AdrClassInfo("6.2", "Bulaşıcı Maddeler", "Virüsler, bakteriler, tıbbi atıklar", 0xFF5D4037, "hazard_infectious"),
        AdrClassInfo("7", "Radyoaktif Maddeler", "Uranyum, tıbbi izotoplar, nükleer yakıt", 0xFFFBC02D, "hazard_radioactive"),
        AdrClassInfo("8", "Aşındırıcı (Korozif) Maddeler", "Sülfürik asit, hidroklorik asit, kostik", 0xFF424242, "hazard_corrosive"),
        AdrClassInfo("9", "Muhtelif Tehlikeli Maddeler", "Lityum piller, kuru buz, sıcak asfalt, çevreye zararlılar", 0xFF37474F, "hazard_misc")
    )
}

