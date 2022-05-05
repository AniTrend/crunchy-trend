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

package co.anitrend.support.crunchyroll.data.authentication.contract

enum class AccountType(val id: String) {
    //universal account type for all apps now using this specific module
    UNIVERSAL("co.anitrend.authenticator")
}

/**
 * This value doesn't matter unless you want users with
 * specialized access tokens (read only, full access, etc)
 */
enum class TokenType(val alias: String) {
    ANONYMOUS("Anonymous"),
    AUTHENTICATED("Authenticated")
}