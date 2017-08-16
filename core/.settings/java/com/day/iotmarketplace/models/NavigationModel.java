package com.day.iotmarketplace.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.iotmarketplace.constants.PageConstants;
import com.day.iotmarketplace.dao.PageNode;
import com.day.iotmarketplace.util.JcrUtilService;
import com.day.iotmarketplace.util.PageUtils;
import com.day.iotmarketplace.util.PathResolver;


@Model(adaptables=Resource.class)
public class NavigationModel {
	private static final Logger log = LoggerFactory.getLogger(NavigationModel.class);
	 @Inject @Named("rootpath") @Default(values="")
	    private String rootPath;
	 
	 public List<PageNode> getMainNavPages()
	 {
		 log.info("getMainNavPages method execution started:");
		 
		 List<PageNode> navigationList=new ArrayList<PageNode>();
		 try{
		 Iterator<Page> pageIterator=PageUtils.getChildPages(rootPath, JcrUtilService.getResourceResolver());
		
		 while(pageIterator.hasNext())
		 {
			 PageNode pageNode=new PageNode();
			 Page childPage=pageIterator.next();
			 ValueMap properties=childPage.getProperties();
			 String hideInNav=properties.get(PageConstants.HIDE_IN_NAV_PROP,String.class);
			 pageNode.setPageTitle(childPage.getTitle().toUpperCase());
			 pageNode.setPagePath(PathResolver.getShortURLPath(childPage.getPath()));
			 pageNode.setHideNav(hideInNav);
			 navigationList.add(pageNode);
			 }
			 
		 }  
		 catch(Exception e)
		 {
			 log.error("Error occured while creating main navigation list",e.getCause());
		 }
		 log.debug("getMainNavPages method ending:");
		 return navigationList;
	 }
}

