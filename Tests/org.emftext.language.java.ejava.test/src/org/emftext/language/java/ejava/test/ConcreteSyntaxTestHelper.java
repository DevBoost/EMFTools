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
package org.emftext.language.java.ejava.test;

import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation;

/**
 * A helper class for all EMFText tests.
 */
public class ConcreteSyntaxTestHelper {

	public static void registerEcoreGenModel() {
		String genModelPath = "/model/Ecore.genmodel";
		String nsURI = "http://www.eclipse.org/emf/2002/Ecore";
		Class<EClass> clazz = EClass.class;

		registerGenModel(genModelPath, nsURI, clazz);
	}

	public static void registerGenModelGenModel() {
		String genModelPath = "/model/GenModel.genmodel";
		String nsURI = "http://www.eclipse.org/emf/2002/GenModel";
		Class<GenClass> clazz = GenClass.class;

		registerGenModel(genModelPath, nsURI, clazz);
	}

	private static void registerGenModel(String genModelPath, String nsURI, Class<?> clazz) {
		final Map<String, URI> packageNsURIToGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap();
		String path = clazz.getResource(genModelPath).getFile();
		path = path.replace("file:/", "archive:file:/");
		URI uri = URI.createURI(path);
		packageNsURIToGenModelLocationMap.put(nsURI, uri);
	}

	public static void registerResourceFactories() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"ecore",
				new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"genmodel",
				new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"xmi",
				new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());

		new CsMetaInformation().registerResourceFactory();
	}
}
