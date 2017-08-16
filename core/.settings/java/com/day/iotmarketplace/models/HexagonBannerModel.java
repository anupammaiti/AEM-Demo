package com.day.iotmarketplace.models;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.iotmarketplace.util.JcrUtilService;
import com.day.iotmarketplace.util.MultifieldUtil;

@Model(adaptables = Resource.class)
public class HexagonBannerModel extends MultifieldUtil {
	@Inject
	@Named("hexagons")
	@Default(values = { "" })
	private String[] hexagons;

	@Inject
	@Named("ctacontent")
	@Default(values = { "" })
	private String[] ctacontent;

	public List<Map<String, Object>> getHexagons() {
		return super.getMultiFieldPanelValues(hexagons);
	}

	public List<Map<String, Object>> getCtacontent() {
		return super.getMultiFieldPanelValues(ctacontent);
	}

}
