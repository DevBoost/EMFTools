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
package org.buildboost.genext.emfcustomize;

import java.io.File;

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

	private String buildDirPath;
	private String eclipseHome;
	
	public void setBuildDirPath(String buildDirPath) {
		this.buildDirPath = buildDirPath;
	}

	public void setEclipseHome(String eclipseHome) {
		this.eclipseHome = eclipseHome;
	}

	public AntScript getScript() throws BuildException {
		File buildDir = new File(buildDirPath);
		File targetPlatform = new File(eclipseHome);

		BuildContext context = createContext(true);
		
		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(targetPlatform));
		context.addBuildParticipant(new PluginFinder(buildDir));
		context.addBuildParticipant(new GenModelFinder(buildDir));
		context.addBuildParticipant(new EMFCustomieDependencyAdder());
		context.addBuildParticipant(new ExecuteEMFCustomizeStepProvider());
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Execute EMFCustomize's generated factory refactoring");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}

	@Override
	public int getPriority() {
		return 2001;
	}
}
