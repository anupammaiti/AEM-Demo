package com.day.iotmarketplace.authentication;

import com.day.cq.replication.Replicator;
import com.day.cq.security.UserManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.auth.core.spi.AbstractAuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.runmode.RunMode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Session;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component(
        label = UnhcrAuthConstants.HANDLER_NAME,
        description = UnhcrAuthConstants.HANDLER_NAME,
        metatype = true,
        immediate = true
)
@Properties({
        @Property(
                name = Constants.SERVICE_DESCRIPTION,
                value = UnhcrAuthConstants.HANDLER_NAME
        ),
        @Property(
                name = AuthenticationHandler.TYPE_PROPERTY,
                value = UnhcrAuthConstants.AUTH_TYPE,
                propertyPrivate = true
        ),
        @Property(
                label = "Protected path",
                description = "Path protected by this authentication handler (e.g. /content/intranet)",
                name = AuthenticationHandler.PATH_PROPERTY,
                cardinality = Integer.MAX_VALUE
        ),
        @Property(
                label = "Login path",
                description = "Path of the login page (without extension, e.g. /content/intranet/unhcr/en/public/login)",
                name = UnhcrAuthHandler.LOGIN_PATH_NAME
        ),
        @Property(
                label = "Public paths",
                description = "List of public paths - in addition to the 'Login path' which is automatically made public - that do not require authentication (e.g. /content/intranet/unhcr/en/public)",
                name = UnhcrAuthHandler.PUBLIC_PATHS_NAME,
                cardinality = Integer.MAX_VALUE
        ),
        @Property(
                label = "Internal User ID Suffix",
                description = "Suffix to be automatically added to the ID of an internal user",
                name = UnhcrAuthHandler.INTERNAL_USER_ID_SUFFIX
        ),
        @Property(
                label = "Authentication Cookie Lifetime",
                description = "Authentication Cookie Lifetime used when \"Remember Me\" is checked",
                name = UnhcrAuthHandler.AUTHENTICATION_COOKIE_LIFETIME
        )

})
@Service
@SuppressWarnings("unused")
public class UnhcrAuthHandler extends AbstractAuthenticationHandler implements UnhcrAuthHandlerConfigService {

    private static final Logger log = LoggerFactory.getLogger(UnhcrAuthHandler.class);

    // instance variables used for OSGI registration
    private BundleContext bundleContext;
    private ServiceRegistration registration;
    private Map<String, Object> properties;

    // config properties (loaded via OSGI configure method)
    private List<String> paths = Collections.emptyList();
    private String loginPath = StringUtils.EMPTY;
    private List<String> publicPaths = Collections.emptyList();
    private String internalUserIdSuffix = StringUtils.EMPTY;
    private Integer authenticationCookieLifetime = -1;

    public static final String PUBLIC_PATHS_NAME = "publicPaths";
    public static final String LOGIN_PATH_NAME = "loginPath";
    public static final String INTERNAL_USER_ID_SUFFIX = "internalUserIdSuffix";
    public static final String AUTHENTICATION_COOKIE_LIFETIME = "authenticationCookieLifetime";
    public static final String DEFAULT_INTERNAL_USER_ID_SUFFIX = "@hitachi.in";
    public static final int DEFAULT_COOKIE_LIFETIME = 432000;

    @Reference
    protected RunMode runModeService;

    @Reference
    protected UserManagerFactory userManagerFactory;

    @Reference
    protected SlingRepository repository;

    @Reference
    protected Replicator replicator;
    
    /**
     * *************************************************************************************************
     * <p/>
     * Authentication Handler Methods
     * <p/>
     * **************************************************************************************************
     */

