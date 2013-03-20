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
package org.buildboost.genext.emfcustomize.stages;

import java.io.File;

import org.buildboost.genext.emfcustomize.EMFCustomizeDependencyAdder;
import org.buildboost.genext.emfcustomize.steps.ExecuteEMFCustomizeStepProvider;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.genext.emf.discovery.GenModelFinder;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;

public class ExecuteEMFCustomizeStage extends AbstractBuildStage 
	implements IUniversalBuildStage {

	private String artifactsFolder;
	
	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	public AntScript getScript() throws BuildException {
		BuildContext context = createContext(true);
		
		File artifactsFolderFile = new File(artifactsFolder);
		
		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(artifactsFolderFile));
		context.addBuildParticipant(new PluginFinder(artifactsFolderFile));
		context.addBuildParticipant(new GenModelFinder(artifactsFolderFile));
		context.addBuildParticipant(new EMFCustomizeDependencyAdder());
		context.addBuildParticipant(new ExecuteEMFCustomizeStepProvider());
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Execute EMFCustomize's generated factory refactoring");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}

	@Override
	public int getPriority() {
		// must be executed after compiling EMFCustomize itself
		return CompileEMFCustomizeStage.PRIORITY + 1;
	}
}
