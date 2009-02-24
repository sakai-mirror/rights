/**
 * 
 */
/**********************************************************************************
 * $URL:  $
 * $Id: $
 ***********************************************************************************
 *
 * Copyright (c) 2009 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.rights.tool.view;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.sakaiproject.rights.api.CreativeCommonsLicense;
import org.sakaiproject.rights.api.CreativeCommonsLicenseManager;
import org.sakaiproject.rights.tool.params.LicenseViewParameters;
import org.sakaiproject.util.ResourceLoader;

import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.view.DataInputHandler;
import uk.org.ponder.rsf.view.DataView;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

/**
 * @author jimeng
 *
 */
public class CreativeCommonsAssignmentView implements DataView, ViewParamsReporter, DataInputHandler 
{
	protected static ResourceLoader rl = new ResourceLoader();
	
	public static final String VIEW_ID = "LicenseDialogUpdate";

	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.view.ViewIDReporter#getViewID()
	 */
	public String getViewID() 
	{
		return VIEW_ID;
	}
	
	CreativeCommonsLicenseManager creativeCommonsLicenseManager;
	public void setCreativeCommonsLicense(CreativeCommonsLicenseManager creativeCommonsLicenseManager)
	{
		this.creativeCommonsLicenseManager = creativeCommonsLicenseManager;
	}

	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.view.DataView#getData(uk.org.ponder.rsf.viewstate.ViewParameters)
	 */
	public Object getData(ViewParameters viewparams) 
	{
		String uri = null;
		String version = null;
		String jurisdiction = null;
		Set<String> permits = null;
		Set<String> prohibits = null;
		Set<String> requires = null;
		
		if(viewparams instanceof LicenseViewParameters)
		{
			LicenseViewParameters lvp = (LicenseViewParameters) viewparams;
			uri = lvp.uri;
			version = lvp.version;
			jurisdiction = lvp.jurisdiction;
		}
		
		System.out.print(this + ".getData(" + viewparams + ")");

		JSONObject json = new JSONObject();
		
		Collection<CreativeCommonsLicense> licenses = null;
		if(uri == null)
		{
			if(version == null)
			{
				version = CreativeCommonsLicenseManager.LATEST_VERSION;
			}
			if(jurisdiction == null)
			{
				jurisdiction = CreativeCommonsLicenseManager.DEFAULT_JURISDICTION;
			}
			licenses = this.creativeCommonsLicenseManager.getLicenses(version, jurisdiction, permits, prohibits, requires);
		}
		
		if(licenses != null)
		{
			JSONArray array = new JSONArray();
			SortedSet<CreativeCommonsLicense> licenseSet = new TreeSet<CreativeCommonsLicense>(licenses);
			for(CreativeCommonsLicense license : licenseSet)
			{
				array.add(license.toJSON(rl.getLocale()));
			}
			json.element("licenses", array);
		}
		
		Map<String,String> jurisdictions = this.creativeCommonsLicenseManager.getJurisdictions();
		
		return json.toString();
	}

	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.content.ContentTypeReporter#getContentType()
	 */
	public String getContentType() 
	{
		return ContentTypeInfoRegistry.JSON;
	}

	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.viewstate.ViewParamsReporter#getViewParameters()
	 */
	public ViewParameters getViewParameters() 
	{
		return new LicenseViewParameters(VIEW_ID);
	}

	public String getHandledMethods() 
	{
		return "post, put";
	}

	public void handleInput(ViewParameters viewparams, String method, Object data) 
	{
		System.out.print(this + ".handleInput(" + viewparams + "," + method + "," + data + ")");
	}

}
