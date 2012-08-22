/*******************************************************************************
 * Copyright (c) 2006-2012
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
package de.devboost.emfcustomize.app;

import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.resource.JaMoPPUtil;

import de.devboost.emfcustomize.GeneratedFactoryRefactorer;

public class EMFCustomizeApplication {
	
	public static void main(String[] args) {
		if (args.length < 3) {
			return;
		}
		String pathToGenModel = args[0];
		String basePath = args[1];
		String classpath = args[2];
		URI genModelURI = URI.createFileURI(pathToGenModel);
		JaMoPPUtil.initialize();
		Map<String, Object> extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
		extensionToFactoryMap.put("ecore", new EcoreResourceFactoryImpl());
		extensionToFactoryMap.put("genmodel", new EcoreResourceFactoryImpl()); GenModelPackage.eINSTANCE.getClass();
		ResourceSet resourceSet = new ResourceSetImpl();
		for (String classpathEntry : classpath.split(";")) {
			classpathEntry = classpathEntry.trim();
			if ("".equals(classpathEntry) || classpathEntry == null) {
				continue;
			}
			URI uri = URI.createFileURI(classpathEntry);
			if ("jar".equals(uri.fileExtension())) {
				JavaClasspath.get(resourceSet).registerClassifierJar(uri);				
			} else {
				JavaClasspath.get(resourceSet).registerSourceOrClassFileFolder(uri);
			}
		}
		new GeneratedFactoryRefactorer().refactorFactory(genModelURI, 
				URI.createFileURI(basePath), false, resourceSet);
	}

}
