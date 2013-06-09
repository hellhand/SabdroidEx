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
import com.sabdroidex.data.sabnzbd.QueueElement;
import com.utils.Formatter;

public class QueueAdapter extends ArrayAdapter<QueueElement> {

    public QueueAdapter(Context context, List<QueueElement> items) {
        super(context, R.layout.list_item, items);
    }

    @Override
    public void addAll(Collection<? extends QueueElement> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            for (QueueElement element : collection) {
                super.add(element);
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        QueueListItem mQueueListItem;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            mQueueListItem = new QueueListItem();
            mQueueListItem.filemame = (TextView) convertView.findViewById(R.id.queueRowLabelFilename);
            mQueueListItem.eta = (TextView) convertView.findViewById(R.id.queueRowLabelEta);
            mQueueListItem.completed = (TextView) convertView.findViewById(R.id.queueRowLabelCompleted);
            mQueueListItem.status = (ImageView) convertView.findViewById(R.id.queueRowStatus);
        } else {
            mQueueListItem = (QueueListItem) convertView.getTag();
        }

        QueueElement element = getItem(position);
        String eta = element.getTimeLeft();
        //todo: resource bundle
        String completed = Formatter.formatShort(new Double(element.getMbLeft())) + " / " + Formatter.formatShort(new Double(element.getMb())) + " MB";
        String status = element.getStatus();
        String fileName = element.getFilename();

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

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    class QueueListItem {

        TextView filemame;
        TextView eta;
        TextView completed;
        ImageView status;
    }
}