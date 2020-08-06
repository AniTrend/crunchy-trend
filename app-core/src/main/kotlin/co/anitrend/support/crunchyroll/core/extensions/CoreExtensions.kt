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

package co.anitrend.support.crunchyroll.core.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import co.anitrend.support.crunchyroll.core.CrunchyApplication
import co.anitrend.support.crunchyroll.core.common.DEFAULT_ANIMATION_DURATION
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.fragment.model.FragmentItem
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import org.koin.androidx.scope.lifecycleScope as kScope
import org.koin.core.scope.Scope
import timber.log.Timber
import kotlin.Result

const val separator = "\u2022"

fun CrunchyActivity.recreateModules() {
    val coreApplication = applicationContext as CrunchyApplication
    runCatching {
        coreApplication.restartDependencyInjection()
    }.exceptionOrNull()?.printStackTrace()
}

fun FragmentActivity?.closeScreen() {
    this?.finishAfterTransition()
}


/** get a material container arc transform. */
internal fun getContentTransform(): MaterialContainerTransform {
    val transform =  MaterialContainerTransform()
    transform.addTarget(android.R.id.content)
    transform.setPathMotion(MaterialArcMotion())
    transform.duration = DEFAULT_ANIMATION_DURATION
    return transform
}

/**
 * Creates a default dialog with a lifecycle already attached to it and will not dismiss
 * when the user touches outside the view
 */
fun FragmentActivity?.createDialog(
    dialogBehavior: DialogBehavior = MaterialDialog.DEFAULT_BEHAVIOR
) = this?.run {
    MaterialDialog(this, dialogBehavior)
        .lifecycleOwner(this)
        .cancelOnTouchOutside(false)
}

/**
 * Helper extension for changing parcels to bundles
 */
fun Parcelable.toBundle(key: String) =
    Bundle().apply {
        putParcelable(key, this@toBundle)
    }

const val moduleTag = "CoreExtensions"

/**
 * Checks for existing fragment in [FragmentManager], if one exists that is used otherwise
 * a new instance is created.
 *
 * @return tag of the fragment
 *
 * @see androidx.fragment.app.commit
 */
inline fun FragmentManager.commit(
    @IdRes contentFrame: Int,
    fragmentItem: FragmentItem<*>?,
    action: FragmentTransaction.() -> Unit
) : String? {
    return if (fragmentItem != null) {
        val fragmentTag = fragmentItem.tag()
        val backStack = findFragmentByTag(fragmentTag)

        commit {
            action()
            backStack?.let {
                replace(contentFrame, it, fragmentTag)
            } ?: replace(
                contentFrame,
                fragmentItem.fragment,
                fragmentItem.parameter,
                fragmentTag
            )
        }
        fragmentTag
    } else {
        Timber.tag(moduleTag).v("FragmentItem model is null")
        null
    }
}

fun <T> Result<T>.stackTrace(tag: String): T? {
    val value = getOrNull()
    if (isFailure) {
        val throwable = value as Throwable?
        if (throwable != null)
            Timber.tag(tag).v(throwable)
    }
    return value
}

/**
 * Get current Koin scope, bound to current lifecycle
 */
val LifecycleOwner.koinScope: Scope
    get() = kScope