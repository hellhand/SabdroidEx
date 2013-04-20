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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sabdroidex.data.JSONBased;
import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class MovieList implements JSONBased, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1626938454183667902L;
	private List<Movie> movieElements;

	/**
	 * @return the movieElements
	 */
	public List<Movie> getMovieElements() {
		if (movieElements == null){
			movieElements = new ArrayList<Movie>();
		}
		
		return movieElements;
	}
	
	public int getSize(){
		return movieElements.size();
	}
	
	public Iterator<Movie> getMovieIterable(){
		return movieElements.iterator();
	}

	/**
	 * @param movieElements the movieElements to set
	 */
	@JSONSetter(name="movies", type=JSONType.LIST, objectClazz=Movie.class)
	public void setMovieElements(List<Movie> movieElements) {
		
		// Check if there are no invalid entries in list
		for(Movie movie : movieElements){
			if (!(movie.getTitle() != null && !movie.getTitle().isEmpty())){
				movieElements.remove(movie);
			}
		
		}
		
		this.movieElements = movieElements;
	}
}
