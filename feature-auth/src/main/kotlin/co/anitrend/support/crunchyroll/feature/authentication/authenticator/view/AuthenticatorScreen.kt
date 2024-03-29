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

package co.anitrend.support.crunchyroll.feature.authentication.authenticator.view

import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import co.anitrend.arch.extension.ext.extra
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity

/**
 * Implements functionality from [AccountAuthenticatorActivity] that is used to help implement an
 * AbstractAccountAuthenticator. If the AbstractAccountAuthenticator needs to use an activity
 * to handle the request then it can have the activity extend AccountAuthenticatorActivity.
 * The AbstractAccountAuthenticator passes in the response to the intent using the following:
 *
 * > `intent.putExtra({[AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE]}, response)`
 *
 * The activity then sets the result that is to be handed to the response via
 * [setAccountAuthenticatorResult]
 * This result will be sent as the result of the request when the activity finishes. If this
 * is never set or if it is set to null then error [AccountManager.ERROR_CODE_CANCELED]
 * will be called on the response.
 */
abstract class AuthenticatorScreen<V : ViewBinding> : CrunchyActivity<V>() {

    private val accountAuthenticatorResponse: AccountAuthenticatorResponse?
            by extra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)
    private var mResultBundle: Bundle? = null


    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    fun setAccountAuthenticatorResult(result: Bundle) {
        mResultBundle = result
    }

    /**
     * Retrieves the AccountAuthenticatorResponse from either the intent of the icicle, if the
     * icicle is non-zero.
     *
     * @param savedInstanceState the save instance data of this Activity, may be null
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountAuthenticatorResponse?.onRequestContinued()
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    override fun finish() {
        // send the result bundle back if set, otherwise send an error.
        if (mResultBundle != null) {
            accountAuthenticatorResponse?.onResult(mResultBundle)
        } else {
            accountAuthenticatorResponse?.onError(
                AccountManager.ERROR_CODE_CANCELED,
                "canceled"
            )
        }
        super.finish()
    }
}