    @Override
    public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {
        log.trace("Extracting credentials ...");

        // first try to get info from POST
        AuthenticationInfo authenticationInfo = this.extractAuthenticationInfoFromPost(request);

        // not found in POST, try to extract from cookie
        if (authenticationInfo == null) {
            log.trace("extractCredentials - Did not find POSTed authentication info, now checking for auth cookie");

            String authCookieString = UnhcrCookieHelper.getCookieString(request);
            if (authCookieString != null && !authCookieString.isEmpty()) {
                log.trace("extractCredentials - Found authentication cookie");
                authenticationInfo = UnhcrCookieHelper.cookieToAuthInfo(authCookieString, internalUserIdSuffix);
            }
        }

        log.trace("extractCredentials - AuthenticationInfo = {}", authenticationInfo);

        log.trace("Extracting credentials ... DONE");
        return authenticationInfo;
    }

    @Override
    public boolean requestCredentials(HttpServletRequest request, HttpServletResponse response) {
        log.trace("Requesting credentials ...");

        // safety check so that if a specific handler is directly requested we don't accidentally grab the request
        if (isRequestForOtherHandler(request)) {
            log.trace("requestCredentials - Aborting because another handler was directly requested");
            return false;
        }

        // send the user to the login form
        try {
            // prepare redirect parameters
            Map<String, String> redirectParams = new HashMap<String, String>(2);

            // tell the form which URL the user originally requested
            String resource = setLoginResourceAttribute(request, request.getRequestURI());
            redirectParams.put(Authenticator.LOGIN_RESOURCE, resource);

            // tell the form why the last authentication has failed
            if (request.getAttribute(UnhcrAuthConstants.REASON_PARAMETER_NAME) != null) {
                Object failureReason = request.getAttribute(UnhcrAuthConstants.REASON_PARAMETER_NAME);
                String failureReasonString = failureReason instanceof UnhcrLoginResult ?
                                ((UnhcrLoginResult) failureReason).name() :
                                failureReason.toString();

                redirectParams.put(UnhcrAuthConstants.REASON_PARAMETER_NAME, failureReasonString);
            }
            Integer passwordTriesLeft = (Integer)request.getAttribute(UnhcrAuthConstants.PASSWORD_TRIES_LEFT);
            if (passwordTriesLeft != null) {
                redirectParams.put(UnhcrAuthConstants.PASSWORD_TRIES_LEFT, passwordTriesLeft.toString());
            }
            // redirect
            log.trace("requestCredentials - Redirecting to login form");
            sendRedirect(request, response, getLoginFormURL(), redirectParams);
        } catch (IOException e) {
            log.error("requestCredentials - Failed to redirect to the login form - ", e);
        }

        log.trace("Requesting credentials ... DONE");
        return true;
    }

    @Override
    public void dropCredentials(HttpServletRequest request, HttpServletResponse response) {
        log.trace("Dropping credentials ...");

        UnhcrCookieHelper.clearCookie(request, response);

        log.trace("Dropping credentials ... DONE");
    }

    /**
     * *************************************************************************************************
     * <p/>
     * Authentication Feedback Methods
     * <p/>
     * **************************************************************************************************
     */

    @Override
    public void authenticationFailed(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authInfo) {
        log.trace("Authentication Failed ...");

        // user did funny stuff, let's remove the cookie
        UnhcrCookieHelper.clearCookie(request, response);

        // indicate why it failed
        UnhcrLoginResult failureReason = UnhcrLoginResult.INVALID_CREDENTIALS;
        if (authInfo != null) {
            char[] password = authInfo.getPassword();
            if (password == null || password.length == 0) {
                failureReason = UnhcrLoginResult.EMPTY_PASSWORD;
            } else {
                String userId = authInfo.getUser();
                failureReason = UnhcrLoginResult.INVALID_CREDENTIALS;
                if (userId != null && !userId.isEmpty()) {
                    Session adminSession = ServiceUtils.getAdminSessionFromRepo(this.repository);
                    UserManager uMgr = ServiceUtils.getUserManager(this.userManagerFactory, adminSession);
                    if (uMgr.hasAuthorizable(userId)) {
                        User user = (User)uMgr.get(userId);
                        if (user != null) {
                            UnhcrUser unhcrUser = user.adaptTo(UnhcrUser.class);
                            UnhcrUserChange unhcrUserChange = user.adaptTo(UnhcrUserChange.class);
                            if (unhcrUser != null && unhcrUserChange != null) {
                                Integer triesLeft = unhcrUser.getTriesLeft();
                                if (triesLeft != null && triesLeft > 0) {
                                    triesLeft--;
                                } else {
                                    triesLeft = 0;
                                }
                                unhcrUserChange.setTriesLeft(triesLeft);
                                request.setAttribute(UnhcrAuthConstants.PASSWORD_TRIES_LEFT, triesLeft);
                                if (triesLeft <= 0) {
                                    unhcrUserChange.setSimpleLocked(true);
                                    failureReason = UnhcrLoginResult.ACCOUNT_LOCKED;
                                }
                                unhcrUserChange.saveChanges(this.replicator);
                            }
                        }
                    }
                    ServiceUtils.saveAndCloseSession(adminSession);
                }
            }
        }
        request.setAttribute(UnhcrAuthConstants.REASON_PARAMETER_NAME, failureReason);

        log.trace("Authentication Failed ... DONE");
    }

