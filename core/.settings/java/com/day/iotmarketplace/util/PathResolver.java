package com.day.iotmarketplace.util;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.iotmarketplace.constants.GlobalConstants;

/**
 * {@link PathResolver} class used to resolve urls
 *
 */
public final class PathResolver {

	private static final Logger LOG = LoggerFactory.getLogger(PathResolver.class);
	private static final String EN_US = "en_us";
	private static final String EN = "en-us";
	/**
	 * HTTP prefix constant.
	 */
	public static final String HTTP_PREFIX = "http://";

	/**
	 * HTTPS prefix constant.
	 */
	public static final String HTTPS_PREFIX = "https://";

	/**
	 * <p>
	 * Constant for html file extension.
	 * </p>
	 */

	private PathResolver() {
	}

	/**
	 * Used to get Short form of URL from the Given Page URL
	 * 
	 * @param pageFullPath
	 * @return {@link String}
	 * @throws Exception
	 */
	public static String getShortURLPath(final String pageFullPath) {
		String shortURLPath = null;
		String pageUrlPath = null;
		if (pageFullPath != null && !pageFullPath.startsWith("http") && !pageFullPath.toLowerCase().contains(".com")) {
			try {
				final ResourceResolver resourceResolver = JcrUtilService.getResourceResolver();
				LOG.trace("resourceResolver retunred---- " + resourceResolver);

				pageUrlPath = getShortUrl(pageFullPath, resourceResolver);
				if (!pageUrlPath.startsWith("/")) {
					pageUrlPath = "/" + pageUrlPath;
				}
				shortURLPath = resourceResolver.map(pageUrlPath);
				shortURLPath = getShortUrl(shortURLPath, resourceResolver);

				if (((null != pageFullPath) && pageFullPath.endsWith(GlobalConstants.HTML_EXTENSION))
						|| pageFullPath.contains(GlobalConstants.DAM)
						|| pageFullPath.contains(GlobalConstants.RENDITIONS) || pageFullPath.contains("#")) {
				} else {
					shortURLPath += GlobalConstants.HTML_EXTENSION;
				}
				if (!shortURLPath.startsWith("/")) {
					shortURLPath = "/" + shortURLPath;
				}
				if (shortURLPath.toLowerCase().contains(".com")) {
					if (shortURLPath.startsWith("/")) {
						shortURLPath = shortURLPath.substring(1);
					}
					shortURLPath = shortURLPath.substring(shortURLPath.indexOf("/"));
				}
				// LOG.info("Returning short path ::" + shortURLPath);
			} catch (Exception e) {
				LOG.error(" Error while getting URL for the path :" + pageFullPath + " and the error message is :", e);
			}
		} else {
			shortURLPath = pageFullPath;
		}
		return shortURLPath;
	}

