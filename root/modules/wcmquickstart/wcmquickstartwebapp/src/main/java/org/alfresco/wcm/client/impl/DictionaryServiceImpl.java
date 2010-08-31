/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Dictionary service implementation
 * 
 * @author Roy Wetherall
 */
public class DictionaryServiceImpl extends BaseProcessorExtension implements DictionaryService
{
	/** CMIS type delimiter */
	private static final String TYPE_DELIMITER = ":";
	
	/** Root CMIS types */
	private static final String[] ROOT_TYPES = new String[]{TYPE_CMIS_DOCUMENT, TYPE_CMIS_FOLDER};
	private static final String[] ROOT_TYPE_PREFIXES = new String[]{TYPE_PREFIX_DOCUMENT, TYPE_CMIS_FOLDER};
	// TODO support other CMIS root types cmis:reltionship and cmis:policy
	
	/**  Map containing the parent maps for each root type */
	Map<String, Map<String, String>> typeMaps = new TreeMap<String, Map<String, String>>();
	
	/** Map from root type to prefix */
	Map<String, String> typePrefixMap;
	
	/**
	 * Init method.
	 */
	public void init()
	{
		Session session = CmisSessionHelper.getSession();
		
		typePrefixMap = new TreeMap<String, String>();
		int index = 0;
		
		for (String rootType : ROOT_TYPES)
        {
			// Get the type hierarchy
			List<Tree<ObjectType>> typeHierarchy = session.getTypeDescendants(rootType, -1, false);				
			
			// Create parent/child map
			Map<String, String> typeMap = new TreeMap<String, String>();
			addToMap(typeMap, typeHierarchy, rootType);
			
			// Add to type map
			typeMaps.put(rootType, typeMap);
			
			// Add to type prefix map
			typePrefixMap.put(rootType, ROOT_TYPE_PREFIXES[index]);
			index++;
        }
	}	
	
	private void addToMap(Map<String, String> map, List<Tree<ObjectType>> types, String parentType)
	{
		if (types != null)
		{
			for (Tree<ObjectType> type : types)
	        {
				String typeName = type.getItem().getQueryName(); 
				map.put(typeName, parentType);
		        addToMap(map, type.getChildren(), typeName);
	        }
		}
	}
	
	/**
	 * @see org.alfresco.wcm.client.DictionaryService#getParentType(java.lang.String)
	 */
	@Override
	public String getParentType(String type)
	{
		return getParentType(type, false);
	}
	
	/**
	 * @see org.alfresco.wcm.client.DictionaryService#getParentType(java.lang.String, boolean)
	 */
	@Override
	public String getParentType(String type, boolean queryName)
	{
		String parentType = null;
		
		// Remove the type prefix
		type = removeTypePrefix(type);
		
		// If the type is a root type then there is no parent
		if (typeMaps.containsKey(type) == false)
		{
			for (Map.Entry<String, Map<String, String>> entry : typeMaps.entrySet())
            {
				// Get the root type
				String rootType = entry.getKey();
				
				// See if there is an entry for the type
				parentType = entry.getValue().get(type);
				if (parentType != null)
				{
					// Append the type prefix if required
					if (queryName == false)
					{
						parentType = typePrefixMap.get(rootType) + TYPE_DELIMITER + parentType;
					}
					break;
				}
            }
		}
		
		return parentType;
	}
	
	/**
	 * @see org.alfresco.wcm.client.DictionaryService#isRootType(java.lang.String)
	 */
	@Override
	public boolean isRootType(String type)
	{
		type = removeTypePrefix(type);
		return typeMaps.containsKey(type);
	}

	/**
	 * @see org.alfresco.wcm.client.DictionaryService#isContentType(java.lang.String)
	 */
	@Override
	public boolean isDocumentSubType(String type)
	{
		type = removeTypePrefix(type);
		return (TYPE_CMIS_DOCUMENT.equals(type) ||
		        typeMaps.get(TYPE_CMIS_DOCUMENT).containsKey(type));
	}
	
	/**
	 * @see org.alfresco.wcm.client.DictionaryService#isFolderSubType(java.lang.String)
	 */
	@Override
	public boolean isFolderSubType(String type)
	{
		type = removeTypePrefix(type);
	    return (TYPE_CMIS_FOLDER.equals(type) ||
	    		typeMaps.get(TYPE_CMIS_FOLDER).containsKey(type));
	}
	
	/**
	 * @see org.alfresco.wcm.client.DictionaryService#removeTypePrefix(java.lang.String)
	 */
	public String removeTypePrefix(String type)
	{
		String result = type;
		String[] values = type.split(TYPE_DELIMITER);
		if (values.length == 3)
		{
			result = values[1] + TYPE_DELIMITER + values[2];
		}
		return result;
	}
}
