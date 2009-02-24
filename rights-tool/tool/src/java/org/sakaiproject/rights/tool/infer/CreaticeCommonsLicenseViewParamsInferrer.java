package org.sakaiproject.rights.tool.infer;

import org.sakaiproject.rights.api.CreativeCommonsLicenseProvider;
import org.sakaiproject.rights.tool.params.LicenseViewParameters;
import org.sakaiproject.rights.tool.producers.LicenseDialogProducer;

import uk.ac.cam.caret.sakai.rsf.entitybroker.EntityViewParamsInferrer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class CreaticeCommonsLicenseViewParamsInferrer implements EntityViewParamsInferrer
{

	public String[] getHandledPrefixes() 
	{
		return new String[] { CreativeCommonsLicenseProvider.ENTITY_PREFIX };
	}

	public ViewParameters inferDefaultViewParameters(String reference) 
	{
		return new LicenseViewParameters(LicenseDialogProducer.VIEW_ID);
	}

}
