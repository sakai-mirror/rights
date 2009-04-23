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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.rights.api.CreativeCommonsLicense;
import org.sakaiproject.rights.api.CreativeCommonsLicenseManager;
import org.sakaiproject.rights.api.CreativeCommonsStringMapper;
import org.sakaiproject.rights.tool.params.LicenseViewParameters;
import org.sakaiproject.util.ResourceLoader;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UISelectLabel;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.components.decorators.UIStyleDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * LicenseDialogProducer
 *
 */
public class LicenseDialogProducer implements ViewComponentProducer, ContentTypeReporter
{
	protected static ResourceLoader rl = new ResourceLoader();
	static final Log logger = LogFactory.getLog(LicenseDialogProducer.class);
	
	public static final String VIEW_ID = "LicenseDialog";

	CreativeCommonsLicenseManager creativeCommonsLicenseManager;
	public void setCreativeCommonsLicenseManager(CreativeCommonsLicenseManager creativeCommonsLicenseManager)
	{
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

		licenses = this.creativeCommonsLicenseManager.getLicenses(version, jurisdiction, permits, prohibits, requires);
		CreativeCommonsStringMapper stringMapper = this.creativeCommonsLicenseManager.getStringMapper(CreativeCommonsLicenseManager.CC_LICENSE_CHOOSER);
		
		if(stringMapper == null || licenses == null)
		{
			// need to bail because we have no data
			// figure out how to present this to users
			logger.warn("insufficient data to show licenses: stringMapper == " + stringMapper + " licenses == " + licenses);
		}
		else
		{
			// render the license dialog
			UIBranchContainer basicModalContent = UIBranchContainer.make(tofill, "basicModalContent:");
			UIForm licenseSetterForm = UIForm.make(basicModalContent, "license-setter-form");
			UIMessage.make(licenseSetterForm, "dialog-title", "dialog.title");
			UIMessage.make(licenseSetterForm, "toggle-help", "toggle.help");
			
			
			UIBranchContainer licenseList = UIBranchContainer.make(licenseSetterForm, "licenseList:");
			
			SortedSet<CreativeCommonsLicense> licenseSet = new TreeSet<CreativeCommonsLicense>(licenses);
			for(CreativeCommonsLicense license : licenseSet)
			{
				UIBranchContainer licenseItem = UIBranchContainer.make(licenseList, "licenseItem:");
				licenseItem.decorate(new UIStyleDecorator(license.getIdentifier()));
				UIOutput licenseDescr = UIOutput.make(licenseItem, "licenseDescr", license.getTitle());
			}
			
			UIBranchContainer licenseHelper = UIBranchContainer.make(licenseSetterForm, "licenseHelper:");
			List<String> questionKeys = stringMapper.getQuestionKeys();
			for(String questionKey : questionKeys)
			{
				Map<String, String> responses = stringMapper.getResponses(questionKey);
				if(responses != null && responses.size() < CreativeCommonsStringMapper.USE_SELECT_ELEMENT)
				{
					UIBranchContainer questionDiv = UIBranchContainer.make(licenseHelper, "checkboxDiv:", questionKey);
					questionDiv.decorate(new UIStyleDecorator(questionKey));
					UIOutput question = UIOutput.make(questionDiv, "question", stringMapper.getLabel(questionKey));
					String descr = stringMapper.getDescription(questionKey);
					if(descr == null)
					{
						logger.debug("Description for " + questionKey + " is null");
					}
					else
					{
						question.decorate(new UITooltipDecorator(descr));
					}
					
					UIBranchContainer responseList = UIBranchContainer.make(questionDiv, "responses:");
					// UISelect responseList = UISelect.make(questionDiv, "responses:");
					for(String responseKey : responses.keySet())
					{
						UIBranchContainer responseItem = UIBranchContainer.make(questionDiv, "response:");
						//UISelectChoice radioButton = UISelectChoice.make(responseItem, ID, parentFullID, choiceindex)
						
						UIOutput responseRadio = UIOutput.make(responseItem, "responseRadio", responseKey);
						UIOutput responseLabel = UIOutput.make(responseItem, "responseLabel", responses.get(responseKey));
						responseLabel.decorate(new UIFreeAttributeDecorator("for", responseRadio.getFullID()));
					}
				}
				else
				{
					UIBranchContainer questionDiv = UIBranchContainer.make(licenseHelper, "selectDiv:", questionKey);
					questionDiv.decorate(new UIStyleDecorator(questionKey));
					UIOutput question = UIOutput.make(questionDiv, "question", stringMapper.getLabel(questionKey));
					String descr = stringMapper.getDescription(questionKey);
					if(descr == null)
					{
						logger.debug("Description for " + questionKey + " is null");
					}
					else
					{
						question.decorate(new UITooltipDecorator(descr));
					}
										
					String[] values = new String[responses.size()];
					String[] labels = new String[responses.size()];
					int i = 0;
					for(String responseKey : responses.keySet())
					{
						values[i] = responseKey;
						labels[i] = responses.get(responseKey);
						i++;
					}
					UISelect jurisdictionSelector = UISelect.make(questionDiv, "jurisdictionSelector", values, labels, "", false);
				}
			}
			
			UISelect licenseSelector = UISelect.make(licenseSetterForm, "license-choice-selector", new String[licenseSet.size()], new String[licenseSet.size()], null);
			List<String> values = new ArrayList<String>();
			List<String> labels = new ArrayList<String>();
			
			int choiceIndex = 0; 
			for(CreativeCommonsLicense license : licenseSet)
			{
				UIBranchContainer licenseChoice = UIBranchContainer.make(licenseSetterForm, "license-choice:");
				licenseChoice.decorate(new UIStyleDecorator(license.getIdentifier()));
				UISelectChoice licenseRadio = UISelectChoice.make(licenseChoice, "license-choice-radio", licenseSelector.getFullID(), choiceIndex);
				values.add(license.getIdentifier());
				UISelectLabel licenseLabel = UISelectLabel.make(licenseChoice, "license-choice-label", licenseSelector.getFullID(), choiceIndex);
				labels.add(license.getTitle());
				//licenseLabel.decorate(new UIFreeAttributeDecorator("for", licenseRadio.getFullID()));
				String imageURL = getImageURL(license.getIdentifier());
				if(imageURL != null)
				{
					UILink img = UILink.make(licenseChoice, "license-choice-img", imageURL);
				}
				choiceIndex++;
			}
			UIBranchContainer nolicenseChoice = UIBranchContainer.make(licenseSetterForm, "license-choice:");
			nolicenseChoice.decorate(new UIStyleDecorator("nolicense"));
			UISelectChoice nolicenseRadio = UISelectChoice.make(nolicenseChoice, "license-choice-radio", licenseSelector.getFullID(), choiceIndex);
			values.add("nolicense");
			UISelectLabel nolicenseLabel = UISelectLabel.make(nolicenseChoice, "license-choice-label", licenseSelector.getFullID(), choiceIndex);
			labels.add("No License");
			
			//nolicenseLabel.decorate(new UIFreeAttributeDecorator("for", nolicenseRadio.getFullID()));
			
			String[] valueArray = values.toArray(new String[values.size()]);
			licenseSelector.optionlist.setValue(valueArray);
			String[] labelArray = labels.toArray(new String[labels.size()]);
			licenseSelector.optionnames.setValue(labelArray );
			

		}

	}



	protected String getImageURL(String identifier) 
	{
		String url = null;
		if(identifier == null)
		{
			// do nothing
		}
		else if(identifier.startsWith("by_nc_nd"))
		{
			url = "/sakai-content-tool/images/attr_nocom_noder.png";
		}
		else if(identifier.startsWith("by_nc_sa"))
		{
			url = "/sakai-content-tool/images/attr_nocom_share.png";
		}
		else if(identifier.startsWith("by_nc"))
		{
			url = "/sakai-content-tool/images/attr_nocom.png";
		}
		else if(identifier.startsWith("by_nd"))
		{
			url = "/sakai-content-tool/images/attr_noder.png";
		}
		else if(identifier.startsWith("by_sa"))
		{
			url = "/sakai-content-tool/images/attr_share.png";
		}
		else if(identifier.startsWith("by"))
		{
			url = "/sakai-content-tool/images/attr.png";
		}
		else
		{
			// do nothing?
		}

		return url;
	}

	public String getContentType() 
	{
		return ContentTypeInfoRegistry.HTML_FRAGMENT;
	}

}
