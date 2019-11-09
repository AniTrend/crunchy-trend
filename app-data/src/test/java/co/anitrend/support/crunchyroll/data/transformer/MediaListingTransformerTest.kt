/*
 *    Copyright 2019 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.transformer

import co.anitrend.support.crunchyroll.data.model.rss.CrunchyMediaRestriction
import co.anitrend.support.crunchyroll.data.model.rss.MediaThumbnail
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class MediaListingTransformerTest {

    private val locale by lazy { Locale.getDefault() }

    @Test
    fun `given list of thumbnails is highest quality true`() {
        val case = listOf(
            MediaThumbnail(
                url = "",
                width = 100,
                height = 56
            ),
            MediaThumbnail(
                url = "",
                width = 640,
                height = 360
            ),
            MediaThumbnail(
                url = "",
                width = 200,
                height = 112
            ),
            MediaThumbnail(
                url = "",
                width = 50,
                height = 28
            )
        )

        val actual = MediaListingTransformer.highestQuality(case)

        val expected = MediaThumbnail(
            url = "",
            width = 640,
            height = 360
        )

        assertEquals(expected.width, actual?.width)
    }

    @Test
    fun `given duration in seconds assert string representation is correct`() {
        val case = 1439

        val actual = MediaListingTransformer.durationFormatted(case)

        val expected = "23:59"

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string of locale for available languages assert produces list of platform locales`() {
        val case = "en - us,es - la,es - es,pt - br,ar - me,it - it,de - de,ru - ru"

        val actual = MediaListingTransformer.getSubtitles(
            case, locale
        )

        val expected = listOf(
            "enUS", "esLA", "esES", "ptBR", "arME", "itIT", "deDE", "ruRU"
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `given string of countries assert maps correctly`() {

        val case = CrunchyMediaRestriction(
            relationship = "country",
            type = "allowed",
            elements = """
                af ax al dz as ad ao ai aq ag ar am aw au at az bs bh bd bb by be bz bj 
                bm bt bo bq ba bw bv br io bn bg bf bi kh cm ca cv ky cf td cl cx cc co 
                km cg cd ck cr ci hr cu cw cy cz dk dj dm do tp ec eg sv gq er ee et fk 
                fo fj fi fr gf pf tf ga gm ge de gh gi gr gl gd gp gu gt gg gn gw gy ht 
                hm va hn hk hu is in id ir iq ie im il it jm je jo kz ke ki kp kr kw kg 
                la lv lb ls lr ly li lt lu mo mk mg mw my mv ml mt mh mq mr mu yt mx fm 
                md mc mn me ms ma mz mm na nr np nl an nc nz ni ne ng nu nf mp no om pk 
                pw ps pa pg py pe ph pn pl pt pr qa re ro ru rw bl sh kn lc mf pm vc ws 
                sm st sa sn rs sc sl sg sx sk si sb so za gs ss es lk sd sr sj sz se ch 
                sy tw tj tz th tl tg tk to tt tn tr tm tc tv ug ua ae gb us um uy uz vu 
                ve vn vg vi wf eh ye zm zw
        """.trimIndent()
        )

        val actual = MediaListingTransformer.isAllowed(case, locale)

        val expected = true

        assertEquals(expected, actual)
    }
}