    @Override
    public boolean authenticationSucceeded(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authInfo) {
        log.trace("Authentication Succeeded ...");
        boolean requestAlreadyHandled = false;

        // logout -> clear cookie and redirect
        if (isLogout(request)) {
            log.trace("authenticationSucceeded - Detected a logout request");

            // clear the cookie
            UnhcrCookieHelper.clearCookie(request, response);

            try {
                log.trace("authenticationSucceeded - Redirecting to logout confirmation page");
                response.sendRedirect(getLogoutURL());
            } catch (IOException e) {
                log.error("authenticationSucceeded - Failed to redirect to logout confirmation page - {}", e);
            }

            requestAlreadyHandled = true;
        }
        // login -> redirect to originally requested resource
        else if (isLoginPost(request)) {

            // get originally requested resource
            String originallyRequestedResource = getOriginallyRequestedResource(request);

            // set the cookie
            UnhcrCookieHelper.setCookieAuthenticationInfo(request, response, authInfo, this.authenticationCookieLifetime);

            // redirect
            try {
                log.trace("authenticationSucceeded - Redirecting to originally requested resource '{}'", originallyRequestedResource);
                response.sendRedirect(originallyRequestedResource);
            } catch (IOException e) {
                log.error("authenticationSucceeded - Failed to redirect to originally requested resource '{}' - {}", originallyRequestedResource, e);
            }

            requestAlreadyHandled = true;
        }
        // regular -> do UNHCR checks
        else if (authInfo != null) {

            // get user
            UnhcrUser user = null;

            Session adminSession = ServiceUtils.getAdminSessionFromRepo(this.repository);
            UserManager uMgr = ServiceUtils.getUserManager(this.userManagerFactory, adminSession);

            Authorizable auth = uMgr.get(authInfo.getUser());
            if (auth.isUser()) {
                User cqUser = (User) auth;
                user = cqUser.adaptTo(UnhcrUser.class);
            }

            if (user == null) {
                log.error("authenticationSucceeded - but cannot get User");
                // clear cookie
                UnhcrCookieHelper.clearCookie(request, response);

                log.trace("authenticationSucceeded - Redirecting to login page.");
                redirectToForm(request, response, getLoginFormURL(), UnhcrLoginResult.INTERNAL_ERROR);
                requestAlreadyHandled = true;
            }

            // deny access cases
            else if (user.isAccountConfirmationLimitExpired() || user.isAccountExpired() || user.isDeactivated() || user.isLocked()) {

                // indicate exact reason
                UnhcrLoginResult failureReason = UnhcrLoginResult.INTERNAL_ERROR;
                if (user.isAccountConfirmationLimitExpired()) {
                    log.trace("authenticationSucceeded - Aborting because account confirmation limit expired");
                    failureReason = UnhcrLoginResult.ACCOUNT_CONFIRMATIONLIMIT_EXPIRED;
                } else if (user.isAccountExpired()) {
                    log.trace("authenticationSucceeded - Aborting because account expired");
                    failureReason = UnhcrLoginResult.ACCOUNT_EXPIRED;
                } else if (user.isDeactivated()) {
                    log.trace("authenticationSucceeded - Aborting because account is deactivated");
                    failureReason = UnhcrLoginResult.ACCOUNT_INACTIVE;
                } else if (user.isLocked()) {
                    log.trace("authenticationSucceeded - Aborting because user is locked");
                    failureReason = UnhcrLoginResult.ACCOUNT_LOCKED;
                }

                // clear the cookie
                UnhcrCookieHelper.clearCookie(request, response);

                // redirect back to login page
                log.trace("authenticationSucceeded - Redirecting back to login page");
                redirectToForm(request, response, getLoginFormURL(), failureReason);
                requestAlreadyHandled = true;

            }
            // reset password cases
            else if (!user.hasTriesLeft() || user.isPasswordExpired()) {

                // indicate reason
                UnhcrLoginResult failureReason = UnhcrLoginResult.INTERNAL_ERROR;
                if (!user.hasTriesLeft()) {
                    log.trace("authenticationSucceeded - Requesting password reset because too many tries reached");
                    failureReason = UnhcrLoginResult.PASSWORD_NOTRIESLEFT;
                } else if (user.isPasswordExpired()) {
                    log.trace("authenticationSucceeded - Requesting password reset because password expired");
                    failureReason = UnhcrLoginResult.PASSWORD_EXPIRED;
                }

                // clear the cookie
                UnhcrCookieHelper.clearCookie(request, response);

                // redirect back to reset password page
                log.trace("authenticationSucceeded - Redirecting to password forgotten page");
                redirectToForm(request, response, getForgotFormURL(), failureReason);
                requestAlreadyHandled = true;

            }
            // user account ist ok (but might not have agreed to terms)
            else {
                //log.trace("authenticationSucceeded - Renewing cookie for regular authenticated request");
                //UnhcrCookieHelper.setCookieAuthenticationInfo(request, response, authInfo, authenticationCookieLifetime);

                // terms approval form has been approved
                if (isTermsAcceptance(request)) {
                    log.trace("authenticationSucceeded - User has accepted the terms, updating userRecording terms approval on the user object");

                    // update user
                    UnhcrUserChange userChange = user.adaptTo(UnhcrUserChange.class);
                    userChange.setTermsApproved(true);
                    userChange.setAccountToBeConfirmed(false);
                    userChange.saveChanges(this.replicator);

                    // redirect to originally requested resource
                    redirect(response, getOriginallyRequestedResource(request));
                    requestAlreadyHandled = true;
                }
                // terms approval form has been requested
                else if (isTerms(request)) {
                    log.trace("authenticationSucceeded - Displaying the terms acceptance form");

                    // continue with request
                }
                // user has not approved the terms
                else if (!user.isTermsApproved()) {

                    // indicate reason
                    log.trace("authenticationSucceeded - Requesting terms approval because user has not approved yet");

                    // note: in this case we leave the cookie alone

                    // redirect
                    log.trace("authenticationSucceeded - Redirecting to terms acceptance page");
                    UnhcrLoginResult failureReason = UnhcrLoginResult.TERMS_NOTACCEPTED;
                    redirectToForm(request, response, getTermsFormURL(), failureReason);
                    requestAlreadyHandled = true;

                }
                // regular request
                else {
                    log.trace("authenticationSucceeded - Continuing with regular request");
                }
            }

            ServiceUtils.saveAndCloseSession(adminSession);

        }
        // authenticated request without authinfo (unexpected!)
        else {
            log.error("authenticationSucceeded - Requesting re-login because authenticated request is missing authentication info");
            // clear cookie
            UnhcrCookieHelper.clearCookie(request, response);

            log.trace("authenticationSucceeded - Redirecting to login page");
            UnhcrLoginResult failureReason = UnhcrLoginResult.INTERNAL_ERROR;
            redirectToForm(request, response, getLoginFormURL(), failureReason);
            requestAlreadyHandled = true;
        }

        log.trace("Authentication Succeeded ... DONE");

        // no redirect
        return requestAlreadyHandled;
    }

