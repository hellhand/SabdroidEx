package com.sabdroidex.data;

import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.data.sabnzbd.History;
import com.sabdroidex.data.sabnzbd.Queue;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.data.sickbeard.Shows;

/**
 * Created by Marc on 19/06/13.
 */
public class DataCache {

    Queue queue;
    History history;
    Shows shows;
    FuturePeriod coming;
    MovieList movies;

    public DataCache() {
        queue = new Queue();
        history = new History();
        shows = new Shows();
        coming = new FuturePeriod();
        movies = new MovieList();
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Shows getShows() {
        return shows;
    }

    public void setShows(Shows shows) {
        this.shows = shows;
    }

    public FuturePeriod getComing() {
        return coming;
    }

    public void setComing(FuturePeriod coming) {
        this.coming = coming;
    }

    public MovieList getMovies() {
        return movies;
    }

    public void setMovies(MovieList movies) {
        this.movies = movies;
    }
}
