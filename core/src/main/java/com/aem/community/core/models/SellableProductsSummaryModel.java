package com.aem.community.core.models;

import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUsePojo;
import com.aem.community.core.objects.Product;
import com.aem.community.core.services.FetchProductDataService;

public class SellableProductsSummaryModel extends WCMUsePojo {

    private static final Logger log = LoggerFactory.getLogger(SellableProductsSummaryModel.class);

    private static final String DEFAULT_PRODUCTS_FOLDER_PATH = "/etc/commerce/products/sprint/en";
    
    @Reference
    FetchProductDataService fetchProductDataService;
    
    private ValueMap properties;
    private String title;
    private String description;
	private List<Product> sellableProducts;
	
    public String getTitle() {
		return title;
	}
    
    public String getDescription() {
		return description;
	}
    
    public List<Product> getSellableProducts() {
		return sellableProducts;
	}
    
	@Override
	public void activate() throws Exception {
		properties = getProperties();
		title = properties.get("title", "Default Title");
		description = properties.get("description", "Default Description");
		String productCategory = properties.get("productCategory","all");
		String productsFolderPath = properties.get("productsFolderPath", DEFAULT_PRODUCTS_FOLDER_PATH);
		
		log.info("Product Category in WCM Use Class :: " + productCategory);
		log.info("Products Folder Path in WCM Use Class ::" + productsFolderPath);
		
		sellableProducts = fetchProductDataService.fetchSellableProducts(productCategory, productsFolderPath);
		
		log.info("after calling service. result size :: " + sellableProducts.size());
	}
}