	/**
	 * Used to get Full form of URL from the Given Short Page URL
	 * 
	 * @param pageFullPath
	 * @return {@link String}
	 * @throws Exception
	 */
	public static String getFullURLPath(final String shortUrlPath) throws Exception {
		try {
			final ResourceResolver resourceResolver = JcrUtilService.getResourceResolver();
			final Resource resource = resourceResolver.resolve(shortUrlPath);
			if (null != resource) {
				return resource.getPath();
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Resource doesn't exists..." + shortUrlPath);
				}
			}
		} catch (Exception e) {
			LOG.error(" Error while getting Full URL for the path :" + shortUrlPath + " and the error message is :", e);
		}
		return null;
	}

	/**
	 * Used to get Full form of URL from the Given Short Page URL
	 * 
	 * @param slingRequest
	 * @param pageFullPath
	 * @return {@link String}
	 * @throws Exception
	 */
	public static String getFullURLPathFromRequest(SlingHttpServletRequest slingRequest, final String shortUrlPath)
			throws Exception {
		try {
			final Resource resource = getResourceFromShortURL(slingRequest, shortUrlPath);
			if (null != resource) {
				return resource.getPath();
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Resource doesn't exists..." + shortUrlPath);
				}
			}
		} catch (Exception e) {
			LOG.error(" Error while getting Full URL for the path :" + shortUrlPath + " and the error message is :", e);
		}
		return null;
	}

	/**
	 * Used to get Resource from Short URL
	 * 
	 * @param slingRequest
	 * @param pageFullPath
	 * @return {@link String}
	 * @throws Exception
	 */
	public static Resource getResourceFromShortURL(SlingHttpServletRequest request, final String shortUrlPath)
			throws Exception {
		try {
			final ResourceResolver resourceResolver = request.getResourceResolver();
			final Resource resource = resourceResolver.resolve(request, shortUrlPath);
			if (null != resource) {
				return resource;
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Resource doesn't exists..." + shortUrlPath);
				}
			}
		} catch (Exception e) {
			LOG.error(" Error while getting Full URL for the path :" + shortUrlPath + " and the error message is :", e);
		}
		return null;
	}

	private static String getShortUrl(final String pageUrl, final ResourceResolver resourceResolver) {
		String pageUrlPath = null;
		// LOG.info("Page url is " + pageUrl);
		if (pageUrl != null) {
			if ((StringUtils.isNotEmpty(pageUrl) && pageUrl.startsWith(HTTP_PREFIX))
					|| pageUrl.startsWith(HTTPS_PREFIX)) {
				final String[] url = pageUrl.split(GlobalConstants.PATH_SEPERATOR);
				if ((null != url) && (3 < url.length)) {
					pageUrlPath = pageUrl.substring(pageUrl.indexOf(url[3]), pageUrl.length());
					return pageUrlPath;
				}
			} else {
				return pageUrl;
			}
			return "";
		}
		return "";
	}

	public static String getAbsoluteUrl(final String pageUrl, final HttpServletRequest request) {
		URL externalURL = null;
		String absUrl = null;
		if (pageUrl != null) {
			if (StringUtils.isNotEmpty(pageUrl)) {

				try {
					if (pageUrl.contains(".html")) {
						if (request.getServerPort() == 80)
							externalURL = new URL(request.getScheme(), request.getServerName(), String.format("%s",
									pageUrl));
						else
							externalURL = new URL(request.getScheme(), request.getServerName(),
									request.getServerPort(), String.format("%s", pageUrl));
					} else {
						if (request.getServerPort() == 80)
							externalURL = new URL(request.getScheme(), request.getServerName(), String.format(
									"%s.html", pageUrl));
						else
							externalURL = new URL(request.getScheme(), request.getServerName(),
									request.getServerPort(), String.format("%s.html", pageUrl));
					}

					absUrl = externalURL.toString().replace("/content/lawfirm/", "/");
					absUrl = absUrl.replace(EN_US, EN);

					return absUrl;
				} catch (Exception e) {
					LOG.info("Exception occured while creating the absolute URL" + e.getMessage());
				}
			}
		} else {
			return "";
		}
		return "";

	}

	public static String getAbsoluteDomainUrl(String shortURL, String domain) {
		String returnURL = "";
		if (shortURL.contains("http")) {
			int startIndex = StringUtils.ordinalIndexOf(shortURL, "/", 3);
			String relativePath = shortURL.substring(startIndex);
			returnURL = domain + relativePath;
		} else {
			returnURL = domain + shortURL;
		}
		return returnURL;
	}

	public static String getAbsoluteImgUrl(final String imgUrl, final HttpServletRequest request) {
		URL externalURL = null;
		String absUrl = null;
		if (imgUrl != null) {
			if (StringUtils.isNotEmpty(imgUrl)) {

				try {

					if (request.getServerPort() == 80)
						externalURL = new URL(request.getScheme(), request.getServerName(), String.format("%s", imgUrl));
					else
						externalURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(),
								String.format("%s", imgUrl));

					absUrl = externalURL.toString().replace("/content/dam/", "/");

					return absUrl;
				} catch (Exception e) {
					LOG.info("Exception occured while creating the absolute Imge  URL" + e.getMessage());
				}
			}
		} else {
			return "";
		}
		return "";

	}
}
