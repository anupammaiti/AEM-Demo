package com.day.iotmarketplace.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.iotmarketplace.constants.GlobalConstants;
import com.day.iotmarketplace.constants.ServiceConstants;

/**
 * This Utility has common methods which can be used in the services and other
 * implementation.
 * 
 * @author NVISH
 */

public class ServiceUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceUtil.class);

	/**
	 * Useful to store service data into the JCR.
	 * 
	 * @param wsResponse
	 * @param storagePath
	 * @param savedProperty
	 */
	public static void saveWSResponse(String wsResponse, String storagePath, String savedProperty) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Execution start of method saveWSResponse()  JCR Storage Path: {0} : JCR Save Property: {1}",
					storagePath, savedProperty);
		}
		Session session = null;
		String template = "/apps/hcc/templates/hccdatapage";
		ResourceResolver resourceResolver = JcrUtilService.getResourceResolver();
		try {
			session = JcrUtilService.getSession();
			Resource resource = resourceResolver.resolve(storagePath);
			if (resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				createPagePath(storagePath, session, template);
			}
			Node node = session.getNode(storagePath);
			node.setProperty(savedProperty, wsResponse);
			session.save();
		} catch (Exception e) {
			LOG.error("Exception occurs duing saving data to JCR: ", e);
		} finally {
			resourceResolver.close();
			/**
			 * if (session != null && session.isLive()) { session.logout();
			 * session = null; }
			 */
		}
	}

	/**
	 * Useful to get formated date based on provided formats.
	 * 
	 * @param sourceDate
	 * @param feedResponseFormat
	 * @param feedDisplayFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getDisplayDateFormat(String sourceDate, String feedResponseFormat, String feedDisplayFormat)
			throws ParseException {
		if (StringUtils.isNotEmpty(sourceDate)) {
			SimpleDateFormat sdfSource = new SimpleDateFormat(feedResponseFormat);
			Date dateTo = sdfSource.parse(sourceDate.trim());
			SimpleDateFormat sdfDestination = new SimpleDateFormat(feedDisplayFormat);
			return sdfDestination.format(dateTo);
		} else {
			return ServiceConstants.EMPTY_SPACE;
		}
	}

	/**
	 * Useful to get formated date based on provided formats.
	 * 
	 * @param sourceDate
	 * @param feedResponseFormat
	 * @param feedDisplayFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getDisplayDateFormat(String sourceDate, String feedResponseFormat, String feedDisplayFormat,
			Locale locale) throws ParseException {
		if (sourceDate != null && !"".equals(sourceDate)) {
			SimpleDateFormat sdfSource = new SimpleDateFormat(feedResponseFormat);
			Date dateTo = sdfSource.parse(sourceDate.trim());
			SimpleDateFormat sdfDestination = new SimpleDateFormat(feedDisplayFormat, locale);
			return sdfDestination.format(dateTo);
		} else {
			return ServiceConstants.EMPTY_SPACE;
		}
	}

	/**
	 * 
	 * @param date
	 * @param displayFormat
	 * @return
	 * @throws ParseException
	 */

	public static String getStringFromDate(Date date, String displayFormat, Locale locale) throws ParseException {

		SimpleDateFormat sdfDestination = new SimpleDateFormat(displayFormat, locale);
		return sdfDestination.format(date);

	}

	/**
	 * 
	 * @param date
	 * @param displayFormat
	 * @return
	 * @throws ParseException
	 */

	public static String getStringFromDate(Date date, String displayFormat) throws ParseException {

		SimpleDateFormat sdfDestination = new SimpleDateFormat(displayFormat);
		return sdfDestination.format(date);

	}

	/**
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param locale
	 * @return
	 * @throws ParseException
	 */

	public static String getLocalizedStringFromDate(int day, int month, int year, Locale locale) throws ParseException {
		String localizedDate = GlobalConstants.EMPTY_STRING;
		try {
			DateFormatSymbols symbols = new DateFormatSymbols(locale);
			String[] monthNames = symbols.getMonths();
			localizedDate = monthNames[month] + GlobalConstants.EMPTY_SPACE + day + GlobalConstants.COMMA + year;
		} catch (Exception e) {
			LOG.error("error while conerting date to localized::", e);
		}
		return localizedDate;

	}

	/**
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 * @throws ParseException
	 */

	public static String getStringFromDate(int day, int month, int year) {

		return ServiceUtil.getMonth(month) + GlobalConstants.EMPTY_SPACE + day + GlobalConstants.COMMA + year;

	}

	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromString(String date, String format) throws ParseException {

		SimpleDateFormat sdfDestination = new SimpleDateFormat(format);
		return sdfDestination.parse(date);

	}

	public static String getMonth(int month) {
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		return monthNames[month];
	}

	public static List<String> extractUrls(String value) {

		List<String> result = new ArrayList<String>();

		try {
			if (value != null) {

				String urlPattern = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

				Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);

				Matcher m = p.matcher(value);

				while (m.find()) {
					result.add(value.substring(m.start(0), m.end(0)));

				}

			}
		} catch (Exception e) {
			LOG.error("Exception : ", e);
		}
		return result;
	}

	public static Date getDatefromString(String dateStr, String dateformate) {

		SimpleDateFormat formatter = new SimpleDateFormat(dateformate);

		try {
			Date date = formatter.parse(dateStr);
			return date;
		} catch (Exception e) {
			LOG.info("Exception while converting Date::" + e);
		}
		return null;

	}

	public static String getNextDate(String curDate) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		String nextDate = GlobalConstants.EMPTY_STRING;
		try {
			date = format.parse(curDate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			nextDate = format.format(calendar.getTime());
		} catch (ParseException e) {
			LOG.info("Exception while next Date::" + e);
		}
		return nextDate;
	}

	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
				.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	public static Calendar updateTimezoneWithoutConversion(SimpleDateFormat df, Calendar orignalCal,
			TimeZone targetTimezone) {
		TimeZone tz1 = orignalCal.getTimeZone();
		long l1 = orignalCal.getTimeInMillis();
		df.setTimeZone(tz1);
		Calendar returnCal = Calendar.getInstance(targetTimezone);
		long l2 = l1 + tz1.getRawOffset() - targetTimezone.getRawOffset();
		returnCal.setTimeInMillis(l2);
		df.setTimeZone(targetTimezone);
		return returnCal;
	}

	public static void createPagePath(final String path, final Session session, final String template) {
		final String[] pathArray = path.split(ServiceConstants.SLASH_SEPRATOR);
		String currentPath = GlobalConstants.EMPTY_STRING;
		String previousPath;
		Page page = null;
		Node pageNode;
		ResourceResolver resourceResolver = JcrUtilService.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		try {
			for (int i = 1; i < pathArray.length; i++) {
				previousPath = currentPath;
				currentPath = currentPath.concat(ServiceConstants.SLASH_SEPRATOR).concat(pathArray[i]);
				if (!session.itemExists(currentPath) && pageManager != null) {
					page = pageManager.create(previousPath, pathArray[i], template, pathArray[i]);
					pageNode = session.getNode(page.getPath() + "/jcr:content");
					pageNode.remove();
					session.save();
					pageNode = session.getNode(page.getPath());
					pageNode.addNode("jcr:content", "cq:PageContent");
					pageNode = session.getNode(page.getPath() + "/jcr:content");
					pageNode.setProperty("cq:template", template);
					pageNode.setProperty("sling:resourceType", "hdscorp/components/page/overlay");
					pageNode.setProperty("jcr:title", pathArray[i]);
					session.save();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while creating page path ", e);
		} finally {
			resourceResolver.close();
		}
	}

	
	
}
