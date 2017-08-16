package com.day.iotmarketplace.util;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

/**
 * The Class WorkflowProcessUtil.
 */
@Component(immediate = true, enabled = true)
@Service(WorkflowProcessUtil.class)
public class WorkflowProcessUtil {

	/**
	 * The logger.
	 */
	private final Logger LOG = LoggerFactory.getLogger(WorkflowProcessUtil.class);

	/**
	 * Gets the workflow meta data map.
	 *
	 * @param workItem
	 *            the work item
	 * @return the workflow meta data map
	 */
	public MetaDataMap getWorkflowMetaDataMap(WorkItem workItem) {

		LOG.debug("START OF getWorkflowMetaDataMap METHOD");
		MetaDataMap metaDataMap = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
		LOG.debug("END OF getWorkflowMetaDataMap METHOD");
		return metaDataMap;
	}

	/**
	 * Gets the payload.
	 *
	 * @param workItem
	 *            the work item
	 * @return the payload
	 */
	public String getPayload(WorkItem workItem) {
		return workItem.getWorkflowData().getPayload().toString();
	}

	/**
	 * Gets the workflow initiator profile property.
	 *
	 * @param authorizable
	 *            the authorizable
	 * @param profileProperty
	 *            the profile property
	 * @return the workflow initiator profile property
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public String getWorkflowInitiatorProfileProperty(Authorizable authorizable, String profileProperty)
			throws RepositoryException {

		LOG.debug("START OF getWorkflowInitiatorEmailId METHOD");
		String initiatorMailId = "";
		String profilePropertyName = "profile/" + profileProperty;
		if (authorizable != null) {
			initiatorMailId = PropertiesUtil.toString(authorizable.getProperty(profilePropertyName), "");
		}
		LOG.debug("END OF getWorkflowInitiatorEmailId METHOD");
		return initiatorMailId;
	}

	/**
	 * Gets the initiator.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param workItem
	 *            the work item
	 * @return the initiator
	 */
	public Authorizable getInitiator(ResourceResolver resourceResolver, WorkItem workItem) {
		LOG.debug("START OF getInitiator METHOD");

		Authorizable authorizable = null;
		try {
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);
			if (userManager != null) {
				authorizable = userManager.getAuthorizable(workItem.getWorkflow().getInitiator());
			}
		} catch (RepositoryException repositoryException) {
			LOG.error("Exception occured" + repositoryException, repositoryException);
		}

		LOG.debug("END OF getInitiator METHOD");
		return authorizable;
	}
}
