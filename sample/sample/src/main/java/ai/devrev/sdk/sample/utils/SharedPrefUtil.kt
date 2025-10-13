package ai.devrev.sdk.sample.utils

import android.content.Context

object SharedPrefUtil {
    private const val PREFS_NAME = "FCM_PREFS"
    private const val KEY_FCM_TOKEN = "fcm_token"

    fun saveTokenToPreferences(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_FCM_TOKEN, token)
            apply()
        }
    }
}