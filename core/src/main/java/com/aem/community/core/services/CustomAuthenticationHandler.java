package com.aem.community.core.services;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.DefaultAuthenticationFeedbackHandler;

@Component(metatype = true, immediate = true, label = "My Custom Authentication Handelr", description="Authenticates User Against Custom Hitachi Web Service")
@Service
@Properties({
    @Property(name = AuthenticationHandler.PATH_PROPERTY, value = "/"),
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Custom Authentication Handler") })
public class CustomAuthenticationHandler extends DefaultAuthenticationFeedbackHandler implements AuthenticationHandler,
        AuthenticationFeedbackHandler {

 private static final String REQUEST_METHOD = "POST";
 private static final String USER_NAME = "j_username";
 private static final String PASSWORD = "j_password";
static final String AUTH_TYPE = "YOGESH";

static final String REQUEST_URL_SUFFIX = "/j_mycustom_security_check";

/**
If you see most of the method under sling authentication handler, They have request and response object available. You can use that object to get information about user (Either by reading cookie or some other way).
*/
//Important methods
//Return true if succesful 
public boolean authenticationSucceeded(HttpServletRequest request, HttpServletResponse response,
            AuthenticationInfo authInfo) {

}
//Extract data from request Object

public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {

//You can have logic like. This will read user name and password from form post and set credentials

if (REQUEST_METHOD.equals(request.getMethod()) && request.getRequestURI().endsWith(REQUEST_URL_SUFFIX)
                && request.getParameter(USER_NAME) != null) {

            if (!AuthUtil.isValidateRequest(request)) {
                AuthUtil.setLoginResourceAttribute(request, request.getContextPath());
            }

            SimpleCredentials creds = new SimpleCredentials(request.getParameter(USER_NAME), request.getParameter(PASSWORD).toCharArray());
            //ATTR_HOST_NAME_FROM_REQUEST can be any thing this is just an example
            creds.setAttribute(ATTR_HOST_NAME_FROM_REQUEST, request.getServerName());

            return createAuthenticationInfo(creds);
        }
        return null;
    }

//Custom Create AuthInfo. Not required but you can create
private AuthenticationInfo createAuthenticationInfo(Credentials creds) {
//Note that there is different signature of this method. Use one that you need.
AuthenticationInfo info = new AuthenticationInfo(AUTH_TYPE);
//this you can use it later in auth process
info.put("Your Custom Attribute", creds);
return info;
}

//Do something when authentication failed.
    public void authenticationFailed(HttpServletRequest request, HttpServletResponse response,
            AuthenticationInfo authInfo) {

}