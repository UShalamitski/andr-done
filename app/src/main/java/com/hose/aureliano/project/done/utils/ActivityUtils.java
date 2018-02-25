package com.hose.aureliano.project.done.utils;

import android.content.Context;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Header.
 * <p>
 * Date: 2/13/2018.
 *
 * @author Uladzislau_Shalamits
 */
public final class ActivityUtils {

    private ActivityUtils() {
        throw new AssertionError();
    }

    /**
     * Show a {@link Snackbar}.
     *
     * @param view view
     * @param text text to show on {@link Snackbar}
     * @return instance of {@link Snackbar}
     */
    public static Snackbar showSnackBar(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    /**
     * Show a {@link Snackbar}.
     *
     * @param view            view
     * @param text            text to show on {@link Snackbar}
     * @param actionTextResId resource id for action text
     * @param listener        instance of {@link View.OnClickListener}
     * @return instance of {@link Snackbar}
     */
    public static Snackbar showSnackBar(View view, String text, int actionTextResId,
                                        View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(actionTextResId, listener);
        snackbar.show();
        return snackbar;
    }

    /**
     * Vibrate constantly for the specified period of time.
     *
     * @param context      context
     * @param milliseconds number of milliseconds to vibrate
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        if (null != vibrator) {
            vibrator.vibrate(milliseconds);
        }
    }
}
