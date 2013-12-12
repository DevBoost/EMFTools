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
package org.emftext.language.java.ejava.resource.ejava.mopp;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.ejava.resource.ejava.EjavaEProblemType;
import org.emftext.language.java.ejava.resource.ejava.IEjavaBuilder;

import de.devboost.emftools.utils.SystemIndependentXMIResourceFactory;

public class EjavaBuilderAdapter extends IncrementalProjectBuilder implements IResourceDeltaVisitor, IResourceVisitor {
	
	/**
	 * The ID of the default, generated builder.
	 */
	public final static String BUILDER_ID = "org.emftext.language.java.ejava.resource.ejava.builder";
	
	private IEjavaBuilder defaultBuilder = new EjavaBuilder();
	
	/**
	 * This monitor is used during the build.
	 */
	private IProgressMonitor monitor;
	
	public IProject[] build(int kind, Map<String, String> args, final IProgressMonitor monitor) throws CoreException {
		// Set context for build
		this.monitor = monitor;

		// Perform build by calling the resource visitors
		IResourceDelta delta = getDelta(getProject());
		if (delta != null) {
			// This is an incremental build
			delta.accept(this);
		} else {
			// This is a full build
			getProject().accept(this);
		}

		// Reset build context
		this.monitor = null;
		return null;
	}
	
	public void build(IFile resource, ResourceSet resourceSet, IProgressMonitor monitor) {
		// We must catch exceptions to avoid problem with the build process if
		// something goes wrong with eJava.
		try {
			URI uri = URI.createPlatformResourceURI(resource.getFullPath().toString(), true);
			IEjavaBuilder builder = getBuilder();
			if (builder.isBuildingNeeded(uri)) {
				EjavaResource customResource = (EjavaResource) resourceSet.getResource(uri, true);
				new EjavaMarkerHelper().removeAllMarkers(resource, getBuilderMarkerId());
				builder.build(customResource, monitor);
			}
		} catch (Throwable e) {
			EjavaPlugin.logWarning("Exception while processing eJava file.", e);
		}
	}
	
	/**
	 * Returns the builder that shall be used by this adapter. This allows
	 * subclasses to perform builds with different builders.
	 */
	public IEjavaBuilder getBuilder() {
		return defaultBuilder;
	}
	
	/**
	 * Returns the id for the markers that are created by this builder. This
	 * allows subclasses to produce different kinds of markers.
	 */
	public String getBuilderMarkerId() {
		return new EjavaMarkerHelper().getMarkerID(EjavaEProblemType.BUILDER_ERROR);
	}
	
	/**
	 * Runs the task item builder to search for new task items in changed resources.
	 */
	public void runTaskItemBuilder(IFile resource, ResourceSet resourceSet, IProgressMonitor monitor) {
		EjavaTaskItemBuilder taskItemBuilder = new EjavaTaskItemBuilder();
		new EjavaMarkerHelper().removeAllMarkers(resource, taskItemBuilder.getBuilderMarkerId());
		taskItemBuilder.build(resource, resourceSet, monitor);
	}
	
	@Override	
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		return doVisit(resource, delta.getKind() == IResourceDelta.REMOVED);
	}
	
	@Override	
	public boolean visit(IResource resource) throws CoreException {
		return doVisit(resource, false);
	}
	
	protected boolean doVisit(IResource resource, boolean removed) throws CoreException {
		if (removed) {
			URI uri = URI.createPlatformResourceURI(resource.getFullPath().toString(), true);
			IEjavaBuilder builder = getBuilder();
			if (builder.isBuildingNeeded(uri)) {
				builder.handleDeletion(uri, monitor);
			}
			new EjavaMarkerHelper().removeAllMarkers(resource, getBuilderMarkerId());
			return false;
		}
		
		if (resource instanceof IFile && resource.getName().endsWith("." + new EjavaMetaInformation().getSyntaxName())) {
			// First, call the default generated builder that is usually customized to add
			// compilation-like behavior.
			
			//=== CHANGED: eJava's wrapper mechanism needs one resource set per resource!
			ResourceSet resourceLocalResourceSet = new ResourceSetImpl();
			Map<String, Object> extensionToFactoryMap = resourceLocalResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
			extensionToFactoryMap.put("ecore", new SystemIndependentXMIResourceFactory());
			extensionToFactoryMap.put("genmodel", new SystemIndependentXMIResourceFactory());
			
			build((IFile) resource, resourceLocalResourceSet, monitor);
			runTaskItemBuilder((IFile) resource, resourceLocalResourceSet, monitor);
			//====
			
			return false;
		}
		return true;
	}
	
}
