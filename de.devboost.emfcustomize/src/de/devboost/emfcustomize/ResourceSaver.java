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
package de.devboost.emfcustomize;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;

public class ResourceSaver {

	public void saveResource(Resource resource) throws IOException {
		String key = "line.separator";
		String currentLineBreak = System.getProperty(key);
		System.setProperty(key, "\n");
		resource.save(null);
		System.setProperty(key, currentLineBreak);
	}
}
