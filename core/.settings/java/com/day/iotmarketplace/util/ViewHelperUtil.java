package com.day.iotmarketplace.util;

import java.util.Map;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.servlet.ServletRequest;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.iotmarketplace.exception.SystemException;

/**
 * This is a utility class and provides different methods for the view helper to
 * extract data and use it.
 */
public final class ViewHelperUtil {

	private static final Logger log = LoggerFactory.getLogger(ViewHelperUtil.class);
	/**
	 * Constant for value 1.
	 */
	private static final int CONSTANT_VALUE_ONE = 1;

	/**
	 * Constant for value 2.
	 */
	private static final int CONSTANT_VALUE_TWO = 2;

	/**
	 * Constant for value 3.
	 */
	private static final int CONSTANT_VALUE_THREE = 3;

	/**
	 * Constant for port number 80.
	 */
	private static final int CONSTANT_HTTP_PORT_80 = 80;

	/**
	 * Constant for port number 443.
	 */
	private static final int CONSTANT_HTTP_PORT_443 = 443;
	/**
	 * Constant for value 10.
	 */
	private static final int CONSTANT_TEN = 10;
	/**
	 * Constant for value 100.
	 */
	private static final int CONSTANT_HUNDRED = 100;

	/**
	 * Constructor for the class.
	 */
	private ViewHelperUtil() {

	}

	/**
	 * get property as object array irrespective of what is returned.
	 * 
	 * @param properties
	 *            properties
	 * @param propertyName
	 *            propertyName
	 * @return returns property value as object array or null
	 */
	public static Object[] getPropertyAsObjectArray(final Map<String, Object> properties, final String propertyName) {
		Object[] property = null;
		if (properties.get(propertyName) != null) {
			if (properties.get(propertyName) instanceof Object[]) {
				property = (Object[]) properties.get(propertyName);
			} else {
				property = new Object[] { properties.get(propertyName) };
			}
		}
		return property;
	}

	/**
	 * get property as object array irrespective of what is returned.
	 * 
	 * @param properties
	 *            properties
	 * @param propertyName
	 *            propertyName
	 * @return returns property value as object array or null
	 */
	public static Object[] getPropertyAsObjectArray(final ValueMap properties, final String propertyName) {
		Object[] property = null;
		if (properties.get(propertyName) != null) {
			if (properties.get(propertyName) instanceof Object[]) {
				property = (Object[]) properties.get(propertyName);
			} else {
				property = new Object[] { properties.get(propertyName) };
			}
		}
		return property;
	}

	/**
	 * This method is used to get property as string array irrespective of what
	 * is returned.
	 * 
	 * @param properties
	 *            properties
	 * @param propertyName
	 *            propertyName
	 * @return returns property value as object array or null
	 */
	public static String[] getPropertyAsStringArray(final Map<String, Object> properties, final String propertyName) {
		String[] property = null;
		if (properties.get(propertyName) != null) {
			if (properties.get(propertyName) instanceof Object[]) {
				property = (String[]) properties.get(propertyName);
			} else {
				property = new String[] { properties.get(propertyName).toString() };
			}
		}
		return property;
	}

	/**
	 * This method is used to get property as string array irrespective of what
	 * is returned.
	 * 
	 * @param object
	 *            object
	 * @return returns property value as object array or null
	 */
	public static String[] getPropertyAsStringArray(final Object object) {
		String[] property = null;
		if (object != null) {
			if (object instanceof String[]) {
				property = (String[]) object;
			} else {
				property = new String[] { object.toString() };
			}
		}
		return property;
	}

	/**
	 * This method is used to get property as string array irrespective of what
	 * is returned from Property.
	 * 
	 * @param propertyValue
	 *            properties
	 * @return returns property value as object array or null
	 */
	public static Value[] getPropertyAsValueArray(final Property propertyValue) {
		Value[] property = null;
		try {
			if (propertyValue != null) {
				if (propertyValue.isMultiple()) {
					property = propertyValue.getValues();
				} else {
					property = new Value[] { propertyValue.getValue() };
				}
			}
		} catch (ValueFormatException e) {
			throw new SystemException("Cannot Read value from Property", e);
		} catch (RepositoryException e) {
			throw new SystemException("Cannot Read value from Property", e);
		}
		return property;
	}

