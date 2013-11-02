package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.sabnzbd.HistoryElement;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<HistoryElement> mItems;

    public HistoryAdapter(Context context, List<HistoryElement> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    public void setItems(List<HistoryElement> items) {
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryListItem mHistoryListItem;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            mHistoryListItem = new HistoryListItem();
            mHistoryListItem.filename = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mHistoryListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mHistoryListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mHistoryListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        }
        else {
            mHistoryListItem = (HistoryListItem) convertView.getTag();
        }
        
        HistoryElement element = (HistoryElement) getItem(position);
        mHistoryListItem.filename.setText(element.getName());
        mHistoryListItem.eta.setText(element.getStatus());
        mHistoryListItem.completed.setText(element.getSize());
        mHistoryListItem.status.setImageResource(android.R.drawable.stat_sys_download_done);

        convertView.setId(position);
        convertView.setTag(mHistoryListItem);
        return (convertView);
    }
    
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    class HistoryListItem {

        TextView filename;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}
