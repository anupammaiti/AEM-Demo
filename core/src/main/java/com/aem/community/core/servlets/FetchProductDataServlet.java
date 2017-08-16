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
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aem.community.core.objects.Product;
import com.aem.community.core.services.FetchProductDataService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/fetchSellableProducts", methods = "POST", metatype=true)
@Properties({
        @Property(name = "service.pid", value = "com.aem.community.core.servlets.FetchProductDataServlet", propertyPrivate = false),
        @Property(name = "service.vendor", value = "Fleetcor", propertyPrivate = false) })
public class FetchProductDataServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(FetchProductDataServlet.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
    FetchProductDataService fetchProductDataService;
	 
	ResourceResolver resourceResolver;     	     
	
	private static final String PRODUCTS_FOLDER_PATH = "/etc/commerce/products/sprint/en";
	
	List<Product> sellableProducts;
	
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        
    	try {
    		resp.setContentType("application/json");
    	
	    	PrintWriter out = resp.getWriter();
	    	
	    	String productCategory = "";
	    	String productsFolderPath = "";
	    	
	    	if(req.getParameter("productCategory") != null){
	    		productCategory = req.getParameter("productCategory");
	    	}
	    	
	    	if(req.getParameter("productsFolderPath") != null){
	    		productsFolderPath = req.getParameter("productsFolderPath");
	    	}
	    	
	    	sellableProducts = fetchProductDataService.fetchSellableProducts(productCategory, productsFolderPath);
	    	
	    	/*Gson gson = new Gson();
		    String jsonString = gson.toJson(sellableProducts);   */
	    	JSONArray jsonArray = new JSONArray(); 		   
	    	
	    	for(Product product : sellableProducts) {
	    		JSONObject jsonObj = new JSONObject();
	    		jsonObj.put("title", product.getTitle());
	    		jsonObj.put("description", product.getDescription());
	    		jsonObj.put("image", product.getImage());
	    		
	    		jsonArray.put(jsonObj);
	    	}
	    	
		    out.print(jsonArray.toString());
		    out.flush();
	    } catch (JSONException je){
	    	log.error("JSONException in FetchProductDataServlet :: " + je.getMessage());
	    	resp.getWriter().println("INVALID");
    		resp.getWriter().println("ERROR MESSAGE : " + je.getMessage());
	    } catch (Exception e){
	    	log.error("Exception in FetchProductDataServlet :: " + e.getMessage());
	    	resp.getWriter().println("INVALID");
    		resp.getWriter().println("ERROR MESSAGE : " + e.getMessage());
	    }
    }
}