package com.example.refconnect.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "refconnect_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
        private val UNREAD_CONNECTIONS_COUNT = intPreferencesKey("unread_connections_count")
        private val UNREAD_MESSAGES_COUNT = intPreferencesKey("unread_messages_count")
    }

    val currentUserId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val isFirstTime: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_TIME] ?: true
    }

    val unreadConnectionsCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UNREAD_CONNECTIONS_COUNT] ?: 0
    }

    val unreadMessagesCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UNREAD_MESSAGES_COUNT] ?: 0
    }

    suspend fun setCurrentUser(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearCurrentUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
            preferences[IS_LOGGED_IN] = false
        }
    }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME] = isFirstTime
        }
    }

    suspend fun setUnreadConnectionsCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[UNREAD_CONNECTIONS_COUNT] = count
        }
    }

    suspend fun setUnreadMessagesCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[UNREAD_MESSAGES_COUNT] = count
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
