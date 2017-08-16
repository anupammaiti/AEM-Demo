
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
public class TransformationFinalistModel extends MultifieldUtil {
	@Inject
	@Named("finalistsectiondetails")
	@Default(values = { "" })
	private String[] finalistsectiondetails;
	
	public List<Map<String, Object>> getFinalistsectiondetails() {
		return super.getMultiFieldPanelValues(finalistsectiondetails);
	}

}


