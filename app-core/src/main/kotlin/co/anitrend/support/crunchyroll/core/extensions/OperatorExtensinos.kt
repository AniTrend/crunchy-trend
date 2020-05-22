/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.core.extensions

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    data class Failure<T>(val errorMessage: Any): Result<T>()
}

// Pipe input
infix fun <T,U> T.begin(f: (T) -> Result<U>) =
    Result.Success(this) then f

// Composition: apply a function f to Success results
infix fun <T,U> Result<T>.then(f: (T) -> Result<U>) =
    when (this) {
        is Result.Success -> f(value)
        is Result.Failure -> Result.Failure(errorMessage)
    }

// Handle error output: the end of a railway
infix fun <T> Result<T>.otherwise(f: (Any) -> Unit) =
    if (this is Result.Failure) f(errorMessage) else Unit