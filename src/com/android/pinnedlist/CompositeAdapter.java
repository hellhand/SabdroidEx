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
import android.widget.BaseAdapter;

import java.util.Collection;

/**
 * A general purpose adapter that is composed of multiple Collections. It just
 * appends them in the order they are added.
 */
/**
 * This class is used instead of {@link CompositeAdapter} because creating
 * a cursor with the needed data would be too much in this case, using the POJOS
 * from start to end is an easier choice
 */
public abstract class CompositeAdapter extends BaseAdapter {

    private static final int INITIAL_CAPACITY = 2;

    public static class Partition {

        boolean showIfEmpty;
        int header = -1;

        Collection<?> elements;
        int count;

        public Partition(boolean showIfEmpty, int header) {
            this.showIfEmpty = showIfEmpty;
            this.header = header;
        }

        /**
         * True if the directory should be shown even if no contacts are found.
         */
        public boolean getShowIfEmpty() {
            return showIfEmpty;
        }

        public boolean hasHeader() {
            return header != -1;
        }
        
        public int getHeader() {
            return header;
        }

        public Collection<?> getElements() {
            return elements;
        }

        public void setElements(Collection<?> elements) {
            this.elements = elements;
        }
    }

    private final Context mContext;
    private Partition[] mPartitions;
    private int mSize = 0;
    private int mCount = 0;
    private boolean mCacheValid = true;
    private boolean mNotificationsEnabled = true;
    private boolean mNotificationNeeded;

    public CompositeAdapter(Context context) {
        this(context, INITIAL_CAPACITY);
    }

