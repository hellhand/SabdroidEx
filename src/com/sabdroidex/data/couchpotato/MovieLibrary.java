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
public class MovieLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6295538905743154974L;
	private String imdbID;
	private MovieInfo info;
	private String plot;

	public String getPlot() {
		return plot;
	}

	@JSONSetter(name = "plot")
	public void setPlot(String plot) {
		this.plot = plot;
	}

	public MovieInfo getInfo() {
		return info;
	}

	@JSONSetter(name = "info", type=JSONType.JSON_OBJECT, objectClazz=MovieInfo.class)
	public void setInfo(MovieInfo info) {
		this.info = info;
	}
	
	public String getImdbID() {
		return imdbID;
	}

	@JSONSetter(name = "identifier")
	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}
}
