package com.sabdroidex.utils;

import java.util.HashMap;

public class DataMaps {

	public static final HashMap<String,String> categoryMap = new HashMap<String, String>();
	static 
	{
		categoryMap.put("send_category_none", "None");
		categoryMap.put("send_category_apps", "apps");
		categoryMap.put("send_category_books", "books");
		categoryMap.put("send_category_consoles", "consoles");
		categoryMap.put("send_category_emulation", "emulation");
		categoryMap.put("send_category_games", "games");
		categoryMap.put("send_category_misc", "misc");
		categoryMap.put("send_category_movies", "movies");
		categoryMap.put("send_category_music", "music");
		categoryMap.put("send_category_pda", "pda");
		categoryMap.put("send_category_resources", "resources");
		categoryMap.put("send_category_tv", "tv");
	}
		
	
	public static final HashMap<String, Integer> priorityMap = new HashMap<String, Integer>();
	static 
	{
		priorityMap.put("send_priority_default", -100);
		priorityMap.put("send_priority_paused", -2);
		priorityMap.put("send_priority_low", -1);
		priorityMap.put("send_priority_normal", 0);
		priorityMap.put("send_priority_high", 1);
	}
}
