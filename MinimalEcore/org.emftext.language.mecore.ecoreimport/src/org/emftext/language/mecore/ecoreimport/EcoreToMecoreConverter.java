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
import org.emftext.language.mecore.MComplexMultiplicity;
import org.emftext.language.mecore.MDataType;
import org.emftext.language.mecore.MEcoreType;
import org.emftext.language.mecore.MFeature;
import org.emftext.language.mecore.MMultiplicity;
import org.emftext.language.mecore.MPackage;
import org.emftext.language.mecore.MSimpleMultiplicity;
import org.emftext.language.mecore.MSimpleMultiplicityValue;
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
		
		setMultiplicity(mFeature, eFeature);
		
		mClass.getFeatures().add(mFeature);
	}

	private void setMultiplicity(final MFeature mFeature,
			final EStructuralFeature eFeature) {
		// set multiplicity
		int lowerBound = eFeature.getLowerBound();
		int upperBound = eFeature.getUpperBound();
		MMultiplicity multiplicity;
		if (lowerBound == 0 && upperBound == 1) {
			MSimpleMultiplicity simpleMultiplicity = factory.createMSimpleMultiplicity();
			simpleMultiplicity.setValue(MSimpleMultiplicityValue.OPTIONAL);
			multiplicity = simpleMultiplicity;
		} else if (lowerBound == 0 && upperBound == -1) {
			MSimpleMultiplicity simpleMultiplicity = factory.createMSimpleMultiplicity();
			simpleMultiplicity.setValue(MSimpleMultiplicityValue.STAR);
			multiplicity = simpleMultiplicity;
		} else if (lowerBound == 1 && upperBound == -1) {
			MSimpleMultiplicity simpleMultiplicity = factory.createMSimpleMultiplicity();
			simpleMultiplicity.setValue(MSimpleMultiplicityValue.PLUS);
			multiplicity = simpleMultiplicity;
		} else if (lowerBound == 1 && upperBound == 1) {
			// 1..1 is default and represented as null
			multiplicity = null;
		} else {
			MComplexMultiplicity complexMultiplicity = factory.createMComplexMultiplicity();
			complexMultiplicity.setLowerBound(lowerBound);
			complexMultiplicity.setUpperBound(upperBound);
			multiplicity = complexMultiplicity;
		}
		mFeature.setMultiplicity(multiplicity);
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
			MEcoreType mEcoreType = toMEcoreType(eObjectType);
			return mEcoreType;
		}
		
		EClass eStringToStringMapEntryType = EcorePackage.eINSTANCE.getEStringToStringMapEntry();
		if (eStringToStringMapEntryType.equals(eType)) {
			MEcoreType mEcoreType = toMEcoreType(eStringToStringMapEntryType);
			return mEcoreType;
		}
		
		System.out.println("Can't find type for " + eType + ". Using EObject instead.");
		return toMEcoreType(eObjectType);
	}

	private MEcoreType toMEcoreType(EClass eClass) {
		MEcoreType mEcoreType = factory.createMEcoreType();
		mEcoreType.setEcoreType(eClass);
		return mEcoreType;
	}
}
