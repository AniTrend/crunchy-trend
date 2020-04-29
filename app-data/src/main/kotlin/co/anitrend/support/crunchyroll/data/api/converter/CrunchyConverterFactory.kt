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

import co.anitrend.support.crunchyroll.data.arch.JSON
import co.anitrend.support.crunchyroll.data.arch.XML
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.reflect.Type


internal class CrunchyConverterFactory private constructor(): Converter.Factory() {

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
        internal val GSON_BUILDER by lazy {
            GsonBuilder()
                .generateNonExecutableJson()
                .serializeNulls()
                .setLenient()
        }

        internal fun create() = CrunchyConverterFactory()
    }
}