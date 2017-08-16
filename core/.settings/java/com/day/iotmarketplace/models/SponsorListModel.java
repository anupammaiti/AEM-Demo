
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
public class SponsorListModel extends MultifieldUtil {
	@Inject
	@Named("sponsorrows")
	@Default(values = { "" })
	private String[] sponsorrows;

	@Inject
	@Named("sponsorimages")
	@Default(values = { "" })
	private String[] sponsorimages;

	public List<Map<String, Object>> getSponsorrows() {
		return super.getMultiFieldPanelValues(sponsorrows);
	}
	
	public List<Map<String, Object>> getSponsorimages() {
		return super.getMultiFieldPanelValues(sponsorimages);
	}
	
	
	
}
