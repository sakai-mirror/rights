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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.rights.api.CreativeCommonsJurisdiction;
import org.sakaiproject.rights.api.CreativeCommonsLicense;
import org.sakaiproject.rights.api.CreativeCommonsLicenseManager;
import org.sakaiproject.rights.api.CreativeCommonsStringMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * CCLicenseManagerImpl
 *
 */
public class CreativeCommonsLicenseManagerImpl implements CreativeCommonsLicenseManager, Observer 
{
	protected static final String ATTR_REQUIRES = "requires";
	protected static final String ATTR_PROHIBITS = "prohibits";
	protected static final String ATTR_PERMITS = "permits";
	protected static final String ATTR_JURISDICTION = "jurisdiction";
	protected static final String ATTR_VERSION = "version";
	
	public static final String CC_NAMESPACE = "http://creativecommons.org/ns#";
	public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
	public static final String DCQ_NAMESPACE = "http://purl.org/dc/terms/";
	public static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
	
	public static final String CC_DEFAULT_LANGUAGE = "defaultLanguage";
	public static final String CC_JURISDICTION_SITE = "jurisdictionSite";
	public static final String CC_JURISDICTION = "jurisdiction";
	public static final String DC_LANGUAGE = "language";
	public static final String CC_LEGALCODE = "legalcode";
	public static final String CC_LICENSE = "License";
	public static final String CC_PERMITS = "permits";
	public static final String CC_PROHIBITS = "prohibits";
	public static final String CC_REQUIRES = "requires";
	public static final String DC_CREATOR = "creator";
	public static final String DC_DESCRIPTION = "description";
	public static final String DC_SOURCE = "source";
	public static final String DC_TITLE = "title";
	public static final String DCQ_HAS_VERSION = "hasVersion";
	public static final String DCQ_IS_REPLACED_BY = "isReplacedBy";
	public static final String RDF_ABOUT = "about";
	public static final String RDF_RESOURCE = "resource";
	public static final String XML_LANG = "lang";
	
	protected static final String CDATA_TYPE = "CDATA";
	protected static final String ID_TYPE = "ID";
	protected static final String IDREF_TYPE = "IDREF";
	protected static final String IDREFS_TYPE = "IDREFS";
	protected static final String NMTOKEN_TYPE = "NMTOKEN";
	protected static final String NMTOKENS_TYPE = "NMTOKENS";
	protected static final String ENTITY_TYPE = "ENTITY";
	protected static final String ENTITIES_TYPE = "ENTITIES";
	protected static final String NOTATION_TYPE = "NOTATION";

	protected static final String XML_ENUM = "enum";
	protected static final String XML_TYPE = "type";
	protected static final String XML_DESCRIPTION = "description";
	protected static final String XML_FIELD = "field";
	protected static final String XML_STANDARD = "standard";
	protected static final String XML_LABEL = "label";
	protected static final String XML_ID = "id";
	protected static final String XML_LICENSECLASS = "licenseclass";
	
	private static Log logger = LogFactory.getLog(CreativeCommonsLicenseManagerImpl.class);

	private static DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
	static 
	{
		parserFactory.setNamespaceAware(true);
	}

	protected Map<String,CreativeCommonsLicense> licenses = new HashMap<String,CreativeCommonsLicense>();

	protected Map<String,Map<String,Set<String>>> indexes = new HashMap<String,Map<String,Set<String>>>();
	
	Map<String, CreativeCommonsJurisdiction> jurisdictions = new HashMap<String, CreativeCommonsJurisdiction>();
	
	Map<String, CreativeCommonsStringMapper> standardQuestions = new HashMap<String, CreativeCommonsStringMapper>();
	
	protected ContentHostingService contentService;
	public void setContentService(ContentHostingService contentService)
	{
		this.contentService = contentService;
	}

	protected EntityManager entityManager;
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	protected EventTrackingService eventTrackingService;
	public void setEventTrackingService(EventTrackingService eventTrackingService)
	{
		this.eventTrackingService = eventTrackingService;
	}

	protected String juridictionsFolderPath = "creativecommons/jurisdictions/";
	public void setJuridictionsFolderPath(String juridictionsFolderPath) {
		this.juridictionsFolderPath = juridictionsFolderPath;
	}

	protected String licensesFolderPath = "creativecommons/licenses/";
	public void setLicensesFolderPath(String licensesFolderPath) {
		this.licensesFolderPath = licensesFolderPath;
	}
	
