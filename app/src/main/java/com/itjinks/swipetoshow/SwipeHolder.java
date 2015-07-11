package com.itjinks.swipetoshow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by jinks on 15/6/25.
 */
public class SwipeHolder extends FrameLayout {
    private final static int ALIGN_ANIMATION_DURATION = 200;

    public SwipeHolder(Context context) {
        super(context);
    }

    public SwipeHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private HideContentHolder hideContentHolder;
    private View mainContent;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hideContentHolder = (HideContentHolder) getChildAt(0);
        mainContent = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        hideContentHolder.layout(right - hideContentHolder.getMeasuredWidth(), 0, right, hideContentHolder.getMeasuredHeight());
        mainContent.layout(0, 0, right, mainContent.getMeasuredHeight());
    }

    protected void slide(int x) {
        if (x < 0) {
            mainContent.scrollTo(0, 0);
            hideContentHolder.setShowPixel(0);
            return;
        }
        int hideContentHolderWidth = hideContentHolder.getWidth();
        if (x > hideContentHolderWidth) {
            float delta = x - hideContentHolderWidth;
            mainContent.scrollTo((int) (hideContentHolderWidth + delta * (1 - delta / x)), 0);
        } else {
            mainContent.scrollTo(x, 0);
        }
        hideContentHolder.setShowPixel(x);
    }

    /**
     * hide the hidden view
     */
    public void reset() {
        mainContent.scrollTo(0, 0);
        hideContentHolder.setShowPixel(0);
    }

    boolean animating;

    public boolean isAnimating() {
        return animating;
    }

    void animatedAlignShow() {
        final int hideContentHolderWidth = hideContentHolder.getWidth();
        final int deltaScrollX = mainContent.getScrollX() - hideContentHolderWidth;
        final int deltaShowPixel = hideContentHolder.getShowPixel() - hideContentHolderWidth;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f);
        valueAnimator.setDuration(ALIGN_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mainContent.scrollTo(hideContentHolderWidth + (int) (deltaScrollX * (1 - fraction)), 0);
                hideContentHolder.setShowPixel(hideContentHolderWidth + (int) (deltaShowPixel * (1 - fraction)));
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainContent.scrollTo(hideContentHolderWidth, 0);
                animating = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
        animating = true;
    }

    void animateCollapse() {
        final int deltaShowPixel = hideContentHolder.getShowPixel();
        final int deltaScrollX = mainContent.getScrollX();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f);
        valueAnimator.setDuration(ALIGN_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mainContent.scrollTo((int) (deltaScrollX * (1 - fraction)), 0);
                hideContentHolder.setShowPixel((int) (deltaShowPixel * (1 - fraction)));
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainContent.scrollTo(0, 0);
                hideContentHolder.setShowPixel(0);
                animating = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
        animating = true;
    }

    protected boolean isXInHideArea(int x) {
        return x > hideContentHolder.getLeft();
    }

    protected boolean determineShowOrHide() {
        if (hideContentHolder.getShowPixel() < hideContentHolder.getWidth() / 3) {
            animateCollapse();
            return false;
        } else {
            animatedAlignShow();
            return true;
        }

    }

}
