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

package co.anitrend.support.crunchyroll.feature.deeplink.ui

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.inject
import com.hellofresh.deeplink.DeepLinkParser
import com.hellofresh.deeplink.DeepLinkUri

class DeepLinkScreen : CrunchyActivity<ViewBinding>() {

    private val router by inject<DeepLinkParser<Intent?>>()

    override fun initializeComponents(savedInstanceState: Bundle?) {
        if (intent.data != null) {
            val data = intent.data.toString()
            val uri = DeepLinkUri.parse(data)
            val intent = router.parse(uri)
            startActivity(intent)
            finishAfterTransition()
        }
    }
}