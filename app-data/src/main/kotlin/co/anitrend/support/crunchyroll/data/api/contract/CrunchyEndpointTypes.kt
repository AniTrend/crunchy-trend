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

package co.anitrend.support.crunchyroll.data.api.contract

import co.anitrend.support.crunchyroll.data.BuildConfig


internal enum class EndpointType(val url: String) {
    @Deprecated("Seem to be down, rather user use SESSION_LEGACY, SESSION_PROXY or SESSION_JSON")
    SESSION(BuildConfig.apiSessionV2),
    SESSION_PROXY(BuildConfig.apiSessionV1),
    SESSION_CORE(BuildConfig.apiSession),
    SESSION_JSON(BuildConfig.apiUrl),
    JSON(BuildConfig.apiUrl),
    XML(BuildConfig.apiFeed),
    SLUG(BuildConfig.crunchyUrl)
}