/*
 * Copyright (C) 2011-2013  Roy Kokkelkoren
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sabdroidex.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class MovieGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Movie> mItems;
    
    public MovieGridAdapter(Context context, List<Movie> items) {
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(this.mContext);
    }
    
    public void setDataSet(List<Movie> movieElements) {
        this.mItems = movieElements;
    }
    
    @Override
    public int getCount() {
        return mItems.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	MovieListItem movieItem = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.movie_item, null);
            movieItem = new MovieListItem();
            movieItem.poster = (ImageView) convertView.findViewById(R.id.movie_poster);
            movieItem.title = (TextView) convertView.findViewById(R.id.movie_title);
        }
        else {
        	movieItem = (MovieListItem) convertView.getTag();
        }
        
        Movie movie = (Movie) getItem(position);
        String imageKey = ImageType.MOVIE_POSTER.name() + movie.getMovieID();
        ImageUtils.getImageWorker().loadImage(movieItem.poster, ImageType.MOVIE_BANNER, imageKey, movie.getMovieID(),
                movie.getTitle(), movie.getLibrary().getInfo().getPosters().getOriginalPoster());
        movieItem.title.setText(movie.getTitle());
        
        convertView.setId(position);
        convertView.setTag(movieItem);
        return (convertView);
    }

    class MovieListItem {
        ImageView poster;
        TextView title;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
