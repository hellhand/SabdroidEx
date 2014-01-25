package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.util.List;

/**
 * Created by Marc on 27/12/13.
 */
public class Categories {

    private List<String> categories;

    public List<String> getCategories() {
        return categories;
    }

    @JSONSetter(name = "categories", type = JSONType.LIST)
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
