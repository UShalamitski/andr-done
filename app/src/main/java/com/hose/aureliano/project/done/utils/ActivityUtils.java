package com.hose.aureliano.project.done.utils;

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

    public static Snackbar showSnackBar(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }
}
