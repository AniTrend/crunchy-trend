package co.anitrend.support.crunchyroll.data.model.core

import co.anitrend.support.crunchyroll.data.arch.ResponseStatus

data class CrunchyContainer<D>(
    val code: ResponseStatus,
    val error: Boolean,
    val data: D?,
    val message: String?
)
