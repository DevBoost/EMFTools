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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.devboost.buildboost.ant.AbstractAntTargetGeneratorProvider;
import de.devboost.buildboost.ant.IAntTargetGenerator;
import de.devboost.buildboost.genext.emf.artifacts.GeneratorModel;
import de.devboost.buildboost.model.IArtifact;
import de.devboost.buildboost.model.IBuildContext;

public class ExecuteEMFCustomizeStepProvider extends AbstractAntTargetGeneratorProvider {

	@Override
	public List<IAntTargetGenerator> getAntTargetGenerators(
			IBuildContext context, IArtifact artifact) {
		if (artifact instanceof GeneratorModel) {
			List<IAntTargetGenerator> steps = new ArrayList<IAntTargetGenerator>(1);
			GeneratorModel generatorModel = (GeneratorModel) artifact;
			steps.add(new ExecuteEMFCustomizeStep(generatorModel));
			return steps;
		} else {
			return Collections.emptyList();
		}
	}
}
