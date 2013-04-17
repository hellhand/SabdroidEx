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

package com.sabdroidex.data.couchpotato;

import java.io.Serializable;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class Movie implements Serializable, Comparable<Movie> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6739744536873044885L;
	private Integer movieID;
	private Integer profileID;
	private Integer statusID;
	private MovieLibrary library;
	

	public MovieLibrary getLibrary() {
		return library;
	}

	@JSONSetter(name = "library", type=JSONType.JSON_OBJECT, objectClazz=MovieLibrary.class)
	public void setLibrary(MovieLibrary library) {
		this.library = library;
	}

	public Integer getStatusID() {
		return statusID;
	}

	@JSONSetter(name = "status_id")
	public void setStatusID(Integer statusID) {
		this.statusID = statusID;
	}

	public Integer getProfileID() {
		return profileID;
	}

	@JSONSetter(name = "profile_id")
	public void setProfileID(Integer movieProfileID) {
		this.profileID = movieProfileID;
	}

	public Integer getMovieID() {
		return movieID;
	}

	@JSONSetter(name = "id")
	public void setMovieID(Integer movieID) {
		this.movieID = movieID;
	}
	
	/**
	 * Get Title of Movie
	 * @return Title of Movie
	 */
	public String getTitle(){
		return this.library.getInfo().getTitle();
	}
	
	/**
	 * Get Plot of Movie
	 * @return Plot of Movie
	 */
	public String getPlot(){
		return this.library.getPlot();
	}
	
	/**
	 * Get URL location of Poster
	 * @return URL of Poster
	 */
	public String getURLPoster(){
		return this.library.getInfo().getPosters().getPoster_org().get(0);
	}

	@Override
	public int compareTo(Movie arg0) {
		return this.getTitle().compareTo(arg0.getTitle());
	}

	/**
	 * Retrieves all the genres for this movie in a specific format
	 * @return
	 */
	public String getGenres() {
        String genres = "";
        for (String genre : getLibrary().getInfo().getGenres()) {
            genres += genres.equals("") ? genre : ", " + genre;
        }
        return genres;
	}
}
