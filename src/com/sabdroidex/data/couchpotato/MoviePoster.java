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
public class MoviePoster implements Serializable {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -1054499502124315503L;
    private List<String> backdrop;
    private List<String> backdrop_org;
    private List<String> poster;
    private List<String> poster_org;
    
    /**
     * @return the backdrop
     */
    public List<String> getBackdrop() {
        return backdrop;
    }
    
    /**
     * @param backdrop
     *            the backdrop to set
     */
    @JSONSetter(name = "backdrop", type = JSONType.LIST)
    public void setBackdrop(List<String> backdrop) {
        this.backdrop = backdrop;
    }
    
    /**
     * @return the backdrop_org
     */
    public List<String> getBackdrop_org() {
        return backdrop_org;
    }
    
    /**
     * @param backdrop_org
     *            the backdrop_org to set
     */
    @JSONSetter(name = "backdrop_original", type = JSONType.LIST)
    public void setBackdrop_org(List<String> backdrop_org) {
        this.backdrop_org = backdrop_org;
    }
    
    /**
     * @return the poster
     */
    public List<String> getPoster() {
        return poster;
    }
    
    /**
     * @param poster
     *            the poster to set
     */
    @JSONSetter(name = "poster", type = JSONType.LIST)
    public void setPoster(List<String> poster) {
        this.poster = poster;
    }
    
    public String getOriginalPoster() {
        if (poster_org == null || poster_org.size() == 0) {
            return "";
        }
        return poster_org.get(0);
    }
    
    public String getSimplePoster() {
        if (poster == null || poster.size() == 0) {
            return "";
        }
        return poster.get(0);
    }
    
    /**
     * @return the poster_org
     */
    public List<String> getPoster_org() {
        return poster_org;
    }
    
    /**
     * @param poster_org
     *            the poster_org to set
     */
    @JSONSetter(name = "poster_original", type = JSONType.LIST)
    public void setPoster_org(List<String> poster_org) {
        this.poster_org = poster_org;
    }
}
