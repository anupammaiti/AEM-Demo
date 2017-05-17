package com.aem.community.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUsePojo;
import com.aem.community.core.objects.Product;
import com.day.cq.wcm.api.Page;

public class ProductListingModel extends WCMUsePojo {

    private static final Logger log = LoggerFactory.getLogger(ProductListingModel.class);

    private ValueMap properties;
    private String sectionTitle;
    private String parentPagePath;
	private List<Product> productList;
	
    public List<Product> getProductList() {
		return productList;
	}
    
    public String getSectionTitle() {
		return sectionTitle;
	}
    
	@Override
	public void activate() throws Exception {
		try {
			properties = getProperties();
			sectionTitle = properties.get("sectionTitle", "Default Title");
			parentPagePath = properties.get("parentPagePath", getCurrentPage().getPath());
			log.info("Section Title :: " + sectionTitle);
			Resource parentPageResource = getResourceResolver().getResource(parentPagePath);
			log.info("Page Path :: " + parentPagePath);
			
			productList = new ArrayList<Product>();
			//if(null != parentPageResource){
				log.info("inside null check");
			//if((null != parentPageResource) && (parentPageResource instanceof Resource)) {
					Iterable<Resource> childResources = parentPageResource.getChildren();
					int count = 0;
					for (Resource childResource : childResources) {
						count++;
						log.info("iterating child nodes :: " + count);
						Page childPage = childResource.adaptTo(Page.class);
						ValueMap pageProperties = childPage.getProperties();
						log.info("page :: " + childPage.getPath());
						String pageTitle = pageProperties.get("jcr:title","Default Title");
						String pageDesc = pageProperties.get("jcr:description","Default Description");
						String url = childResource.getPath();
						String subTitle = pageProperties.get("subtitle","Default Subtitle");
						
						Product productObj = new Product();
						productObj.setTitle(pageTitle);
						productObj.setDescription(pageDesc);
						productObj.setUrl(url);
						productObj.setSubtitle(subTitle);
						
						productList.add(productObj);
					}
			//} 
		} catch (Exception e) {
			log.error("Exception in ProductListingModel :: " + e.getMessage());
		}
	}
}
