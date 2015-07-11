package com.itjinks.swipetoshow;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by jinks on 15/6/25.
 */
public class HideContentHolder extends FrameLayout {
    public HideContentHolder(Context context) {
        super(context);
        init();
    }

    public HideContentHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HideContentHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setWillNotDraw(false);
    }

    private int showPixel = 0;

    public void setShowPixel(int showPixel) {
        if (showPixel > getWidth())
            showPixel = getWidth();
        this.showPixel = showPixel;
        this.invalidate();
    }

    public int getShowPixel(){
        return showPixel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(getWidth() - showPixel, 0, getWidth(), getHeight());
        super.onDraw(canvas);
    }
}
