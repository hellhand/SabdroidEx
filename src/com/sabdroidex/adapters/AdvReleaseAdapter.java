package com.sabdroidex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pinnedgrid.PinnedHeaderGridAdapter;
import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieRelease;
import com.sabdroidex.data.couchpotato.MovieReleaseInfo;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker;

import java.util.Collections;

/**
 * Created by Marc on 3/06/13.
 */
public class AdvReleaseAdapter extends PinnedHeaderGridAdapter {

    public AdvReleaseAdapter(Context context, Movie movie) {
        super(context);
        setPinnedPartitionHeadersEnabled(true);
        setDataSet(movie);
    }

    public void setDataSet(Movie movie) {
        Collections.sort(movie.getReleases());
        Partition partition = new Partition(true);
        partition.setElements(movie.getReleases());
        partition.setHeader(movie);
        setPartition(partition);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    protected View newHeaderView(Context context, Object element, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_movie, null);
    }

    @Override
    protected void bindHeaderView(View view, Object element) {
        Movie movie = (Movie) getPartition().getHeader();
        HeaderItem headerItem;
        if (view.getTag() == null) {
            headerItem = new HeaderItem();
            headerItem.poster = (ImageView) view.findViewById(R.id.movie_poster);
            headerItem.title = (TextView) view.findViewById(R.id.movie_title);
        }
        else {
            headerItem = (HeaderItem) view.getTag();
        }

        String imageKey = ImageWorker.ImageType.MOVIE_POSTER.name() + movie.getMovieID();
        ImageUtils.getImageWorker().loadImage(headerItem.poster, ImageWorker.ImageType.MOVIE_BANNER, imageKey, movie.getMovieID(), movie.getTitle(), movie.getLibrary().getInfo().getPosters().getOriginalPoster());
        headerItem.title.setText(movie.getTitle());

        view.setTag(headerItem);
    }

    /**
     * Creates an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    @Override
    protected View newView(Context context, Object element, int position, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        return layoutInflater.inflate(R.layout.list_item_movie_release, null);
    }

    /**
     * Binds an item view for the specified partition and position. Position
     * corresponds directly to the current cursor position.
     */
    @Override
    protected void bindView(View v, Object element, int position) {
        MovieReleaseItem movieReleaseItem;
        if (v.getTag() == null) {
            movieReleaseItem = new MovieReleaseItem();
            movieReleaseItem.releaseName = (TextView) v.findViewById(R.id.movie_release_name);
            movieReleaseItem.status = (TextView) v.findViewById(R.id.movie_release_status);
            movieReleaseItem.size = (TextView) v.findViewById(R.id.movie_release_size);
            movieReleaseItem.age = (TextView) v.findViewById(R.id.movie_release_age);
            movieReleaseItem.score = (TextView) v.findViewById(R.id.movie_release_score);
            movieReleaseItem.provider = (TextView) v.findViewById(R.id.movie_release_provider);
        }
        else {
            movieReleaseItem = (MovieReleaseItem) v.getTag();
        }

        if (position % 2 == 0) {
            v.setBackgroundResource(R.drawable.list_item_bg_dark);
        }
        else {
            v.setBackgroundResource(R.drawable.list_item_bg);
        }

        MovieRelease movieRelease = (MovieRelease) getItem(position);
        MovieReleaseInfo movieReleaseInfo = movieRelease.getMovieReleaseInfo();

        StringBuilder size = new StringBuilder();
        size.append(String.valueOf(movieReleaseInfo.getSize()));
        size.append(" ");
        size.append(getContext().getString(R.string.mb));

        StringBuilder age = new StringBuilder();
        age.append(String.valueOf(movieReleaseInfo.getAge()));
        age.append(" ");
        age.append(getContext().getString(R.string.days));

        movieReleaseItem.releaseName.setText(movieReleaseInfo.getName());
        movieReleaseItem.status.setText(CouchPotatoController.getStatus(movieRelease.getStatusId()));
        movieReleaseItem.size.setText(size);
        movieReleaseItem.age.setText(age);
        movieReleaseItem.score.setText(String.valueOf(movieReleaseInfo.getScore()));
        movieReleaseItem.provider.setText(movieReleaseInfo.getProvider());

        v.setId(position);
        v.setTag(movieReleaseItem);
    }

    @Override
    public View getPinnedHeaderView(View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.list_item_movie, parent, false);
        view.setFocusable(false);
        view.setEnabled(false);
        bindHeaderView(view, null);
        return view;
    }

    private class HeaderItem {

        ImageView poster;
        TextView title;
    }

    private class MovieReleaseItem {

        TextView releaseName;
        TextView status;
        TextView size;
        TextView age;
        TextView score;
        TextView provider;
    }
}
