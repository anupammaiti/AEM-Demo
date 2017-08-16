package com.aem.community.core.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aem.community.core.objects.Product;
import com.aem.community.core.services.FetchProductDataService;

@Component(metatype = true, immediate = true, label = "Fetch Product Data Service")
@Service(FetchProductDataService.class)
@Properties({
	@Property(name = "service.pid", value = "com.aem.community.core.services.impl.FetchProductDataServiceImpl", propertyPrivate = false),
	@Property(name = "service.vendor", value = "VML")
	})
public class FetchProductDataServiceImpl implements FetchProductDataService {

	private static final Logger log = LoggerFactory.getLogger(FetchProductDataServiceImpl.class);
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
    private SlingRepository slingRepository;

    ResourceResolver resourceResolver;
    Session session; 
    
    @Activate
    protected final void activate(ComponentContext ctx) {
        log.info("Activated FetchProductDataServiceImpl");
        
        try {
        	//Get Resource Resolver
    		Map<String,Object> paramMap = new HashMap<String,Object>();
    	    paramMap.put(ResourceResolverFactory.SUBSERVICE, "fetchProductDataService");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
			session = resourceResolver.adaptTo(Session.class);
			log.info("Got resourceresolver and session :: " + session.getUserID());
		} catch (LoginException le) {
			log.error("Login Exception in Activate Method of FetchProductDataServiceImpl :: " + le.getMessage());
		} catch (Exception e) {
			log.error("Exception in Activate Method of FetchProductDataServiceImpl :: " + e.getMessage());
		}
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) throws Exception {
        log.info("Deactivated FetchProductDataServiceImpl");
    }
    
	@Override
    public List<Product> fetchSellableProducts(String productCategory, String productsFolderPath) {
		log.info("inside fetchsellable products");
		List<Product> sellableProducts = new ArrayList<Product>();
		
		Resource productsResource = resourceResolver.getResource(productsFolderPath);
    	
    	if((productsResource != null) && (productsResource instanceof Resource)){
    		log.info("found product folder path resource");
	    	Iterable<Resource> productTypes = productsResource.getChildren();
	    	
	    	sellableProducts = new ArrayList<Product>();
	    	
	    	for(Resource productType : productTypes){
	    		log.info("iterating product types");
	    		List<Product> productTypeList = new ArrayList<Product>();
	    		if(productType.getName().equalsIgnoreCase("accessories")){
	    			if(productCategory.equalsIgnoreCase("all") || productCategory.equalsIgnoreCase("accessories")){
	    				productTypeList = getAccessories(productType);
	    			}
	    		} else if(productType.getName().equalsIgnoreCase("devices")){
	    			if(productCategory.equalsIgnoreCase("all") || productCategory.equalsIgnoreCase("devices")){
	    				productTypeList = getDevices(productType);
	    			}
	    		} else if(productType.getName().equalsIgnoreCase("plans")){
	    			if(productCategory.equalsIgnoreCase("all") || productCategory.equalsIgnoreCase("plans")){
	    				productTypeList = getPlans(productType);
	    			}
	    		} else if(productType.getName().equalsIgnoreCase("services")){
	    			if(productCategory.equalsIgnoreCase("all") || productCategory.equalsIgnoreCase("services")){
	    				productTypeList = getServices(productType);
	    			}
	    		}
	    		
	    		sellableProducts.addAll(productTypeList);
	    	}
    	}
    	
    	return sellableProducts;
	}
	
	private List<Product> getAccessories(Resource resource) {
		log.info("inside getAccessories");
	   	List<Product> accessoriesList = new ArrayList<Product>();    	
	   	Product product = null;
	       
	    Iterable<Resource> productCategoryResources = resource.getChildren();
	    for(Resource productCategoryResource : productCategoryResources){
	       	
	     	Iterable<Resource> productResources = productCategoryResource.getChildren();
	       	for(Resource productResource : productResources){
	       		ValueMap productProperties = productResource.adaptTo(ValueMap.class);  
	       		String availabilityStatus = productProperties.get("availabilityStatus","undefined");
	       	
	       		if(availabilityStatus.equalsIgnoreCase("sellable")) {       			       					
	       			//Fetch other properties
	       			String title = productProperties.get("jcr:title","");
	       			String description = productProperties.get("shortDescription","");
	       			String imagePath = "";
	        			
	       			//Fetch Image/Asset Node
	       			Resource assetsResource = productResource.getChild("assets");
	       			if((assetsResource != null) && (assetsResource instanceof Resource)){
	       				Iterable<Resource> assetResources = assetsResource.getChildren();
	       				for(Resource assetResource : assetResources){
	       					if(assetResource.getName().equalsIgnoreCase("asset")){
	       						ValueMap assetProperties = assetResource.adaptTo(ValueMap.class);  
	       						imagePath = assetProperties.get("fileReference","");
	       					}       					
	       				}
	       			}
	       			
	       			//Set properties in Product Object
	       			product = new Product();
	       			product.setTitle(title);
	       			product.setDescription(description);
	       			product.setImage(imagePath);
	       			
	       			accessoriesList.add(product);
	       		}
	       	}             
	    }
	   	
	   	return accessoriesList;
	}
	    
