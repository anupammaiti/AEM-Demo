/*
 * CQPageConstants.java
 *
 * Created on Jun 18, 2012
 *
 * Copyright 2012, SapientNitro;  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * SapientNitro, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with SapientNitro.
 */
package com.day.iotmarketplace.constants;


/**
 * Class for the constants used at cq:page level including dialog box properties
 * name.
 *
 * @author NVISH
 * 
 */
public final class PageConstants {

	/**
	 * private constructor to prevent instantiation of class.
	 */
	private PageConstants() {

	}

	/**
	 * <p>
	 * Constant to identify <code>currentPage</code> in the properties.
	 * </p>
	 */
	public static final String CURRENT_PAGE = "currentPage";
	/**
	 * <p>
	 * Constant to identify <code>parentPage</code> in the properties.
	 * </p>
	 */
	public static final String PARENT_PAGE = "parentPage";
	/**
	 * <p>
	 * Constant to identify <code>parentPage</code> in the properties.
	 * </p>
	 */
	public static final String PAGE_TYPE = "cq:Page";

	/**
	 * /** Constant for Usergenerated Path.
	 */
	public static final String USERGENERATED_PATH = "/content/dam/";
	/**
	 * Constant for defining property which holds metadata node location.
	 */
	public static final String PROPERTY_METADATA_PATH = "/jcr:content/metadata";
	/**
	 * Constant for defining property which holds metadata node location.
	 */
	public static final String PROPERTY_CQTAG_PATH = "jcr:content/metadata/cq:tags";
	/**
	 * Constant for defining property which holds metadata node location.
	 */
	public static final String PROPERTY_JCRTITLE_PATH = "jcr:content/jcr:title";
	/**
	 * Constant for path separator.
	 */
	public static final String PATH_SEPARATOR = "/";

	/**
	 * <p>
	 * Constant to identify <code>currentStyle</code> in the properties.
	 * </p>
	 */
	public static final String CURRENT_STYLE = "currentStyle";
	/**
	 * map key for the list of child path.
	 */
	public static final String PATH = "path";
	/**
	 * map key for the list of child node name.
	 */
	public static final String NAME = "name";

	/**
	 * map key for the getting parent page title.
	 */
	public static final String PARENT_TITLE = "parentTitle";
	/**
	 * map key for the getting root path.
	 */
	public static final String ROOT_PATH = "rootPath";
	/**
	 * constant for design dialog values entered by author.
	 */
	public static final String ABS_PARENT = "absParent";
	/**
	 * map key for the getting node level.
	 */
	public static final String LEVEL = "level";

	/**
	 * map key for page title.
	 */
	public static final String PAGE_TITLE = "title";

	/**
	 * <p>
	 * Constant to identify property rank from athlete's details page url.
	 * </p>
	 */
	public static final String PROPERTY_PAGE_URL = "pageURL";

	/**
	 * <p>
	 * Constant to identify property rank from athlete's page's title.
	 * </p>
	 */
	public static final String PROPERTY_PAGE_TITLE = "pageTitle";

	/**
	 * Constant to hold current node.
	 */
	public static final String CURRENT_NODE = "currentNode";

	/**
	 * Constant to hold current node.
	 */
	public static final String PAGE_MANAGER = "pageManager";

	/**
	 * Constant for current page content node.
	 */
	public static final String PAGE_CONTENT = "/jcr:content";

	/**
	 * Constant for replication date of current page.
	 */
	public static final String KEY_REPLICATION_DATE = "cq:lastReplicated";

	/**
	 * Constant for last modified date of current page.
	 */
	public static final String KEY_LAST_MODIFIED_DATE = "cq:lastModified";

	/**
	 * Constant key for current page path.
	 */
	public static final String KEY_CURRENT_PAGE_PATH = "CURRENT_PAGE_PATH";

	/**
	 * <p>
	 * Constant to identify display as view from component properties.
	 * </p>
	 */
	public static final String DISPLAY_AS = "displayAs";

	/**
	 * Constant for timestamp.
	 */
	public static final String TIMESTAMP = "timestamp";

	/**
	 * map key for the depth of current page.
	 */
	public static final String DEPTH = "depth";

	/**
	 * Constant for template path.
	 */
	public static final String PROPERTY_JCRTEMPLATE_PATH = "jcr:content/cq:template";

	/**
	 * constant for event path
	 */

	public static final String PROPERTY_JCREVENT_PATH = "/jcr:content/event";

	/**
	 * constant for location path
	 */

	public static final String PROPERTY_JCRLOCATION_PATH = "/jcr:content/locations";
	/**
     * <p>
     * Constant to identify <code>hideInNav</code> in the properties.
     * </p>
     */
    public static final String HIDE_IN_NAV_PROP = "hideInNav";
    

}
