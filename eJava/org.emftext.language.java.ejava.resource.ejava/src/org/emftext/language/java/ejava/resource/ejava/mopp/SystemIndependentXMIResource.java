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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLString;

/**
 * A {@link SystemIndependentXMIResource} is a custom {@link XMIResource} that
 * does not use the default line break for the current OS, but Unix line breaks.
 * It also uses a fixed width for all lines.
 * 
 * This behavior is required to get identical result when saving XMI resource on
 * systems running a different OS.
 */
// TODO This class has been copied to 'org.emftext.language.java.ejava.resource.ejava'
public class SystemIndependentXMIResource extends XMIResourceImpl {
	
	public SystemIndependentXMIResource(URI uri) {
		super(uri);
	}

	@Override
	protected XMLSave createXMLSave() {
		
		return new XMISaveImpl(createXMLHelper()) {
			
			@Override
			protected void init(XMLResource resource, Map<?, ?> options) {
				super.init(resource, options);
				if (doc != null) {
					String temporaryFileName = doc.getTemporaryFileName();
					// get line width
					Integer lineWidth = (Integer) options.get(XMLResource.OPTION_LINE_WIDTH);
					int effectiveLineWidth = lineWidth == null ? Integer.MAX_VALUE : lineWidth;
					
					doc = new XMLString(effectiveLineWidth, publicId, systemId, temporaryFileName) {
						
						private static final long serialVersionUID = -672620373813232183L;

						public void addLine() {
							// use Unix line breaks
							add("\n");
							currentLineWidth = 0;
						}
					};
				}
			}
		};
	}
}
