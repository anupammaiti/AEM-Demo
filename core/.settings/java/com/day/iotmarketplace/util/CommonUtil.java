/**
 * This Class will have all the methods that can
 * be reused.
 */
package com.day.iotmarketplace.util;


import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.Validate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonUtil {

	private static final transient Logger LOG = LoggerFactory.getLogger(CommonUtil.class);

	private CommonUtil() {

	}

	/**
	 * Gets the property value. This method will return the property value
	 * retrieved from a node
	 * 
	 * @param componentNode
	 *            the component node
	 * @param propertyName
	 *            the property name
	 * @return the property value
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static String getPropertyValue(Node componentNode, String propertyName) throws RepositoryException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("In getPropertyValue method  -- propertyName: {0} componentNode : {1}", propertyName,
					componentNode);
		}
		Validate.notNull(componentNode, "componentNode is null");
		if (null != componentNode && componentNode.hasProperty(propertyName)) {
			LOG.debug("componentNode is not null and has property {}", componentNode.getProperty(propertyName)
					.getString());
			return componentNode.getProperty(propertyName).getString();
		}
		LOG.debug("Out of getPropertyValue method");
		return "";
	}

	/**
	 * Gets the service user map. This method return the Service User Map
	 * required to get the Resource Resolver
	 * 
	 * @return the service user map
	 */
	public static Map<String, Object> getServiceUserMap() {
		LOG.info("In getServiceUserMap method");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Out of getServiceUserMap method {}", param.toString());
		}
		return param;
	}
	
	/**
	 * Gets the child nodes.
	 * 
	 * @param path
	 *            the path
	 * @return the child nodes
	 * @throws RepositoryException
	 */
	public static NodeIterator getChildNodes(ResourceResolver resourceResolver, String path) throws RepositoryException {
		NodeIterator nit = null;
		Resource page = resourceResolver.getResource(path);
		if (page != null) {
			Node node = page.adaptTo(Node.class);
			if (node != null) {
				nit = node.getNodes();
			}
		}
		return nit;
	}

	public String[] getPropertyAsArray(Object obj) {
		String[] paths = { "" };
		if (obj != null) {
			if (obj instanceof String[]) {
				paths = (String[]) obj;
			} else {
				paths = new String[1];
				paths[0] = (String) obj;
			}
		}
		return paths;
	}

	
}

