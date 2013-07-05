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
import android.view.View;
import android.view.ViewGroup;

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
    public View getPinnedHeaderView(View convertView, ViewGroup parent) {
        if (mPinnedPartitionHeadersEnabled) {
            View view = null;
            if (convertView != null) {
                Integer headerType = (Integer) convertView.getTag();
                if (headerType != null && headerType == PARTITION_HEADER_TYPE) {
                    view = convertView;
                }
            }
            if (view == null) {
                view = newHeaderView(getContext(), null, parent);
                view.setTag(PARTITION_HEADER_TYPE);
                view.setFocusable(false);
                view.setEnabled(false);
            }
            bindHeaderView(view, getPartition().header);
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
        return 0;
    }
}
