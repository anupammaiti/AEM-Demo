package com.aem.community.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.security.user.User;

public class PersonalizedIndexModel extends WCMUsePojo {

    private static final Logger log = LoggerFactory.getLogger(PersonalizedIndexModel.class);

    private ValueMap properties;
	private ValueMap pageProperties;
	private ValueMap inheritedProperties;
	private ResourceResolver resourceResolver;
	
	@Reference
	//private SPIndexService spIndexService;
	boolean overrideFile = false;
	
	private boolean status;
	private String errorMessage;
	
    public boolean getStatus() {
		return status;
	}
    
    public String getErrorMessage() {
		return errorMessage;
	}
    
	@Override
	public void activate() throws Exception {
		resourceResolver = getResourceResolver();
		
		//Get all properties related to the page & related content
		properties = getProperties();
		pageProperties = getPageProperties();
		inheritedProperties = getInheritedProperties();
		
		//Get the User Data
		User user = getCurrentUser(resourceResolver);
		log.info("User Details :: " + user.toString());
		
		//spIndexService.fullIndex(overrideFile);
	}
	
	public User getCurrentUser(ResourceResolver resolver){
		return resolver.adaptTo(User.class);
	}
}
