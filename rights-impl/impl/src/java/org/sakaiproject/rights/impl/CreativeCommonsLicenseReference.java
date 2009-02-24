/**
 * 
 */
package org.sakaiproject.rights.impl;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rights.api.CreativeCommonsLicenseProvider;

/**
 * 
 *
 */
public class CreativeCommonsLicenseReference extends EntityReference 
{
	protected String makeEntityId()
	{
		return null;
	}
	
	protected String makeEntityPrefix()
	{
		return CreativeCommonsLicenseProvider.ENTITY_PREFIX;
	}
	
	protected String makeEntityReference(boolean spaceOnly)
	{
		String rv = null;
		
		if(spaceOnly)
		{
			rv = "/" + CreativeCommonsLicenseProvider.ENTITY_PREFIX + "/LicenseDialog.html" ;
		}
		
		return rv;
	}
}