    /**
     * *************************************************************************************************
     * <p/>
     * Helper Methods
     * <p/>
     * **************************************************************************************************
     */

    @Override
    public String toString() {
        return UnhcrAuthConstants.HANDLER_NAME;
    }

    private AuthenticationInfo extractAuthenticationInfoFromPost(HttpServletRequest request) {
        AuthenticationInfo returnValue = null;

        // make sure that we only handle POSTs to the login URL
        if (isLoginPost(request)) {

            String user = request.getParameter("userId");
            String password = request.getParameter("userPassword");

            log.debug("requestToAuthenticationInfo - User = '{}' / Password = '{}'", user, password);

            if (user != null && password != null) {
                // check whether the user is not external and did not provide his whole email address as login
                if (!EmailValidator.isValid(user) && !user.endsWith(this.internalUserIdSuffix)) {
                    user += this.internalUserIdSuffix;
                }
                returnValue = new AuthenticationInfo(UnhcrAuthConstants.AUTH_TYPE, user, password.toCharArray());

                // tell the system to pass on the resource parameter (using servlet auth API methods)
                if (!isValidateRequest(request)) {
                    setLoginResourceAttribute(request, request.getContextPath());
                }
            }
        }

        return returnValue;
    }

    private static void redirectToForm(HttpServletRequest request, HttpServletResponse response, String url, UnhcrLoginResult failureReason) {
        String redirectUrl = url + "?" + UnhcrAuthConstants.REASON_PARAMETER_NAME + "=" + failureReason + "&" + Authenticator.LOGIN_RESOURCE + "=" + getOriginallyRequestedResource(request);
        redirect(response, redirectUrl);
    }

