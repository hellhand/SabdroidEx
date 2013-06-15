package com.android.pinnedgrid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.pinnedlist.CompositeAdapter;

/**
 * Created by Marc on 13/06/13.
 */
public abstract class PinnedHeaderGridAdapter extends CompositeAdapter implements PinnedHeaderGridView.PinnedHeaderAdapter {

    public static final int PARTITION_HEADER_TYPE = 0;
    private boolean mPinnedPartitionHeadersEnabled;

    public PinnedHeaderGridAdapter(Context context) {
        super(context);
    }

    @Override
    public int getPinnedHeaderCount() {
        if (mPinnedPartitionHeadersEnabled) {
            return 1;
        }
        else {
            return 0;
        }
    }

    protected void setPinnedPartitionHeadersEnabled(boolean pinnedPartitionHeadersEnabled) {
        this.mPinnedPartitionHeadersEnabled = pinnedPartitionHeadersEnabled;
    }

    protected boolean isPinnedPartitionHeaderVisible() {
        return mPinnedPartitionHeadersEnabled;
    }

    /**
     * The default implementation creates the same type of view as a normal
     * partition header.
     */
    @Override
    public View getPinnedHeaderView(int partition, View convertView, ViewGroup parent) {
        if (hasHeader(partition)) {
            View view = null;
            if (convertView != null) {
                Integer headerType = (Integer) convertView.getTag();
                if (headerType != null && headerType == PARTITION_HEADER_TYPE) {
                    view = convertView;
                }
            }
            if (view == null) {
                view = newHeaderView(getContext(), partition, null, parent);
                view.setTag(PARTITION_HEADER_TYPE);
                view.setFocusable(false);
                view.setEnabled(false);
            }
            bindHeaderView(view, partition, getElements(partition));
            return view;
        }
        else {
            return null;
        }
    }

    @Override
    public void configurePinnedHeaders(PinnedHeaderGridView gridView) {
        if (!mPinnedPartitionHeadersEnabled) {
            return;
        }
        boolean visible = isPinnedPartitionHeaderVisible();
        if (!visible) {
            gridView.setHeaderInvisible(0, true);
        }

        if (visible) {
            gridView.setHeaderPinnedAtTop(0, 0, false);
        }
    }

    @Override
    public int getScrollPositionForHeader(int viewIndex) {
        return getPositionForPartition(viewIndex);
    }
}
