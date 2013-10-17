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
package org.buildboost.genext.emfcustomize.steps;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.IConstants;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.genext.emf.artifacts.GeneratorModel;
import de.devboost.buildboost.model.IDependable;
import de.devboost.buildboost.steps.ClasspathHelper;
import de.devboost.buildboost.util.XMLContent;

/**
 * The {@link ExecuteEMFCustomizeStep} generates a script that calls the EMF
 * code generators to obtain code from Ecore models. 
 */
public class ExecuteEMFCustomizeStep extends AbstractAntTargetGenerator {

	public final static String MAIN_TASK = "emfcustomize-refactoring-";

	private GeneratorModel generatorModel;

	public ExecuteEMFCustomizeStep(GeneratorModel generatorModel) {
		this.generatorModel = generatorModel;
	}

	public Collection<AntTarget> generateAntTargets() throws BuildException {
		Collection<IDependable> dependencies = generatorModel.getDependencies();
		if (dependencies.isEmpty()) {
			throw new BuildException("Generator models are expected to have a dependency to the BuildBoost EMF plug-in.");
		}
		XMLContent classpath = new ClasspathHelper().getClasspath(generatorModel, true);
		XMLContent runtimeClasspath = new ClasspathHelper("",";").getClasspath(generatorModel, true);
		
		String srcDirs = "";
		for (File subFolder : generatorModel.getProjectDir().listFiles()) {
			if (subFolder.isDirectory() && subFolder.getName().startsWith("src")) {
				srcDirs = srcDirs + subFolder.getAbsolutePath() + ";";
			}
		}

		File genModelFile = generatorModel.getFile();
		String genModelPath = genModelFile.getAbsolutePath();
		String pluginsLocation = generatorModel.getProjectDir().getParent();

		XMLContent sb = new XMLContent();
		sb.append("<delete dir=\"temp_eclipse_workspace\" />");
		sb.append("<mkdir dir=\"temp_eclipse_workspace\" />");
		sb.append(IConstants.NL);
		
		sb.append("<echo message=\"Refactoring generated EMF factory for customization " + genModelPath + "\" />");
		sb.append("<java classname=\"de.devboost.emfcustomize.app.EMFCustomizeApplication\" failonerror=\"true\" fork=\"true\" >");
		sb.append("<arg value=\"" + genModelPath + "\"/>");
		sb.append("<arg value=\"" + pluginsLocation + "\"/>");
		sb.append("<arg value=\"" + runtimeClasspath + srcDirs + "\"/>");
		sb.append("<classpath>");
		sb.append(classpath);
		sb.append("</classpath>");
		sb.append("</java>");
		sb.append(IConstants.NL);
		
		return Collections.singleton(new AntTarget(MAIN_TASK + genModelFile.getName(), sb));
	}
}
