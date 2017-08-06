package com.aem.community.core.servlets;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aem.community.core.services.CustomAuthenticationHandler;
import com.aem.community.core.services.LoginValidatorService;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import com.aem.community.core.objects.User;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/loginServlet", methods = "POST", metatype=true)
@Properties({
        @Property(name = "service.pid", value = "com.aem.community.core.servlets.LoginServlet", propertyPrivate = false),
        @Property(name = "service.vendor", value = "Hitachi", propertyPrivate = false) })
public class LoginServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
	
	@Reference
	CustomAuthenticationHandler authHandler;
	
	@Reference
	LoginValidatorService loginValidatorService;
	
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        
    	resp.setContentType("application/json");
    	PrintWriter out = resp.getWriter();
    	
    	String username = "";
    	String password = "";
    	String rememerMe = "";
    	
    	if(req.getParameter("username") != null){
    		username = req.getParameter("username");
    	}
    	
    	if(req.getParameter("password") != null){
    		password = req.getParameter("password");
    	}
    	
    	if(req.getParameter("rememberme") != null){
    		rememerMe = req.getParameter("rememerme");
    	}
    	
    	//Validate Credentials via Web Service and get User Details
    	User user = loginValidatorService.validateCredentials(username, password);
    	
    	AuthenticationInfo authInfo = new AuthenticationInfo("");
    	if(StringUtils.isNotBlank(user.getTokenId())){
    		authHandler.authenticationSucceeded(req, resp, authInfo);
    	}
    	
    	Gson gson = new Gson();
	    String jsonString = gson.toJson(user);    	
	    out.print(jsonString);
	    out.flush();
	    
	    resp.setStatus(200);
    }
}