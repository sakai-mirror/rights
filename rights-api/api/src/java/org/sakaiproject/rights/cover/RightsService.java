package org.sakaiproject.rights.cover;

import org.sakaiproject.component.cover.ComponentManager;

public class RightsService implements org.sakaiproject.rights.api.RightsService 
{
	protected static org.sakaiproject.rights.api.RightsService m_instance = null;
	
	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.rights.api.RightsService getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
				m_instance = (org.sakaiproject.rights.api.RightsService) ComponentManager
						.get(org.sakaiproject.rights.api.RightsService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.rights.api.RightsService) ComponentManager
					.get(org.sakaiproject.rights.api.RightsService.class);
		}
	}


	public org.sakaiproject.rights.api.RightsAssignment addRightsAssignment(String entityRef) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.addRightsAssignment(entityRef);
	}

	public org.sakaiproject.rights.api.SiteRightsPolicy addSiteRightsPolicy(String context) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.addSiteRightsPolicy(context);
	}

	public org.sakaiproject.rights.api.UserRightsPolicy addUserRightsPolicy(String context, String userId) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.addUserRightsPolicy(context, userId);
	}

	public org.sakaiproject.rights.api.RightsAssignment getRightsAssignment(String entityRef) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.getRightsAssignment(entityRef);
	}

	public org.sakaiproject.rights.api.SiteRightsPolicy getSiteRightsPolicy(String context) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.getSiteRightsPolicy(context);
	}

	public org.sakaiproject.rights.api.UserRightsPolicy getUserRightsPolicy(String context, String userId) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return null;

		return service.getUserRightsPolicy(context, userId);
	}

	public void save(org.sakaiproject.rights.api.RightsAssignment rights) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return;

		service.save(rights);
	}

	public void save(org.sakaiproject.rights.api.RightsPolicy policy) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return;

		service.save(policy);
	}

	public void setRightsAssignment(String entityRef, org.sakaiproject.rights.api.RightsAssignment rights) 
	{
		org.sakaiproject.rights.api.RightsService service = getInstance();
		if (service == null) return;

		service.setRightsAssignment(entityRef, rights);
	}

}
