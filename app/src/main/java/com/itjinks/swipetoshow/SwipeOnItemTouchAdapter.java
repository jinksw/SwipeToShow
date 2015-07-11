package com.itjinks.swipetoshow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by jinks on 15/7/2.
 */
public abstract class SwipeOnItemTouchAdapter implements RecyclerView.OnItemTouchListener {
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private final int TOUCH_SLOP;

    public SwipeOnItemTouchAdapter(Context context, RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        TOUCH_SLOP = ViewConfiguration.get(context).getScaledTouchSlop();
        this.context = context;
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
    }

    int startX;
    int startY;
    //代表开始滑动以显示hide的过程
    boolean beginSlide;
    //代表开始竖直滚动了
    boolean beginScroll;
    //因为在指定动画时禁止一切手势,在动画结束后可能仍会接受到move up但错过了开头的down,所以这个手势被污染了,需要丢弃
    boolean animatingPollute;

    //当HiddenView显示时,targetView被赋值,所以可以用targetView来判断是否当前有显示HiddenView的Item
    SwipeHolder targetView;
    SwipeHolder animatingView;

    //targetView对应的position
    int targetPosition;

    //代表这个HiddenView可能被点击
    boolean pendingHiddenClick;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (enableSwipe)
            return false;
        if (animatingView != null && animatingView.isAnimating()) {
            //置标志位,接下来的手势被污染了
            animatingPollute = true;
            return true;
        } else {
            animatingView = null;
        }
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) motionEvent.getX();
                startY = (int) motionEvent.getY();
                beginSlide = false;
                beginScroll = false;
                animatingPollute = false;
                //当targetView不等于空时,我们需要判断以缩回可能显示的隐藏按钮
                if (targetView != null) {
                    float tx, ty;
                    tx = startX;
                    ty = startY;
                    if (isXInHiddenView(tx, ty)) {
                        pendingHiddenClick = true;
                    } else {
                        targetView.animateCollapse();
                        animatingView = targetView;
                        targetView = null;
                        targetPosition = -1;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (animatingPollute)
                    return false;
                if (beginScroll) {
                    return false;
                }
                if (beginSlide) {
                    return true;
                }
                if (!beginSlide) {
                    int horizontalDelta = (int) (motionEvent.getX() - startX);
                    int verticalDelta = (int) (motionEvent.getY() - startY);
                    //达到水平拉动的阈值
                    if (Math.abs(horizontalDelta) > TOUCH_SLOP) {
                        if (!(recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY()) instanceof SwipeHolder)) {
                            return false;
                        }
                        beginSlide = true;
                        targetView = (SwipeHolder) recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                        if (targetView == null) {
                            beginSlide = false;
                            return false;
                        }
                        targetPosition = layoutManager.getPosition(targetView);
                        startX = (int) motionEvent.getX();
                        return true;
                    } else if (Math.abs(verticalDelta) > TOUCH_SLOP) {//达到竖直滚动的阈值
                        beginScroll = true;
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (animatingPollute) {
                    animatingPollute = false;
                    return false;
                }
                if (targetView != null) {
                    int dx = (int) motionEvent.getX();
                    int dy = (int) motionEvent.getY();
                    if (isXInHiddenView(dx, dy)) {
                        onItemHiddenClick(targetView, targetPosition);
                        targetView = null;
                    }
                    break;
                }
                if (!beginSlide && !beginScroll && targetView == null) {
                    //如果不是在滑动过程中,那么就判定这是一个点击事件
                    View clickView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (clickView != null) {
                        int clickPosition = layoutManager.getPosition(clickView);
                        onItemClick(clickPosition);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                beginSlide = false;
                beginScroll = false;
                animatingPollute = false;

        }
        return false;
    }


    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (animatingView != null && animatingView.isAnimating()) {
            animatingPollute = true;
            return;
        } else {
            animatingView = null;
        }
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (animatingPollute) {
                    return;
                }
                targetView.slide((int) (startX - motionEvent.getX()));
                break;
            case MotionEvent.ACTION_UP:
                if (animatingPollute) {
                    animatingPollute = false;
                    return;
                }
                //这里的ActionUp是响应水平拖动过程中的结束事件,需要重置hiddenView的状态,要么显示,要么隐藏
                boolean isShow = targetView.determineShowOrHide();
                //如果没有显示一定要置空,表示他没有显示
                if (!isShow) {
                    targetView = null;
                    targetPosition = -1;
                }
                break;
        }
    }

    public boolean isBusy() {
        return targetView != null;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    /**
     * 判断x,y是否在当前targetView的范围 并且在targetView的点击区域
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isXInHiddenView(float x, float y) {
        return recyclerView.findChildViewUnder(x, y) == targetView && targetView.isXInHideArea((int) x);
    }

    public abstract void onItemHiddenClick(SwipeHolder swipeHolder, int position);

    public abstract void onItemClick(int position);


    private boolean enableSwipe;

    /**
     * to enable swipe,set this to true
     *
     * @param enableSwipe
     */
    public void setEnableSwipe(boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
    }
}
