package com.cascade.listenerscrollview;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

public class ListenerScrollView extends ScrollView {
    //region CONSTRUCTS
    interface ScrollViewListener {
        void onScrollStateChanged(ScrollState scrollState, float positionProportion);

        void onSwipedDown();
    }

    enum ScrollState {
        TOP,
        IN_BETWEEN,
        BOTTOM
    }
    //endregion

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private ScrollViewListener scrollViewListener;

    public ListenerScrollView(Context context) {
        super(context);
        initialize();
    }

    public ListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @RequiresApi(21)
    public ListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        computeScrollChange();
    }

    private void initialize() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                addSwipeListener();
            }
        });

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                computeScrollChange();
            }
        });
    }

    private void addSwipeListener() {
        View child = getChildAt(0);
        int childHeight = child.getHeight();
        boolean isScrollable = getHeight() < childHeight + getPaddingTop() + getPaddingBottom();
        if (!isScrollable) {
            setOnTouchListener(new OnTouchListener() {

                private final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (e1 != null && e2 != null) {
                            float diffY = e2.getY() - e1.getY();
                            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffY < 0) {
                                    if (scrollViewListener != null)
                                        scrollViewListener.onSwipedDown();
                                }
                            }

                        }
                        return true;
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);

                    return ListenerScrollView.super.onTouchEvent(event);
                }
            });
        }
    }

    private void computeScrollChange() {
        if (scrollViewListener != null) {
            float scrollProportion = 0;

            if (getChildCount() > 0) {
                float scrollHeight = getChildAt(0).getHeight() - getHeight();
                float scrollAmount = getScrollY();

                if (scrollAmount == 0.0 || scrollHeight == 0.0)
                    scrollProportion = 0;
                else
                    scrollProportion = scrollAmount / scrollHeight;

                if (scrollProportion < 0)
                    scrollProportion = 0;
                else if (scrollProportion > 1)
                    scrollProportion = 1;
            }

            View view = getChildAt(getChildCount() - 1);
            int diff = view.getBottom() - (getHeight() + getScrollY());

            if (getScrollY() == 0)
                scrollViewListener.onScrollStateChanged(ScrollState.TOP, 0);
            else if (diff == 0)
                scrollViewListener.onScrollStateChanged(ScrollState.BOTTOM, 1);
            else if (getScrollY() > 0)
                scrollViewListener.onScrollStateChanged(ScrollState.IN_BETWEEN, scrollProportion);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
}