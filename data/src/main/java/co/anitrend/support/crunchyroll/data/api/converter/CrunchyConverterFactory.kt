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

@file:Suppress("DEPRECATION")

package co.anitrend.support.crunchyroll.data.api.converter

import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import co.anitrend.support.crunchyroll.data.api.contract.JSON
import co.anitrend.support.crunchyroll.data.api.contract.XML
import org.simpleframework.xml.convert.AnnotationStrategy
import retrofit2.converter.gson.GsonConverterFactory
import org.simpleframework.xml.core.Persister
import com.google.gson.GsonBuilder
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit


class CrunchyConverterFactory private constructor(): Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            when (annotation) {
                is XML -> {
                    return SimpleXmlConverterFactory.createNonStrict(
                        Persister(AnnotationStrategy())
                    ).responseBodyConverter(type, annotations, retrofit)
                }
                is JSON -> {
                    return GsonConverterFactory.create(GSON_BUILDER.create())
                        .responseBodyConverter(type, annotations, retrofit)
                }
            }
        }
        return GsonConverterFactory.create(GSON_BUILDER.create())
            .responseBodyConverter(type, annotations, retrofit)
    }

    companion object {
        val GSON_BUILDER: GsonBuilder by lazy {
            GsonBuilder()
                .generateNonExecutableJson()
                .serializeNulls()
                .setLenient()
        }

        fun create() = CrunchyConverterFactory()
    }
}