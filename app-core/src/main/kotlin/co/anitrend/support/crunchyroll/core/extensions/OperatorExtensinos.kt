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

sealed class State<T> {
    data class Success<T>(val value: T): State<T>()
    data class Failure<T>(val error: Throwable): State<T>()
}

// Pipe input
infix fun <T,U> T.validate(f: (T) -> State<U>) =
    State.Success(this) then f

// Composition: apply a function f to Success results
infix fun <T,U> State<T>.then(f: (T) -> State<U>) =
    when (this) {
        is State.Success -> f(value)
        is State.Failure -> State.Failure(error)
    }

// Handle error output: the end of a railway
infix fun <T> State<T>.otherwise(f: (Any) -> Unit) =
    when (this) {
        is State.Failure -> f(error)
        else -> Unit
    }