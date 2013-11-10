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
package com.android.pinnedlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.pinnedlist.PinnedHeaderListView.PinnedHeaderAdapter;

/**
 * A subclass of {@link CompositeAdapter} that manages pinned partition
 * headers.
 */
public abstract class PinnedHeaderListAdapter extends CompositeAdapter implements PinnedHeaderAdapter {

    public static final int PARTITION_HEADER_TYPE = 0;

    private boolean mPinnedPartitionHeadersEnabled;
    private boolean mHeaderVisibility[];

    public PinnedHeaderListAdapter(Context context) {
        super(context);
    }

    public void setPinnedPartitionHeadersEnabled(boolean flag) {
        this.mPinnedPartitionHeadersEnabled = flag;
    }

    @Override
    public int getPinnedHeaderCount() {
        if (mPinnedPartitionHeadersEnabled) {
            return getPartitionCount();
        }
        else {
            return 0;
        }
    }

    protected boolean isPinnedPartitionHeaderVisible(int partition) {
        return mPinnedPartitionHeadersEnabled && hasHeader(partition) && !isPartitionEmpty(partition);
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
    public void configurePinnedHeaders(PinnedHeaderListView listView) {
        if (!mPinnedPartitionHeadersEnabled) {
            return;
        }

        int size = getPartitionCount();

        // DataCache visibility bits, because we will need them several times later
        // on
        if (mHeaderVisibility == null || mHeaderVisibility.length != size) {
            mHeaderVisibility = new boolean[size];
        }
        for (int i = 0; i < size; i++) {
            boolean visible = isPinnedPartitionHeaderVisible(i);
            mHeaderVisibility[i] = visible;
            if (!visible) {
                listView.setHeaderInvisible(i, true);
            }
        }

        int headerViewsCount = listView.getHeaderViewsCount();

        // Starting at the top, find and pin headers for partitions preceding
        // the visible one(s)
        int maxTopHeader = -1;
        int topHeaderHeight = 0;
        for (int i = 0; i < size; i++) {
            if (mHeaderVisibility[i]) {
                int position = listView.getPositionAt(topHeaderHeight) - headerViewsCount;
                int partition = getPartitionForPosition(position);
                if (i > partition) {
                    break;
                }

                if (i > 0) {
                    listView.setHeaderInvisible(i - 1, true);
                }
                
                listView.setHeaderPinnedAtTop(i, topHeaderHeight, false);
                maxTopHeader = i;
            }
        }

        // Headers in between the top-pinned and bottom-pinned should be hidden
        for (int i = maxTopHeader + 1; i < size; i++) {
            if (mHeaderVisibility[i]) {
                listView.setHeaderInvisible(i, isPartitionEmpty(i));
            }
        }
    }

    @Override
    public int getScrollPositionForHeader(int viewIndex) {
        return getPositionForPartition(viewIndex);
    }
}
