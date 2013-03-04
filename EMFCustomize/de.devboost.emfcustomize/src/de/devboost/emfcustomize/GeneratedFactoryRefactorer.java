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
package de.devboost.emfcustomize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.imports.ImportsFactory;
import org.emftext.language.java.imports.PackageImport;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypesFactory;

public class GeneratedFactoryRefactorer {

	public static String CUSTOM_CLASS_SUFFIX = "Custom";
	public static String CUSTOM_SUB_PACKAGE = "custom";

	public static String FACTORY_CLASS_SUFFIX = "FactoryImpl";

	public List<Resource> refactorFactory(URI genModelURI, URI baseURI, boolean createMissingCustomClasses, ResourceSet resourceSet) {
		Resource resource = null;
		try {
			resource = resourceSet.getResource(genModelURI, true);	
		} catch (Exception e) {	
			e.printStackTrace();
		}

		if (resource == null) {
			return Collections.emptyList();
		}
		if (resource.getContents().isEmpty()) {
			return Collections.emptyList();
		}
		if (!(resource.getContents().get(0) instanceof GenModel)) {
			return Collections.emptyList();
		}
		GenModel genModel = (GenModel) resource.getContents().get(0);
		return refactorFactory(genModel, null, baseURI, createMissingCustomClasses);
	}

	public List<Resource> refactorFactory(GenModel genModel, GenClass genClass, URI baseURI, boolean createMissingCustomClasses) {
		ResourceSet resourceSet = genModel.eResource().getResourceSet();
		URI uri = null;
		String modelDirectory = genModel.getModelDirectory();
		if (genClass != null) {
			GenPackage genPackage = genClass.getGenPackage();
			String factoryClassName = genPackage.getQualifiedFactoryClassName();
			String uriString = modelDirectory + "/" + factoryClassName.replaceAll("\\.", "/") + ".java";
			uri = URI.createPlatformResourceURI(uriString, true);
		} else {
			// old impl
			GenPackage genPackage = genModel.getGenPackages().get(0);
			String baseFolder = "";
			if (genPackage.getBasePackage() != null) {
				baseFolder = genPackage.getBasePackage().replace('.', '/');
			}
			String factoryImplementationFile = modelDirectory + "/" + 
					baseFolder + "/" + genPackage.getEcorePackage().getName() + "/impl/" + 
					genPackage.getFactoryClassName() + ".java";
			if (factoryImplementationFile.startsWith("/") && baseURI.toString().endsWith("/")) {
				factoryImplementationFile = factoryImplementationFile.substring(1);
			}
			uri = URI.createURI(baseURI.toString() + factoryImplementationFile);
		}
		Resource factoryImplementation = null;
		try {
			factoryImplementation = resourceSet.getResource(uri, true);
		} catch (Exception e) {
			// TODO handle this exception more gracefully
			e.printStackTrace();
		}

		if (factoryImplementation != null) {
			return refactorFactory(factoryImplementation, createMissingCustomClasses);
		}
		return Collections.emptyList();
	}


