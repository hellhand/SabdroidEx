/*
 * Copyright (C) 2011-2012  Marc Boulanger
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
package com.sabdroidex.utils.json;

public enum JSONType {

    /**
     * Simple is used for every simple object that is understood by the JSON framework
     * List is used when the List interface is needed (An ArrayList is generated is the parameter of the JSONSetter method is a List)
     * JSONObject is used when the unmarshall need to be processed for that item too (complex Object)
     */
    SIMPLE, LIST, JSON_OBJECT, UNKNOWN_KEY_ELEMENTS
}
