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
package de.devboost.emfcustomize.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.resource.java.mopp.JavaResource;

import de.devboost.emfcustomize.EcoreModelRefactorer;
import de.devboost.emfcustomize.GeneratedFactoryRefactorer;
import de.devboost.emfcustomize.ResourceSaver;

public class EMFCustomizeBuilder {
	
	public boolean isBuildingNeeded(URI uri) {
		if (isCustomCodeFile(uri)) {
			//react on changes in custom code
			return true;
		}
		if (isFactoryCodeFile(uri)) {
			//react on re-generated factory
			return true;
		}
		return false;
	}

	private boolean isCustomCodeFile(URI uri) {
		return "java".equals(uri.fileExtension()) && 
				uri.trimFileExtension().lastSegment().endsWith(
						GeneratedFactoryRefactorer.CUSTOM_CLASS_SUFFIX);
	}
	
	private boolean isFactoryCodeFile(URI uri) {
		return "java".equals(uri.fileExtension()) && 
				uri.trimFileExtension().lastSegment().endsWith(
						GeneratedFactoryRefactorer.FACTORY_CLASS_SUFFIX);
	}

	public List<Resource> build(Resource resource, IFile iFile) {
		ResourceSet resourceSet = resource.getResourceSet();
		String possibleGenModelName = resource.getURI().trimSegments(2).lastSegment();
		if (possibleGenModelName == null) {
			possibleGenModelName = "";
		}
		GenModel genModel = findGenModel(iFile.getProject(), possibleGenModelName, resourceSet);
		resourceSet.getURIConverter().getURIMap().putAll(
				EcorePlugin.computePlatformURIMap());
		
		if (genModel == null) {
			return Collections.emptyList();
		}
		
		if (isCustomCodeFile(resource.getURI())) {
			// new strategy: adjust factory only when a custom class file is built for having the chance to determine which GenClass is needed
			String className = iFile.getFullPath().removeFileExtension().lastSegment();
			int index = className.indexOf(GeneratedFactoryRefactorer.CUSTOM_CLASS_SUFFIX);
			if(index != -1){
				className = className.substring(0, index);
			}
			List<GenClass> genClasses = getGenClassByName(genModel, className);
			if(genClasses.size() > 1){
				return Collections.emptyList();
			}
			GenClass genClass = genClasses.get(0);
			
			//propagate EOperations
			GenPackage genPackage = genModel.getGenPackages().get(0);
			
			new EcoreModelRefactorer().propagateEOperations((JavaResource) resource, 
					genPackage.getEcorePackage());
			genModel.reconcile();
			try {
				Resource ecoreModelResource = genPackage.getEcorePackage().eResource();
				Resource genModelResource = genModel.eResource();
				new ResourceSaver().saveResource(genModelResource);
				new ResourceSaver().saveResource(ecoreModelResource);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// adjust factory
			adjustFactory(iFile, resourceSet);
			return new GeneratedFactoryRefactorer().refactorFactory(genModel, genClass, URI.createPlatformResourceURI("/", true), false);
//			return Collections.emptyList();
		} 
//		else {
//			return new GeneratedFactoryRefactorer().refactorFactory(genModel, URI.createPlatformResourceURI("/", true), false);
//		}
		return Collections.emptyList();
	}

	private List<GenClass> getGenClassByName(GenModel genModel, String name){
		List<GenClass> genClassCandidates = new ArrayList<GenClass>();
		List<GenPackage> genPackages = genModel.getAllGenPackagesWithClassifiers();
		for (GenPackage genPackage : genPackages) {
			EList<GenClass> genClasses = genPackage.getGenClasses();
			for (GenClass genClass : genClasses) {
				if(genClass.getName().equals(name)){
					genClassCandidates.add(genClass);
				}
			}
		}
		return genClassCandidates;
	}
	
	private void adjustFactory(IFile iFile, ResourceSet resourceSet) {
		//adjust factory for generation gap pattern
		IJavaProject javaProject = JavaCore.create(iFile.getProject());
		try {
			for (IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
				if (cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					//to register newly generated code;
					updateClaspath(new File(iFile.getWorkspace().getRoot().getLocation().toString() + 
							cpEntry.getPath().toString()), "", resourceSet);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private GenModel findGenModel(IProject project, final String name, ResourceSet rs) {
		final List<IFile> genModelResources = new ArrayList<IFile>();
		try {
			project.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFile && resource.getName().toLowerCase().equals(
							name.toLowerCase() + ".genmodel")) {
						genModelResources.add(0, (IFile) resource);
					} else if (resource instanceof IFile && resource.getName().endsWith(".genmodel")) {
						genModelResources.add((IFile) resource);
					}
					return true;
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (genModelResources.isEmpty()) {
			return null;
		}
		IFile genModelFile =genModelResources.get(0);
		URI genModelURI = URI.createPlatformResourceURI(genModelFile.getFullPath().toString(), true);
		Resource genModelResource = rs.getResource(genModelURI, true);
		return (GenModel) genModelResource.getContents().get(0);
	}

	private void updateClaspath(File parent, String fullPackageName, ResourceSet resourceSet) {
		if (!parent.exists()) {
			return;			
		}
		JavaClasspath cp = JavaClasspath.get(resourceSet);
		for (File child : parent.listFiles()) {
			if (!child.getName().startsWith(".")) {
				if (child.isDirectory()) {
					updateClaspath(child, fullPackageName + child.getName() + ".", resourceSet);
				} else if (child.getName().endsWith(".java")) {
					String className = child.getName();
					className = className.substring(0, className.lastIndexOf("."));
					URI uri = URI.createFileURI(child.getAbsolutePath());
					cp.registerClassifier(fullPackageName, className, uri);
				}
			}

			
		}
	}

	public IStatus handleDeletion(URI uri) {
		return Status.OK_STATUS;
	}

}
