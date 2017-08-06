package com.aem.community.core.services.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.objects.User;
import com.aem.community.core.services.LoginValidatorService;

@Component(metatype = true, immediate = true, label = "Login Validator Service")
@Service(LoginValidatorService.class)
@Properties({
	@Property(name = "service.pid", value = "com.aem.community.core.services.LoginValidatorService", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Hitachi"),
	@Property(name = "login.webservice.url", value = "")
	})
public class LoginValidatorServiceImpl implements LoginValidatorService {

	private static final Logger log = LoggerFactory.getLogger(LoginValidatorServiceImpl.class);
    
    @Activate
    protected final void activate(ComponentContext ctx) {
        log.info("Activated LoginValidatorServiceImpl");
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) throws Exception {
        log.info("Deactivated LoginValidatorServiceImpl");
    }
    
	@Override
    public User validateCredentials(String username, String password) {
		User user = new User();
    	
    	try {
    		//Call Web Service Stub Method for User Validation
    		user.setName("Mayank");
    		user.setEmail("mayankmaggon86@gmail.com");
    		user.setCompany("TechChefz");
    		user.setPhone("9711829204");
    		user.setCountry("IN");
    		user.setTokenId("11111111111");
    
	  	} catch (Exception e) {
			log.error("Excpetion in LoginValidatorServiceImpl validateCredentials : " + e.getMessage() );
		}
    	
    	return user;
    }
}