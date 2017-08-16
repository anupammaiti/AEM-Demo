package org.unhcr.cq5.auth;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unhcr.cq5.validators.EmailValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class UnhcrCookieHelper {

    private static final Logger log = LoggerFactory.getLogger(UnhcrCookieHelper.class);

    private static final String HEADER_SET_COOKIE = "Set-Cookie";


    public static String getCookieString(HttpServletRequest request) {
        String returnValue = "";

        // get cookies
        Cookie cookie = getUnhcrAuthCookie(request);
        if (cookie != null) {
            String value = cookie.getValue();
            if (value.length() > 0) {
                // extract cookie string
                try {
                    String cookieString = new String(Base64.decodeBase64(value), "UTF-8");
                    log.trace("getCookieString - Cookie string = {}", cookieString);
                    returnValue = cookieString;
                } catch (UnsupportedEncodingException e) {
                    log.error("getCookieString - Could not get cookie string - {}", e);
                }
            }
        }

        return returnValue;
    }


    public static Cookie getUnhcrAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            // loop all cookies and try to find a matching cookie
            for (Cookie cookie : cookies) {
                if (UnhcrAuthConstants.COOKIE_NAME.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }


    public static void setCookieAuthenticationInfo(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authenticationInfo, Integer authenticationCookieLifetime) {
        String remember = request.getParameter("remember");
        try {
            if (remember == null || remember.isEmpty() || authenticationCookieLifetime == null || authenticationCookieLifetime == 0) {
                setCookie(request, response, Base64.encodeBase64URLSafeString(authInfoToCookieString(authenticationInfo).getBytes("UTF-8")), -1);
            } else {
                setCookie(request, response, Base64.encodeBase64URLSafeString(authInfoToCookieString(authenticationInfo).getBytes("UTF-8")), authenticationCookieLifetime);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("setCookieAuthenticationInfo - Could not set cookie - {}", e);
        }
    }

    public static void renewAuthenticationCookie(HttpServletRequest request, HttpServletResponse response, String username, String password, int oldAge) {
        try {
            setCookie(request, response, Base64.encodeBase64URLSafeString(credentialsToCookieString(username, password).getBytes("UTF-8")), oldAge);
        } catch (UnsupportedEncodingException e) {
            log.error("renewAuthenticationCookie - Could not set cookie - ", e);
        }
    }


    public static void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookieToRemove = getUnhcrAuthCookie(request);

        // remove the cookie from the client
        if (cookieToRemove != null) {
            setCookie(request, response, "", 0);
            log.trace("clearCookie - Cookie cleared");
        }
    }


    private static void setCookie(HttpServletRequest request, HttpServletResponse response, String value, int age) {

        String ctxPath = request.getContextPath();
        String cookiePath = StringUtils.isEmpty(ctxPath) ? "/" : ctxPath;

        Cookie cookieToSet = new Cookie(UnhcrAuthConstants.COOKIE_NAME, value);
        cookieToSet.setPath(cookiePath);
        if (age >= 0) {
            cookieToSet.setMaxAge(age);
        }
        if (request.isSecure()) {
            cookieToSet.setSecure(true);
        }

        //log.error("\nJens "+cookieToSet.getName()+"="+cookieToSet.getValue()+"\n");
        response.addCookie(cookieToSet);

        /*
         * The Servlet Spec 2.5 does not allow us to set the commonly used
         * HttpOnly attribute on cookies (Servlet API 3.0 does) so we create
         * the Set-Cookie header manually. See
         * http://www.owasp.org/index.php/HttpOnly for information on what
         * the HttpOnly attribute is used for.
         */

        /*StringBuilder header = new StringBuilder(60);

        // default setup with name, value, cookie path and HttpOnly
        header.append(UnhcrAuthConstants.COOKIE_NAME).append("=").append(value);
        header.append("; Path=").append(cookiePath);
        header.append("; HttpOnly"); // don't allow JS access

        // Only set the Max-Age attribute to remove the cookie
        if (age >= 0) {
            header.append("; Max-Age=").append(age);
        }

        // ensure the cookie is secured if this is an https request
        if (request.isSecure()) {
            header.append("; Secure");
        }

        String cookieString = header.toString();
        log.trace("Setting cookie '{}'", cookieString);
        log.error("\nJens "+cookieString+"\n");
        response.addHeader(HEADER_SET_COOKIE, cookieString); */
    }


    public static AuthenticationInfo cookieToAuthInfo(String cookie, String internalUserIdSuffix) {
        String userEncoded = "";
        String[] parts = StringUtils.split(cookie, ":");
        if (parts.length >= 1) {
            userEncoded = parts[0];
        }
        String passwordEncoded = "";
        if (parts.length >= 2) {
            passwordEncoded = parts[1];
        }

        String userString = byteArrayToString(Base64.decodeBase64(userEncoded));
        if (!EmailValidator.isValid(userString) && !userString.endsWith(internalUserIdSuffix)) {
            userString += internalUserIdSuffix;
        }
        String passwordString = byteArrayToString(Base64.decodeBase64(passwordEncoded));
        
        log.trace("cookieToAuthInfo: {} -> {}:{}", new Object[]{cookie, userString, passwordString});
        return new AuthenticationInfo(UnhcrAuthConstants.AUTH_TYPE, userString, passwordString.toCharArray());
    }


    private static String credentialsToCookieString(String userString, String passwordString) {
        String userEncoded = Base64.encodeBase64URLSafeString(userString.getBytes());
        String passwordEncoded = Base64.encodeBase64URLSafeString(passwordString.getBytes());

        String cookieString = userEncoded + ":" + passwordEncoded;
        log.trace("authInfoToCookieString - {}:{} -> {}", new Object[]{userString, passwordString, cookieString});
        return cookieString;
    }

    
    private static String authInfoToCookieString(AuthenticationInfo authenticationInfo) {
        String userString = authenticationInfo.getUser();
        String passwordString = new String(authenticationInfo.getPassword());

        return credentialsToCookieString(userString, passwordString);
    }


    private static String byteArrayToString(byte[] ba) {
        String returnValue = "";
        try {
            returnValue = new String(ba, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Could not decode byte array to string");
        }
        return returnValue;
    }


    private UnhcrCookieHelper() {
        //no instances
    }
}
