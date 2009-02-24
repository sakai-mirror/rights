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

package org.sakaiproject.rights.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.rights.api.CreativeCommonsJurisdiction;
import org.sakaiproject.util.ResourceLoader;

/**
 * CreativeCommonsJurisdictionImpl
 *
 */
public class CreativeCommonsJurisdictionImpl implements CreativeCommonsJurisdiction 
{
	private static Log logger = LogFactory.getLog(CreativeCommonsLicenseImpl.class);
	
	protected static ResourceLoader rl = new ResourceLoader();
	
	protected String defaultLanguage;
	protected String jurisdictionSite;
	private String language;
	private Map<String, String> titles;

	private String uri;

	public CreativeCommonsJurisdictionImpl()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getDefaultLanguage()
	 */
	public String getDefaultLanguage() 
	{
		return this.defaultLanguage;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getJurisdictionSite()
	 */
	public String getJurisdictionSite() 
	{
		return this.jurisdictionSite;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getLanguage()
	 */
	public String getLanguage() 
	{
		return this.language;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getTitle()
	 */
	public String getTitle() 
	{
		Locale locale = rl.getLocale();
		String title = getTitle(locale);
		return title;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getTitle(java.util.Locale)
	 */
	public String getTitle(Locale locale) 
	{
		if(locale == null)
		{
			locale = locale.ENGLISH;
		}
		String language = locale.getLanguage();
		if(language == null)
		{
			language = locale.ENGLISH.getLanguage().toLowerCase();
		}
		else
		{
			language = language.toLowerCase();
		}
		String country = locale.getCountry();
		if(country == null)
		{
			country = "";
		}
		else
		{
			country = "-" + country.toLowerCase();
		}
		String title = this.titles.get(language + country);
		if(title == null)
		{
			title = this.titles.get(language);
			if(title == null)
			{
				title = this.titles.get(Locale.ENGLISH.getLanguage());
			}
		}
		return title;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getTitles()
	 */
	public Map<String, String> getTitles() 
	{
		return new HashMap(this.titles);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#getUri()
	 */
	public String getUri() 
	{
		return this.uri;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#setDefaultLanguage(java.lang.String)
	 */
	public void setDefaultLanguage(String defaultLanguage) 
	{
		this.defaultLanguage = defaultLanguage;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#setJurisdictionSite(java.lang.String)
	 */
	public void setJurisdictionSite(String uri) 
	{
		this.jurisdictionSite = uri;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language) 
	{
		this.language = language;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#setTitles(java.util.Map)
	 */
	public void setTitles(Map<String, String> titles) 
	{
		this.titles = new HashMap<String,String>(titles);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsJurisdiction#setUri(java.lang.String)
	 */
	public void setUri(String uri) 
	{
		this.uri = uri;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) 
	{
		int rv = 0;
		if(! (o instanceof CreativeCommonsJurisdiction))
		{
			throw new ClassCastException();
		}
		CreativeCommonsJurisdiction other = (CreativeCommonsJurisdiction) o;
		String title1 = this.getTitle();
		String title2 = other.getTitle();
		String uri1 = this.getUri();
		String uri2 = other.getUri();
		if(title1 != null && title2 != null  && title1.compareTo(title2) != 0)
		{
			rv = title1.compareTo(title2);
		}
		else if (uri1 != null && uri2 != null)
		{
			rv = uri1.compareTo(uri2);
		}
		else
		{
			throw new ClassCastException();
		}
			
		return rv;
	}

}
