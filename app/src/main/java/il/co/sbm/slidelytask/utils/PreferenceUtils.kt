package il.co.sbm.slidelytask.utils

import android.annotation.SuppressLint
import android.content.Context
import java.util.*

@SuppressLint("ApplySharedPref")
object PreferenceUtils {

    private const val sf_SHARED_PREFS: String = "SlidelyTestSharedPrefs"
    private const val ARRAY_PREFERENCES_KEY_INDEX_DELIMITER: String = "."

    //region keys
    const val MEDIA_DISPLAY_TIME: String = "mediaDisplayTime"
    //endregion


    fun getSharedPreferences(context: Context, key: String): String? {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        return settings.getString(key, null)
    }

    fun getSharedPreferencesBoolean(context: Context, key: String): Boolean {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        return settings.getBoolean(key, false)
    }

    fun getSharedPreferencesInt(context: Context, key: String): Int {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        return settings.getInt(key, 0)
    }

    fun getSharedPreferencesFloat(context: Context, key: String): Float {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        return settings.getFloat(key, 0.0f)
    }

    fun getSharedPreferencesLong(context: Context, key: String): Long {
        return getSharedPreferencesLongOrDefault(context, key, 0)
    }

    fun getSharedPreferencesLongOrDefault(context: Context, key: String, defaultValue: Long): Long {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        return settings.getLong(key, defaultValue)
    }

    fun setSharedPreference(context: Context, key: String, value: String) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putString(key, value)

        prefEditor.commit()
    }

    fun setSharedPreference(context: Context, key: String, value: Boolean) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putBoolean(key, value)

        prefEditor.commit()
    }

    fun setSharedPreference(context: Context, key: String, value: Float) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putFloat(key, value)

        prefEditor.commit()
    }

    fun setSharedPreference(context: Context, key: String, value: Int) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putInt(key, value)

        prefEditor.commit()
    }

    fun setSharedPreference(context: Context, key: String, value: Long) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putLong(key, value)

        prefEditor.commit()
    }

    fun setSharedPreferences(context: Context, key: String, values: ArrayList<String>) {
        // first, remove the old array
        removeSharedPreferencesArray(context, key)

        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        var keyToInsert: String?

        for ((indexToInsert, value) in values.withIndex()) {

            keyToInsert = getSharedPreferencesArrayKey(key, indexToInsert)
            prefEditor.putString(keyToInsert, value)
        }

        prefEditor.commit()
    }

    fun getSharedPreferencesStringsArray(context: Context, key: String): ArrayList<String>? {
        val values = ArrayList<String>()

        var nextIndex = 0
        var nextKey = getSharedPreferencesArrayKey(key, nextIndex)
        var currentValue: String?
        currentValue = getSharedPreferences(context, nextKey)

        while (currentValue != null) {

            values.add(currentValue)

            nextIndex++
            nextKey = getSharedPreferencesArrayKey(key, nextIndex)
            currentValue = getSharedPreferences(context, nextKey)
        }

        return if (values.size < 1) {

            null
        } else values

    }

    private fun removeSharedPreferencesArray(context: Context, key: String) {
        val settings = context.getSharedPreferences(sf_SHARED_PREFS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        var indexToCheck = 0
        var keyToRemove = getSharedPreferencesArrayKey(key, indexToCheck)

        while (getSharedPreferences(context, keyToRemove) != null) {

            prefEditor.remove(keyToRemove)
            indexToCheck++
            keyToRemove = getSharedPreferencesArrayKey(key, indexToCheck)
        }

        prefEditor.commit()
    }

    private fun getSharedPreferencesArrayKey(key: String, index: Int): String {
        return key + ARRAY_PREFERENCES_KEY_INDEX_DELIMITER + index
    }
}