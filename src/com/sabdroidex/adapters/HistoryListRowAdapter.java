package com.sabdroidex.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;

public class HistoryListRowAdapter extends ArrayAdapter<Object[]> {

    private Context mContext;
    private LayoutInflater mInflater;
    private HistoryListItem mQueueListItem;
    private ArrayList<Object[]> mItems;

    @Override
    public int getCount() {
        return mItems.size();
    }

    public HistoryListRowAdapter(Context context, ArrayList<Object[]> items) {
        super(context, R.layout.list_item, items);
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            mQueueListItem = new HistoryListItem();
            mQueueListItem.filemame = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mQueueListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mQueueListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mQueueListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        }
        else {
            mQueueListItem = (HistoryListItem) convertView.getTag();
        }

        Object[] values = mItems.get(position);
        String completed = (String) values[1];

        mQueueListItem.filemame.setText((String) values[0]);
        mQueueListItem.eta.setText(R.string.adapter_done);
        mQueueListItem.completed.setText(completed);
        mQueueListItem.status.setImageResource(android.R.drawable.stat_sys_download_done);

        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    class HistoryListItem {

        TextView filemame;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}
