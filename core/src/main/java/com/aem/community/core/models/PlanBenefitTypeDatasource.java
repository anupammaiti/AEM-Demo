package com.aem.community.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

public class PlanBenefitTypeDatasource extends WCMUsePojo {

	private static final Logger log = LoggerFactory.getLogger(PlanBenefitTypeDatasource.class);
	
	private static final String GENERICLIST_KEY_PROPERTY_NAME = "key";
    private static final String GENERICLIST_VALUE_PROPERTY_NAME = "value";
    private static final String KP_GENERICLIST_PAGE_LIST_PARSYS_NAME = "list";
    	
	@Override
	public void activate() throws Exception {  	
    	String configPagePath = "";
    	String dataType = "";
    	
    	Resource datasourceResource = getResource().getChild("datasource");
    	if((null != datasourceResource) && (datasourceResource instanceof Resource)){
    		ValueMap datasourceProperties = datasourceResource.adaptTo(ValueMap.class);
    		configPagePath = datasourceProperties.get("path",""); 
    		dataType = datasourceProperties.get("type","");
    		
    		//Config Page Path Tweaks for Multi Language and MultiNational Implementation
    		String requestURI = getRequest().getRequestURI();
        	String[] uriArray = requestURI.split("/");		
        	log.info("REQUEST URI :: " + requestURI);
    		configPagePath = configPagePath.replace("/en/", "/"+uriArray[12]+"/");
    		configPagePath = configPagePath.replace("/national/", "/"+uriArray[13]+"/");
    	}
    	
    	List<Resource> resourceList = new ArrayList<Resource>();
    	
    	if(StringUtils.isNotBlank(configPagePath) && StringUtils.isNotBlank(dataType)) {
			Resource pageResource = getResourceResolver().getResource(configPagePath);
			
			if((null != pageResource) && (pageResource instanceof Resource)){
				Resource jcrContentResource = pageResource.getChild("jcr:content");
				
				if((null != jcrContentResource) && (jcrContentResource instanceof Resource)){
					Resource listResource = jcrContentResource.getChild(KP_GENERICLIST_PAGE_LIST_PARSYS_NAME);
					
					if((null != listResource) && (listResource instanceof Resource)){
						Iterable<Resource> genericListResources = listResource.getChildren();
				
						for(Resource genericListResource : genericListResources) {
							ValueMap vm = genericListResource.adaptTo(ValueMap.class);
					
							if(vm.get(GENERICLIST_KEY_PROPERTY_NAME, "").equals(dataType)){
								Resource parResource = genericListResource.getChild(KP_GENERICLIST_PAGE_LIST_PARSYS_NAME);
						
								Iterable<Resource> genericListChildResources = parResource.getChildren();
								for(Resource genericListChildResource : genericListChildResources){
									ValueMap childVM = genericListChildResource.adaptTo(ValueMap.class);
							
									if(StringUtils.isNotBlank(childVM.get(GENERICLIST_VALUE_PROPERTY_NAME, ""))){
										ValueMap resouceListItemVM = new ValueMapDecorator(new HashMap<String, Object>());
										resouceListItemVM.put("value", childVM.get(GENERICLIST_VALUE_PROPERTY_NAME, ""));
										resouceListItemVM.put("text", childVM.get(GENERICLIST_VALUE_PROPERTY_NAME, ""));
										resourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), "nt:unstructured", resouceListItemVM));
									}
								}
							}
						}
					}
				}
			}
    	}
    	
		// Create a DataSource that is used to populate the drop-down control
		DataSource ds = new SimpleDataSource(resourceList.iterator());
		this.getRequest().setAttribute(DataSource.class.getName(), ds);
	}
}