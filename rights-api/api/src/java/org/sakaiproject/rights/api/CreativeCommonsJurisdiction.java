/**********************************************************************************
 * $URL:  $
 * $Id:  $
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

package org.sakaiproject.rights.api;

import java.util.Locale;
import java.util.Map;

/**
 * CreativeCommonsJurisdiction
 *
 */
public interface CreativeCommonsJurisdiction 
{
	public String getUri();
	public void setUri(String uri);
	
	public String getLanguage();
	public void setLanguage(String language);
	
	public String getDefaultLanguage();
	public void setDefaultLanguage(String language);
	
	public String getJurisdictionSite();
	public void setJurisdictionSite(String uri);
	
	public Map<String, String> getTitles();
	public void setTitles(Map<String, String> titles);
	
	public String getTitle();
	public String getTitle(Locale locale);
}
