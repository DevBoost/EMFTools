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
import java.util.LinkedHashSet;
import java.util.Set;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.filters.IdentifierFilter;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;
import de.devboost.buildboost.steps.compile.CompileProjectStepProvider;

/**
 * The {@link CompileEMFCustomizeStage} compiles the EMFCustomize tooling which
 * is required to integration customization code which generated EMF mode code.
 */
public class CompileEMFCustomizeStage extends AbstractBuildStage 
	implements IUniversalBuildStage {

	public static final int PRIORITY = 2005;
	
	private static final Set<String> EMFCUSTOMIZE_PLUGIN_IDENTIFIERS = new LinkedHashSet<String>();
	
	static {
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("de.devboost.emfcustomize");

		//JaMoPP is required
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.commons.layout");
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.commons.antlr3_4_0");
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.language.java");
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.language.java.resource");
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.language.java.resource.java");
		EMFCUSTOMIZE_PLUGIN_IDENTIFIERS.add("org.emftext.language.java.resource.bcel");
	}

	private String artifactsFolder;
	
	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	public AntScript getScript() throws BuildException {
		BuildContext context = createContext(false);
		
		File artifactsFolderFile = new File(artifactsFolder);
		
		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(artifactsFolderFile));
		context.addBuildParticipant(new PluginFinder(artifactsFolderFile));
		
		context.addBuildParticipant(new CompileProjectStepProvider());
		
		context.addBuildParticipant(new IdentifierFilter(EMFCUSTOMIZE_PLUGIN_IDENTIFIERS));
		
		AutoBuilder builder = new AutoBuilder(context);

		AntScript script = new AntScript();
		script.setName("Compile EMFCustomize plug-ins");
		script.addTargets(builder.generateAntTargets());
		return script;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}
}
