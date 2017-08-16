package com.day.iotmarketplace.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.iotmarketplace.util.PathResolver;

public class PathModel extends WCMUsePojo{
String fullPath="";
String ShortURL="";
	
	@Override
	public void activate() throws Exception {
		
		fullPath=get("shortURL", String.class);
	}
	
	public String getShortURL() {
		if(fullPath.startsWith("/content/"))
		{
			ShortURL=PathResolver.getShortURLPath(fullPath);
		}
		else
		{
			ShortURL=fullPath;	
		}
			return ShortURL;
	}
}
