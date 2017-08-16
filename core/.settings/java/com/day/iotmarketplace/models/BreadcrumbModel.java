package com.day.iotmarketplace.models;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.iotmarketplace.dao.LinksList;
import com.day.iotmarketplace.util.PathResolver;

public class BreadcrumbModel extends WCMUsePojo {
	public String homepagePath = "";
	public String link = "";
	public List<LinksList> linkList;

	@Override
	public void activate() throws Exception {
		homepagePath = get("brchomepath", String.class);

	}

	public List<LinksList> getLinkList() {

		linkList = new ArrayList<LinksList>();
		LinksList homepagelist = new LinksList();
		homepagelist.setPath(PathResolver.getShortURLPath(homepagePath));

		homepagelist.setTitle("Home");
		linkList.add(homepagelist);

		for (int i = 3; i < getCurrentPage().getDepth(); i++) {
			LinksList list = new LinksList();
			String link = getCurrentPage().getAbsoluteParent(i).getPath();
			if (!link.equals(getCurrentPage().getPath())) {
				list.setPath(PathResolver.getShortURLPath(getCurrentPage().getAbsoluteParent(i).getPath()));
				list.setTitle(getCurrentPage().getAbsoluteParent(i).getTitle());
				linkList.add(list);

			}

		}
		return linkList;
	}
}
