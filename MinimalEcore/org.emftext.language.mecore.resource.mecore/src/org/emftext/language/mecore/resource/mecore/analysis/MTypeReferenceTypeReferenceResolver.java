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

package org.emftext.language.mecore.resource.mecore.analysis;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.mecore.MFeature;
import org.emftext.language.mecore.MType;
import org.emftext.language.mecore.MTypeReference;
import org.emftext.language.mecore.resource.mecore.IMecoreReferenceResolveResult;
import org.emftext.language.mecore.resource.mecore.analysis.types.TypeResolver;

public class MTypeReferenceTypeReferenceResolver implements org.emftext.language.mecore.resource.mecore.IMecoreReferenceResolver<org.emftext.language.mecore.MTypeReference, org.emftext.language.mecore.MType> {
	
	private MecoreDefaultResolverDelegate<MFeature, MType> delegate1 = new MecoreDefaultResolverDelegate<MFeature, MType>();
	private TypeResolver delegate2 = new TypeResolver();
	
	public void resolve(String identifier, MTypeReference container, EReference reference, int position, boolean resolveFuzzy, final IMecoreReferenceResolveResult<MType> result) {
		EObject root = EcoreUtil.getRootContainer(container);
		delegate1.tryToResolveIdentifierInObjectTree(identifier, container, root, reference, position, resolveFuzzy, result, true);
		if (!result.wasResolved() || resolveFuzzy) {
			delegate2.resolve(identifier, container, resolveFuzzy, result);
		}
	}
	
	public String deResolve(MType element, MTypeReference container, EReference reference) {
		return delegate2.deResolve(element);
	}
	
	public void setOptions(Map<?,?> options) {
		// not needed
	}
}
