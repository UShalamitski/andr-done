package com.hose.aureliano.project.done.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Util class that contains methods to work with preferences.
 * <p/>
 * Date: 19.05.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class PreferencesUtil {

    private static final String PREFERENCES_FILE_KEY = "com.hose.aureliano.project.done.PREFERENCES_FILE_KEY";

    private PreferencesUtil() {
        throw new AssertionError();
    }

    /**
     * Returns map with preferences.
     *
     * @param context application context
     * @return map with all preferences
     */
    public static Map<String, ?> getPreferences(Context context) {
        return getSharedPreferences(context).getAll();
    }

    /**
     * Adds preference.
     *
     * @param context         application context
     * @param preferenceName  name of the preference to add
     * @param preferenceValue value of the preference to add
     */
    public static void addPreference(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Gets preference by name. In case preference doesn't exists it will return {@code false}.
     *
     * @param context        application context
     * @param preferenceName name of the preference to retrieve
     * @return stored preference or false
     */
    public static boolean getPreference(Context context, String preferenceName) {
        return getSharedPreferences(context).getBoolean(preferenceName, false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }
}
