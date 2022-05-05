package co.anitrend.support.crunchyroll.core.ui.model

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Fragment loader holder helper
 */
data class FragmentItem(
    val fragment: Class<out Fragment>?,
    val parameter: Bundle? = null
) {
    fun tag() = fragment?.simpleName
}