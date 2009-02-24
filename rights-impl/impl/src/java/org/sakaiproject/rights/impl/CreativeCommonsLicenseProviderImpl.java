/**
 * 
 */
package org.sakaiproject.rights.impl;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ReferenceParseable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.rights.api.CreativeCommonsLicenseProvider;

/**
 * CreativeCommonsLicenseProviderImpl
 */
public class CreativeCommonsLicenseProviderImpl implements CreativeCommonsLicenseProvider, CoreEntityProvider, AutoRegisterEntityProvider, ReferenceParseable 
{

	public String getEntityPrefix() 
	{
		return CreativeCommonsLicenseProvider.ENTITY_PREFIX;
	}

	public boolean entityExists(String id) 
	{
		return true;
	}

	public EntityReference getParsedExemplar() 
	{
		
		return new CreativeCommonsLicenseReference();
	}

}
