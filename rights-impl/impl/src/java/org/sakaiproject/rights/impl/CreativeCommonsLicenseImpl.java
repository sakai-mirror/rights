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

package org.sakaiproject.rights.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.rights.api.CreativeCommonsLicense;
import org.sakaiproject.rights.api.CreativeCommonsLicenseManager;
import org.sakaiproject.rights.util.RightsException;
import org.sakaiproject.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreativeCommonsLicenseImpl implements CreativeCommonsLicense
{
	private static Log logger = LogFactory.getLog(CreativeCommonsLicenseImpl.class);
	
	protected static ResourceLoader rl = new ResourceLoader();
	
	protected static String baseURL = "http://creativecommons.org/licenses/";
	protected String uri;
	protected String source;
	protected String replacedBy;
	
	protected String identifier;
	protected String version;
	protected String jurisdiction;
	
	protected String creator;
	protected String legalcode;
	
	protected Set<String> permissions = new TreeSet<String>();
	protected Set<String> prohibitions = new TreeSet<String>();
	protected Set<String> requirements = new TreeSet<String>();

	protected Map<String, String> descriptions = new HashMap<String, String>();
	protected Map<String, String> titles = new HashMap<String, String>();


	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addDescriptions(java.util.Map)
	 */
	public void addDescriptions(Map<String, String> descriptions) 
	{
		this.descriptions.putAll(descriptions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addPermission(java.lang.String)
	 */
	public void addPermission(String permission) 
	{
		this.permissions.add(permission);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addPermissions(java.util.Set)
	 */
	public void addPermissions(Set<String> permissions) 
	{
		this.permissions.addAll(permissions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addProhibition(java.lang.String)
	 */
	public void addProhibition(String prohibition) 
	{
		this.prohibitions.add(prohibition);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addProhibitions(java.util.Set)
	 */
	public void addProhibitions(Set<String> prohibitions) 
	{
		this.prohibitions.addAll(prohibitions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addRequirement(java.lang.String)
	 */
	public void addRequirement(String requirement) 
	{
		this.requirements.add(requirement);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addRequirements(java.util.Set)
	 */
	public void addRequirements(Set<String> requirements) 
	{
		this.requirements.addAll(requirements);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#addTitles(java.util.Map)
	 */
	public void addTitles(Map<String, String> titles) 
	{
		this.titles.putAll(titles);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getCreator()
	 */
	public String getCreator() 
	{
		return this.creator;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getDescription()
	 */
	public String getDescription() 
	{
		Locale locale = rl.getLocale();
		String description = getDescription(locale);
		return description;
	}

	/**
	 * @param locale
	 * @return
	 */
	protected String getDescription(Locale locale) 
	{
		if(locale == null)
		{
			locale = Locale.ENGLISH;
		}
		String language = locale.getLanguage();
		if(language == null)
		{
			language = Locale.ENGLISH.getLanguage().toLowerCase();
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
		String description = this.descriptions.get(language + country);
		if(description == null)
		{
			description = this.descriptions.get(language);
			if(description == null)
			{
				description = this.descriptions.get(Locale.ENGLISH.getLanguage());
			}
		}
		return description;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getDescriptions()
	 */
	public Map<String, String> getDescriptions() 
	{
		return new HashMap<String, String>(this.descriptions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getIdentifier()
	 */
	public String getIdentifier() 
	{
		return this.identifier;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getJurisdiction()
	 */
	public String getJurisdiction() 
	{
		return this.jurisdiction;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getLegalcode()
	 */
	public String getLegalcode() 
	{
		return this.legalcode;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getPermissions()
	 */
	public Collection<String> getPermissions() 
	{
		return new TreeSet<String>(permissions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getProhibitions()
	 */
	public Collection<String> getProhibitions() 
	{
		return new TreeSet<String>(prohibitions);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getReplacedBy()
	 */
	public String getReplacedBy() 
	{
		return this.replacedBy;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getRequirements()
	 */
	public Collection<String> getRequirements() 
	{
		return new TreeSet<String>(requirements);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getSource()
	 */
	public String getSource() 
	{
		return this.source;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getTitle()
	 */
	public String getTitle() 
	{
		Locale locale = rl.getLocale();
		String title = getTitle(locale);
		return title;
	}

	/**
	 * @param locale
	 * @return
	 */
	protected String getTitle(Locale locale) 
	{
		if(locale == null)
		{
			locale = Locale.ENGLISH;
		}
		String language = locale.getLanguage();
		if(language == null)
		{
			language = Locale.ENGLISH.getLanguage().toLowerCase();
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
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getTitles()
	 */
	public Map<String, String> getTitles() 
	{
		return new HashMap<String, String>(this.titles);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getUri()
	 */
	public String getUri() 
	{
		return this.uri;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#getVersion()
	 */
	public String getVersion() 
	{
		return this.version;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#hasPermissions()
	 */
	public boolean hasPermissions() 
	{
		return permissions != null && ! permissions.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#hasProhibitions()
	 */
	public boolean hasProhibitions() 
	{
		return prohibitions != null && ! prohibitions.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#hasRequirements()
	 */
	public boolean hasRequirements() 
	{
		return requirements != null && ! requirements.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#isReplaced()
	 */
	public boolean isReplaced() 
	{
		return this.replacedBy != null && ! this.replacedBy.equals("");
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setCreator(java.lang.String)
	 */
	public void setCreator(String creator) 
	{
		this.creator = creator;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setJurisdiction(java.lang.String)
	 */
	public void setJurisdiction(String jurisdiction) 
	{
		this.jurisdiction = jurisdiction;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setLegalcode(java.lang.String)
	 */
	public void setLegalcode(String legalcode) 
	{
		this.legalcode = legalcode;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setReplacedBy(java.lang.String)
	 */
	public void setReplacedBy(String replacedBy) 
	{
		this.replacedBy = replacedBy;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setSource(java.lang.String)
	 */
	public void setSource(String source) 
	{
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setUri(java.lang.String)
	 */
	public void setUri(String uri) 
	{
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#setVersion(java.lang.String)
	 */
	public void setVersion(String version) 
	{
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#toJSON()
	 */
	public String toJSON() 
	{
		JSONObject json = getJSONObject();
		
		if(this.titles != null && ! this.titles.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.DC_TITLE, this.titles);
		}
		if(this.descriptions != null && ! this.descriptions.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.DC_DESCRIPTION, this.descriptions);
		}
		
		return json.toString();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#toJSON(java.util.Locale)
	 */
	public String toJSON(Locale locale) 
	{
		JSONObject json = getJSONObject();
		
		if(this.titles != null && ! this.titles.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.DC_TITLE, getTitle(locale));
		}
		if(this.descriptions != null && ! this.descriptions.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.DC_DESCRIPTION, getDescription(locale));
		}
		
		
		return json.toString();
	}

	/**
	 * @return
	 */
	protected JSONObject getJSONObject() 
	{
		JSONObject json = new JSONObject();
		
		json.element(CreativeCommonsLicenseManager.RDF_ABOUT, this.uri);
		
		if(this.creator != null && ! this.creator.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.DC_CREATOR, this.creator);
		}
		if(this.jurisdiction != null && ! this.jurisdiction.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.CC_JURISDICTION, this.jurisdiction);
		}
		if(this.legalcode != null && ! this.legalcode.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.CC_LEGALCODE, this.legalcode);
		}
		if(this.replacedBy != null && ! this.replacedBy.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.DCQ_IS_REPLACED_BY, this.replacedBy);
		}
		if(this.source != null && ! this.source.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.DC_SOURCE, this.source);
		}
		if(this.version != null && ! this.version.trim().equals(""))
		{
			json.element(CreativeCommonsLicenseManager.DCQ_HAS_VERSION, this.version);
		}
		if(this.permissions != null && ! this.permissions.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.CC_PERMITS, this.permissions);
		}
		if(this.prohibitions != null && ! this.prohibitions.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.CC_PROHIBITS, this.prohibitions);
		}
		if(this.requirements != null && ! this.requirements.isEmpty())
		{
			json.element(CreativeCommonsLicenseManager.CC_REQUIRES, this.requirements);
		}
		return json;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicense#fromJSON(java.lang.String)
	 */
	public void fromJSON(String jsonStr)
	{
		JSONObject json = JSONObject.fromObject(jsonStr);
		try
		{
			if(json.containsKey(CreativeCommonsLicenseManager.RDF_ABOUT))
			{
				this.uri = json.getString(CreativeCommonsLicenseManager.RDF_ABOUT);
				
				if(json.containsKey(CreativeCommonsLicenseManager.DC_CREATOR))
				{
					this.creator = json.getString(CreativeCommonsLicenseManager.DC_CREATOR);
				}
				
				if(json.containsKey(CreativeCommonsLicenseManager.CC_JURISDICTION))
				{
					this.jurisdiction = json.getString(CreativeCommonsLicenseManager.CC_JURISDICTION);
				}

				if(json.containsKey(CreativeCommonsLicenseManager.CC_LEGALCODE))
				{
					this.legalcode = json.getString(CreativeCommonsLicenseManager.CC_LEGALCODE);
				}

				if(json.containsKey(CreativeCommonsLicenseManager.DCQ_IS_REPLACED_BY))
				{
					this.replacedBy = json.getString(CreativeCommonsLicenseManager.DCQ_IS_REPLACED_BY);
				}

				if(json.containsKey(CreativeCommonsLicenseManager.DC_SOURCE))
				{
					this.source = json.getString(CreativeCommonsLicenseManager.DC_SOURCE);
				}

				if(json.containsKey(CreativeCommonsLicenseManager.DCQ_HAS_VERSION))
				{
					this.version = json.getString(CreativeCommonsLicenseManager.DCQ_HAS_VERSION);
				}

				if(json.containsKey(CreativeCommonsLicenseManager.CC_PERMITS))
				{
					JSONArray jsonlist = json.getJSONArray(CreativeCommonsLicenseManager.CC_PERMITS);
					for(int i = 0; i < jsonlist.size(); i++)
					{
						try
						{
							this.permissions.add(jsonlist.getString(i));
						}
						catch(JSONException e)
						{
							logger.debug("Problem getting permission at index " + i + " from JSON array: \n" + jsonlist.toString() + "\nuri " + this.uri);
						}
					}
				}
				
				if(json.containsKey(CreativeCommonsLicenseManager.CC_PROHIBITS))
				{
					JSONArray jsonlist = json.getJSONArray(CreativeCommonsLicenseManager.CC_PROHIBITS);
					for(int i = 0; i < jsonlist.size(); i++)
					{
						try
						{
							this.prohibitions.add(jsonlist.getString(i));
						}
						catch(JSONException e)
						{
							logger.debug("Problem getting prohibition at index " + i + " from JSON array: \n" + jsonlist.toString() + "\nuri " + this.uri);
						}
					}
				}
				
				if(json.containsKey(CreativeCommonsLicenseManager.CC_REQUIRES))
				{
					JSONArray jsonlist = json.getJSONArray(CreativeCommonsLicenseManager.CC_REQUIRES);
					for(int i = 0; i < jsonlist.size(); i++)
					{
						try
						{
							this.requirements.add(jsonlist.getString(i));
						}
						catch(JSONException e)
						{
							logger.debug("Problem getting requirement at index " + i + " from JSON array: \n" + jsonlist.toString() + "\nuri " + this.uri);
						}
					}
				}
				
				if(json.containsKey(CreativeCommonsLicenseManager.DC_TITLE))
				{
					JSONObject jsonobj = json.getJSONObject(CreativeCommonsLicenseManager.DC_TITLE);
					
					for(String key : (Set<String>) jsonobj.keySet())
					{
						try
						{
							this.titles.put(key, jsonobj.getString(key));
						}
						catch(JSONException e)
						{
							logger.debug("Problem getting title for key " + key + " for uri " + this.uri + " JSON for titles:\n" + jsonobj.toString());
						}
					}
				}
				
				if(json.containsKey(CreativeCommonsLicenseManager.DC_DESCRIPTION))
				{
					JSONObject jsonobj = json.getJSONObject(CreativeCommonsLicenseManager.DC_DESCRIPTION);
					
					for(String key : (Set<String>) jsonobj.keySet())
					{
						try
						{
							this.descriptions.put(key, jsonobj.getString(key));
						}
						catch(JSONException e)
						{
							logger.debug("Problem getting description for key " + key + " for uri " + this.uri + " JSON for titles:\n" + jsonobj.toString());
						}
					}
				}
			}
			else
			{
				logger.warn("No uri in json string:\n" + jsonStr);
			}
			
		}
		catch(JSONException e)
		{
			logger.warn("Error processing license from json:\n" + jsonStr, e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) 
	{
		int rv = 0;
		if(! (o instanceof CreativeCommonsLicense))
		{
			throw new ClassCastException();
		}
		CreativeCommonsLicense other = (CreativeCommonsLicense) o;
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