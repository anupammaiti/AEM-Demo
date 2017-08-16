package com.day.iotmarketplace.models;

import java.util.Set;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class RunModesModel {
private Logger LOG = LoggerFactory.getLogger(RunModesModel.class);

	@Inject
	SlingSettingsService settingsService;
	
	public String testrunmode;
	
	
	

	public String getTestrunmode() {
		if(settingsService!=null){
		Set<String> runmodes = settingsService.getRunModes();
		testrunmode=runmodes.toString();
		}
		if(testrunmode.contains("prod") && !testrunmode.contains("author")){
			testrunmode = "prod";
		}
		else{
			testrunmode = "noprod";
		}
		return testrunmode;
	
		}
	
}
