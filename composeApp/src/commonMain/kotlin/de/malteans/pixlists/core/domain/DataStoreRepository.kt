package de.malteans.pixlists.core.domain

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    fun getShowStartColorDialogFlow(): Flow<Boolean>
    suspend fun setShowStartColorDialog(show: Boolean?)
}