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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * CreativeCommonsLicenseManager
 *
 */
public interface CreativeCommonsLicenseManager 
{
	public static final String LATEST_VERSION = "LATEST_VERSION";
	public static final String DEFAULT_JURISDICTION = "unported";
	public static final String CC_LICENSE_CHOOSER = "cc-license-chooser";

	/**
	 * 
	 * @param version A Creative Commons version (e.g. "3.0", "2.5"), or LATEST_VERSION 
	 * 		for the most recent version for which at least one license matches the other 
	 * 		criteria, or null for all versions. 
	 * @param jurisdition The jurisdiction using one of the jurisdiction codes at 
	 * 		http://creativecommons.org/international/ (e.g. http://creativecommons.org/international/us/
	 * 		or http://creativecommons.org/international/it/). For the unported license, use 
	 * 		DEFAULT_JURISDICTION as the jurisdiction.  For all jurisdictions, use null as the
	 * 		jurisdiction.
	 * @param permits A set of string identifiers for Creative Commons permissions (actual URLs). 
	 * 		Or null to specify no filtering by permissions.
	 * @param prohibits A set of string identifiers for Creative Commons prohibitions (actual URLs)
	 * 		Or null to specify no filtering by prohibitions.
	 * @param requires A set of string identifiers for Creative Commons requirements (actual URLs)
	 * 		Or null to specify no filtering by requirements.
	 * @return
	 */
	public Collection<CreativeCommonsLicense> getLicenses(String version, String jurisdiction, Set<String> permits, Set<String> prohibits, Set<String> requires);

	/**
	 * @param locale
	 * @return
	 */
	public java.util.Map<String, String> getJurisdictions();

	/**
	 * @param ccLicenseChooser
	 * @return
	 */
	public CreativeCommonsStringMapper getStringMapper(String ccLicenseChooser);

}
