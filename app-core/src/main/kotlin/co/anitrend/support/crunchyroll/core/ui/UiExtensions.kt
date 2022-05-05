package co.anitrend.support.crunchyroll.core.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.*
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.support.crunchyroll.core.R
import org.koin.core.component.KoinScopeComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Get given dependency
 *
 * @param qualifier - bean qualifier / optional
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T = runCatching {
    scope.get<T>(qualifier, parameters)
}.getOrElse {
    getKoin().get(qualifier, parameters)
}

/**
 * Inject lazily
 *
 * @param qualifier - bean qualifier / optional
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy(UNSAFE) { get<T>(qualifier, parameters) }


/**
 * Checks for existing fragment in [FragmentManager], if one exists that is used otherwise
 * a new instance is created.
 *
 * @return tag of the fragment
 *
 * @see androidx.fragment.app.commit
 */
inline fun FragmentItem.commit(
    @IdRes contentFrame: Int,
    fragmentActivity: FragmentActivity,
    action: FragmentTransaction.() -> Unit
) : String? {
    if (fragment == null) return null
    val fragmentManager = fragmentActivity.supportFragmentManager

    val fragmentTag = tag()
    val backStack = fragmentManager.findFragmentByTag(fragmentTag)

    fragmentManager.commit {
        action()
        setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            R.anim.popup_enter,
            R.anim.popup_exit
        )
        backStack?.let {
            replace(contentFrame, it, fragmentTag)
        } ?: replace(contentFrame, fragment, parameter, fragmentTag)
    }
    return fragmentTag
}

/**
 * Checks for existing fragment in [FragmentManager], if one exists that is used otherwise
 * a new instance is created.
 *
 * @return tag of the fragment
 *
 * @see androidx.fragment.app.commit
 */
inline fun FragmentItem.commit(
    contentFrame: View,
    fragmentActivity: FragmentActivity,
    action: FragmentTransaction.() -> Unit
) = commit(contentFrame.id, fragmentActivity, action)

inline fun <reified T : Fragment> FragmentActivity.fragment() = lazy {
    supportFragmentManager.fragmentFactory.instantiate(classLoader, T::class.java.name) as T
}

inline fun <reified T : Fragment> FragmentActivity.fragmentByTagOrNew(
    tag: String, noinline factory: () -> T
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentByTag(tag) as? T ?: factory()
    }
}