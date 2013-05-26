package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.pinnedlist.PinnedHeaderListAdapter;
import com.sabdroidex.R;
import com.sabdroidex.data.sickbeard.FutureEpisode;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

import java.util.Collection;

public class ComingListRowAdapter extends PinnedHeaderListAdapter {

    public ComingListRowAdapter(Context context, FuturePeriod futurePeriod) {
        super(context);
        setPinnedPartitionHeadersEnabled(true);
        setDataSet(futurePeriod);
    }

    public void setDataSet(FuturePeriod futurePeriod) {
        Partition[] partitions = new Partition[4];
        
        partitions[0] = new Partition(false, futurePeriod.getMissedTitle());
        partitions[0].setElements(futurePeriod.getMissed());

        partitions[1] = new Partition(false, futurePeriod.getTodayTitle());
        partitions[1].setElements(futurePeriod.getToday());

        partitions[2] = new Partition(false, futurePeriod.getSoonTitle());
        partitions[2].setElements(futurePeriod.getSoon());

        partitions[3] = new Partition(false, futurePeriod.getLaterTitle());
        partitions[3].setElements(futurePeriod.getLater());
        
        setPartitions(partitions);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    protected View newHeaderView(Context context, int partition, Collection<?> elements, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_section, null);
    }

    @Override
    protected void bindHeaderView(View view, int partition, Collection<?> elements) {
        TextView headerText = (TextView) view.findViewById(R.id.title);
        headerText.setText(getPartition(partition).getHeader());
    }

    @Override
    protected View newView(Context context, int partition, Object element, int position, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.coming_item, null);
    }

    @Override
    protected void bindView(View v, int partition, Object element, int position) {
        ShowsListItem comingItem;
        if (v.getTag() == null) {
            comingItem = new ShowsListItem();
            comingItem.banner = (ImageView) v.findViewById(R.id.coming_show_banner);
            comingItem.next = (TextView) v.findViewById(R.id.coming_next_episode);
            comingItem.airDate = (TextView) v.findViewById(R.id.coming_air_date);
            comingItem.airs = (TextView) v.findViewById(R.id.coming_airs);
        }
        else {
            comingItem = (ShowsListItem) v.getTag();
        }

        FutureEpisode futureEpisode = (FutureEpisode) element;

        String imageKey = ImageType.SHOW_BANNER.name() + futureEpisode.getTvdbId();
        ImageUtils.getImageWorker().loadImage(comingItem.banner, ImageType.SHOW_BANNER, imageKey, futureEpisode.getTvdbId(), futureEpisode.getShowName());

        String nextDescriptor = String.format("%02dx%02d", futureEpisode.getEpisode(), futureEpisode.getSeason());
        String next = nextDescriptor + " - " + futureEpisode.getEpName();
        String airs = String.format("%s %s [%s]", futureEpisode.getAirs(), futureEpisode.getNetwork(), futureEpisode.getQuality());

        comingItem.next.setText(next);
        comingItem.airDate.setText(futureEpisode.getAirDate());
        comingItem.airs.setText(airs);

        v.setId(position);
        v.setTag(comingItem);
    }

    @Override
    public View getPinnedHeaderView(int viewIndex, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.list_section, parent, false);
        view.setFocusable(false);
        view.setEnabled(false);
        bindHeaderView(view, viewIndex, null);
        return view;
    }

    /**
     * This inner class is used to represent the content of a list item.
     */
    class ShowsListItem {

        ImageView banner;
        TextView next;
        TextView airDate;
        TextView airs;
    }

}
