package com.day.iotmarketplace.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.day.cq.wcm.api.Page;

public class PageUtils {
	
	
	public static Iterator<Page> getChildPages(final String path, final ResourceResolver resourceResolver) {
		Iterator<Page> pageIter = null;
		if (StringUtils.isNotEmpty(path)) {
			final Page page = resourceResolver.getResource(path).adaptTo(Page.class);
			
			pageIter = page.listChildren();
		}

		return pageIter;
	}
	
	public static Map<String, Object> jsontoMap(JSONObject object)
			throws JSONException, org.apache.sling.commons.json.JSONException {

		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = jsonArraytoList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = jsontoMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> jsonArraytoList(JSONArray array)
			throws JSONException, org.apache.sling.commons.json.JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = jsonArraytoList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = jsontoMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}

