package com.hose.aureliano.project.done.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Utility class for view animations.
 * <p/>
 * Date: 25.03.2018
 *
 * @author Uladzislau Shalamitski
 */
public class AnimationUtil {

    private final static int DURATION = 250;

    private AnimationUtil() {
        throw new AssertionError();
    }

    /**
     * Fade in animation of changing given {@link View} opacity.
     *
     * @param view {@link View} to animate
     */
    public static void animateAlphaFadeIn(View view) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 0.55f)
                .setDuration(DURATION)
                .start();
    }

    /**
     * Fade out animation of changing given {@link View} opacity.
     *
     * @param view     {@link View} to animate
     * @param listener instance of {@link AnimationEndListener}
     */
    public static void animateAlphaFadeOut(View view, AnimationEndListener listener) {
        Animator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.55f, 0.0f);
        animator.setDuration(DURATION);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.handle();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    /**
     * Listener that handles animation end event.
     */
    public interface AnimationEndListener {
        void handle();
    }
}
