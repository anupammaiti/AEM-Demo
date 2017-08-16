package com.day.iotmarketplace.constants;

/**
 * The Service Constants is used for all the services.
 * 
 * @author NVISH
 */
public final class ServiceConstants {

	public static final String JSON_THUMBNAIL_PATH = "thumbnailPath";
	public static final String JSON_PREVIEW_IMAGE_PATH = "previewImagePath";
	public static final String JSON_STATUS_CODE = "statusCode";
	public static final String JSON_STATUS_REASON = "statusReason";

	public static final String NOT_FOUND_STATUS_CODE = "400";
	public static final String NOT_FOUND_STATUS_REASON = "Bad Request";
	public static final String OK_FOUND_STATUS_CODE = "200";
	public static final String OK_FOUND_STATUS_REASON = "Provided feed URL have not valid data format";
	public static final String EMPTY_SPACE = " ";
	public static final String SLASH_SEPRATOR = "/";
	public static final String COMMA_SEPRATOR = ",";
	public static final String DATE_FORMAT_TO = "MM/dd/yyyy";
	public static final String DATE_FORMAT_FROM = "yyyy-MM-dd";
	public static final String DATE_SEPERATOR = "T";
	public static final String UTF_8 = "UTF-8";
	public static final String CONTENT_TYPE = "application/xml";
	public static final String POST_METHOD_TYPE = "Post";
	public static final String GET_METHOD_TYPE = "Get";
	public static final String FEED_PARAMETER = "";
	public static final String SAVE_FEED_DATA_PROPERTY_NAME = "serviceResponse";
	public static final String FEED_UPCOMING = "upcoming";
	public static final String FEED_RECORDED = "recorded";



	public static final String FEED_URL_KEY = "feed.url";
	public static final String FEED_SCHEDULER_EXPRESSION = "scheduler.expression";
	public static final String FEED_STORAGE_PATH = "storage.path";
	public static final String FEED_POST_LIMIT = "feed.limit";


	/**
	 * Proxy constraints
	 */

	public static final String PROP_PROXY_HOST = "proxy.host";
	public static final String PROP_PROXY_ENABLED = "proxy.enabled";

	/**
	 * Shorten URL
	 */
	public static final String JSON_MIME_TYPE = "application/json";
	public static final String DEACTIVATE_METHOD = "DEACTIVATE METHOD - ";
	public static final String ACTIVATE_METHOD = "ACTIVATE METHOD - ";
	public static final String ENTERING = "ENTERING - ";
	public static final int RETRY_COUNT = 3;
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String DEFAULT_CONTENT_ENCODING = ENCODING_UTF_8;
	public static final String FEED_URL = "feedUrl";
	public static final String API_KEY = "apiKey";

}