	protected String questionsFolderPath = "creativecommons/questions/";
	public void setQuestionsFolderPath(String questionsFolderPath) {
		this.questionsFolderPath = questionsFolderPath;
	}

	protected String rootFolderReference = "/content/user/admin/";
	public void setRootFolderReference(String rootFolderReference) {
		this.rootFolderReference = rootFolderReference;
	}

	protected List<String> versionList = new ArrayList<String>();
	public void setVersionList(List versions)
	{
		this.versionList = versions;
	}
	
	public List<String> getVersionList() 
	{
		return new ArrayList<String>(this.versionList);
	}
		
	/**
	 * @param license
	 */
	protected void addLicense(CreativeCommonsLicense license) 
	{
		String uri = license.getUri();
		this.licenses.put(uri, license);
		this.licenses.put(license.getIdentifier(), license);
		
		indexLicense(uri, ATTR_VERSION, license.getVersion());
		
		String jurisdiction = license.getJurisdiction();
		if(jurisdiction == null || jurisdiction.trim().equals(""))
		{
			jurisdiction = DEFAULT_JURISDICTION;
		}
		indexLicense(uri, ATTR_JURISDICTION, jurisdiction);
		Collection<String> permissions = license.getPermissions();
		if(permissions != null)
		{
			for(String permission : permissions)
			{
				indexLicense(uri, ATTR_PERMITS, permission);
			}
		}
		Collection<String> prohibitions = license.getProhibitions();
		if(prohibitions != null)
		{
			for(String prohibition : prohibitions)
			{
				indexLicense(uri, ATTR_PROHIBITS, prohibition);
			}
		}
		Collection<String> requirements = license.getRequirements();
		if(requirements != null)
		{
			for(String requirement : requirements)
			{
				indexLicense(uri, ATTR_REQUIRES, requirement);
			}
		}
	}
	
