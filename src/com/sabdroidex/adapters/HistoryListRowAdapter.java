package com.sabdroidex.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.HistoryElement;

public class HistoryListRowAdapter extends ArrayAdapter<HistoryElement> {

    private Context mContext;
    private LayoutInflater mInflater;
    private HistoryListItem mHistoryListItem;
    private List<HistoryElement> mItems;

    @Override
    public int getCount() {
        return mItems.size();
    }

    public HistoryListRowAdapter(Context context, List<HistoryElement> items) {
        super(context, R.layout.list_item, items);
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            mHistoryListItem = new HistoryListItem();
            mHistoryListItem.filemame = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mHistoryListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mHistoryListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mHistoryListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        }
        else {
            mHistoryListItem = (HistoryListItem) convertView.getTag();
        }

        if (mItems == null || mItems.size() <= position) {
            return convertView;
        }
        
        HistoryElement element = mItems.get(position);
        
        mHistoryListItem.filemame.setText(element.getName());
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

        TextView filemame;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}
