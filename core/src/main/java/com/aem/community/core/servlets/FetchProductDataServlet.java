package com.aem.community.core.servlets;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aem.community.core.objects.Product;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/fetchSellableProducts", methods = "POST", metatype=true)
@Properties({
        @Property(name = "service.pid", value = "com.aem.community.core.servlets.FetchProductDataServlet", propertyPrivate = false),
        @Property(name = "service.vendor", value = "Fleetcor", propertyPrivate = false) })
public class FetchProductDataServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(FetchProductDataServlet.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	 
	ResourceResolver resourceResolver;     	     
	
	private static final String PRODUCTS_FOLDER_PATH = "/etc/commerce/products/sprint/en";
	
	List<Product> sellableProductsList;
	
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        
    	resp.setContentType("application/json");
    	PrintWriter out = resp.getWriter();
    	
    	resourceResolver = req.getResourceResolver();
    	
    	Resource productsResource = resourceResolver.getResource(PRODUCTS_FOLDER_PATH);
    	
    	if((productsResource != null) && (productsResource instanceof Resource)){
	    	Iterable<Resource> productTypes = productsResource.getChildren();
	    	
	    	sellableProductsList = new ArrayList<Product>();
	    	
	    	for(Resource productType : productTypes){
	    		List<Product> productTypeList = new ArrayList<Product>();
	    		if(productType.getName().equalsIgnoreCase("accessories")){
	    			productTypeList = getAccessories(productType);
	    		} else if(productType.getName().equalsIgnoreCase("devices")){
	    			productTypeList = getDevices(productType);
	    		} else if(productType.getName().equalsIgnoreCase("plans")){
	    			productTypeList = getPlans(productType);
	    		} else if(productType.getName().equalsIgnoreCase("services")){
	    			productTypeList = getServices(productType);
	    		}
	    		
	    		sellableProductsList.addAll(productTypeList);
	    	}
	    	
	    	Gson gson = new Gson();
	    	String jsonString = gson.toJson(sellableProductsList);    	
	    	out.print(jsonString);
	    	out.flush();
    	}
    }
    
    private List<Product> getAccessories(Resource resource) {
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
    	List<Product> servicesList = new ArrayList<Product>();
    	
    	return servicesList;
    }
}