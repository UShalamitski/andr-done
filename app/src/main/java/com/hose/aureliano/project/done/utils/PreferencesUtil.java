package com.hose.aureliano.project.done.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
     * Adds int preference.
     *
     * @param context         application context
     * @param preferenceName  name of the preference to add
     * @param preferenceValue value of the preference to add
     */
    public static void addIntPreference(Context context, String preferenceName, int preferenceValue) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Adds string preference.
     *
     * @param context         application context
     * @param preferenceName  name of the preference to add
     * @param preferenceValue value of the preference to add
     */
    public static void addPreference(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Gets preference by name. In case preference doesn't exists it will return {@code false}.
     *
     * @param context        application context
     * @param preferenceName name of the preference to retrieve
     * @return stored preference or false
     */
    public static boolean getBooleanPreference(Context context, String preferenceName) {
        return getSharedPreferences(context).getBoolean(preferenceName, false);
    }

    /**
     * Gets preference by name. In case preference doesn't exists it will return {@code false}.
     *
     * @param context        application context
     * @param preferenceName name of the preference to retrieve
     * @return stored preference or false
     */
    public static String getPreference(Context context, String preferenceName) {
        return getSharedPreferences(context).getString(preferenceName, "");
    }

    /**
     * Gets preference by name. In case preference doesn't exists it will return {@code false}.
     *
     * @param context        application context
     * @param preferenceName name of the preference to retrieve
     * @return stored preference or false
     */
    public static int getIntPreference(Context context, String preferenceName) {
        return getSharedPreferences(context).getInt(preferenceName, 0);
    }

    /**
     * Removes preference by name.
     *
     * @param context        application context
     * @param preferenceName name of the preference to remove
     */
    public static void removePreference(Context context, String preferenceName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(preferenceName);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }
}
