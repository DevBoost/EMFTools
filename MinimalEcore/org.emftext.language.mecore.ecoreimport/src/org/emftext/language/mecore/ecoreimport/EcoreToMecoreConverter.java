/*******************************************************************************
 * Copyright (c) 2012
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.language.mecore.ecoreimport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.emftext.language.mecore.MClass;
import org.emftext.language.mecore.MDataType;
import org.emftext.language.mecore.MEcoreType;
import org.emftext.language.mecore.MFeature;
import org.emftext.language.mecore.MPackage;
import org.emftext.language.mecore.MType;
import org.emftext.language.mecore.MecoreFactory;

public class EcoreToMecoreConverter {
	
	private MecoreFactory factory = MecoreFactory.eINSTANCE;
	private Map<EClassifier, MType> eTypeToMTypeMap = new LinkedHashMap<EClassifier, MType>();
	private List<Runnable> commands = new ArrayList<Runnable>();

	public MPackage convert(EPackage ePackage) {
		MPackage mPackage = factory.createMPackage();
		mPackage.setName(ePackage.getName());
		mPackage.setNamespace(ePackage.getNsURI());
		
		List<EClassifier> eClassifiers = ePackage.getEClassifiers();
		for (EClassifier eClassifier : eClassifiers) {
			convertEClassifier(mPackage, eClassifier);
		}
		
		for (Runnable command : commands) {
			command.run();
		}
		return mPackage;
	}

	private void convertEClassifier(MPackage mPackage, EClassifier eClassifier) {
		if (eClassifier instanceof EClass) {
			EClass eClass = (EClass) eClassifier;
			convertEClass(mPackage, eClass);
		} else {
			throw new IllegalArgumentException("Unknown element to convert: " + eClassifier);
		}
	}

	private void convertEClass(MPackage mPackage, EClass eClass) {
		MClass mClass = factory.createMClass();
		mClass.setName(eClass.getName());
		mClass.setAbstract(eClass.isAbstract());
		mClass.setInterface(eClass.isInterface());
		
		mPackage.getContents().add(mClass);
		eTypeToMTypeMap.put(eClass, mClass);
		
		List<EStructuralFeature> structuralFeatures = eClass.getEStructuralFeatures();
		for (EStructuralFeature eStructuralFeature : structuralFeatures) {
			convertEStructuralFeatures(mClass, eStructuralFeature);
		}
	}

	private void convertEStructuralFeatures(MClass mClass,
			EStructuralFeature eStructuralFeature) {
		convertEFeature(mClass, eStructuralFeature);
	}

	private void convertEFeature(MClass mClass, final EStructuralFeature eFeature) {
		final MFeature mFeature = factory.createMFeature();
		mFeature.setName(eFeature.getName());
		
		commands.add(new Runnable() {
			
			@Override
			public void run() {
				EClassifier eType = eFeature.getEType();
				MType mType = getMType(eType);
				mFeature.setType(mType);
			}
		});
		
		// TODO set multiplicity
		
		mClass.getFeatures().add(mFeature);
	}

	private MType getMType(EClassifier eType) {
		MType mappedType = eTypeToMTypeMap.get(eType);
		if (mappedType != null) {
			return mappedType;
		}
		
		if (eType instanceof EDataType) {
			EDataType eDataType = (EDataType) eType;
			Class<?> instanceClass = eDataType.getInstanceClass();
			for (EClassifier classifier : EcorePackage.eINSTANCE.getEClassifiers()) {
				if (classifier instanceof EDataType) {
					EDataType nextDataType = (EDataType) classifier;
					if (nextDataType.getInstanceClass().equals(instanceClass)) {
						MDataType mDataType = factory.createMDataType();
						mDataType.setEDataType(nextDataType);
						return mDataType;
					}
				}
			}
		}
		
		EClass eObjectType = EcorePackage.eINSTANCE.getEObject();
		if (eObjectType.equals(eType)) {
			MEcoreType mEcoreType = factory.createMEcoreType();
			mEcoreType.setEcoreType(eObjectType);
			return mEcoreType;
		}
		
		EClass eStringToStringMapEntry = EcorePackage.eINSTANCE.getEStringToStringMapEntry();
		if (eStringToStringMapEntry.equals(eType)) {
			MEcoreType mEcoreType = factory.createMEcoreType();
			mEcoreType.setEcoreType(eStringToStringMapEntry);
			return mEcoreType;
		}
		
		System.out.println("Can't find type for " + eType);
		return null;
	}
}