    private static void redirect(HttpServletResponse response, String redirectUrl) {
        try {
            log.debug("redirect - Redirecting to {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.error("redirect - Failed to redirect to {} - {}", redirectUrl, e);
        }
    }

    private boolean isLoginPost(HttpServletRequest request) {
        return UnhcrAuthConstants.LOGIN_REQUEST_METHOD.equals(request.getMethod()) && request.getRequestURI().startsWith(getLoginActionURL());
    }

    private boolean isLogout(HttpServletRequest request) {
        return request.getRequestURI().startsWith(getLogoutURL());
    }

    private boolean isTerms(HttpServletRequest request) {
        return request.getRequestURI().startsWith(getTermsFormURL());
    }

    private boolean isTermsAcceptance(HttpServletRequest request) {
        return request.getRequestURI().startsWith(getTermsActionURL()) && request.getParameter(UnhcrAuthConstants.TERMS_IAGREE) != null && UnhcrAuthConstants.TERMS_IAGREE.equals(request.getParameter(UnhcrAuthConstants.TERMS_IAGREE));
    }

    private static boolean isRequestForOtherHandler(ServletRequest request) {
        return request.getParameter(REQUEST_LOGIN_PARAMETER) != null && !UnhcrAuthConstants.AUTH_TYPE.equals(request.getParameter(REQUEST_LOGIN_PARAMETER));
    }

    private static String getOriginallyRequestedResource(final HttpServletRequest request) {
        // first try to auto-extract the resource-parameter
        String originallyRequestedResource = getLoginResource(request, null);

        // then try to extract manually (this happens when regular auth is passed and redirect to original URL happens but then unhcr specific checks fail)
        if (originallyRequestedResource == null || originallyRequestedResource.isEmpty()) {
            log.trace("getOriginallyRequestedResource - No login resource found, failing over to request URI '{}'", request.getRequestURI());
            originallyRequestedResource = request.getRequestURI();

            if (originallyRequestedResource == null || originallyRequestedResource.isEmpty()) {
                log.trace("getOriginallyRequestedResource - No request parameter indicating resource found, failing over to /");
                originallyRequestedResource = "/";
            }
        }

        return originallyRequestedResource;
    }

    /*
     * *************************************************************************************************
     * OSGI methods
     * **************************************************************************************************
     */

    @Activate
    private void activate(final BundleContext bundleCtx, final Map<String, Object> props) {
        this.bundleContext = bundleCtx;
        configure(this.bundleContext, props);
    }

    @Modified
    private void configure(final BundleContext context, final Map<String, Object> config) {

        // ensure non-null configuration properties
        Map<String, Object> props = config != null ? config : new HashMap<String, Object>(2);

        // ensure not registered as service during reconfiguration
        if (null != this.registration) {
            registration.unregister();
            this.registration = null;
        }

        // reconfigure while not being registered
        this.properties = props;

        // get config properties
        String[] pathsArray = OsgiUtil.toStringArray(properties.get(PATH_PROPERTY), ZERO_LENGTH_ARRAY);
        this.paths = Arrays.asList(pathsArray);
        log.info("UnhcrAuthHandler.configure() - Paths = '{}'", this.paths);
        this.loginPath = OsgiUtil.toString(properties.get(LOGIN_PATH_NAME), "");
        log.info("UnhcrAuthHandler.configure() - Login path = '{}'", this.loginPath);
        String[] publicPathsArray = OsgiUtil.toStringArray(properties.get(PUBLIC_PATHS_NAME), ZERO_LENGTH_ARRAY);
        this.publicPaths = Arrays.asList(publicPathsArray);
        log.info("UnhcrAuthHandler.configure() - Public paths = '{}'", this.publicPaths);
        this.internalUserIdSuffix = OsgiUtil.toString(properties.get(INTERNAL_USER_ID_SUFFIX), DEFAULT_INTERNAL_USER_ID_SUFFIX);
        log.info("UnhcrAuthHandler.configure() - Internal User Id Suffix = '{}'", this.internalUserIdSuffix);
        this.authenticationCookieLifetime = OsgiUtil.toInteger(properties.get(AUTHENTICATION_COOKIE_LIFETIME), DEFAULT_COOKIE_LIFETIME);
        log.info("UnhcrAuthHanler.configure() - Authentication Cookie Lifetime = {}", this.authenticationCookieLifetime);
        // register again after (re)configuration
        this.registration = bundleContext.registerService(UnhcrAuthHandler.class.getName(), this, getProperties());
    }

    @Deactivate
    private void deactivate() {

        // "disable" first
        if (this.registration != null) {
            registration.unregister();
            this.registration = null;
        }
    }

    private Dictionary<String, Object> getProperties() {

        // need a copy of the component properties
        Dictionary<String, Object> newProps = new Hashtable<String, Object>(5);
        for(Map.Entry<String, Object> entry : properties.entrySet()) {
            newProps.put(entry.getKey(), entry.getValue());
        }

        // prepare authentication requirements
        Collection<String> authReqs = new HashSet<String>(5);

        // only enable auth requirements on publish (and only if a path is protected)
        if (Arrays.asList(runModeService.getCurrentRunModes()).contains("publish")) {
            if (!this.paths.isEmpty()) {
                for (String path : this.paths) {
                    authReqs.add("+" + path);
                }
                authReqs.add("-" + this.loginPath);
                for (String publicPath : this.publicPaths) {
                    authReqs.add("-" + publicPath);
                }
            }
        }

        // set the authentication requirement for Sling Auth
        newProps.put("sling.auth.requirements", authReqs.toArray(new String[authReqs.size()]));

        return newProps;
    }

    /**
     * *************************************************************************************************
     * <p/>
     * methods exposing config properties
     * <p/>
     * **************************************************************************************************
     */

    @Override
    public String getLoginFormURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_LOGINFORM;
    }

    @Override
    public String getLoginActionURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_LOGINACTION;
    }

    @Override
    public String getTermsFormURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_TERMSFORM;
    }

    @Override
    public String getTermsActionURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_TERMSACTION;
    }

    @Override
    public String getForgotFormURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_FORGOTFORM;
    }

    @Override
    public String getLogoutURL() {
        return this.loginPath + UnhcrAuthConstants.URLSUFFIX_LOGOUT;
    }

}
