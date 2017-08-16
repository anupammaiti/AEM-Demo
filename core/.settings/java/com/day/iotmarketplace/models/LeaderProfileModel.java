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
public class LeaderProfileModel extends MultifieldUtil {
	@Inject
	@Named("profiles")
	@Default(values = { "" })
	private String[] profiles;


	@Inject
	@Named("sociallinks")
	@Default(values = { "" })
	private String[] sociallinks;


	public List<Map<String, Object>> getProfiles() {
		return super.getMultiFieldPanelValues(profiles);
	}
	
	public List<Map<String, Object>> getSociallinks() {
		return super.getMultiFieldPanelValues(sociallinks);
	}

	
}