	private List<Product> getDevices(Resource resource) {
		log.info("inside getDevices");
	  	List<Product> devicesList = new ArrayList<Product>();
	   	Product product = null;
	       
	   	Iterable<Resource> productCategoryFolders = resource.getChildren();
	    for(Resource productCategoryFolder : productCategoryFolders){
	      	String deviceType = productCategoryFolder.getName();
	        	
	       	Iterable<Resource> productCategoryResources = productCategoryFolder.getChildren();
	       	for(Resource productCategoryResource : productCategoryResources){
	       		ValueMap productCategoryProperties = productCategoryResource.adaptTo(ValueMap.class);
	       		
	        	Iterable<Resource> productResources = productCategoryResource.getChildren();
	        	for(Resource productResource : productResources){
	        		ValueMap productProperties = productResource.adaptTo(ValueMap.class);  
	        		String availabilityStatus = productProperties.get("availabilityStatus","undefined");
	        	
	        		if(availabilityStatus.equalsIgnoreCase("sellable")) {       			       					
	        			//Fetch other properties
	        			String title = productProperties.get("jcr:title","");
	        			String description = productCategoryProperties.get("deviceLongDescription","");
	        			String imagePath = "";
	        			
	        			//Fetch Image/Asset Node
	        			Resource assetsResource = productResource.getChild("assets");
	        			if((assetsResource != null) && (assetsResource instanceof Resource)){
	        				Iterable<Resource> assetResources = assetsResource.getChildren();
	        				for(Resource assetResource : assetResources){
	        					if(assetResource.getName().equalsIgnoreCase("asset")){
	        						ValueMap assetProperties = assetResource.adaptTo(ValueMap.class);  
	        						imagePath = assetProperties.get("fileReference","");
	        					}       					
	        				}
	        			}
	        			
	        			//Set properties in Product Object
	        			product = new Product();
	        			product.setTitle(title);
	        			product.setDescription(description);
	        			product.setImage(imagePath);
	        			
	        			devicesList.add(product);
	        		}
	        	}  
	       	}
	    }
	    	
	   	return devicesList;
	}
	    
	private List<Product> getPlans(Resource resource) {
		log.info("inside getPlans");
	   	List<Product> plansList = new ArrayList<Product>();
	   	
	   	Product product = null;
	        
	    Iterable<Resource> productCategoryResources = resource.getChildren();
	    for(Resource productCategoryResource : productCategoryResources){    	
	        Iterable<Resource> productResources = productCategoryResource.getChildren();
	        for(Resource productResource : productResources){
	        	ValueMap productProperties = productResource.adaptTo(ValueMap.class);  
	        	String availabilityStatus = productProperties.get("availabilityStatus","undefined");
	        	
	        	if(availabilityStatus.equalsIgnoreCase("sellable")) {       			       					
	        		//Fetch other properties
	        		String title = productProperties.get("jcr:title","");
	        		String description = productProperties.get("shortDescription","");
	        		String imagePath = "";
	        		
	        		//Fetch Image/Asset Node
	        		Resource assetsResource = productResource.getChild("assets");
	        		if((assetsResource != null) && (assetsResource instanceof Resource)){
	        			Iterable<Resource> assetResources = assetsResource.getChildren();
	        			for(Resource assetResource : assetResources){
	        				if(assetResource.getName().equalsIgnoreCase("asset")){
	        					ValueMap assetProperties = assetResource.adaptTo(ValueMap.class);  
	        					imagePath = assetProperties.get("fileReference","");
	        				}       					
	        			}
	        		}
	        			
	        		//Set properties in Product Object
	        		product = new Product();
	        		product.setTitle(title);
	        		product.setDescription(description);
	        		product.setImage(imagePath);
	        		
	        		plansList.add(product);
	        	}
	        }             
	    }
	    	
	    return plansList;
	}
	    
	private List<Product> getServices(Resource resource) {
		log.info("inside getServices");
	   	List<Product> servicesList = new ArrayList<Product>();
	   	
	   	return servicesList;
	}
}