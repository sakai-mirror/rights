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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.rights.api.CreativeCommonsStringMapper;
import org.sakaiproject.util.ResourceLoader;

/**
 * CreativeCommonsQuestionImpl
 *
 */
public class CreativeCommonsStringMapperImpl implements CreativeCommonsStringMapper 
{
	private static Log logger = LogFactory.getLog(CreativeCommonsStringMapperImpl.class);
	
	// locale --> label
	private Map<String, String> ccLabelMap = new HashMap<String,String>();
	
	// questionKey --> locale --> description
	protected Map<String, Map<String, String>> descriptions = new HashMap<String, Map<String, String>>();
	
	// questionKey --> locale --> label
	protected Map<String, Map<String, String>> labels = new HashMap<String, Map<String, String>>();

	// questionKey --> locale --> responseKey --> responseLabel
	protected Map<String, Map<String, Map<String, String>>> responses = new HashMap<String,Map<String, Map<String, String>>>();
	

	public void addCreativeCommonsLabels(Map<String, String> labelMap) 
	{
		this.ccLabelMap = labelMap;
	}

	/**
	 * @param questionKey
	 * @param descriptionTranslations
	 */
	public void addDescriptions(String questionKey, Map<String, String> descriptionTranslations)
	{
		// questionKey --> locale --> description
		if(questionKey == null)
		{
			logger.warn("Cannot use null key. Question Key: \"" + questionKey + "\"");
		}
		else
		{
			Map<String, String> mapByQuestion = this.descriptions.get(questionKey);
			if(mapByQuestion == null)
			{
				mapByQuestion = new HashMap<String,String>();
				this.descriptions.put(questionKey, mapByQuestion);
			}
			
			// isn't this just MAP.putAll()?
			for(String localeId : descriptionTranslations.keySet())
			{
				mapByQuestion.put(localeId, descriptionTranslations.get(localeId));
			}
		}
	}

	/**
	 * @param questionKey
	 * @param labelTranslations
	 */
	public void addLabels(String questionKey, Map<String, String> labelTranslations)
	{
		// questionKey --> locale --> label
		if(questionKey == null)
		{
			logger.warn("Cannot use null key. Question Key: \"" + questionKey + "\"");
		}
		else
		{
			Map<String, String> mapByQuestion = this.labels.get(questionKey);
			if(mapByQuestion == null)
			{
				mapByQuestion = new HashMap<String,String>();
				this.labels.put(questionKey, mapByQuestion);
			}
			
			// isn't this just MAP.putAll()?
			for(String localeId : labelTranslations.keySet())
			{
				mapByQuestion.put(localeId, labelTranslations.get(localeId));
			}
			
		}
	}

	/**
	 * @param questionKey
	 * @param responseMap
	 */
	public void addResponses(String questionKey, String responseKey, Map<String, String> responseMap)
	{
		// questionKey --> locale --> responseKey --> responseLabel
		if(questionKey == null || responseKey == null)
		{
			logger.warn("Cannot use null key. Question Key: \"" + questionKey + "\" Response Key: \"" + responseKey + "\"");
		}
		else
		{
			Map<String,Map<String, String>> mapByQuestion = this.responses.get(questionKey);
			if(mapByQuestion == null)
			{
				mapByQuestion = new HashMap<String,Map<String, String>>();
				this.responses.put(questionKey, mapByQuestion);
			}
			
			for(String localeId : responseMap.keySet())
			{
				Map<String, String> mapByLocale = mapByQuestion.get(localeId);
				
				if(responses == null)
				{
					mapByLocale = new HashMap<String,String>();
					mapByQuestion.put(localeId, mapByLocale);
				}
				
				mapByLocale.put(responseKey, responseMap.get(localeId));
			}
		}
	}
	
	public String getCreativeCommonsLabel() 
	{
		ResourceLoader rl = new ResourceLoader();
		return this.getCreativeCommonsLabel(rl.getLocale());
	}

	public String getCreativeCommonsLabel(Locale locale) 
	{
		String label = null;
		
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
		label = this.ccLabelMap.get(language + country);
		if(label == null)
		{
			label = this.ccLabelMap.get(language);
			
			if(label == null)
			{
				label = this.ccLabelMap.get(Locale.ENGLISH.getLanguage());
			}
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getDescription(java.lang.String)
	 */
	public String getDescription(String key) 
	{
		ResourceLoader rl = new ResourceLoader();
		return getDescription(key, rl.getLocale());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getDescription(java.lang.String, java.util.Locale)
	 */
	public String getDescription(String key, Locale locale) 
	{
		String description = "";
		if(key == null)
		{
			logger.debug("Can not find description with null key");
		}
		else
		{
			Map<String, String> translations = this.descriptions.get(key);
			if(key == null)
			{
				translations = new HashMap<String, String>();
				this.descriptions.put(key, translations);
				
			}
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
			description = translations.get(language + country);
			if(description == null)
			{
				description = translations.get(language);
				if(description == null)
				{
					description = translations.get(Locale.ENGLISH.getLanguage());
				}
			}
		}
		return description;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getLabel(java.lang.String)
	 */
	public String getLabel(String key) 
	{
		ResourceLoader rl = new ResourceLoader();
		return getLabel(key, rl.getLocale());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getLabel(java.lang.String, java.util.Locale)
	 */
	public String getLabel(String key, Locale locale) 
	{
		String label = "";
		if(key == null)
		{
			logger.debug("Can not find label with null key");
		}
		else
		{
			Map<String, String> translations = this.labels.get(key);
			if(key == null)
			{
				translations = new HashMap<String, String>();
				this.labels.put(key, translations);
				
			}
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
			label = translations.get(language + country);
			if(label == null)
			{
				label = translations.get(language);
				if(label == null)
				{
					label = translations.get(Locale.ENGLISH.getLanguage());
				}
			}
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getQuestionKeys()
	 */
	public List<String> getQuestionKeys() 
	{
		List<String> keys = new ArrayList<String>();
		if(this.responses != null)
		{
			keys.addAll(this.responses.keySet());
		}
		return  keys;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getResponseMap(java.lang.String)
	 */
	public Map<String, String> getResponses(String key) 
	{
		ResourceLoader rl = new ResourceLoader();
		Locale locale = rl.getLocale();
		return this.getResponses(key, locale);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsQuestion#getResponses(java.lang.String, java.util.Locale)
	 */
	public Map<String, String> getResponses(String key, Locale locale) 
	{
		Map<String, String> responses = new HashMap<String, String>();
		
		if(key == null)
		{
			logger.debug(this + ".getResponses() Cannot find null question key");
		}
		else
		{
		
			Map<String, Map<String, String>> translations = this.responses.get(key);
			
			if(translations == null || translations.isEmpty())
			{
				;
			}
			
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
			Map<String, String> descriptionMap = translations.get(language + country);
			if(descriptionMap == null || descriptionMap.isEmpty())
			{
				descriptionMap = translations.get(language);
				if(descriptionMap == null || descriptionMap.isEmpty())
				{
					descriptionMap = translations.get(Locale.ENGLISH.getLanguage());
				}
			}
			if(descriptionMap != null)
			{
				responses.putAll(descriptionMap);
			}
		}
		return  responses ;
	}

}