	private List<Resource> refactorFactory(Resource factoryImplementation, boolean createMissingCustomClasses) {
		boolean customPackageImported = false;
		boolean customClassFound = false;
		List<Resource> createdClasses = new ArrayList<Resource>();
		for (Iterator<EObject> i = factoryImplementation.getAllContents(); i.hasNext();) {

			EObject next = i.next();
			if (next instanceof PackageImport) {
				EList<String> namespaces = ((PackageImport) next).getNamespaces();
				if (namespaces.get(namespaces.size() - 1).equals(CUSTOM_SUB_PACKAGE)) {
					customPackageImported = true;
				}
			}
			if (next instanceof NewConstructorCall) {
				NewConstructorCall ncc = (NewConstructorCall) next;
				Type instantiatedType = ncc.getType();
				if (instantiatedType instanceof Class) {
					Class instatiatedClass = (Class) instantiatedType;
					if (instatiatedClass.getName().endsWith("Impl") && instatiatedClass.eResource() != factoryImplementation) {
						String customClassName = instatiatedClass.getName(
								).substring(0, instatiatedClass.getName().indexOf("Impl"));
						customClassName = customClassName + CUSTOM_CLASS_SUFFIX;
						List<String> customNameSegments = new BasicEList<String>(
								instatiatedClass.getContainingCompilationUnit().getNamespaces());
						customNameSegments.remove(customNameSegments.size() - 1);
						customNameSegments.add(CUSTOM_SUB_PACKAGE);
						if (!customPackageImported) {
							PackageImport customPackageImport = ImportsFactory.eINSTANCE.createPackageImport();
							customPackageImport.getNamespaces().addAll(customNameSegments);
							ncc.getContainingCompilationUnit().getImports().add(customPackageImport);
							customPackageImported = true;
						}
						customNameSegments.add(customClassName);
						String fullName = null;
						for(String namePart : customNameSegments) {
							if (fullName == null) {
								fullName = namePart;
							} else {
								fullName = fullName + "." + namePart;
							}
						}
						ConcreteClassifier customClass = instatiatedClass.getConcreteClassifier(fullName);
						if (customClass.eIsProxy()) {
							if (createMissingCustomClasses) {
								customClass = createInitialCustomClass(customNameSegments, instatiatedClass, factoryImplementation.getURI());								
								createdClasses.add(customClass.eResource());
							} else {
								customClass = null;
							}
						}
						if (customClass != null) {
							customClassFound = true;
							EList<ClassifierReference> classifierReferenceChain = ((NamespaceClassifierReference) ncc.getTypeReference()).getClassifierReferences();
							classifierReferenceChain.get(classifierReferenceChain.size() - 1).setTarget(customClass);							
						}
					}
				}
			}
		}

		if (customClassFound) {
			try {
				factoryImplementation.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return createdClasses;
	}

	public ConcreteClassifier createInitialCustomClass(
			List<String> customNameSegments, Class generatedImplementation, URI relativeURI) {

		ResourceSet resourceSet = generatedImplementation.eResource().getResourceSet();
		URI javaClassURI = relativeURI.trimSegments(customNameSegments.size() + 1);
		javaClassURI = javaClassURI.appendSegment("src");
		javaClassURI = javaClassURI.appendSegments(
				customNameSegments.toArray(new String[customNameSegments.size()])).appendFileExtension("java");
		Resource customClassResource = resourceSet.createResource(javaClassURI);
		CompilationUnit cu = generateCompilationUnit(customNameSegments);
		customClassResource.getContents().add(cu);

		prepareCompilationUnit(cu, generatedImplementation);

		try {
			customClassResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cu.getClassifiers().get(0);
	}

	public ConcreteClassifier createInitialCustomClass(GenClass genClass) {
		GenModel genModel = genClass.getGenModel();
		String modelPluginID = genModel.getModelPluginID();
		String name = genClass.getName();
		GenPackage genPackage = genClass.getGenPackage();
		String qualifiedPackageName = genPackage.getQualifiedPackageName();
		String path = genModel.getModelDirectory();
		int indexEnd = path.indexOf(modelPluginID) + modelPluginID.length();
		String srcFolder = path.substring(indexEnd + 1);
		URI uri = null;
		if("src".equals(srcFolder)){
			uri = URI.createPlatformResourceURI(path, true);
		} else {
			uri = URI.createPlatformResourceURI("/" + modelPluginID, true).appendSegment("src");
		}
		List<String> segments = new ArrayList<String>();
		for (String segment : qualifiedPackageName.split("\\.")) {
			segments.add(segment);
		}
		segments.add(CUSTOM_SUB_PACKAGE);
		String customClassName = name + CUSTOM_CLASS_SUFFIX;
		segments.add(customClassName);

		uri = uri.appendSegments(segments.toArray(new String[0])).appendFileExtension("java");
		ResourceSet resourceSet = genClass.eResource().getResourceSet();
		Resource customClassResource = resourceSet.createResource(uri);

		CompilationUnit cu = generateCompilationUnit(segments);
		customClassResource.getContents().add(cu);
		String qualifiedSuperClassName = genClass.getQualifiedClassName();
		URI superClassUri = URI.createPlatformResourceURI(path, true);
		superClassUri = superClassUri.appendSegments(qualifiedSuperClassName.split("\\.")).appendFileExtension("java");
		Resource superClassResource = resourceSet.getResource(superClassUri, true);
		CompilationUnit scCompUnit = (CompilationUnit) superClassResource.getContents().get(0);
		Class superClass = (Class) scCompUnit.getConcreteClassifier(qualifiedSuperClassName);

		Class customClass = prepareCompilationUnit(cu, superClass);
		try {
			customClassResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return customClass;
	}

	private Class prepareCompilationUnit(CompilationUnit cu, Class superClass) {
		Class clazz = (Class) cu.getClassifiers().get(0);
		ClassifierReference tr = TypesFactory.eINSTANCE.createClassifierReference();
		tr.setTarget(superClass);
		clazz.setExtends(tr);
		ClassifierImport classifierImport = ImportsFactory.eINSTANCE.createClassifierImport();
		classifierImport.getNamespaces().addAll(superClass.getContainingCompilationUnit().getNamespaces());
		classifierImport.setClassifier(superClass);
		cu.getImports().add(classifierImport);
		return clazz;
	}

	private CompilationUnit generateCompilationUnit(List<String> fullName) {
		CompilationUnit cu = ContainersFactory.eINSTANCE.createCompilationUnit();
		cu.getNamespaces().addAll(fullName.subList(0, fullName.size() - 1));

		Class clazz = ClassifiersFactory.eINSTANCE.createClass();
		clazz.setName(fullName.get(fullName.size() - 1));
		clazz.getAnnotationsAndModifiers().add(ModifiersFactory.eINSTANCE.createPublic());
		cu.getClassifiers().add(clazz);

		return cu;
	}

}
