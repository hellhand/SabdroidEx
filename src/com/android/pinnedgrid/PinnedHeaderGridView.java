/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.pinnedgrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class PinnedHeaderGridView extends GridView implements OnScrollListener, OnItemSelectedListener {

    private long mAnimationTargetTime;
    private OnScrollListener mOnScrollListener;
    private int mSize;
    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int FADING = 2;
    private static final int MAX_ALPHA = 255;
    private boolean mAnimating;
    private RectF mBounds = new RectF();
    private Rect mClipRect = new Rect();

    /**
     * Adapter interface.  The list adapter must implement this interface.
     */
    public interface PinnedHeaderAdapter {

        /**
         * Returns the overall number of pinned headers, visible or not.
         */
        int getPinnedHeaderCount();

        /**
         * Creates or updates the pinned header view.
         */
        View getPinnedHeaderView(View convertView, ViewGroup parent);

        void configurePinnedHeaders(PinnedHeaderGridView gridView);

        /**
         * Returns the list position to scroll to if the pinned header is touched.
         * Return -1 if the list does not need to be scrolled.
         */
        int getScrollPositionForHeader(int viewIndex);
    }

    private PinnedHeader mHeader;
    private int mHeaderPaddingLeft;
    private int mHeaderWidth;
    private int mAnimationDuration;
    private PinnedHeaderAdapter mAdapter;

    private static final class PinnedHeader {
        View view;
        boolean visible;
        int y;
        int height;
        int alpha;
        int state;

        boolean animating;
        boolean targetVisible;
        int sourceY;
        int targetY;
        long targetTime;
    }

    public PinnedHeaderGridView(Context context) {
        this(context,null);
    }

    public PinnedHeaderGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
        super.setOnItemSelectedListener(this);
    }

    public void setPinnedHeaderAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = (PinnedHeaderAdapter)adapter;
        super.setAdapter(adapter);
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
        super.setOnScrollListener(this);
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        super.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mHeaderPaddingLeft = getPaddingLeft();
        mHeaderWidth = r - l - mHeaderPaddingLeft - getPaddingRight();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (mAdapter != null) {
            if (mHeader == null) {
                mHeader = new PinnedHeader();
            }
            mHeader.view = mAdapter.getPinnedHeaderView(mHeader.view, this);

            mAnimationTargetTime = System.currentTimeMillis() + mAnimationDuration;
            mAdapter.configurePinnedHeaders(this);
            invalidateIfAnimating();

        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        // Disable vertical fading at the top when the pinned header is present
        return mSize > 0 ? 0 : super.getTopFadingEdgeStrength();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(this, scrollState);
        }
    }

    /**
     * Set header to be pinned at the top.
     *
     * @param viewIndex index of the header view
     * @param y is position of the header in pixels.
     * @param animate true if the transition to the new coordinate should be animated
     */
    public void setHeaderPinnedAtTop(int viewIndex, int y, boolean animate) {
        ensurePinnedHeaderLayout(viewIndex);
        PinnedHeader header = mHeader;
        header.visible = true;
        header.y = y;

        // TODO perhaps we should animate at the top as well
        header.animating = false;
    }

    /**
     * Set header to be pinned at the bottom.
     *
     * @param viewIndex index of the header view
     * @param y is position of the header in pixels.
     * @param animate true if the transition to the new coordinate should be animated
     */
    public void setHeaderPinnedAtBottom(int viewIndex, int y, boolean animate) {
        ensurePinnedHeaderLayout(viewIndex);
        PinnedHeader header = mHeader;
        header.state = BOTTOM;
        if (header.animating) {
            header.targetTime = mAnimationTargetTime;
            header.sourceY = header.y;
            header.targetY = y;
        } else if (animate && (header.y != y || !header.visible)) {
            if (header.visible) {
                header.sourceY = header.y;
            } else {
                header.visible = true;
                header.sourceY = y + header.height;
            }
            header.animating = true;
            header.targetVisible = true;
            header.targetTime = mAnimationTargetTime;
            header.targetY = y;
        } else {
            header.visible = true;
            header.y = y;
        }
    }

    /**
     * Set header to be pinned at the top of the first visible item.
     *
     * @param viewIndex index of the header view
     * @param position is position of the header in pixels.
     */
    public void setFadingHeader(int viewIndex, int position, boolean fade) {
        ensurePinnedHeaderLayout(viewIndex);

        View child = getChildAt(position - getFirstVisiblePosition());
        if (child == null) return;

        PinnedHeader header = mHeader;
        header.visible = true;
        header.state = FADING;
        header.alpha = MAX_ALPHA;
        header.animating = false;

        int top = getTotalTopPinnedHeaderHeight();
        header.y = top;
        if (fade) {
            int bottom = child.getBottom() - top;
            int headerHeight = header.height;
            if (bottom < headerHeight) {
                int portion = bottom - headerHeight;
                header.alpha = MAX_ALPHA * (headerHeight + portion) / headerHeight;
                header.y = top + portion;
            }
        }
    }

    /**
     * Makes header invisible.
     *
     * @param viewIndex index of the header view
     * @param animate true if the transition to the new coordinate should be animated
     */
    public void setHeaderInvisible(int viewIndex, boolean animate) {
        PinnedHeader header = mHeader;
        if (header.visible && (animate || header.animating) && header.state == BOTTOM) {
            header.sourceY = header.y;
            if (!header.animating) {
                header.visible = true;
                header.targetY = getBottom() + header.height;
            }
            header.animating = true;
            header.targetTime = mAnimationTargetTime;
            header.targetVisible = false;
        } else {
            header.visible = false;
        }
    }

    private void ensurePinnedHeaderLayout(int viewIndex) {
        View view = mHeader.view;
        if (view.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.EXACTLY);
            int heightSpec;
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            view.measure(widthSpec, heightSpec);
            int height = view.getMeasuredHeight();
            mHeader.height = height;
            view.layout(0, 0, mHeaderWidth, height);
        }
    }

    /**
     * Returns the sum of heights of headers pinned to the top.
     */
    public int getTotalTopPinnedHeaderHeight() {
        PinnedHeader header = mHeader;
        if (header.visible && header.state == TOP) {
            return header.y + header.height;
        }
        return 0;
    }

    /**
     * Returns the list item position at the specified y coordinate.
     */
    public int getPositionAt(int y) {
        do {
            int position = pointToPosition(getPaddingLeft() + 1, y);
            if (position != -1) {
                return position;
            }
            // If position == -1, we must have hit a separator. Let's examine
            // a nearby pixel
            y--;
        } while (y > 0);
        return 0;
    }

    private void invalidateIfAnimating() {
        mAnimating = false;
        for (int i = 0; i < mSize; i++) {
            if (mHeader.animating) {
                mAnimating = true;
                invalidate();
                return;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        long currentTime = mAnimating ? System.currentTimeMillis() : 0;

        int top = 0;
        int bottom = getBottom();
        boolean hasVisibleHeaders = false;
        PinnedHeader header = mHeader;
        if (header.visible) {
            hasVisibleHeaders = true;
            if (header.state == BOTTOM && header.y < bottom) {
                bottom = header.y;
            } else if (header.state == TOP || header.state == FADING) {
                int newTop = header.y + header.height;
                if (newTop > top) {
                    top = newTop;
                }
            }
        }

        if (hasVisibleHeaders) {
            canvas.save();
            mClipRect.set(0, top, getWidth(), bottom);
            canvas.clipRect(mClipRect);
        }

        super.dispatchDraw(canvas);

        if (hasVisibleHeaders) {
            canvas.restore();
            drawHeader(canvas, header, currentTime);
        }

        invalidateIfAnimating();
    }

    private void drawHeader(Canvas canvas, PinnedHeader header, long currentTime) {
        if (header.animating) {
            int timeLeft = (int)(header.targetTime - currentTime);
            if (timeLeft <= 0) {
                header.y = header.targetY;
                header.visible = header.targetVisible;
                header.animating = false;
            } else {
                header.y = header.targetY + (header.sourceY - header.targetY) * timeLeft
                    / mAnimationDuration;
            }
        }
        if (header.visible) {
            View view = header.view;
            int saveCount = canvas.save();
            canvas.translate(mHeaderPaddingLeft, header.y);
            if (header.state == FADING) {
                mBounds.set(0, 0, mHeaderWidth, view.getHeight());
                canvas.saveLayerAlpha(mBounds, header.alpha, Canvas.ALL_SAVE_FLAG);
            }
            view.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }
}
