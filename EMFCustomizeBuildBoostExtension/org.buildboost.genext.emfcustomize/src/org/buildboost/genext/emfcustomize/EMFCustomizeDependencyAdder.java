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

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.DependencyResolver;
import de.devboost.buildboost.artifacts.Plugin;
import de.devboost.buildboost.genext.emf.artifacts.GeneratorModel;
import de.devboost.buildboost.genext.emf.discovery.GenModelFinder;
import de.devboost.buildboost.model.AbstractBuildParticipant;
import de.devboost.buildboost.model.IArtifact;
import de.devboost.buildboost.model.IBuildContext;
import de.devboost.buildboost.model.IBuildParticipant;
import de.devboost.buildboost.model.UnresolvedDependency;

public final class EMFCustomizeDependencyAdder extends
		AbstractBuildParticipant {
	
	@Override
	public void execute(IBuildContext context) throws BuildException {
		for (IArtifact artifact : context.getDiscoveredArtifacts()) {
			if (artifact instanceof GeneratorModel) {
				UnresolvedDependency dependency = new UnresolvedDependency(Plugin.class, "de.devboost.emfcustomize", null, true, null, true, false, false);
				artifact.getUnresolvedDependencies().add(dependency);		
			}
		}
	}

	@Override
	public boolean dependsOn(IBuildParticipant otherParticipant) {
		if (otherParticipant instanceof GenModelFinder) {
			return true;
		}
		return super.dependsOn(otherParticipant);
	}

	@Override
	public boolean isReqiredFor(IBuildParticipant otherParticipant) {
		if (otherParticipant instanceof DependencyResolver) {
			return true;
		}
		return super.isReqiredFor(otherParticipant);
	}
}
