
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
public class FAQCategoriesModel extends MultifieldUtil {
	@Inject
	@Named("categorylinks")
	@Default(values = { "" })
	private String[] categorylinks;

	@Inject
	@Named("questionnaire")
	@Default(values = { "" })
	private String[] questionnaire;

	public List<Map<String, Object>> getCategorylinks() {
		return super.getMultiFieldPanelValues(categorylinks);
	}

	public List<Map<String, Object>> getQuestionnaire() {
		return super.getMultiFieldPanelValues(questionnaire);
	}

}

