package de.malteans.pixlists.core.domain

interface DataStoreRepository {
    fun getShowStartColorDialog(): Boolean
    suspend fun setShowStartColorDialog(show: Boolean?)
}