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

package org.sakaiproject.rights.tool.producers;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.json.JSONArray;

import org.sakaiproject.rights.api.CreativeCommonsLicense;
import org.sakaiproject.rights.api.CreativeCommonsLicenseManager;
import org.sakaiproject.rights.tool.params.LicenseViewParameters;
import org.sakaiproject.util.ResourceLoader;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * LicenseDialogProducer
 *
 */
public class LicenseDialogProducer implements ComponentProducer, ContentTypeReporter 
{
	protected static ResourceLoader rl = new ResourceLoader();
	
	public static final String VIEW_ID = "LicenseDialog";

	CreativeCommonsLicenseManager creativeCommonsLicenseManager;
	public void setCreativeCommonsLicenseManager(CreativeCommonsLicenseManager creativeCommonsLicenseManager)
	{
		System.out.print(this + "creativeCommonsLicenseManager(" + creativeCommonsLicenseManager + ")");
		this.creativeCommonsLicenseManager = creativeCommonsLicenseManager;
	}

	/**
	 * @return
	 */
	public String getViewID() 
	{
		return VIEW_ID;
	}
	


	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.view.ComponentProducer#fillComponents(uk.org.ponder.rsf.components.UIContainer, uk.org.ponder.rsf.viewstate.ViewParameters, uk.org.ponder.rsf.view.ComponentChecker)
	 */
	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) 
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
		}

		System.out.print(this + ".fillComponents(" + viewparams + ") uri == " + uri + " version == " + version + " jurisdiction == " + jurisdiction);

		licenses = this.creativeCommonsLicenseManager.getLicenses(version, jurisdiction, permits, prohibits, requires);

		if(licenses != null)
		{
			UIBranchContainer licenseDiv = UIBranchContainer.make(tofill, "licenseInfo:");
			UIBranchContainer licenseList = UIBranchContainer.make(licenseDiv, "licenseList:");
			
			SortedSet<CreativeCommonsLicense> licenseSet = new TreeSet<CreativeCommonsLicense>(licenses);
			for(CreativeCommonsLicense license : licenseSet)
			{
				String descr = license.getDescription();
				UIOutput licenseItem = UIOutput.make(licenseList, "licenseItem", (descr == null || descr.trim().equals("") ? license.getTitle() : descr));
			}
		}
		
		Map<String,String> jurisdictions = this.creativeCommonsLicenseManager.getJurisdictions();
		if(jurisdictions != null && ! jurisdictions.isEmpty())
		{
			String[] options = new String[jurisdiction.length() + 1];
			String[] labels = new String[jurisdiction.length() + 1];
			options[0] = "unported";
			// need to get this from bundle
			labels[0] = "Unported";
			
			int index = 1;
			for(String title : jurisdictions.keySet())
			{
				String value = jurisdictions.get(title);
				options[index] = value;
				labels[index] = title;
				index++;
			}
			
			String initvalue = "unported";
			String valuebinding = null;
			UISelect jurisdictionSelector = UISelect.make(tofill, "jurisdictionSelector", options, labels, valuebinding, initvalue);
		}

	}



	public String getContentType() 
	{
		return ContentTypeInfoRegistry.HTML_FRAGMENT;
	}

}
