package de.malteans.pixlists.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import de.malteans.pixlists.core.domain.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DefaultDataStoreRepository(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {
    private enum class StringKey(val prefKey: Preferences.Key<String>) {
        
    }
    private enum class BooleanKey(val prefKey: Preferences.Key<Boolean>) {
        SHOW_START_COLOR_DIALOG(booleanPreferencesKey("showStartColorDialog"))
    }

    private fun getString(key: StringKey): String? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getStringFlow(key: StringKey, default: String? = null): Flow<String?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveString(key: StringKey, value: String?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }

    private fun getBoolean(key: BooleanKey): Boolean? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getBooleanFlow(key: BooleanKey, default: Boolean? = null): Flow<Boolean?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveBoolean(key: BooleanKey, value: Boolean?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }

    override fun getShowStartColorDialog() = getBoolean(BooleanKey.SHOW_START_COLOR_DIALOG) ?: true
    override suspend fun setShowStartColorDialog(show: Boolean?) = saveBoolean(BooleanKey.SHOW_START_COLOR_DIALOG, show)
}