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
public class MovieRating implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 550495028366908978L;
	private List<Number> imdbListRating;

	/**
	 * @param imdbRating the imdbRating to set
	 */
	@JSONSetter(name = "imdb", type=JSONType.LIST)
	public void setImdbListRating(List<Number> imdbRating) {
		this.imdbListRating = imdbRating;
	}
	
	public Integer getImdbVotes(){
		if(imdbListRating == null)
			return 0;
		return (Integer) imdbListRating.get(1);
	}
	
	public Double getImdbRating(){
		if(imdbListRating == null)
			return 0.0;
		return imdbListRating.get(0).doubleValue();
	}
}
