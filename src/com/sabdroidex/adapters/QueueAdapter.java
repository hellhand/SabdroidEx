package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.sabnzbd.QueueElement;
import com.utils.Formatter;

import java.util.List;

public class QueueAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<QueueElement> mItems;

    public QueueAdapter(Context context, List<QueueElement> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    public void setItems(List<QueueElement> items) {
        this.mItems = items;
    }

    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
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
        QueueListItem mQueueListItem;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            mQueueListItem = new QueueListItem();
            mQueueListItem.fileName = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mQueueListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mQueueListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mQueueListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        } else {
            mQueueListItem = (QueueListItem) convertView.getTag();
        }

        QueueElement element = (QueueElement) getItem(position);
        String eta = element.getTimeLeft();
        String completed = Formatter.formatShort(Double.valueOf(element.getMbLeft())) + " / " + Formatter.formatShort(Double.valueOf(element.getMb())) + " MB";
        String status = element.getStatus();
        String fileName = element.getFilename();

        mQueueListItem.fileName.setText(fileName);
        mQueueListItem.eta.setText(eta);
        mQueueListItem.completed.setText(completed);
        if ("Paused".equals(status))
            mQueueListItem.status.setImageResource(R.drawable.ic_action_pause);
        else
            mQueueListItem.status.setImageResource(R.drawable.ic_action_play);

        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    class QueueListItem {

        TextView fileName;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}
