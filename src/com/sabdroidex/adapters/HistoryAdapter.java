package com.sabdroidex.adapters;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.sabnzbd.HistoryElement;

public class HistoryAdapter extends ArrayAdapter<HistoryElement> {

    public HistoryAdapter(Context context, List<HistoryElement> items) {
        super(context, R.layout.list_item, items);
    }

    @Override
    public void addAll(Collection<? extends HistoryElement> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            for (HistoryElement element : collection) {
                super.add(element);
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryListItem mHistoryListItem;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            mHistoryListItem = new HistoryListItem();
            mHistoryListItem.filename = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mHistoryListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mHistoryListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mHistoryListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        }
        else {
            mHistoryListItem = (HistoryListItem) convertView.getTag();
        }
        
        HistoryElement element = getItem(position);
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