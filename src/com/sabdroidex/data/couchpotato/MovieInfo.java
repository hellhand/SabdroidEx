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
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class MovieInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3075383457074610952L;
	private String orgTitle;
	private String released;
	private Integer runtime;
	private Integer releasedYear;
	private MoviePoster posters;
	private MovieRating rating;
	private List<String> titles;
	private List<String> actors;
	private List<String> directors;
	private List<String> genres;
	private List<String> writers;

	public List<String> getGenres() {
		return genres;
	}

	@JSONSetter(name = "genres", type=JSONType.LIST)
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public List<String> getDirectors() {
		return directors;
	}

	@JSONSetter(name = "directors", type=JSONType.LIST)
	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

	public List<String> getActors() {
		return actors;
	}

	@JSONSetter(name = "actors", type=JSONType.LIST)
	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public String getTitle() {
		return orgTitle;
	}

	@JSONSetter(name = "original_title")
	public void setTitle(String title) {
		this.orgTitle = title;
	}

	public String getReleased() {
		return released;
	}

	@JSONSetter(name = "released")
	public void setReleased(String released) {
		this.released = released;
	}

	public Integer getRuntime() {
		return runtime;
	}

	@JSONSetter(name = "runtime")
	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	/**
	 * @return the writers
	 */
	public List<String> getWriters() {
		return writers;
	}

	/**
	 * @param writers the writers to set
	 */
	@JSONSetter(name = "writers", type=JSONType.LIST)
	public void setWriters(List<String> writers) {
		this.writers = writers;
	}

	/**
	 * @return the releasedYear
	 */
	public Integer getReleasedYear() {
		return releasedYear;
	}

	/**
	 * @param releasedYear the releasedYear to set
	 */
	@JSONSetter(name = "year")
	public void setReleasedYear(Integer releasedYear) {
		this.releasedYear = releasedYear;
	}

	/**
	 * @return the titles
	 */
	public List<String> getTitles() {
		return titles;
	}

	/**
	 * @param titles the titles to set
	 */
	@JSONSetter(name = "titles", type=JSONType.LIST)
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	/**
	 * @return the posters
	 */
	public MoviePoster getPosters() {
		return posters;
	}

	/**
	 * @param posters the posters to set
	 */
	@JSONSetter(name = "images", type=JSONType.JSON_OBJECT)
	public void setPosters(MoviePoster posters) {
		this.posters = posters;
	}

	/**
	 * @return the rating
	 */
	public MovieRating getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	@JSONSetter(name = "rating", type=JSONType.JSON_OBJECT, listClazz=MovieRating.class)
	public void setRating(MovieRating rating) {
		this.rating = rating;
	}
	
	
}
