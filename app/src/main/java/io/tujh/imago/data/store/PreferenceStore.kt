package io.tujh.imago.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> preferencesStore(
    context: Context,
    name: String,
    serialize: (T) -> String,
    deserialize: (String) -> T
): Store<T> = object : Store<T> {
    private val key = stringPreferencesKey("${name}_key")
    private val Context.dataStore by preferencesDataStore(name)
    override val data: Flow<T?> = context.dataStore.data.map { value ->
        value[key]?.let(deserialize)
    }

    override suspend fun clear() {
        context.dataStore.edit { value -> value.remove(key) }
    }

    override suspend fun update(item: T) {
        context.dataStore.edit { value -> value[key] = serialize(item) }
    }
}

inline fun <reified T> jsonStore(context: Context, name: String): Store<T> {
    val gson = Gson()
    return preferencesStore(
        context = context,
        name = name,
        serialize = { value -> gson.toJson(value, T::class.java) },
        deserialize = { string -> gson.fromJson(string, T::class.java) }
    )
}

fun stringStore(context: Context, name: String): Store<String> = preferencesStore(
    context = context,
    name = name,
    serialize = { it },
    deserialize = { it }
)