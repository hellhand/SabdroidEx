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
import android.widget.BaseAdapter;

import java.util.Collection;

public abstract class CompositeAdapter extends BaseAdapter {
    
    private Context mContext;
    private Partition partition;

    public static class Partition {

        boolean showIfEmpty;
        int count;
        Collection<?> elements;
        Object header;

        public Partition(boolean showIfEmpty) {
            this.showIfEmpty = showIfEmpty;
        }

        public boolean hasHeader() {
            return header != null;
        }
        
        public void setHeader(Object header) {
            this.header = header;
        }
        
        public Object getHeader() {
            return header;
        }
        
        public void setElements(Collection<?> elements) {
            this.elements = elements;
            this.count = elements.size();
        }
        
        public int getCount() {
            return count;
        }

        public boolean isShowIfEmpty() {
            return showIfEmpty;
        }

        public void setShowIfEmpty(boolean showIfEmpty) {
            this.showIfEmpty = showIfEmpty;
        }
    }
    
    protected CompositeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public Context getContext() {
        return mContext;
    }

    public void setPartition(Partition partition) {
        this.partition = partition;
        notifyDataSetChanged();
    }
        
    public Partition getPartition() {
        return partition;
    }
    
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return partition.elements.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return partition.elements.toArray()[position];
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (position < partition.elements.size()) {
            int offset = position - (partition.hasHeader() ? 1 : 0);
            View view;
            if (offset == -1) {
                view = getHeaderView(partition.header, convertView, parent);
            }
            else {
                if (partition.elements.size() < offset) {
                    throw new IllegalStateException("Cannot access element : " + offset);
                }
                view = getView(partition.elements.toArray()[offset], offset, convertView, parent);
            }
            if (view == null) {
                throw new NullPointerException("View should not be null position: " + offset);
            }
            return view;
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    /**
     * Returns the header view for the specified partition, creating one if
     * needed.
     */
    protected View getHeaderView(Object element, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : newHeaderView(mContext, element, parent);
        bindHeaderView(view, element);
        return view;
    }

    /**
     * Creates the header view for the specified partition.
     */
    protected abstract View newHeaderView(Context context, Object element, ViewGroup parent);

    /**
     * Binds the header view for the specified partition.
     */
    protected abstract void bindHeaderView(View view, Object element);

    /**
     * Returns an item view for the specified partition, creating one if needed.
     */
    protected View getView(Object element, int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        }
        else {
            view = newView(mContext, element, position, parent);
        }
        bindView(view, element, position);
        return view;
    }

    /**
     * Creates an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    protected abstract View newView(Context context, Object element, int position, ViewGroup parent);

    /**
     * Binds an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    protected abstract void bindView(View v, Object element, int position);

}