	/**
	 * This method returns the ordinal number (st, th, nd, rd etc) for the input
	 * value.
	 * 
	 * @param value
	 *            - whose ordinal number is required.
	 * @return - String ordinal number.
	 */
	public static String getOrdinalNumber(final int value) {
		final int hunRem = value % CONSTANT_HUNDRED;
		final int tenRem = value % CONSTANT_TEN;
		if ((hunRem - tenRem) == CONSTANT_TEN) {
			return "th";
		}
		switch (tenRem) {
		case CONSTANT_VALUE_ONE:
			return "st";
		case CONSTANT_VALUE_TWO:
			return "nd";
		case CONSTANT_VALUE_THREE:
			return "rd";
		default:
			return "th";
		}
	}

	/**
	 * This is a utility method to return the Image dimension eg length,width as
	 * specified in the input.
	 * 
	 * @param resourceResolver
	 *            Resource resolver object.
	 * @param imageFileRef
	 *            The image entered by the author.
	 * @param property
	 *            Property whose dimension needs to be calculated.
	 * @return dimension value in Integer
	 */
	public static Integer getImageDimension(final ResourceResolver resourceResolver, final String imageFileRef,
			final String property) {
		String imageDimension = "";
		Resource imageFileRefRes = resourceResolver.getResource(imageFileRef);
		if (imageFileRefRes != null) {
			Asset imageDimensionAsset = imageFileRefRes.adaptTo(Asset.class);
			if (imageDimensionAsset != null) {
				imageDimension = imageDimensionAsset.getMetadataValue(property);
			}
		}
		return Integer.parseInt(imageDimension);
	}

	/**
	 * This method is used to calculate the percentage of the a value with
	 * respect to the base value provided.
	 * 
	 * @param value
	 *            The value for which the percentage needs to be calculated.
	 * @param baseValue
	 *            The vase value against which the percentage needs to be
	 *            calculated.
	 * @return prcntge The percentage calculated is returned.
	 */
	public static float getPercentage(final Integer value, final Integer baseValue) {
		final float fractionNumber = 100.0f;
		float prcntage = 0;
		if ((baseValue != null) && (baseValue > 0)) {
			prcntage = (value * fractionNumber) / baseValue;
		}

		return prcntage;

	}

	/**
	 * <p>
	 * Converts Object array to String array.
	 * </p>
	 * 
	 * @param objectArray
	 *            - Array of Objects
	 * 
	 * @return - Array of Strings
	 * 
	 */
	public static String[] convertObjectArrayToStringArray(final Object[] objectArray) {

		String[] stringArray = null;

		if (null != objectArray) {
			stringArray = new String[objectArray.length];
			for (int i = 0; i < objectArray.length; i++) {
				stringArray[i] = objectArray[i].toString();
			}
		}

		return stringArray;
	}

	/**
	 * Get server path (e.g. http://localhost:4502)
	 * 
	 * @param request
	 *            - Sling Request.
	 * @return server path - Server path.
	 */
	public static String getServerPath(final ServletRequest request) {
		final StringBuilder serverPath = new StringBuilder();
		serverPath.append(request.getScheme());
		serverPath.append("://");
		if (("https".equals(request.getScheme()) && (request.getServerPort() != CONSTANT_HTTP_PORT_443))
				|| ("http".equals(request.getScheme()) && (request.getServerPort() != CONSTANT_HTTP_PORT_80))) {
			serverPath.append(request.getServerName());
			serverPath.append(':');
			serverPath.append(request.getServerPort());
		} else {
			if (!request.getRemoteHost().contains(":")) {
				serverPath.append(request.getRemoteHost());
			} else {
				serverPath.append(request.getServerName());
			}
		}
		return serverPath.toString();
	}

	/**
	 * Get Sling Service Reference
	 * 
	 * @param serviceType
	 *            - Sling Request.
	 * @return Service Instance.
	 */
	public static Object getService(final Class<?> serviceType) {
		final Bundle bndl = FrameworkUtil.getBundle(serviceType);
		final BundleContext bundleContext = bndl.getBundleContext();
		final ServiceReference serviceReference = bundleContext.getServiceReference(serviceType.getName());
		return bundleContext.getService(serviceReference);
	}

	public static String addSeparator(String number) {
		if (number != null && !number.isEmpty()) {
			String first = (String) number.subSequence(0, 3);
			String second = (String) number.subSequence(3, 6);
			String third = (String) number.subSequence(6, 10);
			return first + "-" + second + "-" + third;
		}
		return number;

	}

	public static boolean isNumeric(String str) {
		double d = 0.0;
		try {
			d = Double.parseDouble(str);
			log.info("IsNumeric : {}", d);
		} catch (NumberFormatException nfe) {
			log.error("Error in formating number : ", nfe);
			System.out.println(d);
			return false;
		}
		return true;
	}

}
