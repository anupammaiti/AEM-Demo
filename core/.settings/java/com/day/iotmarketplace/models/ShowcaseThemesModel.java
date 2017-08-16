
package com.day.iotmarketplace.models;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

import com.day.iotmarketplace.util.MultifieldUtil;
@Model(adaptables = Resource.class)
public class ShowcaseThemesModel extends MultifieldUtil {
	@Inject
	@Named("showcasebannerrows")
	@Default(values = { "" })
	private String[] showcasebannerrows;

	@Inject
	@Named("showcasebannerimages")
	@Default(values = { "" })
	private String[] showcasebannerimages;

	
	
	public List<Map<String, Object>> getShowcasebannerrows() {
		return super.getMultiFieldPanelValues(showcasebannerrows);
	}

	public List<Map<String, Object>> getShowcasebannerimages() {
		return super.getMultiFieldPanelValues(showcasebannerimages);
	}

	
	
	
}