    public CompositeAdapter(Context context, int initialCapacity) {
        mContext = context;
        mPartitions = new Partition[INITIAL_CAPACITY];
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Registers a partition. The cursor for that partition can be set later.
     * Partitions should be added in the order they are supposed to appear in
     * the list.
     */
    public void addPartition(boolean showIfEmpty, int header) {
        addPartition(new Partition(showIfEmpty, header));
    }

    public void addPartition(Partition partition) {
        if (mSize >= mPartitions.length) {
            int newCapacity = mSize + 2;
            Partition[] newAdapters = new Partition[newCapacity];
            System.arraycopy(mPartitions, 0, newAdapters, 0, mSize);
            mPartitions = newAdapters;
        }
        mPartitions[mSize++] = partition;
        invalidate();
        notifyDataSetChanged();
    }

    public void setPartitions(Partition... partitions) {
        mPartitions = partitions;
        mSize = mPartitions.length;
        invalidate();
        notifyDataSetChanged();
    }
    
    public void removePartition(int partitionIndex) {
        System.arraycopy(mPartitions, partitionIndex + 1, mPartitions, partitionIndex, mSize - partitionIndex - 1);
        mSize--;
        invalidate();
        notifyDataSetChanged();
    }

    /**
     * Removes cursors for all partitions.
     */
    public void clearPartitions() {
        mPartitions = new Partition[INITIAL_CAPACITY];
        invalidate();
        notifyDataSetChanged();
    }

    /**
     * Removes all partitions.
     */
    public void close() {
        mSize = 0;
        invalidate();
        notifyDataSetChanged();
    }

    public void setHeader(int partitionIndex, int header) {
        mPartitions[partitionIndex].header = header;
        invalidate();
    }

    public void setShowIfEmpty(int partitionIndex, boolean flag) {
        mPartitions[partitionIndex].showIfEmpty = flag;
        invalidate();
    }

    public Partition getPartition(int partitionIndex) {
        if (partitionIndex >= mSize) {
            throw new ArrayIndexOutOfBoundsException(partitionIndex);
        }
        return mPartitions[partitionIndex];
    }

    protected void invalidate() {
        mCacheValid = false;
    }

    public int getPartitionCount() {
        return mSize;
    }

    protected void ensureCacheValid() {
        if (mCacheValid) {
            return;
        }

        mCount = 0;
        for (int i = 0; i < mSize; i++) {
            Collection<?> elements = mPartitions[i].elements;
            int count = elements != null ? elements.size() : 0;
            if (mPartitions[i].hasHeader()) {
                if (count != 0 || mPartitions[i].showIfEmpty) {
                    count++;
                }
            }
            mPartitions[i].count = count;
            mCount += count;
        }

        mCacheValid = true;
    }

    /**
     * Returns true if the specified partition was configured to have a header.
     */
    public boolean hasHeader(int partition) {
        return mPartitions[partition].hasHeader();
    }

    /**
     * Returns the total number of list items in all partitions.
     */
    public int getCount() {
        ensureCacheValid();
        return mCount;
    }

    /**
     * Returns the cursor for the given partition
     */
    public Collection<?> getElements(int partition) {
        return mPartitions[partition].elements;
    }

    /**
     * Changes the elements for an individual partition.
     */
    public void changeElements(int partition, Collection<?> elements) {
        Collection<?> prevElements = mPartitions[partition].elements;
        if (prevElements != elements) {
            mPartitions[partition].elements = elements;
            invalidate();
            notifyDataSetChanged();
        }
    }

    /**
     * Returns true if the specified partition has no cursor or an empty cursor.
     */
    public boolean isPartitionEmpty(int partition) {
        Collection<?> elements = mPartitions[partition].elements;
        return elements == null || elements.size() == 0;
    }

    /**
     * Given a list position, returns the index of the corresponding partition.
     */
    public int getPartitionForPosition(int position) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                return i;
            }
            start = end;
        }
        return -1;
    }

    /**
     * Given a list position, return the offset of the corresponding item in its
     * partition. The header, if any, will have offset -1.
     */
    public int getOffsetInPartition(int position) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                int offset = position - start;
                if (mPartitions[i].hasHeader()) {
                    offset--;
                }
                return offset;
            }
            start = end;
        }
        return -1;
    }

    /**
     * Returns the first list position for the specified partition.
     */
    public int getPositionForPartition(int partition) {
        ensureCacheValid();
        int position = 0;
        for (int i = 0; i < partition; i++) {
            position += mPartitions[i].count;
        }
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getItemViewTypeCount() + 1;
    }

    /**
     * Returns the overall number of item view types across all partitions. An
     * implementation of this method needs to ensure that the returned count is
     * consistent with the values returned by {@link #getItemViewType(int,int)}.
     */
    public int getItemViewTypeCount() {
        return 1;
    }

    /**
     * Returns the view type for the list item at the specified position in the
     * specified partition.
     */
    protected int getItemViewType(int partition, int position) {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                int offset = position - start;
                if (mPartitions[i].hasHeader() && offset == 0) {
                    return IGNORE_ITEM_VIEW_TYPE;
                }
                return getItemViewType(i, position);
            }
            start = end;
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                int offset = position - start;
                if (mPartitions[i].hasHeader()) {
                    offset--;
                }
                View view;
                if (offset == -1) {
                    view = getHeaderView(i, mPartitions[i].elements, convertView, parent);
                }
                else {
                    if (mPartitions[i].elements.size() < offset) {
                        throw new IllegalStateException("Cannot access element : " + offset);
                    }
                    view = getView(i, mPartitions[i].elements.toArray()[offset], offset, convertView, parent);
                }
                if (view == null) {
                    throw new NullPointerException("View should not be null, partition: " + i + " position: " + offset);
                }
                return view;
            }
            start = end;
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    /**
     * Returns the header view for the specified partition, creating one if
     * needed.
     */
    protected View getHeaderView(int partition, Collection<?> elements, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : newHeaderView(mContext, partition, elements, parent);
        bindHeaderView(view, partition, elements);
        return view;
    }

    /**
     * Creates the header view for the specified partition.
     */
    protected View newHeaderView(Context context, int partition, Collection<?> elements, ViewGroup parent) {
        return null;
    }

    /**
     * Binds the header view for the specified partition.
     */
    protected void bindHeaderView(View view, int partition, Collection<?> elements) {}

    /**
     * Returns an item view for the specified partition, creating one if needed.
     */
    protected View getView(int partition, Object element, int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        }
        else {
            view = newView(mContext, partition, element, position, parent);
        }
        bindView(view, partition, element, position);
        return view;
    }

    /**
     * Creates an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    protected abstract View newView(Context context, int partition, Object element, int position, ViewGroup parent);

    /**
     * Binds an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    protected abstract void bindView(View v, int partition, Object element, int position);

    /**
     * Returns the elements linked to the position.
     */
    public Object getItem(int position) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                int offset = position - start;
                if (mPartitions[i].hasHeader()) {
                    offset--;
                }
                if (offset == -1) {
                    return null;
                }
                return mPartitions[i].elements;
            }
            start = end;
        }

        return null;
    }

    /**
     * Returns the item ID for the specified list position.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns false if any partition has a header.
     */
    @Override
    public boolean areAllItemsEnabled() {
        for (int i = 0; i < mSize; i++) {
            if (mPartitions[i].hasHeader()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true for all items except headers.
     */
    @Override
    public boolean isEnabled(int position) {
        ensureCacheValid();
        int start = 0;
        for (int i = 0; i < mSize; i++) {
            int end = start + mPartitions[i].count;
            if (position >= start && position < end) {
                int offset = position - start;
                if (mPartitions[i].hasHeader() && offset == 0) {
                    return false;
                }
                else {
                    return isEnabled(i, offset);
                }
            }
            start = end;
        }

        return false;
    }

    /**
     * Returns true if the item at the specified offset of the specified
     * partition is selectable and clickable.
     */
    protected boolean isEnabled(int partition, int position) {
        return true;
    }

    /**
     * Enable or disable data change notifications. It may be a good idea to
     * disable notifications before making changes to several partitions at
     * once.
     */
    public void setNotificationsEnabled(boolean flag) {
        mNotificationsEnabled = flag;
        if (flag && mNotificationNeeded) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (mNotificationsEnabled) {
            mNotificationNeeded = false;
            super.notifyDataSetChanged();
        }
        else {
            mNotificationNeeded = true;
        }
    }
}
