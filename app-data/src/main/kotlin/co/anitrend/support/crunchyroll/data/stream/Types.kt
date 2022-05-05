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

package co.anitrend.support.crunchyroll.data.stream

import co.anitrend.support.crunchyroll.data.arch.controller.core.DefaultController
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.stream.entity.CrunchyStreamEntity
import co.anitrend.support.crunchyroll.data.stream.model.CrunchyStreamInfoModel

internal typealias StreamController = DefaultController<CrunchyContainer<CrunchyStreamInfoModel>, CrunchyStreamEntity?>