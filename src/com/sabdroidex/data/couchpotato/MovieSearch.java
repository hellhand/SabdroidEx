package com.sabdroidex.data.couchpotato;

import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.util.ArrayList;
import java.util.List;

public class MovieSearch {

    private List<MovieSearchResult> movieSearchResults;

    public List<MovieSearchResult> getMovieSearchResults() {
        if (movieSearchResults == null) {
            this.movieSearchResults = new ArrayList<MovieSearchResult>();
        }
        return movieSearchResults;
    }

    @JSONSetter(name = "movies", type=JSONType.LIST, objectClazz = MovieSearchResult.class)
    public void setMovieSearchResults(List<MovieSearchResult> movieSearchResults) {
        this.movieSearchResults = movieSearchResults;
    }
}