	/**
	 * @param resourcesRootFolderPath
	 * @param relativeResourcesPath
	 * @param in
	 */
	protected void copyFromJarToResources(String resourcesRootFolderPath, String relativeResourcesPath, InputStream in)
	{
		String[] parts = relativeResourcesPath.split("/");
		String relativePath = "";
		for(String part : parts)
		{
			relativePath += part;
			if(relativePath.equals(relativeResourcesPath))
			{
				try 
				{
					this.contentService.checkResource(resourcesRootFolderPath + relativePath);
				} 
				catch (IdUnusedException e) 
				{
					// need to add the resource
					try 
					{
						ContentResourceEdit edit = this.contentService.addResource(resourcesRootFolderPath + relativePath);
						
						edit.setContent(in);
						edit.setContentType("text/xml");
						edit.setResourceType(ResourceType.TYPE_UPLOAD);
						ResourcePropertiesEdit props = edit.getPropertiesEdit();
						props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, part);
						
						this.contentService.commitResource(edit);
					} 
					catch (PermissionException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (IdUsedException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (IdInvalidException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (InconsistentException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (ServerOverloadException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (OverQuotaException e1) 
					{
						logger.debug("Exception",e);
					}
					
					
				} 
				catch (TypeException e) 
				{
					logger.debug("TypeException checking on resource: " + relativePath);
				} 
				catch (PermissionException e) 
				{
					logger.debug("PermissionException checking on resource: " + relativePath);
				} 
			}
			else
			{
				relativePath += "/";
				try 
				{
						this.contentService.checkCollection(resourcesRootFolderPath + relativePath);
				} 
				catch (IdUnusedException e) 
				{
					// need to add collection
					try
					{
						ContentCollectionEdit edit = this.contentService.addCollection(resourcesRootFolderPath + relativePath);
						edit.setResourceType(ResourceType.TYPE_FOLDER);
						ResourcePropertiesEdit props = edit.getPropertiesEdit();
						props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, part);
						
						this.contentService.commitCollection(edit);
					}
					catch (PermissionException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (IdUsedException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (IdInvalidException e1) 
					{
						logger.debug("Exception",e1);
					} 
					catch (InconsistentException e1) 
					{
						logger.debug("Exception",e1);
					} 

				} 
				catch (TypeException e) 
				{
					logger.debug("TypeException checking on collection: " + relativePath);
				} 
				catch (PermissionException e) 
				{
					logger.debug("PermissionException checking on collection: " + relativePath);
				} 
			}
		}
	}
	
	/**
	 * @param entity
	 * @param parser TODO
	 */
	protected void extractJurisdictions(ContentEntity entity, DocumentBuilder parser) 
	{
		if(entity.isResource())
		{
			ContentResource resource = (ContentResource) entity;
			readJurisdictions(resource, parser);
		}
		else if(entity.isCollection())
		{
			ContentCollection collection = (ContentCollection) entity;
			for(ContentEntity child : (List<ContentEntity>) collection.getMemberResources())
			{
				extractJurisdictions(child, parser);
			}
		}
	}
	
	/**
	 * @param entity
	 * @param parser TODO
	 */
	protected void extractLicenses(ContentEntity entity, DocumentBuilder parser) 
	{
		if(entity.isResource())
		{
			ContentResource resource = (ContentResource) entity;
			readLicenses(resource, parser);
		}
		else if(entity.isCollection())
		{
			ContentCollection collection = (ContentCollection) entity;
			for(ContentEntity child : (List<ContentEntity>) collection.getMemberResources())
			{
				extractLicenses(child, parser);
			}
		}
	}
	
	/**
	 * @param entity
	 * @param parser TODO
	 */
	protected void extractQuestions(ContentEntity entity, DocumentBuilder parser) 
	{
		if(entity.isResource())
		{
			ContentResource resource = (ContentResource) entity;
			readQuestions(resource, parser);
		}
		else if(entity.isCollection())
		{
			ContentCollection collection = (ContentCollection) entity;
			for(ContentEntity child : (List<ContentEntity>) collection.getMemberResources())
			{
				extractQuestions(child, parser);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicenseManager#getJurisdictions()
	 */
	public Map<String, String> getJurisdictions() 
	{
		Map<String,String> jurisdictions = new HashMap<String,String>();
		
		for(String key : this.jurisdictions.keySet())
		{
			CreativeCommonsJurisdiction jurisdiction = this.jurisdictions.get(key);
			jurisdictions.put(jurisdiction.getTitle(), key);
		}
		
		return jurisdictions;
	}
	
	/**
	 * @param index_name
	 * @param values
	 * @return
	 */
	protected Set<String> getLicenseIdentifiers(String index_name, Set<String> values) 
	{
		Set<String> license_ids = this.licenses.keySet();
		if(values == null || values.isEmpty())
		{
			// do nothing
		}
		else
		{
			Map<String,Set<String>> map = this.indexes.get(index_name);
			for(String value : values)
			{
				Set<String> set = map.get(value);
				license_ids.retainAll(set);
			}
		}
		return license_ids;
	}
	
	/**
	 * Get all identifiers for licenses which have the specified value for the named attribute.
	 * If value is null, this method returns all values from all indexes for this attribute. 
	 * @param name
	 * @param value
	 * @return
	 */
	protected Set<String> getLicenseIdentifiers(String name, String value) 
	{
		Set<String> licenses;
		
		Map<String,Set<String>> index = this.indexes.get(name);
		if(index == null)
		{
			index = new HashMap<String,Set<String>>();
			this.indexes.put(name, index);
		}
		
		if(value == null || value.trim().equals(""))
		{
			licenses = new HashSet<String>();
			// get all values
			for(String key : index.keySet())
			{
				licenses.addAll(index.get(key));
			}
		}
		else
		{
			Set<String> set = index.get(value);
			if(set == null)
			{
				set = new HashSet<String>();
				index.put(value, set);
			}
			licenses = new HashSet<String>(set);
		}
		return licenses;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CCLicenseManager#getLicense(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Collection<CreativeCommonsLicense> getLicenses(String version, String jurisdiction, Set<String> permits, Set<String> prohibits, Set<String> requires) 
	{
		Set<String> included = new HashSet<String>(this.licenses.keySet());
		
		included.retainAll(getLicenseIdentifiers(ATTR_JURISDICTION, jurisdiction));
		included.retainAll(getLicenseIdentifiers(ATTR_PERMITS, permits));
		included.retainAll(getLicenseIdentifiers(ATTR_PROHIBITS, prohibits));
		included.retainAll(getLicenseIdentifiers(ATTR_REQUIRES, requires));

		if(included.size() > 0)
		{
			if(version == null)
			{
				// do nothing
			}
			else
			{
				if(version == LATEST_VERSION)
				{
					// filter to find latest version
					Map<String, Set<String>> versionMap = this.indexes.get(ATTR_VERSION);
					if(versionMap == null)
					{
						logger.warn(ATTR_VERSION + " versionMap is null", new Exception());
					}
					else
					{
						for(String v : this.getVersionList())
						{
							Set<String> items = versionMap.get(v);
							if(items == null)
							{
								continue;
							}
							
							Set<String> vSet = new HashSet<String>(items);
							vSet.retainAll(included);
							if(vSet.size() > 0)
							{
								included.retainAll(vSet);
								break;
							}
						}
					}
				}
				else
				{
					included.retainAll( getLicenseIdentifiers(ATTR_VERSION, version) );
				}
			}
		}
		
		Collection<CreativeCommonsLicense> rv = new ArrayList<CreativeCommonsLicense>();
		for(String key : included)
		{
			rv.add(this.licenses.get(key));
		}

		return rv;
	}
	
	/**
	 * @param parent
	 * @param namespace TODO
	 * @param nodename
	 * @return
	 */
	protected Map<String, String> getLocalizationMap(Element parent, String nodename) 
	{
		Map<String,String> values = new HashMap<String,String>();
		NodeList nodes = parent.getElementsByTagName(nodename);
		
		for(int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String lang = element.getAttributeNS(XML_NAMESPACE, XML_LANG);
				String value = element.getTextContent();
				if(value != null && ! value.trim().equals(""))
				{
					values.put(lang, value);
				}
			}
		}
		return values;
	}

	/**
	 * @param parent
	 * @param namespace TODO
	 * @param nodename
	 * @return
	 */
	protected Map<String, String> getLocalizationMap(Element parent, String namespace, String nodename) 
	{
		Map<String,String> values = new HashMap<String,String>();
		NodeList nodes = parent.getElementsByTagNameNS(namespace, nodename);
		
		for(int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String lang = element.getAttributeNS(XML_NAMESPACE, XML_LANG);
				String value = element.getTextContent();
				if(value != null && ! value.trim().equals(""))
				{
					values.put(lang, value);
				}
			}
		}
		return values;
	}

	/**
	 * @param parent
	 * @param namespace TODO
	 * @param nodename
	 * @return
	 */
	protected String getNodeResourceValue(Element parent, String namespace, String nodename) 
	{
		NodeList nodes = parent.getElementsByTagNameNS(namespace, nodename);
		String value = "";
		for( int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String text = element.getAttributeNS(RDF_NAMESPACE, RDF_RESOURCE);
				if(text != null && ! text.trim().equals(""))
				{
					value += text;
				}
			}
		}
		return value;
	}
	
	/**
	 * @param parent
	 * @param namespace TODO
	 * @param nodename
	 * @return
	 */
	protected Set<String> getNodeResourceValues(Element parent, String namespace, String nodename) 
	{
		NodeList nodes = parent.getElementsByTagNameNS(namespace, nodename);
		Set<String> values = new TreeSet<String>();
		for( int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String text = element.getAttributeNS(RDF_NAMESPACE, RDF_RESOURCE);
				if(text != null && ! text.trim().equals(""))
				{
					values.add(text);
				}
			}
		}
		return values;
	}

	/**
	 * @param parent
	 * @param nodename
	 * @return
	 */
	protected String getNodeValue(Element parent, String nodename) 
	{
		NodeList nodes = parent.getElementsByTagName(nodename);
		String value = "";
		for( int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String text = element.getTextContent();
				if(text != null && ! text.trim().equals(""))
				{
					value += text;
				}
			}
		}
		return value;
	}

	/**
	 * @param parent
	 * @param namespace TODO
	 * @param nodename
	 * @return
	 */
	protected String getNodeValue(Element parent, String namespace, String nodename) 
	{
		NodeList nodes = parent.getElementsByTagNameNS(namespace, nodename);
		String value = "";
		for( int n = 0; n < nodes.getLength(); n++)
		{
			Node node = nodes.item(n);
			if(node instanceof Element)
			{
				Element element = (Element) node;
				String text = element.getTextContent();
				if(text != null && ! text.trim().equals(""))
				{
					value += text;
				}
			}
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.rights.api.CreativeCommonsLicenseManager#getStringMapper(java.lang.String)
	 */
	public CreativeCommonsStringMapper getStringMapper(String mapperKey) 
	{
		// TODO: make a copy rather than returning the original??
		return this.standardQuestions.get(mapperKey);
	}
	
	/**
	 * @param uri
	 * @param index_name
	 * @param key
	 */
	protected void indexLicense(String uri, String index_name, String key) 
	{
		if(key != null && ! key.trim().equals(""))
		{
			Map<String,Set<String>> index = this.indexes.get(index_name);
			if(index == null)
			{
				index = new HashMap<String,Set<String>>();
				this.indexes.put(index_name, index);
			}
			Set<String> set = index.get(key);
			if(set == null)
			{
				set = new HashSet<String>();
				index.put(key, set);
			}
			set.add(uri);
		}
	}

	public void init()
	{
		logger.info(this + ".init()");
		this.eventTrackingService.addObserver(this);
		
		Reference licenseFolderRef = this.entityManager.newReference(rootFolderReference + licensesFolderPath);
		String licensesFolderId = licenseFolderRef.getId();
		verifyFolderExists(licensesFolderId);
		
		Reference jurisdictionFolderRef = this.entityManager.newReference(rootFolderReference + juridictionsFolderPath);
		String jurisdictionsFolderId = jurisdictionFolderRef.getId();
		verifyFolderExists(jurisdictionsFolderId);
		
		Reference questionsFolderRef = this.entityManager.newReference(rootFolderReference + questionsFolderPath);
		String questionsFolderId = questionsFolderRef.getId();
		verifyFolderExists(questionsFolderId);
		
		
		initializeFiles(licensesFolderId, jurisdictionsFolderId, questionsFolderId);
		
		// check whether there are files in the folder.  If so, read them and add licenses 
		readAllLicenses(licensesFolderId);
		
		readAllJurisdictions(jurisdictionsFolderId);
		
		readAllQuestions(questionsFolderId);
		
		
		// watch the folder for newly added items 
		//    when new items are added or existing items are changed, read them and add licenses
		//    when new items, start watching them
		// check whether there are license files in the pack. If so add them to the root folder.
		
		// need to get a reference obj using the folderRef and get the id from the reference obj

		if(logger.isDebugEnabled())
		{
			for(String uri : this.licenses.keySet())
			{
				CreativeCommonsLicense license = this.licenses.get(uri);
				
				logger.debug(license.toJSON());
			}
		}
	}

	/**
	 * @param licenseFolderId
	 * @param jurisdictionsFolderId
	 * @param questionsFolderId
	 */
	protected void initializeFiles(String licenseFolderId, String jurisdictionsFolderId, String questionsFolderId) 
	{
		try 
		{
			URI url = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
			
			String licensesDirectoryName = "rdf/licenses/";
			String jurisdictionsDirectoryName = "rdf/jurisdictions/";
			String questionsDirectoryName = "rdf/questions/";
			
			logger.debug("===> " + url.toString());
			JarFile jarFile = new JarFile(new File(url));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			while(entries.hasMoreElements())
			{
				entry = entries.nextElement();
				String jarFolderPath = entry.getName();
				if(jarFolderPath == null || entry.isDirectory())
				{
					continue;
				}
				
				if(jarFolderPath.startsWith(licensesDirectoryName))
				{
					logger.debug("--> " + entry.getName());
					
					InputStream in = jarFile.getInputStream(entry);

					String relativeFolderPath = jarFolderPath.substring(licensesDirectoryName.length());
					
					this.copyFromJarToResources(licenseFolderId, relativeFolderPath, in);
				}
				else if(jarFolderPath.startsWith(jurisdictionsDirectoryName))
				{
					logger.debug("--> " + entry.getName());
					
					InputStream in = jarFile.getInputStream(entry);

					String relativeFolderPath = jarFolderPath.substring(jurisdictionsDirectoryName.length());
					
					this.copyFromJarToResources(jurisdictionsFolderId, relativeFolderPath, in);					
				}
				else if(jarFolderPath.startsWith(questionsDirectoryName))
				{
					logger.debug("--> " + entry.getName());
					
					InputStream in = jarFile.getInputStream(entry);

					String relativeFolderPath = jarFolderPath.substring(questionsDirectoryName.length());
					
					this.copyFromJarToResources(questionsFolderId, relativeFolderPath, in);					
				}
			}
		} 
		catch (IOException e) 
		{
			logger.warn("============>  Exception reading files from jar ");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.debug("Exception",e);
		}
		
	}

	/**
	 * @param folderId
	 */
	protected void readAllJurisdictions(String folderId) 
	{
		try
		{
			
			ContentCollection collection = this.contentService.getCollection(folderId);
			DocumentBuilder parser =  parserFactory.newDocumentBuilder();
			
			for(ContentEntity entity : (List<ContentEntity>) collection.getMemberResources())
			{
				extractJurisdictions(entity, parser);
			}
		}
		catch(IdUnusedException e)
		{
			logger.debug("IdUnusedException " + e, e);
		}
		catch(TypeException e)
		{
			logger.debug("TypeException " + e, e);
		} 
		catch (PermissionException e) 
		{
			logger.debug("PermissionException " + e, e);
		} 
		catch (ParserConfigurationException e) 
		{
			logger.debug("ParserConfigurationException " + e, e);
		}
	}

	/**
	 * @param folderId
	 */
	protected void readAllLicenses(String folderId) 
	{
		try
		{
			
			ContentCollection collection = this.contentService.getCollection(folderId);
	        DocumentBuilder parser = this.parserFactory.newDocumentBuilder();

			
			for(ContentEntity entity : (List<ContentEntity>) collection.getMemberResources())
			{
				extractLicenses(entity, parser);
			}
		}
		catch(IdUnusedException e)
		{
			logger.debug("IdUnusedException " + e, e);
		}
		catch(TypeException e)
		{
			logger.debug("TypeException " + e, e);
		} catch (PermissionException e) 
		{
			logger.debug("PermissionException " + e, e);
		} 
		catch (ParserConfigurationException e) 
		{
			logger.debug("ParserConfigurationException " + e, e);
		}
	}

	/**
	 * @param folderId
	 */
	protected void readAllQuestions(String folderId) 
	{
		try
		{
			
			ContentCollection collection = this.contentService.getCollection(folderId);
			DocumentBuilder parser =  parserFactory.newDocumentBuilder();
			
			for(ContentEntity entity : (List<ContentEntity>) collection.getMemberResources())
			{
				extractQuestions(entity, parser);
			}
		}
		catch(IdUnusedException e)
		{
			logger.debug("IdUnusedException " + e, e);
		}
		catch(TypeException e)
		{
			logger.debug("TypeException " + e, e);
		} 
		catch (PermissionException e) 
		{
			logger.debug("PermissionException " + e, e);
		} 
		catch (ParserConfigurationException e) 
		{
			logger.debug("ParserConfigurationException " + e, e);
		}
	}

	/**
	 * @param resource
	 * @param parser TODO
	 * @return
	 */
	protected void readJurisdictions(ContentResource resource, DocumentBuilder parser) 
	{
		try
		{
			InputStream istream = resource.streamContent();
			Document doc = parser.parse(istream);
			
			NodeList jurisdiction_nodes = doc.getElementsByTagNameNS(CC_NAMESPACE, CC_JURISDICTION);
			for(int i = 0; i < jurisdiction_nodes.getLength(); i++)
			{
				Node jurisdiction_node = jurisdiction_nodes.item(i);
				if (jurisdiction_node instanceof Element)
				{
					Element jurisdction_element = (Element) jurisdiction_node;
					
					CreativeCommonsJurisdiction ccJurisdiction = new CreativeCommonsJurisdictionImpl();
					
					ccJurisdiction.setUri(jurisdction_element.getAttributeNS(RDF_NAMESPACE, RDF_ABOUT));
					ccJurisdiction.setDefaultLanguage(this.getNodeValue(jurisdction_element, CC_NAMESPACE, CC_DEFAULT_LANGUAGE));
					ccJurisdiction.setLanguage(this.getNodeValue(jurisdction_element, DC_NAMESPACE, DC_LANGUAGE));
					ccJurisdiction.setJurisdictionSite(this.getNodeResourceValue(jurisdction_element, CC_NAMESPACE, CC_JURISDICTION_SITE));
					ccJurisdiction.setTitles(this.getLocalizationMap(jurisdction_element, DC_NAMESPACE, DC_TITLE));
					
					this.jurisdictions.put(ccJurisdiction.getUri(), ccJurisdiction);
				}
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception " + e, e);
		}
		
	}

	/**
	 * @param resource
	 * @param db TODO
	 * @return
	 */
	protected void readLicenses(ContentResource resource, DocumentBuilder parser) 
	{
		try
		{
            Document doc = parser.parse(resource.streamContent());
 			parser.reset();
			
			NodeList licenses = doc.getElementsByTagNameNS(CC_NAMESPACE, CC_LICENSE);
			for(int i = 0; i < licenses.getLength(); i++)
			{
				Node license_node = licenses.item(i);
				if(license_node instanceof Element)
				{
					Element license_element = (Element) license_node;
					CreativeCommonsLicense license = new CreativeCommonsLicenseImpl();
					
					license.setUri(license_element.getAttributeNS(RDF_NAMESPACE, RDF_ABOUT));
					license.setJurisdiction(getNodeResourceValue(license_element, CC_NAMESPACE, CC_JURISDICTION));
					license.setSource(getNodeResourceValue(license_element, DC_NAMESPACE, DC_SOURCE));
					license.setReplacedBy(this.getNodeResourceValue(license_element, DCQ_NAMESPACE, DCQ_IS_REPLACED_BY));
					license.setVersion(this.getNodeValue(license_element, DCQ_NAMESPACE, DCQ_HAS_VERSION));
					license.setLegalcode(this.getNodeResourceValue(license_element, CC_NAMESPACE, CC_LEGALCODE));
					license.setCreator(this.getNodeResourceValue(license_element, DC_NAMESPACE, DC_CREATOR));
					
					license.addPermissions(this.getNodeResourceValues(license_element, CC_NAMESPACE, CC_PERMITS));
					license.addRequirements(this.getNodeResourceValues(license_element, CC_NAMESPACE, CC_REQUIRES));
					license.addProhibitions(this.getNodeResourceValues(license_element, CC_NAMESPACE, CC_PROHIBITS));
					
					license.addDescriptions(getLocalizationMap(license_element, DC_NAMESPACE, DC_DESCRIPTION));
					license.addTitles(this.getLocalizationMap(license_element, DC_NAMESPACE, DC_TITLE));
					
					this.addLicense(license);
				}
				
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception " + e, e);
		}
		
	}

	/**
	 * @param resource
	 * @param parser TODO
	 * @return
	 */
	protected void readQuestions(ContentResource resource, DocumentBuilder parser) 
	{
		try
		{
			InputStream istream = resource.streamContent();
			Document doc = parser.parse(istream);
			
			NodeList license_class_nodes = doc.getElementsByTagName(XML_LICENSECLASS);
			for(int i = 0; i < license_class_nodes.getLength(); i++)
			{
				Node license_class_node = license_class_nodes.item(i);
				if (license_class_node instanceof Element)
				{
					Element license_class_element = (Element) license_class_node;
					
					CreativeCommonsStringMapperImpl question = new CreativeCommonsStringMapperImpl();
					
					String license_class_id = license_class_element.getAttribute(XML_ID);
					if(license_class_id != null && license_class_id.equalsIgnoreCase(XML_STANDARD))
					{
						this.standardQuestions.put(CreativeCommonsLicenseManager.CC_LICENSE_CHOOSER, question);
						
						Map<String,String> labelMap = this.getLocalizationMap(license_class_element, XML_LABEL);
						question.addCreativeCommonsLabels(labelMap);
						
						NodeList field_nodes = license_class_element.getElementsByTagName(XML_FIELD);
						for(int f = 0; f < field_nodes.getLength(); f++)
						{
							Node field_node = field_nodes.item(f);
							if(field_node instanceof Element)
							{
								Element field_element = (Element) field_node;
								String questionKey = field_element.getAttribute(XML_ID);
								
								Map<String, String> labelTranslations = this.getLocalizationMap(field_element, XML_LABEL); // new HashMap<String, String>(); 
								Map<String, String> descriptionTranslations = this.getLocalizationMap(field_element, XML_DESCRIPTION); // new HashMap<String, String>(); 

								question.addLabels(questionKey, labelTranslations);
								question.addDescriptions(questionKey, descriptionTranslations);
								
								NodeList children = field_element.getChildNodes();
								for(int c = 0; c < children.getLength(); c++)
								{
									Node child_node = children.item(c);
									if(child_node instanceof Element)
									{
										Element child_element = (Element) child_node;
										
										String tag_name = child_element.getTagName();
										if(tag_name == null)
										{
											//skip
										}
										else if (tag_name.equals(XML_TYPE))
										{
											// skip
											logger.info(questionKey + " type is " + child_element.getTextContent());
										}
										else if (tag_name.equals(XML_LABEL))
										{
											String lang = child_element.getAttributeNS(XML_NAMESPACE,XML_LANG);
											String label = child_element.getTextContent();
											labelTranslations.put(lang , label );
										}
										else if (tag_name.equals(XML_DESCRIPTION))
										{
											String lang = child_element.getAttributeNS(XML_NAMESPACE,XML_LANG);
											String description = child_element.getTextContent();
											descriptionTranslations.put(lang, description );
										}
										else if (tag_name.equals(XML_ENUM))
										{
											String responseKey = child_element.getAttribute(XML_ID);
											
											Map<String,String> responseMap = this.getLocalizationMap(child_element, XML_LABEL);
											question.addResponses(questionKey, responseKey, responseMap);
										}
									}
									
								}
								
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception " + e, e);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) 
	{
		if (arg1 instanceof Event) 
		{
			Event event = (Event) arg1;
			/*
			 * Modified? If so (and this is our file) update the the
			 * configuration
			 */
			if (event.getModify()) 
			{
				String refstr = event.getResource();

				if(refstr == null)
				{
					// do nothing
				}
				else if (refstr.startsWith(this.rootFolderReference + this.licensesFolderPath)) 
				{
					logger.debug("Updating configuration from " + refstr);
					updateLicenses(refstr);
				}
				else if (refstr.startsWith(this.rootFolderReference + this.juridictionsFolderPath))
				{
					logger.debug("Updating configuration from " + refstr);
					updateJurisdictions(refstr);
				}
			}
		}
	}

	/**
	 * @param refstr
	 */
	protected void updateJurisdictions(String refstr) 
	{
		Reference reference = this.entityManager.newReference(refstr);
		ContentEntity entity = (ContentEntity) reference.getEntity();
		if(entity == null)
		{
			logger.debug("Null entity: refstr == " + refstr);
		}
		else
		{
			try 
			{
				DocumentBuilder parser = this.parserFactory.newDocumentBuilder();
				this.extractJurisdictions(entity, parser);
				
			} 
			catch (ParserConfigurationException e) 
			{
				logger.warn("Unable to get new DocumentBuilder " + e);
			}
		}

		
	}

	/**
	 * @param refstr
	 */
	protected void updateLicenses(String refstr) 
	{
		Reference reference = this.entityManager.newReference(refstr);
		ContentEntity entity = (ContentEntity) reference.getEntity();
		if(entity == null)
		{
			logger.debug("Null entity: refstr == " + refstr);
		}
		else 
		{
			try 
			{
				DocumentBuilder parser = this.parserFactory.newDocumentBuilder();
				this.extractLicenses(entity, parser);
			} 
			catch (ParserConfigurationException e) 
			{
				logger.warn("Unable to get new DocumentBuilder " + e);
			}


		}
	}

	/**
	 * @param folderId
	 * @return 
	 */
	protected boolean verifyFolderExists(String folderId) 
	{
		
		if(folderId == null || folderId.trim().equals(""))
		{
			return false;
		}
		ContentCollectionEdit edit = null;
		try 
		{
			this.contentService.checkCollection(folderId);
		} 
		catch (IdUnusedException e) 
		{
			try 
			{
				edit = this.contentService.addCollection(folderId);
				String[] parts = folderId.split("/");
				for(int i = parts.length - 1; i >= 0; i--)
				{
					String part = parts[i];
					if(part != null && ! part.trim().equals(""))
					{
						ResourcePropertiesEdit props = edit.getPropertiesEdit();
						props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, part);
						break;
					}
				}
				this.contentService.commitCollection(edit);
				return true;
			} 
			catch (IdUsedException e1) 
			{
				logger.debug("Exception",e1);
			} 
			catch (IdInvalidException e1) 
			{
				logger.debug("Exception",e1);
			} 
			catch (PermissionException e1) 
			{
				logger.debug("Exception",e1);
			} 
			catch (InconsistentException e1) 
			{
				logger.debug("Exception",e1);
			}
		} 
		catch (TypeException e) 
		{
			logger.debug("Exception",e);
		} 
		catch (PermissionException e) 
		{
			logger.debug("Exception",e);
		}
		finally
		{
			if(edit != null && edit.isActiveEdit())
			{
				this.contentService.cancelCollection(edit);
			}
		}
		return false;
	}
	
}
