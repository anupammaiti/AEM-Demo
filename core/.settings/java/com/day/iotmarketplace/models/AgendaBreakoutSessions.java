
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
public class AgendaBreakoutSessions extends MultifieldUtil {
	@Inject
	@Named("beforebreakoutsessiondetail")
	@Default(values = { "" })
	private String[] beforebreakoutsessiondetail;

	@Inject
	@Named("breakoutsessiondetail")
	@Default(values = { "" })
	private String[] breakoutsessiondetail;
	
	@Inject
	@Named("afterbreakoutsessiondetail")
	@Default(values = { "" })
	private String[] afterbreakoutsessiondetail;
	
	@Inject
	@Named("breakoutsessionagendadetail")
	@Default(values = { "" })
	private String[] breakoutsessionagendadetail;
	
	@Inject
	@Named("othersessiondetail")
	@Default(values = { "" })
	private String[] othersessiondetail;
	
	
	
	public List<Map<String, Object>> getOthersessiondetail() {
		return super.getMultiFieldPanelValues(othersessiondetail);
	}

	public List<Map<String, Object>> getBeforebreakoutsessiondetail() {
		return super.getMultiFieldPanelValues(beforebreakoutsessiondetail);
	}

	public List<Map<String, Object>> getBreakoutsessiondetail() {
		return super.getMultiFieldPanelValues(breakoutsessiondetail);
	}
	
	public List<Map<String, Object>> getAfterbreakoutsessiondetail() {
		return super.getMultiFieldPanelValues(afterbreakoutsessiondetail);
	}
	
	public List<Map<String, Object>> getBreakoutsessionagendadetail() {
		return super.getMultiFieldPanelValues(breakoutsessionagendadetail);
	}

}

