/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/content/trunk/content-tool/tool/src/java/org/sakaiproject/content/tool/ResourcesAction.java $
 * $Id: ResourcesAction.java 13885 2006-08-21 16:03:28Z jimeng@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
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
import java.util.Stack;

import org.sakaiproject.rights.util.RightsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Creative Commons Licenses are described by their characteristics, which come in three types:
 * 
 *	Permissions (rights granted by the license)
 *	Prohibitions (things prohibited by the license)
 *	Requirements (restrictions imposed by the license)
 *
  */
public interface CreativeCommonsLicense 
{
	public String getIdentifier();
	
	public String getUri();
	
	/*****************************************************
	 * Permissions
	 *****************************************************/
	
	/**
	 * @return
	 */
	public boolean hasPermissions();
	
	/**
	 * @return
	 */
	public Collection getPermissions();
	
	/**
	 * @param permission
	 * @throws RightsException 
	 */
	public void addPermission(String permission) throws RightsException;
	
	/**
	 * @param permission
	 */
	public void addPermission(Permission permission);
	
	/**
	 * @param permission
	 */
	public void removePermission(String permission);
	
	/**
	 * @param permissions
	 */
	public void setPermissions(Collection permissions);

	/*****************************************************
	 * Prohibitions
	 *****************************************************/

	/**
	 * @return
	 */
	public boolean hasProhibitions();
	
	/**
	 * @return
	 */
	public Collection getProhibitions();
	
	/**
	 * @param prohibition
	 */
	public void addProhibition(String prohibition) throws RightsException;
	
	/**
	 * @param prohibition
	 */
	public void addProhibition(Prohibition prohibition);
	
	/**
	 * @param prohibitions
	 */
	public void removeProhibitions(Collection prohibitions);
	
	/**
	 * @param prohibitions
	 */
	public void setProhibitions(Collection prohibitions);
	
	/*****************************************************
	 * Prohibitions
	 *****************************************************/
	
	/**
	 * @return
	 */
	public boolean hasRequirements();
	
	/**
	 * @return
	 */
	public Collection getRequirements();

	/**
	 * @param requirement
	 */
	public void addRequirement(String requirement) throws RightsException;
	
	/**
	 * @param requirement
	 */
	public void addRequirement(Requirement requirement);
	
	/**
	 * @param requirements
	 */
	public void removeRequirements(Collection requirements);
	
	/**
	 * @param requirements
	 */
	public void setRequirements(Collection requirements);
	
	/**
	 *	Permissions describe rights granted by the license.  Three kinds of permissions may be granted:
	 *	
	 *		Reproduction
	 *		    the work may be reproduced
	 *		Distribution
	 *	    	the work (and, if authorized, derivative works) may be distributed, publicly displayed, and publicly performed
	 *		DerivativeWorks
	 *		    derivative works may be created and reproduced
	 */
	public class Permission
	{
		protected final String m_id;

		/**
		 * @param id
		 */
		private Permission(String id)
		{
			m_id = id;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return m_id;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			boolean rv = false;
			
			if(obj instanceof Permission)
			{
				rv = ((Permission) obj).toString().equals(this.toString());
			}
			
			return rv;
		}

		/**
		 * @param permitted
		 * @return
		 */
		public static Permission fromString(String permitted)
		{
			if (REPRODUCTION.m_id.equals(permitted)) return REPRODUCTION;
			if (DISTRIBUTION.m_id.equals(permitted)) return DISTRIBUTION;
			if (DERIVATIVE_WORKS.m_id.equals(permitted)) return DERIVATIVE_WORKS;
			return null;
		}

		/** the work may be reproduced */
		public static final Permission REPRODUCTION = new Permission("Reproduction");
		
		/** the work (and, if authorized, derivative works) may be distributed, publicly displayed, and publicly performed */
		public static final Permission DISTRIBUTION = new Permission("Distribution");
		
		/** derivative works may be created and reproduced */
		public static final Permission DERIVATIVE_WORKS = new Permission("DerivativeWorks");
	}

	/**
	 * Prohibitions describe things prohibited by the license:
	 *	
	 *		CommercialUse
	 *	    	rights may be exercised for commercial purposes unless CommercialUse is prohibited
	 */
	public class Prohibition
	{
		protected final String m_id;

		/**
		 * @param id
		 */
		private Prohibition(String id)
		{
			m_id = id;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return m_id;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			boolean rv = false;
			
			if(obj instanceof Prohibition)
			{
				rv = ((Prohibition) obj).toString().equals(this.toString());
			}
			
			return rv;
		}

		/**
		 * @param prohibited
		 * @return
		 */
		public static Prohibition fromString(String prohibited)
		{
			if (COMMERCIAL_USE.m_id.equals(prohibited)) return COMMERCIAL_USE;
			return null;
		}

		/** rights may be exercised for commercial purposes unless CommercialUse is prohibited */
		public static final Prohibition COMMERCIAL_USE = new Prohibition("CommercialUse");
		
	}
	
	/**
	 *	Requirements describe restrictions imposed by the license:
	 *	
	 *		Notice
	 *		    copyright and license notices must be kept intact
	 *		Attribution
	 *		    credit must be given to copyright holder and/or author
	 *		ShareAlike
	 *		    derivative works must be licensed under the same terms as the original work
	 *		SourceCode
	 *		    source code (the preferred form for making modifications) must be provided for all derivative works 
	 */
	public class Requirement
	{
		protected final String m_id;

		/**
		 * @param id
		 */
		private Requirement(String id)
		{
			m_id = id;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return m_id;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			boolean rv = false;
			
			if(obj instanceof Requirement)
			{
				rv = ((Requirement) obj).toString().equals(this.toString());
			}
			
			return rv;
		}

		/**
		 * @param required
		 * @return
		 */
		public static Requirement fromString(String required)
		{
			if (NOTICE.m_id.equals(required)) return NOTICE;
			if (ATTRIBUTION.m_id.equals(required)) return ATTRIBUTION;
			if (SHARE_ALIKE.m_id.equals(required)) return SHARE_ALIKE;
			if (SOURCE_CODE.m_id.equals(required)) return SOURCE_CODE;
			return null;
		}

		/** copyright and license notices must be kept intact */
		public static final Requirement NOTICE = new Requirement("Notice");
		
		/** credit must be given to copyright holder and/or author */
		public static final Requirement ATTRIBUTION = new Requirement("Attribution");
		
		/** derivative works must be licensed under the same terms as the original work */
		public static final Requirement SHARE_ALIKE = new Requirement("ShareAlike");
		
		/** source code (the preferred form for making modifications) must be provided for all derivative works */
		public static final Requirement SOURCE_CODE = new Requirement("SourceCode");
	}

	/**
	 * @param doc
	 * @param stack
	 * @return
	 */
	public Element toXml(Document doc, Stack stack);
	
}	// interface CreativeCommonsLicense
