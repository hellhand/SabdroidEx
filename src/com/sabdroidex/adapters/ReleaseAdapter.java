package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.MovieRelease;
import com.sabdroidex.data.couchpotato.MovieReleaseInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by Marc on 3/06/13.
 */
public class ReleaseAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<MovieRelease> mItems;

    public ReleaseAdapter(Context context, List<MovieRelease> items) {
        Collections.sort(items);
        mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieReleaseItem movieReleaseItem;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_movie_release, null);
            movieReleaseItem = new MovieReleaseItem();

            movieReleaseItem.releaseName = (TextView) convertView.findViewById(R.id.movie_release_name);
            movieReleaseItem.status = (TextView) convertView.findViewById(R.id.movie_release_status);
            movieReleaseItem.size = (TextView) convertView.findViewById(R.id.movie_release_size);
            movieReleaseItem.age = (TextView) convertView.findViewById(R.id.movie_release_age);
            movieReleaseItem.score = (TextView) convertView.findViewById(R.id.movie_release_score);
            movieReleaseItem.provider = (TextView) convertView.findViewById(R.id.movie_release_provider);

            /*
            movieReleaseItem.info = (ImageButton) convertView.findViewById(R.id.movie_release_info);
            movieReleaseItem.download = (ImageButton) convertView.findViewById(R.id.movie_release_download);
            movieReleaseItem.delete = (ImageButton) convertView.findViewById(R.id.movie_release_delete);
            */
        }
        else {
            movieReleaseItem = (MovieReleaseItem) convertView.getTag();
        }

        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.list_item_bg_dark);
        }
        else {
            convertView.setBackgroundResource(R.drawable.list_item_bg);
        }
        MovieRelease movieRelease = (MovieRelease) getItem(position);
        MovieReleaseInfo movieReleaseInfo = movieRelease.getMovieReleaseInfo();

        StringBuilder size = new StringBuilder();
        size.append(String.valueOf(movieReleaseInfo.getSize()));
        size.append(" ");
        size.append(mInflater.getContext().getString(R.string.mb));

        StringBuilder age = new StringBuilder();
        age.append(String.valueOf(movieReleaseInfo.getAge()));
        size.append(" ");
        size.append(mInflater.getContext().getString(R.string.days));

        movieReleaseItem.releaseName.setText(movieReleaseInfo.getName());
        movieReleaseItem.status.setText(CouchPotatoController.getStatus(movieRelease.getStatusId()));
        movieReleaseItem.size.setText(size);
        movieReleaseItem.age.setText(age);
        movieReleaseItem.score.setText(String.valueOf(movieReleaseInfo.getScore()));
        movieReleaseItem.provider.setText(movieReleaseInfo.getProvider());

        /*
        movieReleaseItem.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        movieReleaseItem.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        movieReleaseItem.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        convertView.setId(position);
        convertView.setTag(movieReleaseItem);
        return convertView;
    }

    private class MovieReleaseItem {

        TextView releaseName;
        TextView status;
        TextView size;
        TextView age;
        TextView score;
        TextView provider;
        ImageButton info;
        ImageButton download;
        ImageButton delete;
    }
}
