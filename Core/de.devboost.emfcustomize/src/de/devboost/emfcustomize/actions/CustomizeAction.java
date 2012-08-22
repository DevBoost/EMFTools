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
package de.devboost.emfcustomize.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.resource.java.IJavaOptions;

import de.devboost.emfcustomize.GeneratedFactoryRefactorer;

public class CustomizeAction implements IObjectActionDelegate {

	private StructuredSelection selection;
	
	@Override
	public void run(IAction action) {
		if (selection.getFirstElement() instanceof IFile) {
			IFile file = (IFile) selection.getFirstElement();
			process(file);
		}
	}

	private void process(IFile file) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getLoadOptions().put(
				IJavaOptions.DISABLE_CREATING_MARKERS_FOR_PROBLEMS, Boolean.TRUE);
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		Resource resource = resourceSet.getResource(uri, true);
		if (resource.getContents().isEmpty()) {
			return;
		}
		CompilationUnit cu = (CompilationUnit) resource.getContents().get(0);
		if (cu.getClassifiers().isEmpty() || !(cu.getClassifiers().get(0) instanceof Class)) {
			return;
		}
		Class generatedImplementation = (Class) cu.getClassifiers().get(0);
		String customClassName = generatedImplementation.getName(
				).substring(0, generatedImplementation.getName().indexOf("Impl"));
		customClassName = customClassName + GeneratedFactoryRefactorer.CUSTOM_CLASS_SUFFIX;
		
		List<String> customNameSegments = new ArrayList<String>();
		customNameSegments.addAll(cu.getNamespaces());
		customNameSegments.remove(customNameSegments.size() - 1);
		customNameSegments.add(GeneratedFactoryRefactorer.CUSTOM_SUB_PACKAGE);
		
		customNameSegments.add(customClassName);
		new GeneratedFactoryRefactorer().createInitialCustomClass(
				customNameSegments, generatedImplementation, 
				resource.getURI());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (StructuredSelection) selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) { }

}
