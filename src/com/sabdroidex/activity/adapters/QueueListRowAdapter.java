package com.sabdroidex.activity.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.utils.Calculator;
import com.utils.Formatter;

public class QueueListRowAdapter extends ArrayAdapter<Object[]> {

    private Context mContext;
    private LayoutInflater mInflater;
    private QueueListItem mQueueListItem;
    private ArrayList<Object[]> mItems;

    @Override
    public int getCount() {
        return mItems.size();
    }

    public QueueListRowAdapter(Context context, ArrayList<Object[]> items) {
        super(context, R.layout.list_item, items);
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            mQueueListItem = new QueueListItem();
            mQueueListItem.filemame = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mQueueListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mQueueListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mQueueListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        }
        else {
            mQueueListItem = (QueueListItem) convertView.getTag();
        }

        Object[] values = mItems.get(position);
        String eta = Calculator.calculateETA(((Double) values[2]), SABnzbdController.speed);
        String completed = Formatter.formatShort(((Double) values[2])) + " / " + Formatter.formatShort(((Double) values[1])) + " MB";
        String status = (String) values[3];
        String fileName = (String) values[0];

        mQueueListItem.filemame.setText(fileName);
        mQueueListItem.eta.setText(eta);
        mQueueListItem.completed.setText(completed);
        if ("Paused".equals(status))
            mQueueListItem.status.setImageResource(android.R.drawable.ic_media_pause);
        else
            mQueueListItem.status.setImageResource(android.R.drawable.ic_media_play);

        convertView.setId(position);
        convertView.setTag(mQueueListItem);
        return (convertView);
    }

    class QueueListItem {

        TextView filemame;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}
