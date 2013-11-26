/*******************************************************************************
 * Copyright (c) 2006-2013
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.language.mecore.resource.mecore.mopp;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

// TODO This class has been copied to 'org.emftext.language.java.ejava.resource.ejava'
public class SystemIndependentXMIResourceFactory extends
		EcoreResourceFactoryImpl {
	
	@Override
	public Resource createResource(URI uri) {
		final Resource originalResource = super.createResource(uri);
		final Map<Object, Object> originalSaveOptions = new LinkedHashMap<Object, Object>();
		if (originalResource instanceof XMLResource) {
			XMLResource xmlResource = (XMLResource) originalResource;
			originalSaveOptions.putAll(xmlResource.getDefaultSaveOptions());
		}
		
		XMIResourceImpl modifiedResource = new SystemIndependentXMIResource(uri);
		modifiedResource.getDefaultSaveOptions().putAll(originalSaveOptions);
		return modifiedResource;
	}
}