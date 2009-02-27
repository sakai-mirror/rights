package org.sakaiproject.rights.tool.infer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.rights.api.CreativeCommonsLicenseProvider;
import org.sakaiproject.rights.tool.params.LicenseViewParameters;
import org.sakaiproject.rights.tool.producers.LicenseDialogProducer;

import uk.ac.cam.caret.sakai.rsf.entitybroker.EntityViewParamsInferrer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class CreativeCommonsLicenseViewParamsInferrer implements EntityViewParamsInferrer
{
	private static Log logger = LogFactory.getLog(CreativeCommonsLicenseViewParamsInferrer.class);

	public String[] getHandledPrefixes() 
	{
		return new String[] { CreativeCommonsLicenseProvider.ENTITY_PREFIX };
	}

	public ViewParameters inferDefaultViewParameters(String reference) 
	{
		return new LicenseViewParameters(LicenseDialogProducer.VIEW_ID);
	}
	
	public void init()
	{
		logger.info("init");
	}

}
