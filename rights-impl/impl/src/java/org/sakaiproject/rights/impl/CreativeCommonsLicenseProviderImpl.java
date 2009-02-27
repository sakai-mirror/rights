/**
 * 
 */
package org.sakaiproject.rights.impl;

import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.rights.api.CreativeCommonsLicenseProvider;

/**
 * CreativeCommonsLicenseProviderImpl
 */
public class CreativeCommonsLicenseProviderImpl implements CreativeCommonsLicenseProvider, AutoRegisterEntityProvider, CoreEntityProvider 
{

	/* (non-Javadoc)
	 * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
	 */
	public String getEntityPrefix() 
	{
		return CreativeCommonsLicenseProvider.ENTITY_PREFIX;
	}

	public boolean entityExists(String id) 
	{
		return true;
	}

}
