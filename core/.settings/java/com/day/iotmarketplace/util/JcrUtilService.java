package com.day.iotmarketplace.util;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;


@Component(immediate = true, label = "JCR Util Service", description = "JCR Util Service", metatype = true)
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "JCR Util Service"),
        @Property(name = Constants.SERVICE_VENDOR, value = "Hitachi Next"),
        @Property(name = Constants.SERVICE_RANKING,intValue=100) })
@Service(JcrUtilService.class)
/**
 * @author NVISH
 * @description This JcrUtilServicen class acts as OSGI Service which gives JCR resourceResolver and Session Objects
 * 			     to access and manipulate the content in the repository 
 */
public class JcrUtilService{

	private static final Logger LOG = LoggerFactory.getLogger(JcrUtilService.class);
	
	@Reference
	private Replicator replicator;
	@Reference
	private SlingRepository slingRepository;
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	private static ResourceResolverFactory resourceResolverFactory;
	private static Session session;
	private static SlingRepository repository;
	private static ResourceResolver resourceResolver;
	
	/**
	 * 
	 * @return @link {@link Session} Object Instance
	 */
	public static Session getSession() {
		if(null != session && session.isLive()){
           return session;
        }else{
        	try{
        		session=repository.loginService("writeService",  repository.getDefaultWorkspace());
			}catch (RepositoryException e) {
				LOG.error("Error while getting session...."+e);
			}
			return session;
		}
	}
	
	public static void logout() {
		if(null != session && session.isLive()){
			LOG.info("logged out from session");
            session.logout();
        }
	}
	/**
	 * 
	 * @return {@link ResourceResolver} Instance
	 */
	public static ResourceResolver getResourceResolver(){
		if(null != resourceResolver){
            return resourceResolver;
        }else{
        	try{
        		if(null!=resourceResolverFactory){
        			resourceResolver =  resourceResolverFactory.getServiceResourceResolver(CommonUtil.getServiceUserMap());
        		}
			}catch (LoginException e){
				LOG.error("Error while getting Resource Resolver...."+e);
			}
			return resourceResolver;
		}
	}
	
	@Activate
	protected void activate(final ComponentContext componentContext) throws RepositoryException, org.apache.sling.api.resource.LoginException {
		LOG.info("JCR Util Service :: Activate Method Service");
		if(null!=slingRepository){
			repository=this.slingRepository;
			LOG.info("slingRepository Service is Initialized...");
		}
		if(null!=resolverFactory){
			resourceResolverFactory=this.resolverFactory;
			LOG.info("resourceResolverFactory Service is Initialized...");
		}
		if(null != this.replicator){
			ReplicatorProvider.getInstance().setReplicator(this.replicator);
			LOG.info("Replicator Service is Initialized...");
		}
		
	}
	/**
	 * Used to get the Session by passing UserName and Password
	 * @param userName
	 * @param password
	 * @return {@link Session} Object
	 * @throws RepositoryException
	 */
	
	@Deactivate
	protected void deactivate(final ComponentContext componentContext) {
        LOG.info("JCR Util Service :: Unregistering Service");
		try{
			if(null != resourceResolver){
				resourceResolver.close();
                resourceResolver=null;
				if((null != session) && session.isLive()){
	                session.logout();
	                session =null;
				}
			}
			if(null != this.replicator){
				ReplicatorProvider.getInstance().setReplicator(null);
			}
			System.gc();
		}catch (Exception e) {
            LOG.error("Error while clearing session or resourceResolver Objects"+e);
		}
	}

	public static SlingRepository getRepository() {
		return repository;
	}
}