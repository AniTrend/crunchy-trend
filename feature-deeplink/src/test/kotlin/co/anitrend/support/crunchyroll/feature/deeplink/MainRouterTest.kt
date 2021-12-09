/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.feature.deeplink

import android.content.Intent
import co.anitrend.support.crunchyroll.feature.deeplink.routes.FallbackRoute
import co.anitrend.support.crunchyroll.feature.deeplink.routes.MainRoute
import co.anitrend.support.crunchyroll.navigation.Main
import com.hellofresh.deeplink.Action
import com.hellofresh.deeplink.DeepLinkParser
import com.hellofresh.deeplink.DeepLinkUri
import com.hellofresh.deeplink.Environment
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class MainRouterTest : KoinTest {

    private val environment = mockk<Environment>()

    private val parser by lazy {
        DeepLinkParser.of<Intent?>(environment)
            .addFallbackAction(FallbackRoute)
            .addRoute(MainRoute)
            .build()
    }

    @Before
    fun setUp() {
        every { environment.context } returns mockk()
        every { environment.isAuthenticated } returns false
    }

    @Test
    fun `test a top level deep link and validate result`() {
        val expected = Main.Payload(Main.Payload.Nav.CATALOGUE)
        val actual = parser.parse(
            DeepLinkUri.parse("https://www.crunchyroll.com/en-gb/")
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test a news path deep link and validate result`() {
        val expected = Main.Payload(Main.Payload.Nav.NEWS)
        val actual = parser.parse(
            DeepLinkUri.parse("https://www.crunchyroll.com/en-gb/news")
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test a popular path deep link and validate result`() {
        val expected = Main.Payload(Main.Payload.Nav.DISCOVER)
        val actual = parser.parse(
            DeepLinkUri.parse("https://www.crunchyroll.com/en-gb/videos/anime/popular")
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test a videos path deep link and validate result`() {
        val expected = Main.Payload(Main.Payload.Nav.LATEST)
        val actual = parser.parse(
            DeepLinkUri.parse("https://www.crunchyroll.com/en-gb/videos")
        )

        Assert.assertEquals(expected, actual)
    }
}