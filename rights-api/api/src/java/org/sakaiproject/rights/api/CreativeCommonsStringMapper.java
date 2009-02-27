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

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CreativeCommonsQuestion
 *
 */
public interface CreativeCommonsStringMapper 
{
	/**
	 * Access the name of the license class ("Creative Commons"). The user's default locale will be used if it's available.
	 * @return
	 */
	public String getCreativeCommonsLabel();
	
	/**
	 * Access the name of the license class ("Creative Commons") by locale.
	 * @return
	 */
	public String getCreativeCommonsLabel(Locale locale);
	
	/**
	 * Access a description of a question by key.  The user's default locale will be used if it's available.
	 * @param key
	 * @return
	 */
	public String getDescription(String key);
	
	/**
	 * Access a description of a question by key and locale.
	 * @param key
	 * @param locale
	 * @return
	 */
	public String getDescription(String key, Locale locale);
	
	/**
	 * Access a label for a question by key.  The user's default locale will be used if it's available.
	 * @param key
	 * @return
	 */
	public String getLabel(String key);
	
	/**
	 * Access a label for a question by key and locale.
	 * @param key
	 * @param locale
	 * @return
	 */
	public String getLabel(String key, Locale locale);
	
	/**
	 * @return
	 */
	public List<String> getQuestionKeys();
	
	/**
	 * @param key
	 * @return
	 */
	public Map<String,String> getResponses(String key);
	
	/**
	 * @param key
	 * @param locale
	 * @return
	 */
	public Map<String,String> getResponses(String key, Locale locale);
	
}
