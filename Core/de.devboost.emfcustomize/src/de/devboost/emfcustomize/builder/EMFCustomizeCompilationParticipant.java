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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.emftext.language.java.resource.java.IJavaOptions;

public class EMFCustomizeCompilationParticipant extends CompilationParticipant {

	private EMFCustomizeBuilder builder = new EMFCustomizeBuilder();
	
	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		//markers are already created by the JDT
		resourceSet.getLoadOptions().put(
				IJavaOptions.DISABLE_CREATING_MARKERS_FOR_PROBLEMS, Boolean.TRUE);
		for (BuildContext context : files) {
			URI uri = URI.createPlatformResourceURI(context.getFile().getFullPath().toString(), true);
			if (builder.isBuildingNeeded(uri)) {
				IWorkspace workspace = context.getFile().getWorkspace();
				List<Resource> createdClasses = builder.build(resourceSet.getResource(uri, true), context.getFile());
				IFile[] newSrcFiles = new IFile[createdClasses.size()];
				for (int i = 0; i < createdClasses.size(); i++) {
					newSrcFiles[i] = workspace.getRoot().getFile(new Path(
							createdClasses.get(i).getURI().toPlatformString(true)));
				}
				context.recordAddedGeneratedFiles(newSrcFiles);
			}
		}
	}
		
}
