package com.example.smartrecycle.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {

    /**
     * Fade in animation
     */
    public static void fadeIn(View view, int duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(null);
    }

    /**
     * Fade out animation
     */
    public static void fadeOut(View view, int duration) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Scale animation for button press effect
     */
    public static void scaleButton(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100);
                });
    }

    /**
     * Slide in from bottom animation
     */
    public static void slideInFromBottom(View view, int duration) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * Slide out to bottom animation
     */
    public static void slideOutToBottom(View view, int duration) {
        view.animate()
                .translationY(view.getHeight())
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Rotate animation
     */
    public static void rotate(View view, float degrees, int duration) {
        view.animate()
                .rotation(degrees)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * Pulse animation for highlighting
     */
    public static void pulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);

        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);

        scaleX.start();
        scaleY.start();
    }

    /**
     * Shake animation for errors
     */
    public static void shake(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Card flip animation
     */
    public static void flipCard(View frontView, View backView) {
        ObjectAnimator frontAnimator = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f);
        ObjectAnimator backAnimator = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);

        frontAnimator.setDuration(300);
        backAnimator.setDuration(300);

        frontAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.setVisibility(View.VISIBLE);
                backAnimator.start();
            }
        });

        frontAnimator.start();
    }

    /**
     * Bounce animation
     */
    public static void bounce(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);

        ScaleAnimation scaleBackAnimation = new ScaleAnimation(
                1.2f, 1.0f, 1.2f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleBackAnimation.setDuration(200);
        scaleBackAnimation.setStartOffset(200);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(scaleBackAnimation);

        view.startAnimation(animationSet);
    }

    /**
     * Slide in from right animation
     */
    public static void slideInFromRight(View view, int duration) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animation);
    }

    /**
     * Progress animation for loading
     */
    public static void animateProgress(View progressView, int fromProgress, int toProgress, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(fromProgress, toProgress);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            // Update progress view here
        });
        animator.start();
    }
}