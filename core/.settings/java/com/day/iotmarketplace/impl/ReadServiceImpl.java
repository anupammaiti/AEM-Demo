package com.day.iotmarketplace.impl;

import java.util.HashMap;
import java.util.Map;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype=true,immediate=true)
@Service(ReadServiceImpl.class)
public class ReadServiceImpl {

private final Logger log = LoggerFactory.getLogger(this.getClass());

@Reference
private ResourceResolverFactory resourceFactory;



public void getListTitles() throws LoginException{
	Map<String,Object> paramMap = new HashMap<String,Object>();
	//Mention the subServiceName you had used in the User Mapping
	paramMap.put(ResourceResolverFactory.SUBSERVICE, "writeService");
	log.info("After the param");
	ResourceResolver rr = null;
rr = resourceFactory.getServiceResourceResolver(paramMap);
	log.info("UserId : " + rr.getUserID());
		 Resource res = rr.getResource("/content/hdscorp");
		log.info("Resource : " + res.getPath());

		 
}
}
